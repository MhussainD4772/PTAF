"""Tests for Playwright MCP browser context (Phase 1)."""

from __future__ import annotations

import shutil
from concurrent.futures import ThreadPoolExecutor

import pytest

from ptaf.ai.browser.mcp_browser_context_provider import McpBrowserContextProvider
from ptaf.ai.browser.mcp_response_parser import (
    extract_page_title,
    extract_page_url,
    extract_snapshot_yaml,
)
from ptaf.ai.browser.playwright_context_provider import PlaywrightBrowserContextProvider


SAMPLE_SNAPSHOT_TEXT = """### Page
- Page URL: https://example.com/
- Page Title: Example Domain
### Snapshot
```yaml
- heading "Example Domain" [level=1] [ref=e3]:
  - /url: https://iana.org/domains/example
```"""


def test_mcp_response_parser_extracts_fields() -> None:
    assert extract_page_url(SAMPLE_SNAPSHOT_TEXT) == "https://example.com/"
    assert extract_page_title(SAMPLE_SNAPSHOT_TEXT) == "Example Domain"
    snapshot = extract_snapshot_yaml(SAMPLE_SNAPSHOT_TEXT)
    assert "heading" in snapshot
    assert "[ref=e3]" in snapshot


@pytest.mark.skipif(shutil.which("npx") is None, reason="npx not installed")
def test_mcp_provider_collects_via_playwright_mcp_server() -> None:
    def _collect():
        provider = McpBrowserContextProvider()
        return provider.collect("https://example.com", max_snapshot_chars=4000)

    with ThreadPoolExecutor(max_workers=1) as pool:
        ctx = pool.submit(_collect).result(timeout=120)

    assert ctx.provider == "playwright-mcp"
    assert "example.com" in ctx.url
    assert ctx.title
    assert "Example Domain" in ctx.title or "heading" in ctx.aria_snapshot


def test_playwright_direct_provider_still_available() -> None:
    def _collect():
        provider = PlaywrightBrowserContextProvider()
        return provider.collect("https://example.com", max_snapshot_chars=4000)

    with ThreadPoolExecutor(max_workers=1) as pool:
        ctx = pool.submit(_collect).result(timeout=60)

    assert ctx.provider == "playwright-direct"
