from typing import Any, Literal
from pydantic import BaseModel


class SchemaField(BaseModel):
    name: str
    description: str
    type: Literal["string", "number", "date", "boolean"] = "string"
    required: bool = True


class DocumentSchema(BaseModel):
    id: str
    name: str
    description: str
    keywords: list[str] = []
    fields: list[SchemaField]


class SchemaCreateRequest(BaseModel):
    name: str
    description: str
    keywords: list[str] = []
    fields: list[SchemaField]


class ClassificationResult(BaseModel):
    schema_id: str | None
    confidence: float
    reason: str


class ExtractResponse(BaseModel):
    is_identifiable: bool
    matched_schema_id: str | None
    matched_schema_name: str | None
    confidence: float
    reason: str
    fields: dict[str, Any]
    raw_text: str
