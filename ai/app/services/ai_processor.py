import json
import os
import anthropic
from app.models.schema import DocumentSchema

_client = anthropic.Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])

_SYSTEM_PROMPT = """당신은 문서 분석 AI입니다.
사용자가 제공하는 문서 텍스트를 읽고, 주어진 스키마 목록 중 가장 적합한 스키마를 선택한 뒤 해당 스키마의 필드를 채워서 JSON으로 반환합니다.

반환 형식 (반드시 이 JSON 형식만 출력하세요):
{
  "schema_id": "<선택한 스키마의 id>",
  "fields": {
    "<필드명>": "<추출한 값 또는 null>"
  }
}"""


def _build_schema_description(schemas: list[DocumentSchema]) -> str:
    lines = ["사용 가능한 스키마 목록:\n"]
    for s in schemas:
        lines.append(f"[id: {s.id}] {s.name} — {s.description}")
        for f in s.fields:
            req = "필수" if f.required else "선택"
            lines.append(f"  - {f.name} ({f.type}, {req}): {f.description}")
        lines.append("")
    return "\n".join(lines)


def select_schema_and_fill(text: str, schemas: list[DocumentSchema]) -> dict:
    schema_desc = _build_schema_description(schemas)
    user_message = f"{schema_desc}\n\n문서 텍스트:\n{text}"

    message = _client.messages.create(
        model="claude-opus-4-7",
        max_tokens=2048,
        system=_SYSTEM_PROMPT,
        messages=[{"role": "user", "content": user_message}],
    )

    raw = message.content[0].text.strip()

    # JSON 블록이 있으면 코드 펜스 제거
    if raw.startswith("```"):
        raw = raw.split("```")[1]
        if raw.startswith("json"):
            raw = raw[4:]

    return json.loads(raw)
