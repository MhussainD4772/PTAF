"""Abstraction for browser context sources (Playwright now, MCP later)."""

from __future__ import annotations

from typing import Protocol

from ptaf.ai.browser.browser_page_context import BrowserPageContext


class BrowserContextProvider(Protocol):
    """Collect page context from a live browser session."""

    def collect(
        self,
        url: str,
        *,
        start_url_key: str | None = None,
        max_snapshot_chars: int = 12_000,
    ) -> BrowserPageContext:
        """Navigate to url and return URL, title, and accessibility snapshot."""
