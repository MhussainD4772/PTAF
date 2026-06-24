"""Collect browser context via Playwright sync API (MCP-equivalent aria snapshots)."""

from __future__ import annotations

import logging

from playwright.sync_api import sync_playwright

from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.utils import browser_factory, config

logger = logging.getLogger(__name__)


def _trim_snapshot(text: str, max_chars: int) -> str:
    cleaned = text.strip()
    if len(cleaned) <= max_chars:
        return cleaned
    return (
        cleaned[:max_chars]
        + f"\n...[truncated: {len(cleaned) - max_chars} chars omitted]"
    )


class PlaywrightBrowserContextProvider:
    """Direct Python Playwright fallback (use browser_context.provider: playwright)."""

    def collect(
        self,
        url: str,
        *,
        start_url_key: str | None = None,
        max_snapshot_chars: int = 12_000,
    ) -> BrowserPageContext:
        browser_name = config.get_browser() or "chrome"
        browser_type = browser_factory.parse_browser_type(browser_name)

        with sync_playwright() as playwright:
            browser = browser_factory.create_browser(playwright, browser_type)
            context = browser_factory.create_context_with_video(browser)
            page = context.new_page()
            try:
                logger.info("Collecting browser context for %s", url)
                page.goto(url, wait_until="domcontentloaded", timeout=60_000)
                try:
                    page.wait_for_load_state("networkidle", timeout=15_000)
                except Exception:
                    logger.warning(
                        "networkidle not reached within timeout; using current DOM snapshot"
                    )
                title = page.title()
                final_url = page.url
                snapshot = _trim_snapshot(
                    page.aria_snapshot() or "",
                    max_snapshot_chars,
                )
            finally:
                context.close()
                browser.close()

        return BrowserPageContext(
            url=final_url,
            title=title,
            aria_snapshot=snapshot,
            start_url_key=start_url_key,
            provider="playwright-direct",
        )
