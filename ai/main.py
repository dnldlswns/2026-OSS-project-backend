from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import extract, schemas

app = FastAPI(title="ArtPass AI Server")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(schemas.router)
app.include_router(extract.router)

@app.get("/")
def root():
    return {"message": "ArtPass AI Server is running"}
