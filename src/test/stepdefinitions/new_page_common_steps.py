"""Gherkin step definitions for new-page and nested frame interactions."""

from __future__ import annotations

from playwright.sync_api import Page
from pytest_bdd import parsers

from stepdefinitions.step_binding import keyword_step

from ptaf.ui.frame_common import FrameCommonMethods
from ptaf.ui.page_common import PageCommonMethods

_new_page: Page | None = None
_I_FRAME = "iframe[name='iframeApplicationContent']"
_I_FRAME_2 = ""
_I_FRAME_3 = ""
_POP_FRAME = '//*[@id="AcceptUIContainer"]/iframe'


def _get_new_page(page: Page) -> Page:
    global _new_page
    if _new_page is None:
        with page.expect_popup() as popup_info:
            page.get_by_role("button", name="New Tab").click()
        _new_page = popup_info.value
    return _new_page


def _page_common(page: Page) -> PageCommonMethods:
    return PageCommonMethods(_get_new_page(page))


def _frame_common(page: Page) -> FrameCommonMethods:
    return FrameCommonMethods(_get_new_page(page))


@keyword_step(parsers.parse("we click on new page {element} locator {locator}"))
def we_click_action_new_on_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).click(new_page, element, locator)


@keyword_step(parsers.parse("we double click on new page {element} locator {locator}"))
def we_double_click_action_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).dblclick(new_page, element, locator)


@keyword_step(parsers.parse('we enter value on new page {element} locator {locator} value "{value}"'))
def we_enter_value_on_new_page(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).fill(new_page, element, locator, value)


@keyword_step(parsers.parse('we select on new page {element} locator {locator} value "{value}"'))
def we_select_value_on_new_page(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).select(new_page, element, locator, value)


@keyword_step(parsers.parse("we check on new page {element} locator {locator}"))
def we_check_action_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).check(new_page, element, locator)


@keyword_step(parsers.parse("we uncheck on new page {element} locator {locator}"))
def we_uncheck_action_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).uncheck(new_page, element, locator)


@keyword_step(parsers.parse("we hover on new page {element} locator {locator}"))
def we_hover_action_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).hover(new_page, element, locator)


@keyword_step(parsers.parse('we type on new page {element} locator {locator} value "{value}"'))
def we_type_value_on_new_page(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).type(new_page, element, locator, value)


@keyword_step(parsers.parse("we scroll on new page {element} locator {locator}"))
def we_scroll_to_locator_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).scroll(new_page, element, locator)


@keyword_step(parsers.parse('we clear value on new page {element} locator {locator} value "{value}"'))
def we_clear_value_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).clear(new_page, element, locator)


@keyword_step(parsers.parse("we verify on new page {element} of locator {locator} is visible"))
def we_verify_on_new_page_locator_is_visible(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).isvisible(new_page, element, locator)


@keyword_step(parsers.parse("we verify on new page {element} of locator {locator} is checked"))
def we_verify_on_new_page_locator_is_checked(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).ischecked(new_page, element, locator)


@keyword_step(parsers.parse("we verify on new page {element} of locator {locator} is enabled"))
def we_verify_on_new_page_locator_is_enabled(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).isenabled(new_page, element, locator)


@keyword_step(parsers.parse("we get value on new page {element} locator {locator}"))
def we_get_value_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).getvalue(new_page, element, locator)


@keyword_step(parsers.parse('we verify element has value on new page {element} of locator {locator} value "{value}"'))
def we_has_value_on_new_page_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).hasvalue(new_page, element, locator, value)


@keyword_step(parsers.parse("we verify on new page {element} of locator {locator} is existed"))
def we_verify_on_new_page_locator_is_existed(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).exists(new_page, element, locator)


@keyword_step(parsers.parse('we contain on new page {element} of locator {locator} value "{value}"'))
def we_contain_on_new_page_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).contain(new_page, element, locator, value)


@keyword_step(parsers.parse("we get text on new page {element} locator {locator}"))
def we_get_text_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).gettext(new_page, element, locator)


@keyword_step(parsers.parse('we capture screenshot on new page {element} locator {locator} name "{name}"'))
def we_capture_screenshot_on_new_page(page, element, locator, name) -> None:
    new_page = _get_new_page(page)
    file_path = f"test-output/screenshots/{name}.png"
    _page_common(page).screenshot(new_page, element, locator, file_path)


