from fastapi import APIRouter, HTTPException
from app.models.schema import DocumentSchema, SchemaCreateRequest
from app.storage import schema_registry

router = APIRouter(prefix="/schemas", tags=["schemas"])


@router.post("", response_model=DocumentSchema, status_code=201)
def create_schema(request: SchemaCreateRequest):
    return schema_registry.create_schema(request)


@router.get("", response_model=list[DocumentSchema])
def list_schemas():
    return schema_registry.list_schemas()


@router.get("/{schema_id}", response_model=DocumentSchema)
def get_schema(schema_id: str):
    schema = schema_registry.get_schema(schema_id)
    if not schema:
        raise HTTPException(status_code=404, detail="Schema not found")
    return schema


@router.delete("/{schema_id}", status_code=204)
def delete_schema(schema_id: str):
    if not schema_registry.delete_schema(schema_id):
        raise HTTPException(status_code=404, detail="Schema not found")
