import cv2
import numpy as np


def preprocess(image_bytes: bytes) -> bytes:
    """회전 보정 + 노이즈 제거 후 PNG bytes 반환"""
    nparr = np.frombuffer(image_bytes, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    denoised = cv2.GaussianBlur(gray, (3, 3), 0)
    corrected = _deskew(denoised)

    _, buf = cv2.imencode(".png", corrected)
    return buf.tobytes()


def _deskew(gray: np.ndarray) -> np.ndarray:
    edges = cv2.Canny(gray, 50, 150, apertureSize=3)
    lines = cv2.HoughLines(edges, 1, np.pi / 180, 200)

    if lines is None:
        return gray

    angles = []
    for rho, theta in lines[:, 0]:
        angle = (theta - np.pi / 2) * 180 / np.pi
        if -45 < angle < 45:
            angles.append(angle)

    if not angles:
        return gray

    median_angle = float(np.median(angles))
    if abs(median_angle) < 0.5:
        return gray

    h, w = gray.shape
    M = cv2.getRotationMatrix2D((w // 2, h // 2), median_angle, 1.0)
    return cv2.warpAffine(gray, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)
