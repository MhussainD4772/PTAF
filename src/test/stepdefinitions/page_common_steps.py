"""Gherkin step definitions for page interactions (PageCommonSteps.java)."""

from __future__ import annotations

import logging
import time

from pytest_bdd import parsers

from stepdefinitions.step_binding import keyword_step

from ptaf.ui.page_common import PageCommonMethods
from ptaf.utils import config

logger = logging.getLogger(__name__)


def _page_common(page) -> PageCommonMethods:
    return PageCommonMethods(page)


@keyword_step(parsers.parse("we click on page {element} locator {locator}"))
def we_click_action_on_page(page, element, locator) -> None:
    _page_common(page).click(page, element, locator)


@keyword_step(parsers.parse("we double click on page {element} locator {locator}"))
def we_double_click_action_on_page(page, element, locator) -> None:
    _page_common(page).dblclick(page, element, locator)


@keyword_step(parsers.parse('we enter value on page {element} locator {locator} value "{value}"'))
def we_enter_value_on_page(page, element, locator, value) -> None:
    _page_common(page).fill(page, element, locator, value)


@keyword_step(parsers.parse('we select on page {element} locator {locator} value "{value}"'))
def we_select_value_on_page(page, element, locator, value) -> None:
    _page_common(page).select(page, element, locator, value)


@keyword_step(parsers.parse("we check on page {element} locator {locator}"))
def we_check_action_on_page(page, element, locator) -> None:
    _page_common(page).check(page, element, locator)


@keyword_step(parsers.parse("we uncheck on page {element} locator {locator}"))
def we_uncheck_action_on_page(page, element, locator) -> None:
    _page_common(page).uncheck(page, element, locator)


@keyword_step(parsers.parse("we hover on page {element} locator {locator}"))
def we_hover_action_on_page(page, element, locator) -> None:
    _page_common(page).hover(page, element, locator)


@keyword_step(parsers.parse('we type on page {element} locator {locator} value "{value}"'))
def we_type_value_on_page(page, element, locator, value) -> None:
    _page_common(page).type(page, element, locator, value)


@keyword_step(parsers.parse("we scroll on page {element} locator {locator}"))
def we_scroll_to_locator_on_page(page, element, locator) -> None:
    _page_common(page).scroll(page, element, locator)


@keyword_step(parsers.parse('we clear value on page {element} locator {locator} value "{value}"'))
def we_clear_value_on_page(page, element, locator) -> None:
    _page_common(page).clear(page, element, locator)


@keyword_step(parsers.parse("we verify on page {element} of locator {locator} is visible"))
def we_verify_on_page_locator_is_visible(page, element, locator) -> None:
    _page_common(page).isvisible(page, element, locator)


@keyword_step(parsers.parse("we verify on page {element} of locator {locator} is checked"))
def we_verify_on_page_locator_is_checked(page, element, locator) -> None:
    _page_common(page).ischecked(page, element, locator)


@keyword_step(parsers.parse("we verify on page {element} of locator {locator} is enabled"))
def we_verify_on_page_locator_is_enabled(page, element, locator) -> None:
    _page_common(page).isenabled(page, element, locator)


@keyword_step(parsers.parse("we verify on page {element} of locator {locator} is existed"))
def we_verify_on_page_locator_is_existed(page, element, locator) -> None:
    _page_common(page).exists(page, element, locator)


@keyword_step(parsers.parse('we contain on page {element} of locator {locator} value "{value}"'))
def we_contain_on_page_locator_value(page, element, locator, value) -> None:
    _page_common(page).contain(page, element, locator, value)


@keyword_step(parsers.parse("we get text on page {element} locator {locator}"))
def we_get_text_on_page(page, element, locator) -> None:
    _page_common(page).gettext(page, element, locator)


@keyword_step(parsers.parse("we get value on page {element} locator {locator}"))
def we_get_value_on_page(page, element, locator) -> None:
    _page_common(page).getvalue(page, element, locator)


@keyword_step(parsers.parse('we has value on page {element} of locator {locator} value "{value}"'))
def we_has_value_on_page_locator_value(page, element, locator, value) -> None:
    _page_common(page).hasvalue(page, element, locator, value)


@keyword_step(parsers.parse("we get list of elements on page {element} locator {locator}"))
def we_get_list_of_elements_on_page(page, element, locator) -> None:
    _page_common(page).get_list_of_elements(page, element, locator)


@keyword_step(parsers.parse("we get text of elements on page {element} locator {locator}"))
def we_get_text_of_elements_on_page(page, element, locator) -> None:
    value = _page_common(page).gettext(page, element, locator)
    print(f"Value: {value}")


@keyword_step(parsers.parse("we click radio on page {element} list locator {locator}"))
def click_radio_on_page(page, element, locator) -> None:
    _page_common(page).click_radio_button(page, element, locator)


@keyword_step(parsers.parse('we capture screenshot on page {element} locator {locator} name "{name}"'))
def we_capture_screenshot_on_page(page, element, locator, name) -> None:
    file_path = f"test-output/screenshots/{name}.png"
    _page_common(page).screenshot(page, element, locator, file_path)


@keyword_step(parsers.parse('we press on page {element} locator {locator} key "{value}" keyboard'))
def we_press_on_page_key(page, element, locator, value) -> None:
    _page_common(page).press(page, element, locator, value)


@keyword_step(parsers.parse("we click download on page {element} locator {locator}"))
def we_download_on_page_key(page, element, locator) -> None:
    file_path = config.get_value("downloadDocument")
    _page_common(page).download(page, element, locator, file_path or "")


@keyword_step(parsers.parse("get title of page"))
def get_title_of_page(page) -> None:
    title = page.title()
    logger.info("Page title: %s", title)


@keyword_step(parsers.parse("we wait for some time"))
def we_wait_for_some_time(page) -> None:
    time.sleep(3)


@keyword_step(parsers.parse("Stop Execution"))
def stop_execution(page) -> None:
    time.sleep(30)
