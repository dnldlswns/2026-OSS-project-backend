from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
import os
import shutil
from uuid import uuid4

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

UPLOAD_DIR = "uploads"
os.makedirs(UPLOAD_DIR, exist_ok=True)


class AiResultItem(BaseModel):
    fieldName: str
    value: str
    confidence: float
    status: str
    reason: str


class AiAnalyzeResponse(BaseModel):
    documentType: str
    overallStatus: str
    results: List[AiResultItem]
    message: str


@app.get("/")
def root():
    return {
        "message": "ArtPass AI Server is running"
    }


@app.post("/ai/analyze", response_model=AiAnalyzeResponse)
async def analyze_image(file: UploadFile = File(...)):
    if not file.content_type.startswith("image/"):
        raise HTTPException(
            status_code=400,
            detail="이미지 파일만 업로드할 수 있습니다."
        )

    file_extension = os.path.splitext(file.filename)[1]
    saved_filename = f"{uuid4()}{file_extension}"
    saved_path = os.path.join(UPLOAD_DIR, saved_filename)

    with open(saved_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    result = AiAnalyzeResponse(
        documentType="ART_ACTIVITY_PROOF",
        overallStatus="NEED_REVIEW",
        results=[
            AiResultItem(
                fieldName="artistName",
                value="홍길동",
                confidence=0.92,
                status="PASS",
                reason="이미지에서 이름 항목이 확인되었습니다."
            ),
            AiResultItem(
                fieldName="activityTitle",
                value="개인전 전시 포스터",
                confidence=0.85,
                status="PASS",
                reason="활동명으로 판단되는 문구가 확인되었습니다."
            ),
            AiResultItem(
                fieldName="activityDate",
                value="2025-03-12",
                confidence=0.63,
                status="NEED_REVIEW",
                reason="날짜로 보이는 정보가 있으나 정확한 검토가 필요합니다."
            ),
            AiResultItem(
                fieldName="proofType",
                value="전시 자료",
                confidence=0.78,
                status="PASS",
                reason="포스터 또는 전시 관련 자료로 판단됩니다."
            )
        ],
        message="AI 분석이 완료되었습니다. 일부 항목은 관리자 검토가 필요합니다."
    )

    return result