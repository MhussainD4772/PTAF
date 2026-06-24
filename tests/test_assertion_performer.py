"""Unit tests for AssertionPerformer with mock locators."""

from __future__ import annotations

import re
from unittest.mock import MagicMock, patch

import pytest
from playwright.sync_api import Locator, Page

from ptaf.ui.assertion_performer import AssertionPerformer


@pytest.fixture
def page() -> MagicMock:
    return MagicMock(spec=Page)


@pytest.fixture
def locator() -> MagicMock:
    return MagicMock(spec=Locator)


@pytest.fixture
def performer() -> AssertionPerformer:
    return AssertionPerformer()


class TestAssertionPerformer:
    @pytest.mark.parametrize(
        ("action", "value", "expect_method", "expect_args"),
        [
            ("contain", "text", "to_contain_text", ("text",)),
            ("notcontain", "text", "not_to_contain_text", ("text",)),
            ("hastext", "text", "to_have_text", ("text",)),
            ("hastextexactly", "text", "to_have_text", ("text",)),
            ("nothastext", "text", "not_to_have_text", ("text",)),
            ("isvisible", None, "to_be_visible", ()),
            ("notvisible", None, "not_to_be_visible", ()),
            ("ishidden", None, "to_be_hidden", ()),
            ("isattached", None, "to_be_attached", ()),
            ("detached", None, "not_to_be_attached", ()),
            ("enabled", None, "to_be_enabled", ()),
            ("disabled", None, "to_be_disabled", ()),
            ("checked", None, "to_be_checked", ()),
            ("notchecked", None, "not_to_be_checked", ()),
            ("focused", None, "to_be_focused", ()),
            ("notfocused", None, "not_to_be_focused", ()),
            ("hasattribute", "href=/x", "to_have_attribute", ("href", "/x")),
            ("nothasattribute", "href=/x", "not_to_have_attribute", ("href", "/x")),
            ("hasclass", "active", "to_have_class", ("active",)),
            ("nothasclass", "active", "not_to_have_class", ("active",)),
            ("hasvalue", "abc", "to_have_value", ("abc",)),
            ("hascount", "3", "to_have_count", (3,)),
        ],
    )
    def test_locator_assertions(
        self,
        performer: AssertionPerformer,
        page: MagicMock,
        locator: MagicMock,
        action: str,
        value: str | None,
        expect_method: str,
        expect_args: tuple,
    ) -> None:
        assertion_mock = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=assertion_mock
        ) as mock_expect:
            performer.perform_assertion(page, action, locator, value)

        mock_expect.assert_called_once_with(locator)
        called = getattr(assertion_mock, expect_method)
        if action == "hastextexactly":
            called.assert_called_once_with("text", use_inner_text=True)
        else:
            called.assert_called_once_with(*expect_args)

    def test_hastitle(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        page_assertion = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=page_assertion
        ) as mock_expect:
            performer.perform_assertion(page, "hastitle", locator, "Home")

        mock_expect.assert_called_once_with(page)
        page_assertion.to_have_title.assert_called_once_with("Home")

    def test_hasurl(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        page_assertion = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=page_assertion
        ) as mock_expect:
            performer.perform_assertion(page, "hasurl", locator, "https://x.test")

        mock_expect.assert_called_once_with(page)
        page_assertion.to_have_url.assert_called_once_with("https://x.test")

    def test_matchregex(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assertion_mock = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=assertion_mock
        ):
            performer.perform_assertion(page, "matchregex", locator, "abc.*")

        assertion_mock.to_have_text.assert_called_once_with(re.compile("abc.*"))

    def test_textstartswith(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assertion_mock = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=assertion_mock
        ):
            performer.perform_assertion(page, "textstartswith", locator, "Hello")

        assertion_mock.to_have_text.assert_called_once_with(
            re.compile("^" + re.escape("Hello"))
        )

    def test_textendswith(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assertion_mock = MagicMock()
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=assertion_mock
        ):
            performer.perform_assertion(page, "textendswith", locator, "World")

        assertion_mock.to_have_text.assert_called_once_with(
            re.compile(re.escape("World") + "$")
        )

    def test_unknown_assertion_raises(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        with pytest.raises(AssertionError, match="Unknown Assertion"):
            performer.perform_assertion(page, "bogus", locator, None)

    def test_playwright_error_wrapped_as_assertion_error(
        self, performer: AssertionPerformer, page: MagicMock, locator: MagicMock
    ) -> None:
        assertion_mock = MagicMock()
        assertion_mock.to_be_visible.side_effect = RuntimeError("boom")
        with patch(
            "ptaf.ui.assertion_performer.expect", return_value=assertion_mock
        ):
            with pytest.raises(AssertionError, match="Assertion failed for action"):
                performer.perform_assertion(page, "isvisible", locator, None)
