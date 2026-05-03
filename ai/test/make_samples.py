"""
테스트용 샘플 문서 이미지 생성 스크립트.
실행: python test/make_samples.py
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

OUT_DIR = Path(__file__).parent / "images"
OUT_DIR.mkdir(exist_ok=True)

W, H = 900, 1200
BG = (255, 255, 255)
FG = (20, 20, 20)
LINE = (180, 180, 180)
TITLE_BG = (240, 240, 240)


def _try_font(size: int) -> ImageFont.ImageFont:
    """시스템 한글 폰트를 순서대로 시도하고 없으면 기본 폰트 사용."""
    candidates = [
        "/System/Library/Fonts/AppleSDGothicNeo.ttc",
        "/System/Library/Fonts/Supplemental/AppleGothic.ttf",
        "/Library/Fonts/NanumGothic.ttf",
        "/usr/share/fonts/truetype/nanum/NanumGothic.ttf",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size)
        except (OSError, IOError):
            continue
    return ImageFont.load_default()


def _draw_doc(title: str, rows: list[tuple[str, str]], filename: str) -> None:
    img = Image.new("RGB", (W, H), BG)
    d = ImageDraw.Draw(img)

    font_title = _try_font(32)
    font_label = _try_font(22)
    font_value = _try_font(22)
    font_small = _try_font(16)

    # 상단 타이틀 박스
    d.rectangle([(0, 0), (W, 80)], fill=TITLE_BG)
    d.text((W // 2, 40), title, font=font_title, fill=FG, anchor="mm")
    d.line([(0, 80), (W, 80)], fill=LINE, width=2)

    # 본문 행
    y = 120
    row_h = 60
    col_label = 60
    col_value = 300

    for label, value in rows:
        d.rectangle([(col_label - 10, y), (col_value - 20, y + row_h - 8)], fill=(248, 248, 248))
        d.text((col_label, y + 10), label, font=font_label, fill=(80, 80, 80))
        d.text((col_value, y + 10), value, font=font_value, fill=FG)
        d.line([(col_label - 10, y + row_h - 8), (W - 60, y + row_h - 8)], fill=LINE, width=1)
        y += row_h

    # 하단 서명란
    y += 40
    d.line([(60, y), (W - 60, y)], fill=LINE, width=1)
    d.text((60, y + 10), "위 내용이 사실임을 확인합니다.", font=font_small, fill=(120, 120, 120))
    d.text((W - 200, y + 40), "서명: ___________", font=font_label, fill=FG)

    out_path = OUT_DIR / filename
    img.save(out_path)
    print(f"  생성됨: {out_path}")


def make_performance_contract():
    _draw_doc(
        title="공연 출연 계약서",
        rows=[
            ("공연명",   "봄의 왈츠 정기 연주회"),
            ("공연일",   "2025년 6월 14일 (토) 19:00"),
            ("공연장소", "세종문화회관 소극장"),
            ("출연료",   "1,200,000원"),
            ("계약자명", "홍길동"),
            ("계약일",   "2025년 5월 1일"),
        ],
        filename="performance_contract.png",
    )


def make_exhibition_certificate():
    _draw_doc(
        title="전시 참가 확인서",
        rows=[
            ("전시명",   "2025 신진작가 기획전 《경계》"),
            ("전시기간", "2025년 7월 1일 ~ 2025년 7월 20일"),
            ("전시장소", "인사동 아트스페이스 갤러리"),
            ("작가명",   "김예진"),
            ("작품수",   "5점"),
            ("발급일",   "2025년 7월 21일"),
        ],
        filename="exhibition_certificate.png",
    )


def make_grant_application():
    _draw_doc(
        title="예술 지원금 신청서",
        rows=[
            ("신청자명",   "이창우"),
            ("사업명",     "전통음악 현대화 프로젝트 '소리길'"),
            ("신청금액",   "3,500,000원"),
            ("활동기간",   "2025년 9월 1일 ~ 2025년 11월 30일"),
            ("활동분야",   "음악 (국악/퓨전)"),
            ("신청일",     "2025년 8월 10일"),
        ],
        filename="grant_application.png",
    )


if __name__ == "__main__":
    print("샘플 이미지 생성 중...")
    make_performance_contract()
    make_exhibition_certificate()
    make_grant_application()
    print("완료.")
