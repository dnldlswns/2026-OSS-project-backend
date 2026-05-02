from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware

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
        "review_result": {
            "is_valid": is_valid,
            "summary": summary,
            "reasons": reasons,
            "suggestions": suggestions
        }
    }