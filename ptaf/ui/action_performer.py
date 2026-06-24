"""Playwright locator action dispatch (ported from ActionPerformer.java)."""

from __future__ import annotations

import logging
from pathlib import Path

from playwright.sync_api import Locator, Page

logger = logging.getLogger(__name__)


class ActionPerformer:
    """Perform actions on Playwright locators within a page context."""

    def perform_action_with_return(
        self,
        page: Page,
        action: str,
        target_locator: Locator,
        value: str | None,
    ) -> str | None:
        return self.perform_action(page, action, target_locator, value)

    def perform_action(
        self,
        page: Page,
        action: str,
        target_locator: Locator,
        value: str | None,
    ) -> str | None:
        try:
            action_key = action.lower()
            first = target_locator.first

            match action_key:
                case "click":
                    first.click()
                    return None
                case "fill":
                    first.fill(value or "")
                    return None
                case "select":
                    first.select_option(value or "")
                    return None
                case "selectmultiple":
                    first.select_option((value or "").split(","))
                    return None
                case "check":
                    first.check()
                    return None
                case "uncheck":
                    first.uncheck()
                    return None
                case "hover":
                    first.hover()
                    return None
                case "type":
                    first.type(value or "")
                    return None
                case "press":
                    first.press(value or "")
                    return None
                case "dblclick":
                    first.dblclick()
                    return None
                case "rightclick":
                    first.click(button="right")
                    return None
                case "tap":
                    first.tap()
                    return None
                case "input":
                    first.evaluate("(element, val) => element.value = val", value)
                    return None
                case "screenshot":
                    first.screenshot(path=Path(value or ""))
                    return None
                case "scroll":
                    first.evaluate(
                        "element => element.scrollIntoView({ behavior: 'smooth', block: 'center' })"
                    )
                    return None
                case "focus":
                    first.focus()
                    return None
                case "blur":
                    first.evaluate("element => element.blur()")
                    return None
                case "clear":
                    first.clear()
                    return None
                case "drag":
                    target = target_locator.page.locator(value or "").first
                    first.drag_to(target)
                    return None
                case "dragstart":
                    first.dispatch_event("dragstart")
                    return None
                case "dragend":
                    first.dispatch_event("dragend")
                    return None
                case "uploadfile" | "selectfile":
                    first.set_input_files(Path(value or ""))
                    return None
                case "download":
                    with page.expect_download() as download_info:
                        first.click()
                    download = download_info.value
                    save_path = Path(value or "")
                    download.save_as(save_path)
                    logger.info(
                        "File downloaded successfully and saved to: %s", save_path
                    )
                    return str(download.path())
                case "file_chooser_for_upload":
                    with page.expect_file_chooser():
                        self._click(first)
                    return None
                case "setattribute":
                    first.evaluate("(el, val) => el.setAttribute('value', val)", value)
                    return None
                case "removeattribute":
                    first.evaluate(
                        "(el, attr) => el.removeAttribute(attr)", value
                    )
                    return None
                case "evaluate":
                    first.evaluate(value or "")
                    return None
                case "waitforelement":
                    first.wait_for()
                    return None
                case "waitforstate":
                    first.wait_for(state=(value or "").lower())
                    return None
                case "getattribute":
                    return first.get_attribute(value or "")
                case "gettext":
                    return first.text_content()
                case "get_and_contain_text":
                    get_text = first.text_content()
                    self._assert_condition(
                        get_text is not None and get_text in get_text,
                        "Element does not contain expected text.",
                    )
                    return get_text
                case "getvalue":
                    return first.input_value()
                case "hasvalue":
                    current_value = first.input_value()
                    self._assert_condition(
                        current_value == value,
                        f"Expected: {value}, but found: {current_value}",
                    )
                    return current_value
                case "isvisible":
                    is_visible = first.is_visible()
                    self._assert_condition(
                        is_visible, "Element is not visible."
                    )
                    return str(is_visible)
                case "isenabled":
                    is_enabled = first.is_enabled()
                    self._assert_condition(
                        is_enabled, "Element is not enabled."
                    )
                    return str(is_enabled)
                case "ischecked":
                    is_checked = first.is_checked()
                    self._assert_condition(
                        is_checked, "Element is not checked."
                    )
                    return str(is_checked)
                case "isdisabled":
                    is_disabled = first.is_disabled()
                    self._assert_condition(
                        is_disabled, "Element is not disabled."
                    )
                    return str(is_disabled)
                case "ishidden":
                    is_hidden = first.is_hidden()
                    self._assert_condition(
                        is_hidden, "Element is not hidden."
                    )
                    return str(is_hidden)
                case "hastext":
                    locator_text = first.text_content()
                    self._assert_condition(
                        locator_text is not None and value in locator_text,
                        "Text mismatch.",
                    )
                    return locator_text
                case "hasclass":
                    class_attr = first.get_attribute("class")
                    has_class = class_attr is not None and value in class_attr
                    self._assert_condition(has_class, "Class mismatch.")
                    return str(has_class)
                case "hasequalvalue":
                    actual_value = first.input_value()
                    self._assert_condition(
                        actual_value == value, "Value mismatch."
                    )
                    return actual_value
                case "isempty":
                    input_value = first.input_value()
                    self._assert_condition(
                        input_value == "", "Element is not empty."
                    )
                    return input_value
                case "waitfortext":
                    first.wait_for(state="visible")
                    text_content = first.text_content()
                    if text_content is None or value not in text_content:
                        raise AssertionError(f"Text not found: {value}")
                    return text_content
                case "waitforvalue":
                    first.wait_for(state="visible")
                    input_value = first.input_value()
                    if input_value != value:
                        raise AssertionError("Value mismatch.")
                    return input_value
                case "exists":
                    exists = target_locator.count() > 0
                    self._assert_condition(
                        exists, "Element does not exist."
                    )
                    return str(exists)
                case "not_exists":
                    not_exists = target_locator.count() == 0
                    self._assert_condition(
                        not_exists, "Element exists but should not."
                    )
                    return str(not_exists)
                case _:
                    raise ValueError(f"Unknown action: {action}")

        except Exception as exc:
            logger.error(
                "Error while performing action: %s for Target Locator %s",
                action,
                target_locator,
                exc_info=True,
            )
            raise RuntimeError(
                f"Action failed: {action} for Target Locator: {target_locator} - {exc}"
            ) from exc

    def _click(self, target_locator: Locator) -> None:
        try:
            target_locator.click()
        except Exception as exc:
            logger.error(
                "Error while clicking on target locator: %s", exc, exc_info=True
            )
            raise RuntimeError(f"Click action failed: {exc}") from exc

    @staticmethod
    def _assert_condition(condition: bool, error_message: str) -> None:
        if not condition:
            raise AssertionError(error_message)

    def wait_for_locator(self, locator: Locator) -> None:
        try:
            locator.first.wait_for(state="visible", timeout=60_000)
        except Exception:
            logger.error(
                "Failed to wait for the element to be displayed", exc_info=True
            )
