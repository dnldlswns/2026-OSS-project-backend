import uuid
from app.models.schema import DocumentSchema, SchemaCreateRequest

_registry: dict[str, DocumentSchema] = {}


def create_schema(request: SchemaCreateRequest) -> DocumentSchema:
    schema = DocumentSchema(
        id=str(uuid.uuid4()),
        name=request.name,
        description=request.description,
        fields=request.fields,
    )
    _registry[schema.id] = schema
    return schema


def get_schema(schema_id: str) -> DocumentSchema | None:
    return _registry.get(schema_id)


def list_schemas() -> list[DocumentSchema]:
    return list(_registry.values())


def delete_schema(schema_id: str) -> bool:
    if schema_id not in _registry:
        return False
    del _registry[schema_id]
    return True
