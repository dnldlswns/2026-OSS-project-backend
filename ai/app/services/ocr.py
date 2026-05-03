import easyocr

_reader: easyocr.Reader | None = None


def _get_reader() -> easyocr.Reader:
    global _reader
    if _reader is None:
        # 최초 호출 시 모델 다운로드 (수 초 소요)
        _reader = easyocr.Reader(["ko", "en"], gpu=False)
    return _reader


def ocr(image_bytes: bytes) -> str:
    reader = _get_reader()
    results = reader.readtext(image_bytes, detail=0, paragraph=True)
    return "\n".join(results)
