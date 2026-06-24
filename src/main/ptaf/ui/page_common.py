"""Common page interaction helpers (ported from PageCommonMethods.java)."""

from __future__ import annotations

import logging
from typing import Any, Callable

from playwright.sync_api import Locator, Page

from ptaf.ui.element_action import ElementAction

logger = logging.getLogger(__name__)


def _get_hooks_module() -> Any | None:
    try:
        from ptaf import hooks as hooks_module

        return hooks_module
    except ImportError:
        return None


def _get_scenario_util_module() -> Any | None:
    try:
        from ptaf.utils import scenario_util

        return scenario_util
    except ImportError:
        return None


class PageCommonMethods:
    """Foundational utility for common web element interactions."""

    def __init__(self, page: Page) -> None:
        self.page = page
        self.element_action = ElementAction(page)
        self._is_failed = False

    def click(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("click", page, element, locator, None)

    def radio(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("radio", page, element, locator, None)

    def fill(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("fill", page, element, locator, value)

    def select(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("select", page, element, locator, value)

    def check(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("check", page, element, locator, None)

    def uncheck(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("uncheck", page, element, locator, None)

    def hover(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("hover", page, element, locator, None)

    def type(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("type", page, element, locator, value)

    def press(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("press", page, element, locator, value)

    def dblclick(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("dblclick", page, element, locator, None)

    def screenshot(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        target_locator = self.element_action.get_exact_locator(element, locator)
        self._perform_action("screenshot", page, element, locator, value)
        self.finalize_scenario_screenshot(page, target_locator)

    def download(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("download", page, element, locator, value)

    def scroll(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("scroll", page, element, locator, None)

    def focus(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("focus", page, element, locator, None)

    def blur(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("blur", page, element, locator, value)

    def clear(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("clear", page, element, locator, None)

    def drag(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("drag", page, element, locator, None)

    def gettext(self, page: Page, element: str, locator: str) -> str | None:
        value = self._get_string_value("gettext", page, element, locator, None)
        if value is None or value.strip() == "":
            print(
                f"There is no text value for element: {element}, locator: {locator}"
            )
        return value

    def get_and_contain_text(
        self, page: Page, element: str, locator: str
    ) -> None:
        self._perform_action(
            "get_and_contain_text", page, element, locator, None
        )

    def isvisible(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("isvisible", page, element, locator, None)

    def isenabled(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("isenabled", page, element, locator, None)

    def isdisabled(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("isdisabled", page, element, locator, None)

    def ishidden(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("ishidden", page, element, locator, None)

    def ischecked(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("ischecked", page, element, locator, None)

    def get_element_locator(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> Locator:
        return self.element_action.get_locator(
            iframe, iframe_2, iframe_3, element, locator, page, None
        )

    def exists(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("exists", page, element, locator, None)

    def not_exists(self, page: Page, element: str, locator: str) -> None:
        target_locator = self.get_element_locator(
            page, None, None, None, element, locator
        )
        if target_locator.count() > 0:
            self._perform_action("not_exists", page, element, locator, None)

    def rightclick(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("rightclick", page, element, locator, None)

    def tap(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("tap", page, element, locator, None)

    def upload_file(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("uploadfile", page, element, locator, value)

    def select_multiple(
        self, page: Page, element: str, locator: str
    ) -> None:
        self._perform_action("selectmultiple", page, element, locator, None)

    def get_attribute(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("getattribute", page, element, locator, value)

    def set_attribute(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("setattribute", page, element, locator, value)

    def remove_attribute(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("removeattribute", page, element, locator, value)

    def evaluate(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("evaluate", page, element, locator, value)

    def wait_for_element(
        self, page: Page, element: str, locator: str
    ) -> None:
        self._perform_action("waitForelement", page, element, locator, None)

    def wait_for_state(
        self, page: Page, element: str, locator: str
    ) -> None:
        self._perform_action("waitforstate", page, element, locator, None)

    def wait_for_text(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("waitfortext", page, element, locator, value)

    def wait_for_value(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("waitforvalue", page, element, locator, value)

    def drag_start(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("dragstart", page, element, locator, None)

    def drag_end(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("dragend", page, element, locator, None)

    def input(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("input", page, element, locator, value)

    def select_file(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("selectfile", page, element, locator, value)

    def has_text(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("hastext", page, element, locator, value)

    def hasclass(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("hasclass", page, element, locator, None)

    def has_equal_value(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("hasqualvalue", page, element, locator, value)

    def isempty(self, page: Page, element: str, locator: str) -> None:
        self._perform_action("isempty", page, element, locator, None)

    def contain(
        self, page: Page, element: str, locator: str, expected_text: str
    ) -> None:
        self._perform_action("hastext", page, element, locator, expected_text)

    def file_chooser_for_upload(
        self, page: Page, file_name: str, element: str, locator: str
    ) -> None:
        self.element_action.upload_file(page, file_name, element, locator)

    def click_document_link(
        self, page: Page, element: str, locator: str
    ) -> None:
        self.element_action.click_on_document_link_name(page, element, locator)

    def hasvalue(
        self, page: Page, element: str, locator: str, value: str
    ) -> None:
        self._perform_action("hasvalue", page, element, locator, value)

    def getvalue(self, page: Page, element: str, locator: str) -> str:
        self._perform_action("getvalue", page, element, locator, None)
        return element

    def get_list_of_elements(
        self, page: Page, element: str, locator: str
    ) -> None:
        elements = self.element_action.get_element_handle_list(
            page, element, locator, None
        )
        if not elements:
            logger.info("No elements found for the specified locator.")
        else:
            lines = [
                f"Element {index + 1}: {handle!s}"
                for index, handle in enumerate(elements)
            ]
            logger.info("\n".join(lines))

    def click_radio_button(
        self, page: Page, element: str, locator: str
    ) -> None:
        elements = self.element_action.get_element_handle_list(
            page, element, locator, None
        )
        for radio_button in elements:
            if radio_button.is_enabled():
                radio_button.check()
                logger.info("Radio button clicked!")
                break
            logger.info("Radio button is not enabled, moving to the next one.")

    def finalize_scenario(self) -> None:
        if self._is_failed:
            scenario_util = _get_scenario_util_module()
            if scenario_util is not None:
                scenario_util.handle_scenario_teardown(
                    self._get_current_scenario(), self.page, "Passed Step"
                )

    def finalize_scenario_screenshot(
        self, page: Page, target_locator: str
    ) -> None:
        if not self._is_failed:
            scenario_util = _get_scenario_util_module()
            if scenario_util is not None:
                scenario_util.handle_scenario_teardown_locator(
                    self._get_current_scenario(),
                    page,
                    None,
                    None,
                    None,
                    target_locator,
                    "Passed Step",
                )

    @staticmethod
    def set_current_scenario(scenario: object) -> None:
        PageCommonMethods._current_scenario = scenario

    _current_scenario: object | None = None

    def _perform_action(
        self,
        action: str,
        page: Page,
        element: str,
        locator: str,
        value: str | None,
    ) -> None:
        def step() -> None:
            action_status = self.element_action.perform_action_page(
                page, action, element, locator, value
            )
            if not action_status:
                self._handle_failure(page, action, element)

        self._execute_step(step)

    def _get_string_value(
        self,
        action: str,
        page: Page,
        element: str,
        locator: str,
        value: str | None,
    ) -> str | None:
        result: list[str | None] = [None]

        def step() -> None:
            result[0] = self.element_action.perform_action_page_with_return(
                page, action, element, locator, value
            )
            if result[0] is None and self._action_requires_result(action):
                self._handle_failure(page, action, element)
            elif result[0] is not None and result[0] != "":
                logger.info("Action '%s' returned result: %s", action, result[0])

        self._execute_step(step)
        return result[0]

    @staticmethod
    def _action_requires_result(action: str) -> bool:
        match action.lower():
            case (
                "gettext"
                | "getvalue"
                | "getattribute"
                | "hastext"
                | "hasclass"
                | "hasequalvalue"
                | "isempty"
                | "isvisible"
                | "isenabled"
                | "ischecked"
                | "isdisabled"
                | "exists"
                | "not_exists"
                | "waitfortext"
                | "waitforvalue"
            ):
                return True
            case _:
                return False

    def _execute_step(self, step: Callable[[], None]) -> None:
        if self._is_failed:
            return
        try:
            step()
        except Exception as exc:
            self._is_failed = True
            logger.error("Step execution failed: %s", exc, exc_info=True)
            self._handle_failure(self.page, "Step execution failed", None)

    def _handle_failure(
        self, page: Page, action: str, element: str | None
    ) -> None:
        self._is_failed = True
        logger.error("Action '%s' failed on element '%s'", action, element)
        scenario_util = _get_scenario_util_module()
        if scenario_util is not None:
            scenario_util.handle_scenario_teardown(
                self._get_current_scenario(), page, "Failure Step"
            )
        self._close_browser_on_failure()
        raise RuntimeError(
            f"Action '{action}' failed on element '{element}', skipping further steps"
        )

    def _close_browser_on_failure(self) -> None:
        try:
            if self.page is not None and not self.page.is_closed():
                self.page.close()
                logger.info("Page closed due to failure.")
        except Exception as exc:
            logger.error("Error closing the page: %s", exc, exc_info=True)

        try:
            hooks = _get_hooks_module()
            if hooks is not None and hooks.get_browser() is not None:
                hooks.get_browser().close()
                logger.info("Browser closed due to failure.")
        except Exception as exc:
            logger.error("Error closing the browser: %s", exc, exc_info=True)

    def _get_current_scenario(self) -> object | None:
        hooks = _get_hooks_module()
        if hooks is not None:
            return hooks.get_current_scenario()
        return PageCommonMethods._current_scenario
