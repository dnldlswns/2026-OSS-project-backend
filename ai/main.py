from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from docx import Document
from io import BytesIO
import re

app = FastAPI(title="ArtPass AI Server")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/")
def root():
    return {
        "message": "ArtPass AI Server is running"
    }


def get_file_extension(filename: str) -> str:
    if "." not in filename:
        return ""
    return filename.rsplit(".", 1)[1].lower()


def is_supported_file(file_type: str, file_size: int) -> bool:
    allowed_extensions = ["jpg", "jpeg", "gif", "pdf", "zip", "png", "txt", "docx"]

    if file_type not in allowed_extensions:
        return False

    max_total_size = 50 * 1024 * 1024
    max_png_size = 3 * 1024 * 1024

    if file_size > max_total_size:
        return False

    if file_type == "png" and file_size > max_png_size:
        return False

    return True


def extract_text_from_file(filename: str, content: bytes) -> str:
    file_type = get_file_extension(filename)

    if file_type == "txt":
        return content.decode("utf-8", errors="ignore")

    if file_type == "docx":
        document = Document(BytesIO(content))
        return "\n".join([paragraph.text for paragraph in document.paragraphs])

    if file_type in ["jpg", "jpeg", "gif", "png"]:
        return "이미지 파일 업로드는 확인되었습니다. 이미지 OCR 분석은 추후 고도화 예정입니다."

    if file_type == "pdf":
        return "PDF 파일 업로드는 확인되었습니다. PDF 텍스트 추출은 추후 고도화 예정입니다."

    if file_type == "zip":
        return "ZIP 파일 업로드는 확인되었습니다. 압축 파일 내부 분석은 추후 고도화 예정입니다."

    return ""


def find_value_by_keywords(text: str, keywords: list[str]) -> str:
    lines = [line.strip() for line in text.splitlines() if line.strip()]

    for line in lines:
        for keyword in keywords:
            if keyword in line:
                value = line.replace(keyword, "")
                value = value.replace(":", "")
                value = value.replace("-", "")
                value = value.strip()

                if value:
                    return value

                return line

    return ""


def extract_date_range(text: str) -> tuple[str, str]:
    date_pattern = r"\d{4}[-./년\s]\d{1,2}[-./월\s]\d{1,2}"
    dates = re.findall(date_pattern, text)

    cleaned_dates = []
    for date in dates:
        cleaned = date.replace("년", "-").replace("월", "-").replace("일", "")
        cleaned = cleaned.replace(".", "-").replace("/", "-").replace(" ", "")
        cleaned = cleaned.strip("-")
        cleaned_dates.append(cleaned)

    if len(cleaned_dates) >= 2:
        return cleaned_dates[0], cleaned_dates[1]

    if len(cleaned_dates) == 1:
        return cleaned_dates[0], cleaned_dates[0]

    return "", ""


def analyze_evidence_text(text: str) -> dict:
    work_title = find_value_by_keywords(
        text,
        ["작품명", "작품 제목", "제목", "공연명", "전시명"]
    )

    organizer_name = find_value_by_keywords(
        text,
        ["주최", "제작", "주관", "기관", "단체명"]
    )

    applicant_role = find_value_by_keywords(
        text,
        ["신청자의 역할", "역할", "참여역할", "담당", "출연", "작가", "연출", "기획"]
    )

    activity_start_date, activity_end_date = extract_date_range(text)

    field_results = [
        {
            "fieldName": "작품명",
            "value": work_title,
            "confidence": 0.85 if work_title else 0.0,
            "status": "PASS" if work_title else "REVIEW",
            "reason": "작품명 관련 키워드를 통해 값을 추출했습니다." if work_title else "작품명 정보를 찾지 못했습니다."
        },
        {
            "fieldName": "작품 발표일(기간)",
            "value": f"{activity_start_date} ~ {activity_end_date}" if activity_start_date else "",
            "confidence": 0.85 if activity_start_date else 0.0,
            "status": "PASS" if activity_start_date else "REVIEW",
            "reason": "날짜 형식의 텍스트를 추출했습니다." if activity_start_date else "작품 발표일 또는 기간 정보를 찾지 못했습니다."
        },
        {
            "fieldName": "주최/제작",
            "value": organizer_name,
            "confidence": 0.85 if organizer_name else 0.0,
            "status": "PASS" if organizer_name else "REVIEW",
            "reason": "주최/제작 관련 키워드를 통해 값을 추출했습니다." if organizer_name else "주최/제작 정보를 찾지 못했습니다."
        },
        {
            "fieldName": "신청자의 역할",
            "value": applicant_role,
            "confidence": 0.85 if applicant_role else 0.0,
            "status": "PASS" if applicant_role else "REVIEW",
            "reason": "역할 관련 키워드를 통해 값을 추출했습니다." if applicant_role else "신청자의 역할 정보를 찾지 못했습니다."
        }
    ]

    pass_count = len([result for result in field_results if result["status"] == "PASS"])
    score = int((pass_count / len(field_results)) * 100)

    return {
        "workTitle": work_title,
        "activityStartDate": activity_start_date,
        "activityEndDate": activity_end_date,
        "organizerName": organizer_name,
        "applicantRole": applicant_role,
        "fieldResults": field_results,
        "score": score,
        "confidence": round(score / 100, 2),
        "isValid": score >= 50
    }


@app.post("/ai/review")
async def review_file(file: UploadFile = File(...)):
    filename = file.filename or ""
    content = await file.read()

    file_size = len(content)
    file_type = get_file_extension(filename)
    is_supported = is_supported_file(file_type, file_size)
    extracted_text = extract_text_from_file(filename, content)

    analysis_result = analyze_evidence_text(extracted_text)

    is_valid = is_supported and analysis_result["isValid"]

    return {
        "status": "success",
        "message": "예술활동 증빙자료 검토가 완료되었습니다.",
        "data": {
            "fileName": filename,
            "fileType": file_type,
            "fileSize": file_size,
            "isSupported": is_supported,

            "activityType": "창작",
            "artField": "미술",
            "mainActivityField": "미술(일반)",
            "detailField": "회화",

            "workTitle": analysis_result["workTitle"],
            "activityStartDate": analysis_result["activityStartDate"],
            "activityEndDate": analysis_result["activityEndDate"],
            "organizerType": "주최",
            "organizerName": analysis_result["organizerName"],
            "applicantRole": analysis_result["applicantRole"],

            "evidenceCategory": "증빙자료",
            "extractedText": extracted_text[:1000],
            "fieldResults": analysis_result["fieldResults"],
            "score": analysis_result["score"] if is_supported else 0,
            "confidence": analysis_result["confidence"] if is_supported else 0.0,
            "isValid": is_valid,
            "reason": "증빙자료 텍스트에서 작품명, 발표일, 주최/제작, 신청자 역할을 키워드 기반으로 분석했습니다."
        }
    }