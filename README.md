### Overview
26-1 공개SW프로젝트 백엔드 레포  
- 컴퓨터공학전공 2022111937 박수현
- 컴퓨터공학전공 2022112020 위인준

frontend repo : https://github.com/z1-won/2026-oss-project
## AI 서버

예술활동증명 첨부파일을 검토하기 위한 FastAPI 기반 AI 서버입니다.

현재 구현된 기능은 사용자가 업로드한 파일의 파일명, 파일 형식, 파일 크기를 확인하고 1차 검토 결과를 JSON 형태로 반환하는 기능입니다.

### 실행 방법

```bash
cd ai
pip install -r requirements.txt
uvicorn main:app --reload
```
서버 실행 후 아래 주소에서 API 문서를 확인할 수 있습니다.

```text
http://127.0.0.1:8000/docs
```

### API 명세

#### 파일 검토 API

```text
POST /review
```

요청 방식:

```text
multipart/form-data
```

요청 필드:

| 필드명 | 타입 | 설명 |
|---|---|---|
| file | File | 사용자가 업로드한 예술활동증명 첨부파일 |

응답 예시:

```json
{
  "filename": "sample.pdf",
  "content_type": "application/pdf",
  "file_size": 12345,
  "status": "reviewed",
  "review_result": {
    "is_valid": true,
    "summary": "파일 형식과 크기는 기본 조건을 충족했습니다.",
    "reasons": [
      "허용된 파일 형식입니다.",
      "파일 크기가 제한 범위 이내입니다."
    ],
    "suggestions": []
  }
}
```

### 현재 검토 기준

- 허용 파일 형식: PDF, JPG, JPEG, PNG, DOCX, HWP
- 최대 파일 크기: 10MB
- 파일명에 일반 문서 또는 템플릿으로 보이는 단어가 포함된 경우 보완 제안 반환

### 향후 개발 예정

- PDF 내용 추출 기능
- DOCX 내용 추출 기능
- 이미지 OCR 기능
- 예술활동증명 기준 기반 AI 검토 기능
- 반려 사유 및 보완 방향 자동 설명 기능