from __future__ import annotations

from enum import Enum


class AiGenerationMode(Enum):
    PREVIEW = "PREVIEW"
    WRITE = "WRITE"
    STRICT = "STRICT"

    @classmethod
    def from_string(cls, value: str | None) -> AiGenerationMode:
        if value is None or not value.strip():
            return cls.PREVIEW
        normalized = value.strip().upper()
        mapping = {
            "PREVIEW": cls.PREVIEW,
            "WRITE": cls.WRITE,
            "STRICT": cls.STRICT,
        }
        if normalized not in mapping:
            raise ValueError(f"Unsupported mode: {value}. Use preview|write|strict")
        return mapping[normalized]
