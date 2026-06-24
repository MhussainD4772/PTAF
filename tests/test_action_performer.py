"""Unit tests for ActionPerformer with mock locators."""

from __future__ import annotations

from pathlib import Path
from unittest.mock import MagicMock, patch

import pytest
from playwright.sync_api import Locator, Page

from ptaf.ui.action_performer import ActionPerformer


@pytest.fixture
def page() -> MagicMock:
    mock_page = MagicMock(spec=Page)
    mock_page.expect_download.return_value.__enter__ = MagicMock(
        return_value=MagicMock(
            value=MagicMock(
                path=MagicMock(return_value="/tmp/downloaded.bin"),
                save_as=MagicMock(),
            )
        )
    )
    mock_page.expect_download.return_value.__exit__ = MagicMock(return_value=False)
    mock_page.expect_file_chooser.return_value.__enter__ = MagicMock(
        return_value=MagicMock(value=MagicMock())
    )
    mock_page.expect_file_chooser.return_value.__exit__ = MagicMock(return_value=False)
    return mock_page


@pytest.fixture
def locator(page: MagicMock) -> MagicMock:
    mock_locator = MagicMock(spec=Locator)
    first = MagicMock(spec=Locator)
    mock_locator.first = first
    mock_locator.page = page
    mock_locator.count.return_value = 1
    first.text_content.return_value = "hello world"
    first.input_value.return_value = "value"
    first.get_attribute.return_value = "class-a class-b"
    first.is_visible.return_value = True
    first.is_enabled.return_value = True
    first.is_checked.return_value = True
    first.is_disabled.return_value = False
    first.is_hidden.return_value = False
    return mock_locator


@pytest.fixture
def performer() -> ActionPerformer:
    return ActionPerformer()


