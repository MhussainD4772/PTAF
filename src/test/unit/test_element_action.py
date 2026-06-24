"""Unit tests for element locator chaining executor."""

from __future__ import annotations

from unittest.mock import MagicMock, patch

import pytest
from playwright.sync_api import FrameLocator, Locator, Page

from ptaf.ui import element_action
from ptaf.ui.element_action import get_locator


class TestGetLocator:
    @pytest.fixture
    def page(self) -> MagicMock:
        mock_page = MagicMock(spec=Page)
        row_locator = MagicMock(spec=Locator, name="row_locator")
        mock_page.locator.return_value = row_locator
        mock_page.get_by_role.return_value = row_locator
        mock_page.frame_locator.return_value = MagicMock(spec=FrameLocator)
        return mock_page

    @pytest.fixture
    def frame_locator(self) -> MagicMock:
        mock_frame = MagicMock(spec=FrameLocator)
        mock_frame.locator.return_value = MagicMock(spec=Locator, name="frame_locator")
        mock_frame.get_by_role.return_value = MagicMock(
            spec=Locator, name="frame_locator"
        )
        mock_frame.frame_locator.return_value = MagicMock(spec=FrameLocator)
        return mock_frame

    def test_chained_key_from_yaml_fixture(self, page: MagicMock) -> None:
        row_locator = MagicMock(spec=Locator, name="row_locator")
        edit_locator = MagicMock(spec=Locator, name="edit_locator")
        page.get_by_role.return_value = row_locator
        row_locator.get_by_role.return_value = edit_locator

        result = get_locator("user_table", "edit", page=page)

        assert result is edit_locator
        page.get_by_role.assert_called_once_with("row", name="Jane Doe")
        row_locator.get_by_role.assert_called_once_with("button", name="Edit")

    def test_single_key_works(self, page: MagicMock) -> None:
        submit_locator = MagicMock(spec=Locator, name="submit_locator")
        page.locator.return_value = submit_locator

        result = get_locator("user_table", "single", page=page)

        assert result is submit_locator
        page.locator.assert_called_once_with("#submit")
        page.frame_locator.assert_not_called()

    def test_frame_locator_context_path(self, frame_locator: MagicMock) -> None:
        row_locator = MagicMock(spec=Locator, name="row_locator")
        edit_locator = MagicMock(spec=Locator, name="edit_locator")
        frame_locator.get_by_role.return_value = row_locator
        row_locator.get_by_role.return_value = edit_locator

        result = get_locator(
            "user_table",
            "edit",
            page=MagicMock(spec=Page),
            frame_locator=frame_locator,
        )

        assert result is edit_locator
        frame_locator.get_by_role.assert_called_once_with("row", name="Jane Doe")
        row_locator.get_by_role.assert_called_once_with("button", name="Edit")
        frame_locator.frame_locator.assert_not_called()

    def test_nested_iframe_context_path(self, page: MagicMock) -> None:
        iframe_1 = MagicMock(spec=FrameLocator, name="iframe_1")
        iframe_2 = MagicMock(spec=FrameLocator, name="iframe_2")
        iframe_3 = MagicMock(spec=FrameLocator, name="iframe_3")
        row_locator = MagicMock(spec=Locator, name="row_locator")
        edit_locator = MagicMock(spec=Locator, name="edit_locator")

        page.frame_locator.return_value = iframe_1
        iframe_1.frame_locator.return_value = iframe_2
        iframe_2.frame_locator.return_value = iframe_3
        iframe_3.get_by_role.return_value = row_locator
        row_locator.get_by_role.return_value = edit_locator

        result = get_locator(
            "user_table",
            "edit",
            page=page,
            iframe="#outer",
            iframe_2="#middle",
            iframe_3="#inner",
        )

        assert result is edit_locator
        page.frame_locator.assert_called_once_with("#outer")
        iframe_1.frame_locator.assert_called_once_with("#middle")
        iframe_2.frame_locator.assert_called_once_with("#inner")
        iframe_3.get_by_role.assert_called()

    def test_malformed_type_raises_exact_error_message(
        self, page: MagicMock
    ) -> None:
        with patch.object(
            element_action._locator_helper,
            "get_element",
            return_value="BOGUS_sample",
        ):
            with pytest.raises(
                RuntimeError,
                match="Failed to get locator for: 'BOGUS_sample'",
            ):
                get_locator("user_table", "edit", page=page)
