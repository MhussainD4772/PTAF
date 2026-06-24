"""Playwright locator resolution by PTAF locator type."""

from __future__ import annotations

from typing import Protocol, Union

from playwright.sync_api import FrameLocator, Locator, Page

LocatorContext = Union[Page, FrameLocator, Locator]


class _RoleLocatorContext(Protocol):
    def locator(self, selector: str) -> Locator: ...

    def get_by_role(self, role: str, **kwargs: object) -> Locator: ...

    def get_by_text(self, text: str, **kwargs: object) -> Locator: ...

    def get_by_alt_text(self, text: str, **kwargs: object) -> Locator: ...

    def get_by_title(self, text: str, **kwargs: object) -> Locator: ...

    def get_by_placeholder(self, text: str, **kwargs: object) -> Locator: ...

    def get_by_label(self, text: str, **kwargs: object) -> Locator: ...

    def get_by_test_id(self, test_id: str) -> Locator: ...


class LocatorHandler:
    """Resolve Playwright locators from PTAF locator types across page, frame, and chain contexts."""

    def get_locator_for_type(
        self,
        locator_type: str,
        context: LocatorContext,
        locator: str,
    ) -> Locator:
        """Resolve a locator on a page, frame, or chained base locator."""
        if isinstance(context, Page):
            chained = False
        elif isinstance(context, FrameLocator):
            chained = False
        elif isinstance(context, Locator):
            chained = True
        else:
            raise TypeError(f"Unsupported locator context: {type(context)!r}")
        return self._resolve(locator_type, context, locator, chained=chained)

    def _resolve(
        self,
        locator_type: str,
        context: LocatorContext,
        locator: str,
        *,
        chained: bool,
    ) -> Locator:
        normalized = locator_type.upper()
        role_context: _RoleLocatorContext = context  # type: ignore[assignment]

        if normalized in {"CSS", "TAG", "XPATH"}:
            return role_context.locator(locator)

        if normalized == "BUTTON":
            return role_context.get_by_role("button", name=locator)
        if normalized == "LINKTEXT":
            return role_context.get_by_role("link", name=locator)
        if normalized == "OPTION":
            return role_context.get_by_role("option", name=locator, exact=True)
        if normalized == "TEXTBOX":
            return role_context.get_by_role("textbox", name=locator)
        if normalized == "CHECKBOX":
            return role_context.get_by_role("checkbox", name=locator)
        if normalized == "RADIOBUTTON":
            return role_context.get_by_role("radio", name=locator)
        if normalized == "DROPDOWN":
            return role_context.get_by_role("combobox", name=locator)
        if normalized == "IMAGE":
            return role_context.get_by_role("img", name=locator)
        if normalized == "HEADING":
            return role_context.get_by_role("heading", name=locator)
        if normalized == "TAB":
            return role_context.get_by_role("tab", name=locator)
        if normalized == "LIST":
            return role_context.get_by_role("list", name=locator)
        if normalized == "LISTBOX":
            return role_context.get_by_role("listbox", name=locator)
        if normalized == "LISTITEM":
            return role_context.get_by_role("listitem", name=locator)
        if normalized == "TABLE":
            return role_context.get_by_role("table", name=locator)
        if normalized == "ROW":
            return role_context.get_by_role("row", name=locator)
        if normalized == "CELL":
            return role_context.get_by_role("cell", name=locator)
        if normalized == "GRID":
            return role_context.get_by_role("grid", name=locator)
        if normalized == "GRIDCELL":
            return role_context.get_by_role("gridcell", name=locator)

        if not chained:
            if normalized == "BUTTONSUBMIT":
                return role_context.get_by_role(
                    "button", name=locator, pressed=True
                )
            if normalized == "SLIDER":
                return role_context.get_by_role("slider", name=locator)
            if normalized == "SPINBUTTON":
                return role_context.get_by_role("spinbutton", name=locator)
            if normalized == "PROGRESSBAR":
                return role_context.get_by_role("progressbar", name=locator)
            if normalized == "ALERT":
                return role_context.get_by_role("alert", name=locator)
            if normalized == "ALERTDIALOG":
                return role_context.get_by_role("alertdialog", name=locator)
            if normalized == "DIALOG":
                return role_context.get_by_role("dialog", name=locator)
            if normalized == "NAVIGATION":
                return role_context.get_by_role("navigation", name=locator)
            if normalized == "MENU":
                return role_context.get_by_role("menu", name=locator)
            if normalized == "MENUITEM":
                return role_context.get_by_role("menuitem", name=locator)
            if normalized == "MENUITEMCHECKBOX":
                return role_context.get_by_role("menuitemcheckbox", name=locator)
            if normalized == "MENUITEMRADIO":
                return role_context.get_by_role("menuitemradio", name=locator)
            if normalized == "TREE":
                return role_context.get_by_role("tree", name=locator)
            if normalized == "TREEITEM":
                return role_context.get_by_role("treeitem", name=locator)
            if normalized == "SEPARATOR":
                return role_context.get_by_role("separator", name=locator)
            if normalized == "SWITCH":
                return role_context.get_by_role("switch", name=locator)
            if normalized == "STATUS":
                return role_context.get_by_role("status", name=locator)
            if normalized == "BANNER":
                return role_context.get_by_role("banner", name=locator)
            if normalized in {"FOOTER", "CONTENTINFO"}:
                return role_context.get_by_role("contentinfo", name=locator)
            if normalized == "MAIN":
                return role_context.get_by_role("main", name=locator)
            if normalized == "COMPLEMENTARY":
                return role_context.get_by_role("complementary", name=locator)
            if normalized == "REGION":
                return role_context.get_by_role("region", name=locator)
            if normalized == "ARTICLE":
                return role_context.get_by_role("article", name=locator)
            if normalized == "FORM":
                return role_context.get_by_role("form", name=locator)
            if normalized == "LOG":
                return role_context.get_by_role("log", name=locator)
            if normalized == "MARQUEE":
                return role_context.get_by_role("marquee", name=locator)
            if normalized == "TIMER":
                return role_context.get_by_role("timer", name=locator)
            if normalized == "TOOLTIP":
                return role_context.get_by_role("tooltip", name=locator)
            if normalized == "TOOLBAR":
                return role_context.get_by_role("toolbar", name=locator)
            if normalized == "PRESENTATION":
                return role_context.get_by_role("presentation", name=locator)
            if normalized == "FIGURE":
                return role_context.get_by_role("figure", name=locator)
            if normalized == "ID":
                return role_context.locator(f"#{locator}")
            if normalized == "NAME":
                return role_context.locator(f"[name='{locator}']")
            if normalized == "CLASS":
                return role_context.locator(f".{locator}")

        if normalized == "TEXT":
            return role_context.get_by_text(locator)
        if normalized == "ROLE":
            return role_context.get_by_role(locator.upper())
        if normalized == "ALTTEXT":
            return role_context.get_by_alt_text(locator)
        if normalized == "TITLE":
            return role_context.get_by_title(locator)
        if normalized == "PLACEHOLDER":
            return role_context.get_by_placeholder(locator)
        if normalized == "LABEL":
            return role_context.get_by_label(locator)
        if normalized == "TESTID":
            return role_context.get_by_test_id(locator)

        if chained:
            raise ValueError(f"Unknown chained locator type: {locator_type}")
        raise ValueError(f"Unknown locator type: {locator_type}")
