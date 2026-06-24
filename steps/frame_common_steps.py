"""Gherkin step definitions for frame interactions (FrameCommonSteps.java)."""

from __future__ import annotations

from pytest_bdd import parsers

from steps.step_binding import keyword_step

from ptaf.ui.frame_common import FrameCommonMethods
from ptaf.utils import config

_I_FRAME = "#frame1"
_I_FRAME_2: str | None = None
_I_FRAME_3: str | None = None


def _frame_common_methods(page) -> FrameCommonMethods:
    return FrameCommonMethods(page)


@keyword_step(parsers.parse("we navigate to {config_key} url"))
def we_navigate_to_url(page, config_key) -> None:
    base_url = config.get_base_url(config_key)
    page.goto(base_url or "")


@keyword_step(parsers.parse("we click on frame {element} locator {locator}"))
def we_click_action_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).click(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we double click on frame {element} locator {locator}"))
def we_double_click_action_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).dblclick(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we enter value on frame {element} locator {locator} value "{value}"'))
def we_enter_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods(page).fill(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse('we select on frame {element} locator {locator} value "{value}"'))
def we_select_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods(page).select(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we check on frame {element} locator {locator}"))
def we_check_action_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).check(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we uncheck on frame {element} locator {locator}"))
def we_uncheck_action_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).uncheck(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we hover on frame {element} locator {locator}"))
def we_hover_action_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).hover(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we type on frame {element} locator {locator} value "{value}"'))
def we_type_value_on_frame(page, element, locator, value) -> None:
    _frame_common_methods(page).type(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we scroll on frame {element} locator {locator}"))
def we_scroll_to_locator_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).scroll(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we clear value on frame {element} locator {locator} value "{value}"'))
def we_clear_value_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).clear(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on frame {element} of locator {locator} is visible"))
def we_verify_on_frame_locator_is_visible(page, element, locator) -> None:
    _frame_common_methods(page).isvisible(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on frame {element} of locator {locator} is checked"))
def we_verify_on_frame_locator_is_checked(page, element, locator) -> None:
    _frame_common_methods(page).ischecked(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on frame {element} of locator {locator} is enabled"))
def we_verify_on_frame_locator_is_enabled(page, element, locator) -> None:
    _frame_common_methods(page).isenabled(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on frame {element} of locator {locator} is existed"))
def we_verify_on_frame_locator_is_existed(page, element, locator) -> None:
    _frame_common_methods(page).exists(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we contain on frame {element} of locator {locator} value "{value}"'))
def we_contain_on_frame_locator_value(page, element, locator, value) -> None:
    _frame_common_methods(page).contain(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we get text on frame {element} locator {locator}"))
def we_get_text_on_frame(page, element, locator) -> None:
    value = _frame_common_methods(page).gettext(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )
    print(f"Value: {value}")


@keyword_step(parsers.parse('we has value on frame {element} of locator {locator} value "{value}"'))
def we_has_value_on_frame_locator_value(page, element, locator, value) -> None:
    _frame_common_methods(page).hasvalue(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we get list of elements on frame {element} locator {locator}"))
def we_get_list_of_elements_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).gettext(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we click radio on frame {element} list locator {locator}"))
def click_radio_on_frame(page, element, locator) -> None:
    _frame_common_methods(page).click_radio_button(
        page, _I_FRAME, element, locator
    )


@keyword_step(parsers.parse('we capture screenshot on frame {element} locator {locator} name "{name}"'))
def we_capture_screenshot_on_frame(page, element, locator, name) -> None:
    file_path = f"test-output/screenshots/{name}.png"
    _frame_common_methods(page).screenshot(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, file_path
    )


@keyword_step(parsers.parse('we press on frame {element} locator {locator} key "{value}" keyboard'))
def we_press_on_frame_key(page, element, locator, value) -> None:
    _frame_common_methods(page).press(
        page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )
