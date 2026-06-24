"""Parse text responses from @playwright/mcp tools."""

from __future__ import annotations

import re

_PAGE_URL = re.compile(r"- Page URL:\s*(.+)", re.MULTILINE)
_PAGE_TITLE = re.compile(r"- Page Title:\s*(.+)", re.MULTILINE)
_SNAPSHOT_BLOCK = re.compile(r"### Snapshot\s*```yaml\s*(.*?)```", re.DOTALL)


def extract_page_url(text: str) -> str:
    match = _PAGE_URL.search(text)
    return match.group(1).strip() if match else ""


def extract_page_title(text: str) -> str:
    match = _PAGE_TITLE.search(text)
    return match.group(1).strip() if match else ""


def extract_snapshot_yaml(text: str) -> str:
    match = _SNAPSHOT_BLOCK.search(text)
    if match:
        return match.group(1).strip()
    return text.strip()


def tool_result_text(content: list[object]) -> str:
    parts: list[str] = []
    for block in content:
        text = getattr(block, "text", None)
        if isinstance(text, str) and text.strip():
            parts.append(text.strip())
    return "\n\n".join(parts)
