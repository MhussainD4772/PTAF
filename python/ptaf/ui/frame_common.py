"""Frame interaction helpers (ported from FrameCommonMethods.java)."""

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


class FrameCommonMethods:
    """Utility methods for interacting with elements within iframes."""

    def __init__(self, page: Page | None) -> None:
        self.page = page
        self._element_action: ElementAction | None = None
        self._is_failed = False

    def _get_element_action(self, page: Page) -> ElementAction:
        if self._element_action is None:
            self._element_action = ElementAction(page)
        return self._element_action

    def click(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "click", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def fill(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        self._perform_action(
            "fill", page, iframe, iframe_2, iframe_3, element, locator, value
        )

    def select(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        self._perform_action(
            "select", page, iframe, iframe_2, iframe_3, element, locator, value
        )

    def check(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "check", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def uncheck(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "uncheck", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def hover(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "hover", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def type(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        self._perform_action(
            "type", page, iframe, iframe_2, iframe_3, element, locator, value
        )

    def press(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        self._perform_action(
            "press", page, iframe, iframe_2, iframe_3, element, locator, value
        )

    def dblclick(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "dblclick", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def screenshot(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        target_locator = self._get_element_action(page).get_exact_locator(
            element, locator
        )
        self._perform_action(
            "screenshot", page, iframe, iframe_2, iframe_3, element, locator, value
        )
        self.finalize_scenario(page, iframe, iframe_2, iframe_3, target_locator)

    def scroll(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "scroll", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def clear(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "clear", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def gettext(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> str | None:
        value = self._get_string_value(
            "gettext", page, iframe, iframe_2, iframe_3, element, locator, None
        )
        if value is None or value.strip() == "":
            print(
                f"There is no text value for element: {element}, locator: {locator}"
            )
        return value

    def isvisible(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "isvisible", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def isenabled(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "isenabled", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def ischecked(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "ischecked", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def exists(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "exists", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def contain(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        expected_text: str,
    ) -> None:
        self._perform_action(
            "hastext", page, iframe, iframe_2, iframe_3, element, locator, expected_text
        )

    def hasvalue(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str,
    ) -> None:
        self._perform_action(
            "hasvalue", page, iframe, iframe_2, iframe_3, element, locator, value
        )

    def getvalue(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> None:
        self._perform_action(
            "getvalue", page, iframe, iframe_2, iframe_3, element, locator, None
        )

    def get_element_locator(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
    ) -> Locator:
        return self._get_element_action(page).get_locator(
            iframe, iframe_2, iframe_3, element, locator, page, None
        )

    def click_radio_button(
        self,
        page: Page,
        iframe: str | None,
        element: str,
        locator: str,
    ) -> None:
        elements = self._get_element_action(page).get_element_handle_list(
            page, element, locator, None
        )
        for radio_button in elements:
            if radio_button.is_enabled():
                radio_button.check()
                logger.info("Radio button clicked!")
                break
            logger.info("Radio button is not enabled, moving to the next one.")

    def finalize_scenario(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        target_locator: str,
    ) -> None:
        if not self._is_failed:
            scenario_util = _get_scenario_util_module()
            if scenario_util is not None:
                scenario_util.handle_scenario_teardown_locator(
                    self._get_current_scenario(),
                    page,
                    iframe,
                    iframe_2,
                    iframe_3,
                    target_locator,
                    "Passed Step",
                )

    def _perform_action(
        self,
        action: str,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str | None,
    ) -> None:
        def step() -> None:
            action_status = self._get_element_action(
                page
            ).perform_action_page_frame(
                page, iframe, iframe_2, iframe_3, action, element, locator, value, None
            )
            if not action_status:
                self._handle_failure(page, action, element)

        self._execute_step(step)

    def _get_string_value(
        self,
        action: str,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        locator: str,
        value: str | None,
    ) -> str | None:
        result: list[str | None] = [None]

        def step() -> None:
            result[0] = self._get_element_action(
                page
            ).perform_action_page_frame_with_return(
                page, iframe, iframe_2, iframe_3, action, element, locator, value, None
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
            if self.page is not None:
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
        return None
