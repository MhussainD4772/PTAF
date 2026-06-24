"""Gherkin step definitions for page interactions (PageCommonSteps.java)."""

from __future__ import annotations

import logging
import time

from pytest_bdd import given, parsers, then, when

from ptaf.ui.page_common import PageCommonMethods
from ptaf.utils import config

logger = logging.getLogger(__name__)


def _page_common(page) -> PageCommonMethods:
    return PageCommonMethods(page)


@then(parsers.re(r"^we click on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_click_action_on_page(page, element, locator) -> None:
    _page_common(page).click(page, element, locator)


@then(parsers.re(r"^we double click on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_double_click_action_on_page(page, element, locator) -> None:
    _page_common(page).dblclick(page, element, locator)


@then(
    parsers.re(
        r'^we enter value on page (?P<element>.*?) locator (?P<locator>.*?) value "(?P<value>.*?)"$'
    )
)
def we_enter_value_on_page(page, element, locator, value) -> None:
    _page_common(page).fill(page, element, locator, value)


@then(
    parsers.re(r'^we select on page (?P<element>.*?) locator (?P<locator>.*?) value "(?P<value>.*?)"$')
)
def we_select_value_on_page(page, element, locator, value) -> None:
    _page_common(page).select(page, element, locator, value)


@then(parsers.re(r"^we check on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_check_action_on_page(page, element, locator) -> None:
    _page_common(page).check(page, element, locator)


@then(parsers.re(r"^we uncheck on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_uncheck_action_on_page(page, element, locator) -> None:
    _page_common(page).check(page, element, locator)


@then(parsers.re(r"^we hover on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_hover_action_on_page(page, element, locator) -> None:
    _page_common(page).hover(page, element, locator)


@then(parsers.re(r'^we type on page (?P<element>.*?) locator (?P<locator>.*?) value "(?P<value>.*?)"$'))
def we_type_value_on_page(page, element, locator, value) -> None:
    _page_common(page).type(page, element, locator, value)


@then(parsers.re(r"^we scroll on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_scroll_to_locator_on_page(page, element, locator) -> None:
    _page_common(page).scroll(page, element, locator)


@then(
    parsers.re(
        r'^we clear value on page (?P<element>.*?) locator (?P<locator>.*?) value "(?P<value>.*?)"$'
    )
)
def we_clear_value_on_page(page, element, locator) -> None:
    _page_common(page).clear(page, element, locator)


@then(
    parsers.re(r"^we verify on page (?P<element>.*?) of locator (?P<locator>.*?) is visible$")
)
def we_verify_on_page_locator_is_visible(page, element, locator) -> None:
    _page_common(page).isvisible(page, element, locator)


@then(
    parsers.re(r"^we verify on page (?P<element>.*?) of locator (?P<locator>.*?) is checked$")
)
def we_verify_on_page_locator_is_checked(page, element, locator) -> None:
    _page_common(page).ischecked(page, element, locator)


@then(
    parsers.re(r"^we verify on page (?P<element>.*?) of locator (?P<locator>.*?) is enabled")
)
def we_verify_on_page_locator_is_enabled(page, element, locator) -> None:
    _page_common(page).isenabled(page, element, locator)


@then(
    parsers.re(r"^we verify on page (?P<element>.*?) of locator (?P<locator>.*?) is existed")
)
def we_verify_on_page_locator_is_existed(page, element, locator) -> None:
    _page_common(page).exists(page, element, locator)


@then(
    parsers.re(
        r'^we contain on page (?P<element>.*?) of locator (?P<locator>.*?) value "(?P<value>.*?)"$'
    )
)
def we_contain_on_page_locator_value(page, element, locator, value) -> None:
    _page_common(page).contain(page, element, locator, value)


@then(parsers.re(r"^we get text on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_get_text_on_page(page, element, locator) -> None:
    _page_common(page).gettext(page, element, locator)


@then(parsers.re(r"^we get value on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_get_value_on_page(page, element, locator) -> None:
    _page_common(page).getvalue(page, element, locator)


@then(
    parsers.re(
        r'^we has value on page (?P<element>.*?) of locator (?P<locator>.*?) value "(?P<value>.*?)"$'
    )
)
def we_has_value_on_page_locator_value(page, element, locator, value) -> None:
    _page_common(page).hasvalue(page, element, locator, value)


@then(parsers.re(r"^we get list of elements on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_get_list_of_elements_on_page(page, element, locator) -> None:
    _page_common(page).get_list_of_elements(page, element, locator)


@then(
    parsers.re(r"^we get text of elements on page (?P<element>.*?) locator (?P<locator>.*?)$")
)
def we_get_text_of_elements_on_page(page, element, locator) -> None:
    value = _page_common(page).gettext(page, element, locator)
    print(f"Value: {value}")


@when(parsers.re(r"we click radio on page (?P<element>.*?) list locator (?P<locator>.*?)$"))
def click_radio_on_page(page, element, locator) -> None:
    _page_common(page).click_radio_button(page, element, locator)


@then(
    parsers.re(
        r'^we capture screenshot on page (?P<element>.*?) locator (?P<locator>.*?) name "(?P<name>.*?)"$'
    )
)
def we_capture_screenshot_on_page(page, element, locator, name) -> None:
    file_path = f"test-output/screenshots/{name}.png"
    _page_common(page).screenshot(page, element, locator, file_path)


@then(
    parsers.re(
        r'^we press on page (?P<element>.*?) locator (?P<locator>.*?) key "(?P<value>.*?)" keyboard$'
    )
)
def we_press_on_page_key(page, element, locator, value) -> None:
    _page_common(page).press(page, element, locator, value)


@then(parsers.re(r"^we click download on page (?P<element>.*?) locator (?P<locator>.*?)$"))
def we_download_on_page_key(page, element, locator) -> None:
    file_path = config.get_value("downloadDocument")
    _page_common(page).download(page, element, locator, file_path or "")


@given(parsers.re(r"^get title of page$"))
def get_title_of_page(page) -> None:
    title = page.title()
    logger.info("Page title: %s", title)


@then(parsers.re(r"^we wait for some time$"))
def we_wait_for_some_time(page) -> None:
    time.sleep(3)


@then(parsers.re(r"^Stop Execution"))
def stop_execution(page) -> None:
    time.sleep(30)
