import asyncio
import json
import httpx
from app.core.config import OLLAMA_BASE_URL, OLLAMA_MODEL

# Ollama는 단일 스레드 추론 → 동시 요청을 직렬화
_semaphore = asyncio.Semaphore(1)


async def chat(prompt: str, system: str = "") -> dict:
    messages = []
    if system:
        messages.append({"role": "system", "content": system})
    messages.append({"role": "user", "content": prompt})

    async with _semaphore:
        async with httpx.AsyncClient(timeout=120.0) as client:
            response = await client.post(
                f"{OLLAMA_BASE_URL}/api/chat",
                json={
                    "model": OLLAMA_MODEL,
                    "messages": messages,
                    "format": "json",
                    "stream": False,
                },
            )
            response.raise_for_status()
            content = response.json()["message"]["content"]
            return json.loads(content)
