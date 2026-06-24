"""Smoke tests for browser lifecycle pytest fixtures."""

from __future__ import annotations

import pytest
from playwright.sync_api import Browser, BrowserContext, Page

from ptaf import hooks


@pytest.mark.ui
@pytest.mark.smoke
def test_browser_fixtures_provide_live_page(
    browser: Browser,
    context: BrowserContext,
    page: Page,
) -> None:
    assert browser.is_connected()
    assert page.is_closed() is False
    page.goto("https://example.com", wait_until="domcontentloaded")
    assert "Example Domain" in page.title()
    assert hooks.get_page() is page
    assert hooks.get_browser() is browser


@pytest.mark.ui
@pytest.mark.smoke
class TestParallelSafeFixtures:
    def test_worker_isolated_session_a(self, page: Page) -> None:
        page.goto("about:blank")
        assert page.url == "about:blank"

    def test_worker_isolated_session_b(self, page: Page) -> None:
        page.goto("about:blank")
        assert page.url == "about:blank"


@pytest.mark.ui
@pytest.mark.smoke
@pytest.mark.last_scenario_feature
class TestLastScenarioFeatureReuse:
    def test_first_scenario_uses_shared_stack(self, page: Page) -> None:
        page.goto("about:blank")
        TestLastScenarioFeatureReuse._shared_page_id = id(page)

    def test_second_scenario_reuses_browser_stack(self, page: Page) -> None:
        assert id(page) == TestLastScenarioFeatureReuse._shared_page_id

    _shared_page_id: int | None = None
