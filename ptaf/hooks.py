"""Runtime hook context replacing Java Hooks ThreadLocal accessors."""

from __future__ import annotations

from contextvars import ContextVar
from typing import Any

from playwright.sync_api import Browser, Page

from ptaf.ui.page_common import PageCommonMethods

_playwright: ContextVar[Any | None] = ContextVar("_playwright", default=None)
_browser: ContextVar[Browser | None] = ContextVar("_browser", default=None)
_context: ContextVar[Any | None] = ContextVar("_context", default=None)
_page: ContextVar[Page | None] = ContextVar("_page", default=None)
_scenario: ContextVar[Any | None] = ContextVar("_scenario", default=None)
_page_common: ContextVar[PageCommonMethods | None] = ContextVar(
    "_page_common", default=None
)


def set_playwright(playwright: Any | None) -> None:
    _playwright.set(playwright)


def get_playwright() -> Any | None:
    return _playwright.get()


def set_browser(browser: Browser | None) -> None:
    _browser.set(browser)


def get_browser() -> Browser:
    browser = _browser.get()
    if browser is None:
        raise RuntimeError("The browser is not initialized.")
    return browser


def set_context(context: Any | None) -> None:
    _context.set(context)


def get_context() -> Any | None:
    return _context.get()


def set_page(page: Page | None) -> None:
    _page.set(page)


def get_page() -> Page:
    page = _page.get()
    if page is None or page.is_closed():
        raise RuntimeError("The page is closed or not initialized.")
    return page


def set_current_scenario(scenario: Any | None) -> None:
    _scenario.set(scenario)
    PageCommonMethods.set_current_scenario(scenario)


def get_current_scenario() -> Any | None:
    return _scenario.get()


def set_page_common(page_common: PageCommonMethods | None) -> None:
    _page_common.set(page_common)


def get_page_common() -> PageCommonMethods | None:
    return _page_common.get()


def clear_runtime_context() -> None:
    _playwright.set(None)
    _browser.set(None)
    _context.set(None)
    _page.set(None)
    _scenario.set(None)
    _page_common.set(None)