class TestActionPerformerSingleElementActions:
    @pytest.mark.parametrize(
        ("action", "value", "method"),
        [
            ("click", None, "click"),
            ("fill", "text", "fill"),
            ("select", "option", "select_option"),
            ("check", None, "check"),
            ("uncheck", None, "uncheck"),
            ("hover", None, "hover"),
            ("type", "text", "type"),
            ("press", "Enter", "press"),
            ("dblclick", None, "dblclick"),
            ("tap", None, "tap"),
            ("focus", None, "focus"),
            ("clear", None, "clear"),
            ("dragstart", None, "dispatch_event"),
            ("dragend", None, "dispatch_event"),
        ],
    )
    def test_void_actions_call_first_locator_method(
        self,
        performer: ActionPerformer,
        page: MagicMock,
        locator: MagicMock,
        action: str,
        value: str | None,
        method: str,
    ) -> None:
        performer.perform_action(page, action, locator, value)
        called = getattr(locator.first, method)
        if method == "fill":
            called.assert_called_once_with(value)
        elif method == "select_option" and action == "select":
            called.assert_called_once_with(value)
        elif method == "type":
            called.assert_called_once_with(value)
        elif method == "press":
            called.assert_called_once_with(value)
        elif method == "dispatch_event":
            called.assert_called_once()
        else:
            called.assert_called_once()

    def test_selectmultiple_splits_values(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "selectmultiple", locator, "a,b,c")
        locator.first.select_option.assert_called_once_with(["a", "b", "c"])

    def test_rightclick_uses_right_button(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "rightclick", locator, None)
        locator.first.click.assert_called_once_with(button="right")

    def test_input_evaluates_value(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "input", locator, "abc")
        locator.first.evaluate.assert_called_once_with(
            "(element, val) => element.value = val", "abc"
        )

    def test_screenshot_uses_path(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "screenshot", locator, "/tmp/shot.png")
        locator.first.screenshot.assert_called_once_with(path=Path("/tmp/shot.png"))

    def test_scroll_evaluates(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "scroll", locator, None)
        locator.first.evaluate.assert_called_once()

    def test_blur_evaluates(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "blur", locator, None)
        locator.first.evaluate.assert_called_once_with("element => element.blur()")

    def test_drag_targets_second_locator(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        drop_target = MagicMock(spec=Locator)
        drop_target.first = MagicMock(spec=Locator)
        page.locator.return_value = drop_target
        performer.perform_action(page, "drag", locator, "#drop")
        page.locator.assert_called_once_with("#drop")
        locator.first.drag_to.assert_called_once_with(drop_target.first)

    def test_uploadfile_sets_input_files(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "uploadfile", locator, "/tmp/file.txt")
        locator.first.set_input_files.assert_called_once_with(Path("/tmp/file.txt"))

    def test_selectfile_alias(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "selectfile", locator, "/tmp/file.txt")
        locator.first.set_input_files.assert_called_once_with(Path("/tmp/file.txt"))

    def test_download_saves_file(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        result = performer.perform_action(page, "download", locator, "/tmp/out.bin")
        assert result == "/tmp/downloaded.bin"
        locator.first.click.assert_called_once()

    def test_file_chooser_for_upload(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "file_chooser_for_upload", locator, None)
        page.expect_file_chooser.assert_called_once()
        locator.first.click.assert_called_once()

    def test_setattribute_evaluates(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "setattribute", locator, "val")
        locator.first.evaluate.assert_called_once_with(
            "(el, val) => el.setAttribute('value', val)", "val"
        )

    def test_removeattribute_evaluates(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "removeattribute", locator, "disabled")
        locator.first.evaluate.assert_called_once_with(
            "(el, attr) => el.removeAttribute(attr)", "disabled"
        )

    def test_evaluate_passes_script(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "evaluate", locator, "el => el.id")
        locator.first.evaluate.assert_called_once_with("el => el.id")

    def test_waitforelement(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "waitforelement", locator, None)
        locator.first.wait_for.assert_called_once_with()

    def test_waitforstate(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        performer.perform_action(page, "waitforstate", locator, "VISIBLE")
        locator.first.wait_for.assert_called_once_with(state="visible")


class TestActionPerformerReturnActions:
    def test_getattribute(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        locator.first.get_attribute.return_value = "id-value"
        assert (
            performer.perform_action(page, "getattribute", locator, "id")
            == "id-value"
        )

    def test_gettext(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "gettext", locator, None) == "hello world"

    def test_getvalue(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "getvalue", locator, None) == "value"

    def test_hasvalue_passes(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "hasvalue", locator, "value") == "value"

    def test_hasvalue_fails(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        with pytest.raises(RuntimeError, match="Action failed: hasvalue"):
            performer.perform_action(page, "hasvalue", locator, "other")

    def test_isvisible(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "isvisible", locator, None) == "True"

    def test_hastext(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "hastext", locator, "hello") == "hello world"

    def test_hasclass(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "hasclass", locator, "class-a") == "True"

    def test_hasequalvalue(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "hasequalvalue", locator, "value") == "value"

    def test_isempty_fails_when_not_empty(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        with pytest.raises(RuntimeError, match="Action failed: isempty"):
            performer.perform_action(page, "isempty", locator, None)

    def test_waitfortext(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert (
            performer.perform_action(page, "waitfortext", locator, "hello")
            == "hello world"
        )

    def test_waitforvalue(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "waitforvalue", locator, "value") == "value"


class TestActionPerformerMultiElementActions:
    def test_exists(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assert performer.perform_action(page, "exists", locator, None) == "True"

    def test_not_exists(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        locator.count.return_value = 0
        assert performer.perform_action(page, "not_exists", locator, None) == "True"

    def test_unknown_action_raises(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        with pytest.raises(RuntimeError, match="Unknown action"):
            performer.perform_action(page, "bogus", locator, None)


class TestActionPerformerHelpers:
    def test_wait_for_locator(
        self, performer: ActionPerformer, locator: MagicMock
    ) -> None:
        performer.wait_for_locator(locator)
        locator.first.wait_for.assert_called_once_with(
            state="visible", timeout=60_000
        )

    def test_perform_action_with_return_delegates(
        self, performer: ActionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        with patch.object(
            performer, "perform_action", return_value="ok"
        ) as mock_perform:
            assert (
                performer.perform_action_with_return(page, "gettext", locator, None)
                == "ok"
            )
            mock_perform.assert_called_once_with(page, "gettext", locator, None)
