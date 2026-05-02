from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from docx import Document
from io import BytesIO

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

ALLOWED_EXTENSIONS = [".pdf", ".jpg", ".jpeg", ".png", ".docx", ".hwp"]
MAX_FILE_SIZE = 10 * 1024 * 1024


@app.get("/")
def root():
    return {"message": "ArtPass AI Server is running"}


def extract_text_from_docx(file_bytes):
    document = Document(BytesIO(file_bytes))
    paragraphs = []

    for paragraph in document.paragraphs:
        text = paragraph.text.strip()
        if text:
            paragraphs.append(text)

    return "\n".join(paragraphs)


@app.post("/review")
async def review_file(file: UploadFile = File(...)):
    filename = file.filename
    content_type = file.content_type

    contents = await file.read()
    file_size = len(contents)

    lower_filename = filename.lower()

    extension_valid = any(lower_filename.endswith(ext) for ext in ALLOWED_EXTENSIONS)
    size_valid = file_size <= MAX_FILE_SIZE

    reasons = []
    suggestions = []
    extracted_text = ""

    if extension_valid:
        reasons.append("허용된 파일 형식입니다.")
    else:
        reasons.append("허용되지 않은 파일 형식입니다.")
        suggestions.append("PDF, JPG, PNG, DOCX, HWP 형식의 파일로 다시 업로드해주세요.")

    if size_valid:
        reasons.append("파일 크기가 제한 범위 이내입니다.")
    else:
        reasons.append("파일 크기가 너무 큽니다.")
        suggestions.append("파일 크기를 10MB 이하로 줄여주세요.")

    if lower_filename.endswith(".docx"):
        try:
            extracted_text = extract_text_from_docx(contents)

            if extracted_text:
                reasons.append("DOCX 파일에서 텍스트를 추출했습니다.")
            else:
                reasons.append("DOCX 파일에서 추출된 텍스트가 없습니다.")
                suggestions.append("문서 안에 확인 가능한 텍스트가 있는지 확인해주세요.")

        except Exception:
            reasons.append("DOCX 파일 텍스트 추출 중 오류가 발생했습니다.")
            suggestions.append("파일이 손상되었거나 지원되지 않는 DOCX 형식일 수 있습니다.")

    else:
        reasons.append("현재 텍스트 추출은 DOCX 파일만 지원합니다.")

    if "레포트" in filename or "템플릿" in filename:
        reasons.append("파일명이 예술활동 증빙자료보다는 일반 문서 또는 템플릿처럼 보입니다.")
        suggestions.append("작품명, 공연명, 전시명, 활동명 등이 드러나는 파일명으로 변경하는 것이 좋습니다.")

    is_valid = extension_valid and size_valid

    if is_valid:
        summary = "파일 형식과 크기는 기본 조건을 충족했습니다."
    else:
        summary = "파일 형식 또는 크기에서 보완이 필요합니다."

    return {
        "filename": filename,
        "content_type": content_type,
        "file_size": file_size,
        "status": "reviewed",
        "extracted_text_preview": extracted_text[:500],
        "review_result": {
            "is_valid": is_valid,
            "summary": summary,
            "reasons": reasons,
            "suggestions": suggestions
        }
    }