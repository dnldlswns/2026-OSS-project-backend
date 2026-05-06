from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from docx import Document
from io import BytesIO

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


@app.post("/ai/review")
async def review_file(file: UploadFile = File(...)):
    filename = file.filename or ""
    content = await file.read()

    file_size = len(content)
    file_type = get_file_extension(filename)
    is_supported = is_supported_file(file_type, file_size)
    extracted_text = extract_text_from_file(filename, content)

    is_valid = is_supported and bool(extracted_text.strip())

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

            "workTitle": "청년 작가 전시회",
            "activityStartDate": "2025-03-01",
            "activityEndDate": "2025-03-19",
            "organizerType": "주최",
            "organizerName": "서울아트센터",
            "applicantRole": "작가",

            "evidenceCategory": "작품정보이미지",
            "extractedText": extracted_text[:1000],
            "score": 80 if is_valid else 30,
            "confidence": 0.8 if is_valid else 0.3,
            "isValid": is_valid,
            "reason": "작품명, 발표일, 주최/제작, 신청자 역할, 증빙자료 정보를 기준으로 검토 결과 JSON을 반환했습니다."
        }
    }