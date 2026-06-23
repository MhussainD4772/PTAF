"""Unit tests for element locator helper and locator handler."""

from __future__ import annotations

from unittest.mock import MagicMock

import pytest
from playwright.sync_api import FrameLocator, Locator, Page

from ptaf.ui.locator_helper import ElementLocatorHelper
from ptaf.ui.locator_handler import LocatorHandler

PAGE_FRAME_LOCATOR_TYPES = [
    "CSS",
    "TAG",
    "XPATH",
    "BUTTON",
    "LINKTEXT",
    "OPTION",
    "TEXTBOX",
    "CHECKBOX",
    "RADIOBUTTON",
    "DROPDOWN",
    "IMAGE",
    "HEADING",
    "TAB",
    "LIST",
    "LISTBOX",
    "LISTITEM",
    "TABLE",
    "ROW",
    "CELL",
    "BUTTONSUBMIT",
    "SLIDER",
    "SPINBUTTON",
    "PROGRESSBAR",
    "ALERT",
    "ALERTDIALOG",
    "DIALOG",
    "NAVIGATION",
    "MENU",
    "MENUITEM",
    "MENUITEMCHECKBOX",
    "MENUITEMRADIO",
    "TREE",
    "TREEITEM",
    "GRID",
    "GRIDCELL",
    "SEPARATOR",
    "SWITCH",
    "STATUS",
    "BANNER",
    "FOOTER",
    "CONTENTINFO",
    "MAIN",
    "COMPLEMENTARY",
    "REGION",
    "ARTICLE",
    "FORM",
    "LOG",
    "MARQUEE",
    "TIMER",
    "TOOLTIP",
    "TOOLBAR",
    "PRESENTATION",
    "FIGURE",
    "TEXT",
    "ROLE",
    "ALTTEXT",
    "TITLE",
    "PLACEHOLDER",
    "LABEL",
    "TESTID",
    "ID",
    "NAME",
    "CLASS",
]

CHAINED_LOCATOR_TYPES = [
    "CSS",
    "TAG",
    "XPATH",
    "BUTTON",
    "LINKTEXT",
    "OPTION",
    "TEXTBOX",
    "CHECKBOX",
    "RADIOBUTTON",
    "DROPDOWN",
    "IMAGE",
    "HEADING",
    "TAB",
    "LIST",
    "LISTBOX",
    "LISTITEM",
    "TABLE",
    "ROW",
    "CELL",
    "GRID",
    "GRIDCELL",
    "TEXT",
    "ROLE",
    "ALTTEXT",
    "TITLE",
    "PLACEHOLDER",
    "LABEL",
    "TESTID",
]

PAGE_ONLY_TYPES = sorted(set(PAGE_FRAME_LOCATOR_TYPES) - set(CHAINED_LOCATOR_TYPES))


class TestElementLocatorHelperParsing:
    @pytest.mark.parametrize(
        ("locator_value", "expected_type", "expected_locator"),
        [
            ("ID_field_name", "ID", "field_name"),
            ("CSS_#fname", "CSS", "#fname"),
            ("CSS_button", "CSS", "button"),
            ("XPATH_//div", "XPATH", "//div"),
            ("noUnderscore", "", ""),
            ("TYPE_", "TYPE", ""),
        ],
    )
    def test_parse_locator_value(
        self,
        locator_value: str,
        expected_type: str,
        expected_locator: str,
    ) -> None:
        helper = ElementLocatorHelper()
        assert helper.get_locator_type(locator_value) == expected_type
        assert helper.get_locator(locator_value) == expected_locator

    def test_get_element_reads_yaml_path(self) -> None:
        helper = ElementLocatorHelper()
        assert (
            helper.get_element("personal", "first_name_field") == "CSS_#fname"
        )


