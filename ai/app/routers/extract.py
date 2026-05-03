from fastapi import APIRouter, File, HTTPException, UploadFile
from app.core.config import CONFIDENCE_THRESHOLD
from app.models.schema import ExtractResponse
from app.services import classifier, field_extractor, ocr, preprocessor, text_extractor
from app.storage import schema_registry

router = APIRouter(prefix="/extract", tags=["extract"])

_IMAGE_EXTENSIONS = (".jpg", ".jpeg", ".png", ".gif", ".webp")
MAX_FILE_SIZE = 200 * 1024 * 1024


@router.post("", response_model=ExtractResponse)
async def extract(file: UploadFile = File(...)):
    schemas = schema_registry.list_schemas()
    if not schemas:
        raise HTTPException(status_code=422, detail="등록된 스키마가 없습니다. 먼저 스키마를 등록해주세요.")

    contents = await file.read()
    if len(contents) > MAX_FILE_SIZE:
        raise HTTPException(status_code=413, detail="파일 크기가 200MB를 초과합니다.")

    filename = (file.filename or "").lower()

    # --- 텍스트 추출 ---
    if any(filename.endswith(ext) for ext in _IMAGE_EXTENSIONS):
        preprocessed = preprocessor.preprocess(contents)
        raw_text = ocr.ocr(preprocessed)

    elif filename.endswith(".pdf"):
        raw_text = text_extractor.extract_from_pdf(contents)
        if not raw_text:
            raise HTTPException(
                status_code=422,
                detail="PDF에서 텍스트를 추출할 수 없습니다. 스캔 PDF라면 이미지(JPG/PNG)로 변환 후 업로드해주세요.",
            )

    elif filename.endswith(".docx"):
        raw_text = text_extractor.extract_from_docx(contents)
        if not raw_text:
            raise HTTPException(status_code=422, detail="DOCX에서 텍스트를 추출할 수 없습니다.")

    else:
        raise HTTPException(
            status_code=415,
            detail="지원하지 않는 파일 형식입니다. JPG, PNG, PDF, DOCX를 사용해주세요.",
        )

    # --- 1단계: 문서 유형 분류 ---
    classification = await classifier.classify(raw_text, schemas)

    if classification.schema_id is None or classification.confidence < CONFIDENCE_THRESHOLD:
        return ExtractResponse(
            is_identifiable=False,
            matched_schema_id=None,
            matched_schema_name=None,
            confidence=classification.confidence,
            reason=classification.reason,
            fields={},
            raw_text=raw_text,
        )

    matched = schema_registry.get_schema(classification.schema_id)
    if not matched:
        return ExtractResponse(
            is_identifiable=False,
            matched_schema_id=None,
            matched_schema_name=None,
            confidence=classification.confidence,
            reason="LLM이 반환한 스키마 ID가 존재하지 않습니다.",
            fields={},
            raw_text=raw_text,
        )

    # --- 2단계: 필드 추출 ---
    fields = await field_extractor.extract_fields(raw_text, matched)

    return ExtractResponse(
        is_identifiable=True,
        matched_schema_id=matched.id,
        matched_schema_name=matched.name,
        confidence=classification.confidence,
        reason=classification.reason,
        fields=fields,
        raw_text=raw_text,
    )
