from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
import os
import shutil
from uuid import uuid4
import re

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

ocr_reader = None


class AiResultItem(BaseModel):
    fieldName: str
    value: str
    confidence: float
    status: str
    reason: str


class AiAnalyzeResponse(BaseModel):
    documentType: str
    overallStatus: str
    extractedTextPreview: str
    results: List[AiResultItem]
    message: str


def get_ocr_reader():
    global ocr_reader

    if ocr_reader is None:
        import easyocr
        ocr_reader = easyocr.Reader(["ko", "en"], gpu=False)

    return ocr_reader


def extract_text_from_image(image_path: str):
    reader = get_ocr_reader()
    ocr_results = reader.readtext(image_path)

    texts = []
    confidence_scores = []

    for result in ocr_results:
        text = result[1]
        confidence = result[2]

        texts.append(text)
        confidence_scores.append(confidence)

    extracted_text = "\n".join(texts)

    if confidence_scores:
        avg_confidence = sum(confidence_scores) / len(confidence_scores)
    else:
        avg_confidence = 0.0

    return extracted_text, avg_confidence


def find_date(text: str):
    patterns = [
        r"\d{4}[.-]\d{1,2}[.-]\d{1,2}",
        r"\d{4}년\s*\d{1,2}월\s*\d{1,2}일",
        r"\d{4}[.-]\d{1,2}",
        r"\d{4}년\s*\d{1,2}월",
    ]

    for pattern in patterns:
        match = re.search(pattern, text)
        if match:
            return match.group()

    return ""


def guess_activity_title(text: str):
    lines = [line.strip() for line in text.split("\n") if line.strip()]

    if not lines:
        return ""

    keywords = ["전시", "공연", "발표", "콘서트", "개인전", "단체전", "작품", "프로젝트", "페스티벌"]

    for line in lines:
        for keyword in keywords:
            if keyword in line:
                return line

    return lines[0]


def guess_proof_type(text: str):
    if "전시" in text or "개인전" in text or "단체전" in text:
        return "전시 자료"

    if "공연" in text or "콘서트" in text:
        return "공연 자료"

    if "계약" in text:
        return "계약 자료"

    if "상장" in text or "수상" in text:
        return "수상 자료"

    return "기타 증빙 자료"


def make_status(value: str, confidence: float):
    if not value:
        return "NEED_REVIEW"

    if confidence >= 0.75:
        return "PASS"

    return "NEED_REVIEW"


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

    extracted_text, avg_confidence = extract_text_from_image(saved_path)

    activity_title = guess_activity_title(extracted_text)
    activity_date = find_date(extracted_text)
    proof_type = guess_proof_type(extracted_text)

    activity_title_status = make_status(activity_title, avg_confidence)
    activity_date_status = make_status(activity_date, avg_confidence)
    proof_type_status = make_status(proof_type, avg_confidence)

    results = [
        AiResultItem(
            fieldName="activityTitle",
            value=activity_title,
            confidence=round(avg_confidence, 2),
            status=activity_title_status,
            reason="OCR로 추출된 텍스트에서 활동명으로 보이는 문구를 찾았습니다."
            if activity_title else "활동명으로 판단할 수 있는 문구를 찾지 못했습니다."
        ),
        AiResultItem(
            fieldName="activityDate",
            value=activity_date,
            confidence=round(avg_confidence, 2) if activity_date else 0.0,
            status=activity_date_status,
            reason="OCR로 추출된 텍스트에서 날짜 형식을 찾았습니다."
            if activity_date else "날짜 형식의 문구를 찾지 못했습니다."
        ),
        AiResultItem(
            fieldName="proofType",
            value=proof_type,
            confidence=round(avg_confidence, 2),
            status=proof_type_status,
            reason="OCR 텍스트의 키워드를 기반으로 증빙자료 유형을 추정했습니다."
        )
    ]

    overall_status = "PASS"

    for item in results:
        if item.status == "NEED_REVIEW":
            overall_status = "NEED_REVIEW"
            break

    return AiAnalyzeResponse(
        documentType="ART_ACTIVITY_PROOF",
        overallStatus=overall_status,
        extractedTextPreview=extracted_text[:500],
        results=results,
        message="OCR 기반 AI 분석이 완료되었습니다. 신뢰도가 낮거나 누락된 항목은 관리자 검토가 필요합니다."
    )