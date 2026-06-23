"""Element locator chaining executor and action orchestration."""

from __future__ import annotations

import logging
import re

from playwright.sync_api import ElementHandle, FrameLocator, Locator, Page

from ptaf.ui.action_performer import ActionPerformer
from ptaf.ui.locator_handler import LocatorHandler
from ptaf.ui.locator_helper import ElementLocatorHelper
from ptaf.utils import yaml_reader

logger = logging.getLogger(__name__)

_locator_helper = ElementLocatorHelper()
_locator_handler = LocatorHandler()
_action_performer = ActionPerformer()

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


class ElementAction:
    """Playwright element interactions (ported from ElementActionImpl.java)."""

    def __init__(self, page: Page) -> None:
        self.page = page

    def get_locator(
        self,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        key: str,
        page: Page | None,
        frame_locator: FrameLocator | None,
    ) -> Locator:
        return get_locator(
            element,
            key,
            page=page,
            frame_locator=frame_locator,
            iframe=iframe,
            iframe_2=iframe_2,
            iframe_3=iframe_3,
        )

    def perform_action_page(
        self,
        page: Page,
        action: str,
        element: str,
        key: str,
        value: str | None,
    ) -> bool:
        return self._perform_action(
            page, None, None, None, action, element, key, value, None
        )

    def perform_action_frame(
        self,
        frame_locator: FrameLocator,
        action: str,
        element: str,
        key: str,
        value: str | None,
    ) -> bool:
        return self._perform_action(
            None, None, None, None, action, element, key, value, frame_locator
        )

    def perform_action_page_frame(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        action: str,
        element: str,
        key: str,
        value: str | None,
        frame_locator: FrameLocator | None,
    ) -> bool:
        return self._perform_action(
            page, iframe, iframe_2, iframe_3, action, element, key, value, None
        )

    def perform_action_page_with_return(
        self,
        page: Page,
        action: str,
        element: str,
        key: str,
        value: str | None,
    ) -> str | None:
        try:
            target_locator = self._get_locator_based_on_page(page, element, key)
            if target_locator is None:
                logger.error(
                    "Locator not found for element: %s with key: %s", element, key
                )
                return None
            _action_performer.wait_for_locator(target_locator)
            return _action_performer.perform_action_with_return(
                page, action, target_locator, value
            )
        except Exception:
            logger.error(
                "Exception in perform_action_page_with_return for element '%s' and action '%s':",
                element,
                action,
                exc_info=True,
            )
            return None

    def perform_action_page_frame_with_return(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        action: str,
        element: str,
        key: str,
        value: str | None,
        frame_locator: FrameLocator | None,
    ) -> str | None:
        try:
            target_locator = self._get_locator_based_on_page_frame(
                page, iframe, iframe_2, iframe_3, element, key
            )
            if target_locator is None:
                logger.error(
                    "Locator not found for nested frame element: %s with key: %s",
                    element,
                    key,
                )
                return None
            _action_performer.wait_for_locator(target_locator)
            return _action_performer.perform_action_with_return(
                page, action, target_locator, value
            )
        except Exception:
            logger.error(
                "Exception in perform_action_page_frame_with_return for element '%s' and action '%s':",
                element,
                action,
                exc_info=True,
            )
            return None

    def get_element_handle_page(
        self, page: Page, element: str, key: str
    ) -> bool:
        element_handles = self.get_element_handle_list(page, element, key, None)
        return len(element_handles) > 0

    def get_element_handle_frame(
        self, frame_locator: FrameLocator, element: str, key: str
    ) -> bool:
        element_handles = self.get_element_handle_list(
            None, element, key, frame_locator
        )
        return len(element_handles) > 0

    def assert_element_text_page(
        self, page: Page, element: str, key: str, expected_text: str
    ) -> bool:
        return self._assert_element_text(page, element, key, expected_text, None)

    def assert_element_text_frame(
        self,
        frame_locator: FrameLocator,
        element: str,
        key: str,
        expected_text: str,
    ) -> bool:
        return self._assert_element_text(
            None, element, key, expected_text, frame_locator
        )

    def upload_file(
        self, page: Page, file_name: str, element: str, key: str
    ) -> None:
        with page.expect_file_chooser() as file_chooser_info:
            page.click(self.get_element(element, key))
        file_chooser_info.value.set_files(self.get_element(element, file_name))

    def click_on_document_link_name(
        self, page: Page, element: str, key: str
    ) -> None:
        document_link_name = self.get_element(element, key)
        file_name = extract_file_name(document_link_name)
        print(file_name)
        try:
            page.get_by_role("link", name=file_name).click()
        except Exception:
            logger.error(
                "Failed to click on element by Role '%s'",
                element + key,
                exc_info=True,
            )

    @staticmethod
    def get_element(element: str, key: str) -> str:
        try:
            value = yaml_reader.get(f"elements.{element}.{key}")
            if value is None:
                raise KeyError(f"elements.{element}.{key}")
            return str(value)
        except Exception:
            logger.error(
                "Failed to retrieve selector for element '%s'",
                element + key,
                exc_info=True,
            )
            raise

    def get_element_handle_list(
        self,
        page: Page | None,
        element: str,
        key: str,
        frame_locator: FrameLocator | None,
    ) -> list[ElementHandle]:
        element_handles: list[ElementHandle] = []
        try:
            target_locator = self.get_locator(
                None, None, None, element, key, page, frame_locator
            )
            if target_locator is not None:
                element_handles = target_locator.element_handles()
            else:
                logger.error(
                    "Target locator for element '%s' could not be determined.", element
                )
        except Exception:
            logger.error(
                "Failed to retrieve element handles for '%s'", element, exc_info=True
            )
        return element_handles

    def get_exact_locator(self, element: str, key: str) -> str:
        locator_value = _locator_helper.get_element(element, key)
        return _locator_helper.get_locator(locator_value)

    def _perform_action(
        self,
        page: Page | None,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        action: str,
        element: str,
        key: str,
        value: str | None,
        frame_locator: FrameLocator | None,
    ) -> bool:
        try:
            if frame_locator is not None:
                target_locator = self.get_locator(
                    None, None, None, element, key, None, frame_locator
                )
            elif page is not None:
                target_locator = self.get_locator(
                    iframe, iframe_2, iframe_3, element, key, page, None
                )
            else:
                raise ValueError("A Page or FrameLocator context is required.")

            if target_locator is None:
                raise RuntimeError(
                    f"Failed to resolve a target Locator for element: {element} with key: {key}"
                )

            _action_performer.wait_for_locator(target_locator)
            _action_performer.perform_action(page, action, target_locator, value)
            return True
        except Exception:
            logger.error(
                "Error while performing action '%s' on element '%s' with key '%s'",
                action,
                element,
                key,
                exc_info=True,
            )
        return False

    def _assert_element_text(
        self,
        page: Page | None,
        element: str,
        key: str,
        expected_text: str,
        frame_locator: FrameLocator | None,
    ) -> bool:
        try:
            target_locator = self.get_locator(
                None, None, None, element, key, page, frame_locator
            )
            actual_text = target_locator.first.text_content()
            is_text_matching = expected_text == actual_text
            logger.info(
                "Asserting text on element '%s': expected '%s', actual '%s'",
                element,
                expected_text,
                actual_text,
            )
            if not is_text_matching:
                logger.error(
                    "Text mismatch: expected '%s' but found '%s'",
                    expected_text,
                    actual_text,
                )
            return is_text_matching
        except Exception:
            logger.error(
                "Error while asserting text on element '%s'", element, exc_info=True
            )
            return False

    def _get_locator_based_on_page(
        self, page: Page, element: str, key: str
    ) -> Locator:
        return self.get_locator(None, None, None, element, key, page, None)

    def _get_locator_based_on_page_frame(
        self,
        page: Page,
        iframe: str | None,
        iframe_2: str | None,
        iframe_3: str | None,
        element: str,
        key: str,
    ) -> Locator:
        return self.get_locator(iframe, iframe_2, iframe_3, element, key, page, None)


def extract_file_name(file_path: str) -> str:
    parts = file_path.split("/")
    return parts[-1]
