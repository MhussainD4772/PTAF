"""Gherkin step definitions for frame interactions (FrameCommonSteps.java)."""

from __future__ import annotations

from playwright.sync_api import Page
from pytest_bdd import parsers

from steps.step_binding import step

from ptaf.ui.frame_common import FrameCommonMethods
from ptaf.utils import config

_iframe_page: Page | None = None
_I_FRAME = "#frame1"
_I_FRAME_2: str | None = None
_I_FRAME_3: str | None = None


def _frame_common_methods() -> FrameCommonMethods:
    return FrameCommonMethods(_iframe_page)


@step(parsers.parse("we navigate to {config_key} url"))
def we_navigate_to_url(page, config_key) -> None:
    base_url = config.get_base_url(config_key)
    page.goto(base_url or "")


@step(parsers.re(r"^we click on frame (.*?) locator (.*?)$"))
def we_click_action_on_frame(page, element, locator) -> None:
    _frame_common_methods().click(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(parsers.re(r"^we double click on frame (.*?) locator (.*?)$"))
def we_double_click_action_on_frame(page, element, locator) -> None:
    _frame_common_methods().dblclick(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(
        r'^we enter value on frame (.*?) locator (.*?) value "(.*?)"$'
    )
)
def we_enter_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods().fill(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@step(
    parsers.re(r'^we select on frame (.*?) locator (.*?) value "(.*?)"$')
)
def we_select_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods().select(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@step(parsers.re(r"^we check on frame (.*?) locator (.*?)$"))
def we_check_action_on_frame(page, element, locator) -> None:
    _frame_common_methods().check(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(parsers.re(r"^we uncheck on frame (.*?) locator (.*?)$"))
def we_uncheck_action_on_frame(page, element, locator) -> None:
    _frame_common_methods().check(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(parsers.re(r"^we hover on frame (.*?) locator (.*?)$"))
def we_hover_action_on_frame(page, element, locator) -> None:
    _frame_common_methods().hover(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(r'^we type on frame (.*?) locator (.*?) value "(.*?)"$')
)
def we_type_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods().type(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@step(parsers.re(r"^we scroll on frame (.*?) locator (.*?)$"))
def we_scroll_to_locator_on_frame(page, element, locator) -> None:
    _frame_common_methods().scroll(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(
        r'^we clear value on frame (.*?) locator (.*?) value "(.*?)"$'
    )
)
def we_clear_value_on_frame(page, element, locator) -> None:
    _frame_common_methods().clear(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(r"^we verify on frame (.*?) of locator (.*?) is visible$")
)
def we_verify_on_frame_locator_is_visible(page, element, locator) -> None:
    _frame_common_methods().isvisible(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(r"^we verify on frame (.*?) of locator (.*?) is checked$")
)
def we_verify_on_frame_locator_is_checked(page, element, locator) -> None:
    _frame_common_methods().ischecked(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(r"^we verify on frame (.*?) of locator (.*?) is enabled")
)
def we_verify_on_frame_locator_is_enabled(page, element, locator) -> None:
    _frame_common_methods().isenabled(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(r"^we verify on frame (.*?) of locator (.*?) is existed")
)
def we_verify_on_frame_locator_is_existed(page, element, locator) -> None:
    _frame_common_methods().exists(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(
    parsers.re(
        r'^we contain on frame (.*?) of locator (.*?) value "(.*?)"$'
    )
)
def we_contain_on_frame_locator_value(page, element, locator, value) -> None:
    _frame_common_methods().contain(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@step(parsers.re(r"^we get text on frame (.*?) locator (.*?)$"))
def we_get_text_on_frame(page, element, locator) -> None:
    value = _frame_common_methods().gettext(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )
    print(f"Value: {value}")


@step(
    parsers.re(
        r'^we has value on frame (.*?) of locator (.*?) value "(.*?)"$'
    )
)
def we_has_value_on_frame_locator_value(page, element, locator, value) -> None:
    _frame_common_methods().hasvalue(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@step(
    parsers.re(r"^we get list of elements on frame (.*?) locator (.*?)$")
)
def we_get_list_of_elements_on_frame(page, element, locator) -> None:
    _frame_common_methods().gettext(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@step(parsers.re(r"we click radio on frame (.*?) list locator (.*?)$"))
def click_radio_on_frame(page, element, locator) -> None:
    _frame_common_methods().click_radio_button(
        _iframe_page, _I_FRAME, element, locator
    )


@step(
    parsers.re(
        r'^we capture screenshot on frame (.*?) locator (.*?) name "(.*?)"$'
    )
)
def we_capture_screenshot_on_frame(page, element, locator, name) -> None:
    file_path = f"test-output/screenshots/{name}.png"
    _frame_common_methods().screenshot(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, file_path
    )


@step(
    parsers.re(
        r'^we press on frame (.*?) locator (.*?) key "(.*?)" keyboard$'
    )
)
def we_press_on_frame_key(page, element, locator, value) -> None:
    _frame_common_methods().press(
        _iframe_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )
