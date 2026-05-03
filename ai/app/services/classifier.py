from app.models.schema import ClassificationResult, DocumentSchema
from app.services import ollama_client

_SYSTEM = """당신은 문서 분류 전문가입니다.
OCR로 추출된 문서 텍스트를 분석하여 주어진 문서 유형 목록 중 가장 적합한 유형을 선택하세요.

반드시 아래 JSON 형식으로만 응답하세요:
{
  "schema_id": "<선택한 스키마 ID 또는 null>",
  "confidence": <0.0~1.0 사이 숫자>,
  "reason": "<판단 근거 한 줄>"
}

문서 유형을 확신할 수 없으면 schema_id를 null로, confidence를 0.3 미만으로 반환하세요."""


def _build_schema_list(schemas: list[DocumentSchema]) -> str:
    lines = []
    for s in schemas:
        keywords = ", ".join(s.keywords) if s.keywords else "없음"
        lines.append(f"- id: {s.id}\n  이름: {s.name}\n  설명: {s.description}\n  키워드: {keywords}")
    return "\n".join(lines)


async def classify(text: str, schemas: list[DocumentSchema]) -> ClassificationResult:
    schema_list = _build_schema_list(schemas)
    prompt = f"문서 유형 목록:\n{schema_list}\n\n문서 텍스트 (OCR 결과):\n{text[:2000]}"

    data = await ollama_client.chat(prompt, _SYSTEM)

    return ClassificationResult(
        schema_id=data.get("schema_id"),
        confidence=float(data.get("confidence", 0.0)),
        reason=data.get("reason", ""),
    )
