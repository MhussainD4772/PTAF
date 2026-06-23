"""Element locator chaining executor."""

from __future__ import annotations

import re

from playwright.sync_api import FrameLocator, Locator, Page

from ptaf.ui.locator_handler import LocatorHandler
from ptaf.ui.locator_helper import ElementLocatorHelper

_locator_helper = ElementLocatorHelper()
_locator_handler = LocatorHandler()

_CHAIN_SPLIT = re.compile(r"\s*>\s*")


def get_locator(
    element: str,
    key: str,
    *,
    page: Page | None = None,
    frame_locator: FrameLocator | None = None,
    iframe: str | None = None,
    iframe_2: str | None = None,
    iframe_3: str | None = None,
) -> Locator:
    """Resolve a chained locator string for an element key."""
    full_locator_string = _locator_helper.get_element(element, key)
    locator_parts = _CHAIN_SPLIT.split(full_locator_string)

    current_locator: Locator | None = None

    try:
        context: Page | FrameLocator
        if frame_locator is not None:
            context = frame_locator
        elif iframe is not None and iframe != "":
            frame_context = page.frame_locator(iframe)
            if iframe_2 is not None and iframe_2 != "":
                frame_context = frame_context.frame_locator(iframe_2)
            if iframe_3 is not None and iframe_3 != "":
                frame_context = frame_context.frame_locator(iframe_3)
            context = frame_context
        else:
            context = page

        for index, part in enumerate(locator_parts):
            trimmed_part = part.strip()
            locator_type = _locator_helper.get_locator_type(trimmed_part)
            locator = _locator_helper.get_locator(trimmed_part)

            if index == 0:
                current_locator = _locator_handler.get_locator_for_type(
                    locator_type, context, locator
                )
            else:
                if current_locator is None:
                    raise RuntimeError("Chained locator resolution failed.")
                current_locator = _locator_handler.get_locator_for_type(
                    locator_type, current_locator, locator
                )

        if current_locator is None:
            raise RuntimeError("Locator chain produced no result.")
        return current_locator

    except Exception as exc:
        raise RuntimeError(
            f"Failed to get locator for: '{full_locator_string}'"
        ) from exc
