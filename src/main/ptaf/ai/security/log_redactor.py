from __future__ import annotations

import re


_GOOGLE_API_KEY = re.compile(r"AIza[0-9A-Za-z_-]{20,}")
_BEARER = re.compile(r"(?i)bearer\s+[0-9A-Za-z._-]+")
_GENERIC_LONG_SECRET = re.compile(r"(?i)(api[_-]?key|token|password|secret)\s*[:=]\s*\S{12,}")


def redact(text: str | None) -> str | None:
    if not text:
        return text
    result = _GOOGLE_API_KEY.sub("[REDACTED_API_KEY]", text)
    result = _BEARER.sub("Bearer [REDACTED]", result)
    result = _GENERIC_LONG_SECRET.sub(r"\1: [REDACTED]", result)
    return result