@keyword_step(parsers.parse('we press on new page {element} locator {locator} key "{value}" keyboard'))
def we_press_on_new_page_key(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _page_common(page).press(new_page, element, locator, value)


@keyword_step(parsers.parse("we click radio on new page {element} list locator {locator}"))
def click_radio_on_new_page(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _page_common(page).click_radio_button(new_page, element, locator)


@keyword_step(parsers.parse("we click on plad frame {element} locator {locator}"))
def we_click_action_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).click(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we double click on plad frame {element} locator {locator}"))
def we_double_click_action_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).dblclick(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we enter value on plad frame {element} locator {locator} value "{value}"'))
def we_enter_value_on_plad_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).fill(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse('we select on plad frame {element} locator {locator} value "{value}"'))
def we_select_value_on_plad_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).select(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we check on plad frame {element} locator {locator}"))
def we_check_action_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).check(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we uncheck on plad frame {element} locator {locator}"))
def we_uncheck_action_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).uncheck(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we hover on plad frame {element} locator {locator}"))
def we_hover_action_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).hover(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we type on plad frame {element} locator {locator} value "{value}"'))
def we_type_value_on_plad_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).type(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we scroll on plad frame {element} locator {locator}"))
def we_scroll_to_locator_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).scroll(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we clear value on plad frame {element} locator {locator} value "{value}"'))
def we_clear_value_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).clear(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on plad frame {element} of locator {locator} is visible"))
def we_verify_on_plad_frame_locator_is_visible(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).isvisible(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on plad frame {element} of locator {locator} is checked"))
def we_verify_on_plad_frame_locator_is_checked(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).ischecked(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we verify on plad frame {element} of locator {locator} is enabled"))
def we_verify_on_plad_frame_locator_is_enabled(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).isenabled(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse("we get value on plad frame {element} locator {locator}"))
def we_get_value_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).getvalue(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we verify element has value on plad frame {element} of locator {locator} value "{value}"'))
def we_has_value_on_plad_frame_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).hasvalue(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we verify on plad frame {element} of locator {locator} is existed"))
def we_verify_on_plad_frame_locator_is_existed(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).exists(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we contain on plad frame {element} of locator {locator} value "{value}"'))
def we_contain_on_plad_frame_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).contain(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we get text on plad frame {element} locator {locator}"))
def we_get_text_on_plad_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).gettext(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator
    )


@keyword_step(parsers.parse('we capture screenshot on plad frame {element} locator {locator} name "{name}"'))
def we_capture_screenshot_on_plad_frame(page, element, locator, name) -> None:
    new_page = _get_new_page(page)
    file_path = f"test-output/screenshots/{name}.png"
    _frame_common(page).screenshot(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, file_path
    )


@keyword_step(parsers.parse('we press on plad frame {element} locator {locator} key "{value}" keyboard'))
def we_press_on_plad_frame_key(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).press(
        new_page, _I_FRAME, _I_FRAME_2, _I_FRAME_3, element, locator, value
    )


@keyword_step(parsers.parse("we click on pop frame {element} locator {locator}"))
def we_click_action_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).click(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we double click on pop frame {element} locator {locator}"))
def we_double_click_action_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).dblclick(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we enter value on pop frame {element} locator {locator} value "{value}"'))
def we_enter_value_on_pop_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).fill(
        new_page, _POP_FRAME, None, None, element, locator, value
    )


@keyword_step(parsers.parse('we select on pop frame {element} locator {locator} value "{value}"'))
def we_select_value_on_pop_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).select(
        new_page, _POP_FRAME, None, None, element, locator, value
    )


@keyword_step(parsers.parse("we check on pop frame {element} locator {locator}"))
def we_check_action_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).check(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we uncheck on pop frame {element} locator {locator}"))
def we_uncheck_action_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).uncheck(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we hover on pop frame {element} locator {locator}"))
def we_hover_action_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).hover(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we type on pop frame {element} locator {locator} value "{value}"'))
def we_type_value_on_pop_frame(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).type(
        new_page, _POP_FRAME, None, None, element, locator, value
    )


@keyword_step(parsers.parse("we scroll on pop frame {element} locator {locator}"))
def we_scroll_to_locator_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).scroll(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we clear value on pop frame {element} locator {locator} value "{value}"'))
def we_clear_value_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).clear(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we verify on pop frame {element} of locator {locator} is visible"))
def we_verify_on_pop_frame_locator_is_visible(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).isvisible(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we verify on pop frame {element} of locator {locator} is checked"))
def we_verify_on_pop_frame_locator_is_checked(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).ischecked(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we verify on pop frame {element} of locator {locator} is enabled"))
def we_verify_on_pop_frame_locator_is_enabled(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).isenabled(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse("we get value on pop frame {element} locator {locator}"))
def we_get_value_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).getvalue(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we verify element has value on pop frame {element} of locator {locator} value "{value}"'))
def we_has_value_on_pop_frame_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).hasvalue(
        new_page, _POP_FRAME, None, None, element, locator, value
    )


@keyword_step(parsers.parse("we verify on pop frame {element} of locator {locator} is existed"))
def we_verify_on_pop_frame_locator_is_existed(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).exists(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we contain on pop frame {element} of locator {locator} value "{value}"'))
def we_contain_on_pop_frame_locator_value(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).contain(
        new_page, _POP_FRAME, None, None, element, locator, value
    )


@keyword_step(parsers.parse("we get text on pop frame {element} locator {locator}"))
def we_get_text_on_pop_frame(page, element, locator) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).gettext(
        new_page, _POP_FRAME, None, None, element, locator
    )


@keyword_step(parsers.parse('we capture screenshot on pop frame {element} locator {locator} name "{name}"'))
def we_capture_screenshot_on_pop_frame(page, element, locator, name) -> None:
    new_page = _get_new_page(page)
    file_path = f"test-output/screenshots/{name}.png"
    _frame_common(page).screenshot(
        new_page, _POP_FRAME, None, None, element, locator, file_path
    )


@keyword_step(parsers.parse('we press on pop frame {element} locator {locator} key "{value}" keyboard'))
def we_press_on_pop_frame_key(page, element, locator, value) -> None:
    new_page = _get_new_page(page)
    _frame_common(page).press(
        new_page, _POP_FRAME, None, None, element, locator, value
    )
