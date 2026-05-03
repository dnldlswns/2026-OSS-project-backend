from io import BytesIO
from docx import Document
from pypdf import PdfReader


def extract_from_docx(file_bytes: bytes) -> str:
    document = Document(BytesIO(file_bytes))
    paragraphs = [p.text.strip() for p in document.paragraphs if p.text.strip()]
    return "\n".join(paragraphs)


def extract_from_pdf(file_bytes: bytes) -> str:
    reader = PdfReader(BytesIO(file_bytes))
    pages = [page.extract_text().strip() for page in reader.pages if page.extract_text()]
    return "\n".join(pages)
