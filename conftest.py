"""Pytest lifecycle fixtures replacing Java Cucumber Hooks."""

from __future__ import annotations

pytest_plugins = [
    "steps.page_common_steps",
    "steps.frame_common_steps",
    "steps.new_page_common_steps",
    "steps.api_steps",
    "steps.database_steps",
]

import base64
import logging
from collections.abc import Generator
from typing import Any

import pytest
from playwright.sync_api import Browser, BrowserContext, Page, Playwright, sync_playwright

from ptaf import hooks
from ptaf.api.api_client import ApiRequestHandler
from ptaf.db.db_handler import DatabaseHandler, is_database_configured
from ptaf.ui.page_common import PageCommonMethods
from ptaf.utils import browser_factory, config, scenario_util

logger = logging.getLogger(__name__)

_SCENARIO_BINDINGS = "scenario_bindings.py"

BrowserStack = tuple[Browser, BrowserContext, Page]


def _dispose_api_context() -> None:
    ApiRequestHandler.dispose_context()


def _close_db_connection() -> None:
    DatabaseHandler.close_connection()


def _has_last_scenario_marker(request: pytest.FixtureRequest) -> bool:
    if request.node.get_closest_marker("last_scenario_feature") is not None:
        return True
    if request.node.get_closest_marker("LastScenario") is not None:
        return True
    return False


def _resolve_browser_type() -> browser_factory.BrowserTypeEnum:
    browser_name = config.get_browser()
    if not browser_name:
        raise RuntimeError("Browser is not configured in resources/config/config.yml")
    return browser_factory.parse_browser_type(browser_name)


def _stash_failure_screenshot(item: pytest.Item, test_page: Page) -> None:
    """Queue a failure screenshot for pytest-html before the call report is finalized."""
    if test_page is None or test_page.is_closed():
        return

    screenshot = scenario_util.handle_scenario_teardown(item, test_page, "failed")
    if screenshot is None:
        return

    try:
        import pytest_html
        from pytest_html.fixtures import extras_stash_key
    except ImportError:
        logger.debug("pytest-html is not installed; skipping screenshot attachment")
        return

    extras = item.config.stash.setdefault(extras_stash_key, [])
    encoded = base64.b64encode(screenshot).decode("ascii")
    extras.append(pytest_html.extras.png(encoded, "Failure screenshot"))


def _close_page(test_page: Page | None) -> None:
    if test_page is None:
        return
    try:
        if not test_page.is_closed():
            test_page.close()
    except Exception as exc:
        logger.error("Error closing the page: %s", exc, exc_info=True)


def _close_context(browser_context: BrowserContext | None) -> None:
    if browser_context is None:
        return
    try:
        browser_context.close()
    except Exception as exc:
        logger.error("Error closing the browser context: %s", exc, exc_info=True)


def _close_browser(browser: Browser | None) -> None:
    if browser is None:
        return
    try:
        browser.close()
        logger.info("Browser closed.")
    except Exception as exc:
        logger.error("Error closing the browser: %s", exc, exc_info=True)


@pytest.fixture(autouse=True)
def _skip_db_tests_when_unconfigured(request: pytest.FixtureRequest) -> None:
    if request.node.get_closest_marker("db") is None:
        return
    if not is_database_configured():
        pytest.skip(
            "Database not configured: set DB_PASSWORD and ensure PostgreSQL/SQL "
            "Server is reachable per resources/config/config.yml"
        )


@pytest.fixture(autouse=True)
def _scenario_resource_cleanup() -> Generator[None, None, None]:
    """Dispose API/DB resources after every test (including API-only BDD scenarios)."""
    yield
    _dispose_api_context()
    _close_db_connection()


@pytest.fixture(scope="session")
def playwright_instance() -> Generator[Playwright, None, None]:
    with sync_playwright() as playwright:
        hooks.set_playwright(playwright)
        yield playwright
        hooks.set_playwright(None)


@pytest.fixture(scope="session", autouse=True)
def _shared_playwright_session(playwright_instance: Playwright) -> None:
    """Start one sync Playwright instance for the whole session (API + UI)."""


