"""Pulls pytest-bdd step patterns from Python step definition files."""

from __future__ import annotations

import re

_KEYWORD_PARSE = re.compile(
    r"@keyword_step\(\s*parsers\.parse\(\s*(['\"])(.*?)\1\s*\)\s*\)",
    re.IGNORECASE | re.DOTALL,
)
_KEYWORD_STRING = re.compile(
    r"@keyword_step\(\s*(['\"])(.*?)\1\s*\)",
    re.IGNORECASE | re.DOTALL,
)
_LEGACY_ANNOTATION_STRING = re.compile(
    r'@(?:given|when|then|step)\(\s*["\']([^"\'\\]*(?:\\.[^"\'\\]*)*)["\']\s*\)',
    re.IGNORECASE,
)
_LEGACY_ANNOTATION_RE = re.compile(
    r'@(?:given|when|then|step)\(\s*parsers\.re\(\s*r["\']([^"\'\\]*(?:\\.[^"\'\\]*)*)["\']\s*\)',
    re.IGNORECASE,
)


def from_python_source(source: str) -> list[str]:
    ordered: list[str] = []
    seen: set[str] = set()

    def _add(value: str) -> None:
        cleaned = value.strip()
        if cleaned and cleaned not in seen:
            seen.add(cleaned)
            ordered.append(cleaned)

    for match in _KEYWORD_PARSE.finditer(source):
        _add(match.group(2))
    for match in _KEYWORD_STRING.finditer(source):
        _add(match.group(2))
    for pattern in (_LEGACY_ANNOTATION_STRING, _LEGACY_ANNOTATION_RE):
        for match in pattern.finditer(source):
            _add(match.group(1))

    return ordered
