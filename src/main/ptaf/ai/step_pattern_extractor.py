"""Pulls pytest-bdd step patterns from Python step definition files."""

from __future__ import annotations

import re

_ANNOTATION_STRING = re.compile(
    r'@(?:given|when|then|step)\(\s*["\']([^"\'\\]*(?:\\.[^"\'\\]*)*)["\']\s*\)',
    re.IGNORECASE,
)
_ANNOTATION_RE = re.compile(
    r'@(?:given|when|then|step)\(\s*parsers\.re\(\s*r["\']([^"\'\\]*(?:\\.[^"\'\\]*)*)["\']\s*\)',
    re.IGNORECASE,
)


def from_python_source(source: str) -> list[str]:
    ordered: list[str] = []
    seen: set[str] = set()
    for pattern in (_ANNOTATION_STRING, _ANNOTATION_RE):
        for match in pattern.finditer(source):
            value = match.group(1)
            if value and value.strip() and value.strip() not in seen:
                seen.add(value.strip())
                ordered.append(value.strip())
    return ordered
