#!/bin/bash
# 사용법: bash test/test.sh
# AI 서버(uvicorn main:app --reload)가 실행 중이어야 합니다.

BASE="http://localhost:8000"
SCHEMA_DIR="$(dirname "$0")/schemas"
IMAGE_DIR="$(dirname "$0")/images"

echo "=============================="
echo " 1. 스키마 등록"
echo "=============================="

SCHEMA1_ID=$(curl -s -X POST "$BASE/schemas" \
  -H "Content-Type: application/json" \
  -d @"$SCHEMA_DIR/performance_contract.json" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo "공연 출연 계약서 ID: $SCHEMA1_ID"

SCHEMA2_ID=$(curl -s -X POST "$BASE/schemas" \
  -H "Content-Type: application/json" \
  -d @"$SCHEMA_DIR/exhibition_certificate.json" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo "전시 참가 확인서 ID: $SCHEMA2_ID"

SCHEMA3_ID=$(curl -s -X POST "$BASE/schemas" \
  -H "Content-Type: application/json" \
  -d @"$SCHEMA_DIR/grant_application.json" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo "예술 지원금 신청서 ID: $SCHEMA3_ID"

echo ""
echo "=============================="
echo " 2. 등록된 스키마 목록 확인"
echo "=============================="
curl -s "$BASE/schemas" | python3 -m json.tool

echo ""
echo "=============================="
echo " 3. 문서 추출 테스트"
echo "=============================="

for IMAGE in "$IMAGE_DIR"/*.png; do
  NAME=$(basename "$IMAGE")
  echo ""
  echo "--- $NAME ---"
  curl -s -X POST "$BASE/extract" \
    -F "file=@$IMAGE" | python3 -m json.tool
done