class TestLocatorHandlerPageContext:
    @pytest.fixture
    def handler(self) -> LocatorHandler:
        return LocatorHandler()

    @pytest.fixture
    def page(self) -> MagicMock:
        mock_page = MagicMock(spec=Page)
        mock_page.locator.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_role.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_text.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_alt_text.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_title.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_placeholder.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_label.return_value = MagicMock(name="resolved_locator")
        mock_page.get_by_test_id.return_value = MagicMock(name="resolved_locator")
        return mock_page

    @pytest.mark.parametrize("locator_type", PAGE_FRAME_LOCATOR_TYPES)
    def test_known_page_types_resolve(
        self, handler: LocatorHandler, page: MagicMock, locator_type: str
    ) -> None:
        result = handler.get_locator_for_type(locator_type, page, "sample")
        assert result is page.locator.return_value or result in {
            page.get_by_role.return_value,
            page.get_by_text.return_value,
            page.get_by_alt_text.return_value,
            page.get_by_title.return_value,
            page.get_by_placeholder.return_value,
            page.get_by_label.return_value,
            page.get_by_test_id.return_value,
        }

    @pytest.mark.parametrize("locator_type", ["css", "button", "testid"])
    def test_page_type_matching_is_case_insensitive(
        self, handler: LocatorHandler, page: MagicMock, locator_type: str
    ) -> None:
        handler.get_locator_for_type(locator_type, page, "sample")
        assert page.locator.called or page.get_by_role.called or page.get_by_test_id.called

    def test_css_uses_page_locator(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        handler.get_locator_for_type("CSS", page, "#submit")
        page.locator.assert_called_once_with("#submit")

    def test_id_prefixes_hash(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        handler.get_locator_for_type("ID", page, "field_name")
        page.locator.assert_called_once_with("#field_name")

    def test_option_uses_exact_name(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        handler.get_locator_for_type("OPTION", page, "Yes")
        page.get_by_role.assert_called_once_with("option", name="Yes", exact=True)

    def test_buttonsubmit_uses_pressed(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        handler.get_locator_for_type("BUTTONSUBMIT", page, "Save")
        page.get_by_role.assert_called_once_with("button", name="Save", pressed=True)

    def test_role_uses_uppercase_role_name(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        handler.get_locator_for_type("ROLE", page, "heading")
        page.get_by_role.assert_called_once_with("HEADING")

    def test_unknown_page_type_raises(
        self, handler: LocatorHandler, page: MagicMock
    ) -> None:
        with pytest.raises(ValueError, match="Unknown locator type: BOGUS"):
            handler.get_locator_for_type("BOGUS", page, "sample")


class TestLocatorHandlerFrameContext:
    @pytest.fixture
    def handler(self) -> LocatorHandler:
        return LocatorHandler()

    @pytest.fixture
    def frame(self) -> MagicMock:
        mock_frame = MagicMock(spec=FrameLocator)
        mock_frame.locator.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_role.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_text.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_alt_text.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_title.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_placeholder.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_label.return_value = MagicMock(name="resolved_locator")
        mock_frame.get_by_test_id.return_value = MagicMock(name="resolved_locator")
        return mock_frame

    @pytest.mark.parametrize("locator_type", PAGE_FRAME_LOCATOR_TYPES)
    def test_known_frame_types_resolve(
        self, handler: LocatorHandler, frame: MagicMock, locator_type: str
    ) -> None:
        result = handler.get_locator_for_type(locator_type, frame, "sample")
        assert result is frame.locator.return_value or result in {
            frame.get_by_role.return_value,
            frame.get_by_text.return_value,
            frame.get_by_alt_text.return_value,
            frame.get_by_title.return_value,
            frame.get_by_placeholder.return_value,
            frame.get_by_label.return_value,
            frame.get_by_test_id.return_value,
        }

    def test_unknown_frame_type_raises(
        self, handler: LocatorHandler, frame: MagicMock
    ) -> None:
        with pytest.raises(ValueError, match="Unknown locator type: BOGUS"):
            handler.get_locator_for_type("BOGUS", frame, "sample")


class TestLocatorHandlerChainedContext:
    @pytest.fixture
    def handler(self) -> LocatorHandler:
        return LocatorHandler()

    @pytest.fixture
    def base_locator(self) -> MagicMock:
        mock_locator = MagicMock(spec=Locator)
        mock_locator.locator.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_role.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_text.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_alt_text.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_title.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_placeholder.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_label.return_value = MagicMock(name="resolved_locator")
        mock_locator.get_by_test_id.return_value = MagicMock(name="resolved_locator")
        return mock_locator

    @pytest.mark.parametrize("locator_type", CHAINED_LOCATOR_TYPES)
    def test_known_chained_types_resolve(
        self,
        handler: LocatorHandler,
        base_locator: MagicMock,
        locator_type: str,
    ) -> None:
        result = handler.get_locator_for_type(locator_type, base_locator, "sample")
        assert result is base_locator.locator.return_value or result in {
            base_locator.get_by_role.return_value,
            base_locator.get_by_text.return_value,
            base_locator.get_by_alt_text.return_value,
            base_locator.get_by_title.return_value,
            base_locator.get_by_placeholder.return_value,
            base_locator.get_by_label.return_value,
            base_locator.get_by_test_id.return_value,
        }

    @pytest.mark.parametrize("locator_type", PAGE_ONLY_TYPES)
    def test_page_only_types_rejected_when_chained(
        self,
        handler: LocatorHandler,
        base_locator: MagicMock,
        locator_type: str,
    ) -> None:
        with pytest.raises(
            ValueError, match=f"Unknown chained locator type: {locator_type}"
        ):
            handler.get_locator_for_type(locator_type, base_locator, "sample")

    def test_unknown_chained_type_raises(
        self, handler: LocatorHandler, base_locator: MagicMock
    ) -> None:
        with pytest.raises(ValueError, match="Unknown chained locator type: BOGUS"):
            handler.get_locator_for_type("BOGUS", base_locator, "sample")
