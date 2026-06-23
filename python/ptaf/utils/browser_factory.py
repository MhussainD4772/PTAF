"""Playwright browser and context factory (ported from BrowserFactory.java)."""

from __future__ import annotations

import logging
from enum import Enum
from pathlib import Path

from playwright.sync_api import Browser, BrowserContext, Playwright

from ptaf.utils import config

logger = logging.getLogger(__name__)

VIDEO_DIR = Path(__file__).resolve().parent.parent.parent / "test-output" / "captured-videos"


class BrowserTypeEnum(str, Enum):
    CHROME = "CHROME"
    FIREFOX = "FIREFOX"
    WEBKIT = "WEBKIT"
    EDGE = "EDGE"


def parse_browser_type(browser_name: str) -> BrowserTypeEnum:
    normalized = browser_name.strip().upper()
    try:
        return BrowserTypeEnum(normalized)
    except ValueError as exc:
        raise ValueError(f"Unsupported browser type: {browser_name}") from exc


def create_browser(playwright: Playwright, browser_type: BrowserTypeEnum) -> Browser:
    headless_raw = config.get_headless_mode() or "false"
    headless = headless_raw.strip().lower() == "true"

    match browser_type:
        case BrowserTypeEnum.CHROME:
            logger.info(
                "Launching browser: CHROMIUM with headless mode: %s", headless
            )
            return playwright.chromium.launch(headless=headless)
        case BrowserTypeEnum.FIREFOX:
            logger.info(
                "Launching browser: FIREFOX with headless mode: %s", headless
            )
            return playwright.firefox.launch(headless=headless)
        case BrowserTypeEnum.WEBKIT:
            logger.info(
                "Launching browser: WEBKIT with headless mode: %s", headless
            )
            return playwright.webkit.launch(headless=headless)
        case BrowserTypeEnum.EDGE:
            logger.info("Launching Microsoft Edge with headless mode: %s", headless)
            return playwright.chromium.launch(channel="msedge", headless=headless)


def create_context_with_video(browser: Browser) -> BrowserContext:
    video_capture_raw = config.get_video_capture() or "false"
    record_video = video_capture_raw.strip().lower() == "true"

    context_options: dict[str, object] = {"ignore_https_errors": True}

    if record_video:
        logger.info("Video capture enabled.")
        VIDEO_DIR.mkdir(parents=True, exist_ok=True)
        context_options["record_video_dir"] = str(VIDEO_DIR)
        context_options["record_video_size"] = {"width": 1280, "height": 720}
    else:
        logger.info("Video capture disabled.")

    return browser.new_context(**context_options)
