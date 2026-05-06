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

@app.post("/ai/review")
async def review_file(file: UploadFile = File(...)):
    filename = file.filename or ""
    content = await file.read()

    extracted_text = ""

    if filename.lower().endswith(".docx"):
        document = Document(BytesIO(content))
        extracted_text = "\n".join([p.text for p in document.paragraphs])
    elif filename.lower().endswith(".txt"):
        extracted_text = content.decode("utf-8", errors="ignore")
    else:
        extracted_text = "현재 버전에서는 txt, docx 파일 중심으로 텍스트 추출을 테스트합니다."

    has_text = bool(extracted_text.strip())

    return {
        "status": "success",
        "message": "예술활동 증빙자료 검토가 완료되었습니다.",
        "data": {
            "filename": filename,
            "isValid": has_text,
            "activityType": "전시",
            "artistName": "홍길동",
            "activityDate": "2025-03-19",
            "evidenceType": "증빙자료",
            "score": 80 if has_text else 30,
            "confidence": 0.8 if has_text else 0.3,
            "extractedText": extracted_text[:1000],
            "reason": "업로드된 파일에서 텍스트를 추출했고, 백엔드 연동용 JSON 응답 형식으로 반환했습니다."
        }
    }