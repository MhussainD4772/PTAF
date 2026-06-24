"""Playwright locator assertion dispatch (ported from AssertionPerformer.java)."""

from __future__ import annotations

import logging
import re

from playwright.sync_api import Locator, Page, expect

logger = logging.getLogger(__name__)


class AssertionPerformer:
    """Perform Playwright expect() assertions on locators and pages."""

    def perform_assertion(
        self,
        page: Page,
        action: str,
        target_locator: Locator,
        value: str | None,
    ) -> None:
        try:
            action_key = action.lower()

            match action_key:
                case "contain":
                    expect(target_locator).to_contain_text(value or "")
                case "notcontain":
                    expect(target_locator).not_to_contain_text(value or "")
                case "hastext":
                    expect(target_locator).to_have_text(value or "")
                case "hastextexactly":
                    expect(target_locator).to_have_text(
                        value or "", use_inner_text=True
                    )
                case "nothastext":
                    expect(target_locator).not_to_have_text(value or "")
                case "isvisible":
                    expect(target_locator).to_be_visible()
                case "notvisible":
                    expect(target_locator).not_to_be_visible()
                case "ishidden":
                    expect(target_locator).to_be_hidden()
                case "isattached":
                    expect(target_locator).to_be_attached()
                case "detached":
                    expect(target_locator).not_to_be_attached()
                case "enabled":
                    expect(target_locator).to_be_enabled()
                case "disabled":
                    expect(target_locator).to_be_disabled()
                case "checked":
                    expect(target_locator).to_be_checked()
                case "notchecked":
                    expect(target_locator).not_to_be_checked()
                case "focused":
                    expect(target_locator).to_be_focused()
                case "notfocused":
                    expect(target_locator).not_to_be_focused()
                case "hasattribute":
                    attr_parts = (value or "").split("=", 1)
                    expect(target_locator).to_have_attribute(
                        attr_parts[0], attr_parts[1]
                    )
                case "nothasattribute":
                    not_attr_parts = (value or "").split("=", 1)
                    expect(target_locator).not_to_have_attribute(
                        not_attr_parts[0], not_attr_parts[1]
                    )
                case "hasclass":
                    expect(target_locator).to_have_class(value or "")
                case "nothasclass":
                    expect(target_locator).not_to_have_class(value or "")
                case "hasvalue":
                    expect(target_locator).to_have_value(value or "")
                case "hastitle":
                    expect(page).to_have_title(value or "")
                case "hasurl":
                    expect(page).to_have_url(value or "")
                case "hascount":
                    expect(target_locator).to_have_count(int(value or "0"))
                case "matchregex":
                    expect(target_locator).to_have_text(re.compile(value or ""))
                case "textstartswith":
                    expect(target_locator).to_have_text(
                        re.compile("^" + re.escape(value or ""))
                    )
                case "textendswith":
                    expect(target_locator).to_have_text(
                        re.compile(re.escape(value or "") + "$")
                    )
                case _:
                    raise ValueError(f"Unknown Assertion: {action}")

        except Exception as exc:
            logger.error(
                "Error while performing assertion: %s for Target Locator %s",
                action,
                target_locator,
                exc_info=True,
            )
            raise AssertionError(
                f"Assertion failed for action: {action}, Locator: {target_locator}, "
                f"Reason: {exc}"
            ) from exc
