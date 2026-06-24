"""Scenario teardown helpers for screenshots (ported from ScenarioUtil.java)."""

from __future__ import annotations

import base64
import logging
from typing import Any

from playwright.sync_api import Page

logger = logging.getLogger(__name__)


def handle_scenario_teardown(
    scenario: object | None, page: Page, status: str
) -> bytes | None:
    """Capture a full-page screenshot for the given scenario status."""
    try:
        screenshot = page.screenshot(full_page=True)
        scenario_name = _scenario_name(scenario)
        logger.info("Screenshot taken for %s scenario: %s", status, scenario_name)
        return screenshot
    except Exception as exc:
        logger.error("Error taking screenshot: %s", exc, exc_info=True)
        return None


def handle_scenario_teardown_locator(
    scenario: object | None,
    page: Page,
    iframe: str | None,
    iframe_2: str | None,
    iframe_3: str | None,
    target_locator: str,
    status: str,
) -> bytes | None:
    """Capture a locator-scoped screenshot, optionally within nested iframes."""
    try:
        screenshot: bytes | None = None

        if iframe is None:
            screenshot = page.locator(target_locator).screenshot()
        elif iframe is not None and iframe_2 is None and iframe_3 is None:
            screenshot = (
                page.frame_locator(iframe)
                .locator(target_locator)
                .screenshot()
            )
        elif iframe is not None and iframe_2 is not None and iframe_3 is None:
            screenshot = (
                page.frame_locator(iframe)
                .frame_locator(iframe_2)
                .locator(target_locator)
                .screenshot()
            )
        elif iframe is not None and iframe_2 is not None and iframe_3 is not None:
            screenshot = (
                page.frame_locator(iframe)
                .frame_locator(iframe_2)
                .frame_locator(iframe_3)
                .locator(target_locator)
                .screenshot()
            )

        scenario_name = _scenario_name(scenario)
        if screenshot is not None:
            logger.info(
                "Screenshot taken for %s scenario: %s", status, scenario_name
            )
            return screenshot

        logger.warning("No screenshot captured for scenario: %s", scenario_name)
        return None
    except Exception as exc:
        logger.error("Error taking screenshot: %s", exc, exc_info=True)
        return None


def attach_screenshot_to_report(
    report: Any,
    screenshot: bytes,
    label: str,
) -> None:
    """Attach screenshot bytes to a pytest-html report, when available."""
    if report is None:
        return

    try:
        import pytest_html

        encoded = base64.b64encode(screenshot).decode("ascii")
        png_extra = pytest_html.extras.png(encoded, label)
    except Exception as exc:
        logger.debug("Could not attach screenshot to html report: %s", exc)
        return

    extras = list(getattr(report, "extras", None) or getattr(report, "extra", []))
    extras.append(png_extra)
    report.extras = extras


def _scenario_name(scenario: object | None) -> str:
    if scenario is None:
        return "<unknown>"
    name = getattr(scenario, "name", None)
    if callable(name):
        return str(name())
    if name is not None:
        return str(name)
    return str(scenario)