@pytest.fixture(scope="class")
def _last_scenario_browser_stack(
    request: pytest.FixtureRequest,
    playwright_instance: Playwright,
) -> Generator[BrowserStack | None, None, None]:
    if not _has_last_scenario_marker(request):
        yield None
        return

    browser_type = _resolve_browser_type()
    browser = browser_factory.create_browser(playwright_instance, browser_type)
    context = browser_factory.create_context_with_video(browser)
    page = context.new_page()

    hooks.set_playwright(playwright_instance)
    hooks.set_browser(browser)
    hooks.set_context(context)
    hooks.set_page(page)
    hooks.set_page_common(PageCommonMethods(page))

    logger.info(
        "Browser setup completed for @LastScenario feature: %s",
        getattr(request.cls, "__name__", request.module.__name__),
    )

    yield browser, context, page

    _dispose_api_context()
    _close_db_connection()
    _close_page(page)
    _close_context(context)
    _close_browser(browser)
    hooks.clear_runtime_context()


@pytest.fixture
def browser(
    request: pytest.FixtureRequest,
    playwright_instance: Playwright,
    _last_scenario_browser_stack: BrowserStack | None,
) -> Generator[Browser, None, None]:
    if _last_scenario_browser_stack is not None:
        shared_browser, _, _ = _last_scenario_browser_stack
        hooks.set_playwright(playwright_instance)
        hooks.set_browser(shared_browser)
        yield shared_browser
        return

    browser_type = _resolve_browser_type()
    created_browser = browser_factory.create_browser(
        playwright_instance, browser_type
    )
    hooks.set_playwright(playwright_instance)
    hooks.set_browser(created_browser)

    yield created_browser

    if not _has_last_scenario_marker(request):
        _close_browser(created_browser)
        hooks.set_browser(None)


@pytest.fixture
def context(
    request: pytest.FixtureRequest,
    browser: Browser,
    _last_scenario_browser_stack: BrowserStack | None,
) -> Generator[BrowserContext, None, None]:
    if _last_scenario_browser_stack is not None:
        _, shared_context, _ = _last_scenario_browser_stack
        hooks.set_context(shared_context)
        yield shared_context
        return

    created_context = browser_factory.create_context_with_video(browser)
    hooks.set_context(created_context)

    yield created_context

    if not _has_last_scenario_marker(request):
        _close_context(created_context)
        hooks.set_context(None)


@pytest.fixture
def page(
    request: pytest.FixtureRequest,
    context: BrowserContext,
    _last_scenario_browser_stack: BrowserStack | None,
) -> Generator[Page, None, None]:
    hooks.set_current_scenario(request.node)

    if _last_scenario_browser_stack is not None:
        _, _, shared_page = _last_scenario_browser_stack
        hooks.set_page(shared_page)
        hooks.set_page_common(PageCommonMethods(shared_page))
        logger.info("Reusing browser instance for feature with @LastScenario tag.")
        yield shared_page
    else:
        created_page = context.new_page()
        hooks.set_page(created_page)
        hooks.set_page_common(PageCommonMethods(created_page))
        logger.info("Browser setup completed for scenario: %s", request.node.name)
        yield created_page

    try:
        active_page = hooks.get_page()
    except RuntimeError:
        active_page = None

    _dispose_api_context()
    _close_db_connection()

    if _last_scenario_browser_stack is not None:
        logger.info("Skipping browser closure for feature with @LastScenario tag.")
    else:
        _close_page(active_page)
        hooks.set_page(None)
        hooks.set_page_common(None)

    hooks.set_current_scenario(None)


@pytest.fixture
def page_common(page: Page) -> PageCommonMethods:
    page_common_methods = hooks.get_page_common()
    if page_common_methods is None:
        page_common_methods = PageCommonMethods(page)
        hooks.set_page_common(page_common_methods)
    return page_common_methods


def pytest_collection_modifyitems(items: list[pytest.Item]) -> None:
    """Tag BDD UI scenarios with `ui` (Java: @ui or untagged non-api/db features)."""
    for item in items:
        if _SCENARIO_BINDINGS not in str(item.path):
            continue
        if item.get_closest_marker("api") or item.get_closest_marker("db"):
            continue
        item.add_marker(pytest.mark.ui)


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(
    item: pytest.Item, call: pytest.CallInfo[Any]
) -> Generator[None, None, None]:
    outcome = yield
    report = outcome.get_result()
    setattr(item, f"rep_{report.when}", report)

    if report.when != "call" or not report.failed:
        return

    try:
        active_page = hooks.get_page()
    except RuntimeError:
        active_page = None

    _stash_failure_screenshot(item, active_page)
