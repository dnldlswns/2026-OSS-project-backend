from app.models.schema import DocumentSchema
from app.services import ollama_client

_SYSTEM = """당신은 문서 정보 추출 전문가입니다.
OCR로 추출된 문서 텍스트에서 지정된 필드 값을 정확히 추출하세요.
문서에 없는 값은 절대 추측하지 말고 null을 사용하세요.

반드시 아래 JSON 형식으로만 응답하세요:
{
  "fields": {
    "<필드명>": "<추출한 값 또는 null>"
  }
}"""

_FEW_SHOT = """예시:
문서: "공연명: 봄의 왈츠 / 공연일: 2024년 3월 15일 / 출연료: 500,000원"
필드: 공연명, 공연일, 출연료
응답: {"fields": {"공연명": "봄의 왈츠", "공연일": "2024년 3월 15일", "출연료": "500,000원"}}
---
"""


def _build_fields_desc(schema: DocumentSchema) -> str:
    return "\n".join(
        f"- {f.name} ({f.type}, {'필수' if f.required else '선택'}): {f.description}"
        for f in schema.fields
    )


async def extract_fields(text: str, schema: DocumentSchema) -> dict:
    fields_desc = _build_fields_desc(schema)
    prompt = f"{_FEW_SHOT}추출할 필드:\n{fields_desc}\n\n문서 텍스트:\n{text[:3000]}"

    data = await ollama_client.chat(prompt, _SYSTEM)
    return data.get("fields", {})
