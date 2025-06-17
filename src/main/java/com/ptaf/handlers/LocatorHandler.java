package com.ptaf.handlers;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * LocatorHandler provides methods for retrieving Locators based on various locator types
 * from a given Playwright Page, FrameLocator, or another Locator for chaining.
 * It serves as a utility class for abstraction around how different types of elements
 * are located within the UI.
 * <p>
 * Usage:
 * - Use the getLocatorForType method to obtain a Locator based on its type and context.
 */
public class LocatorHandler {

    /**
     * Retrieves the locator for a specific type from a Playwright Page.
     *
     * @param locatorType The type of locator (e.g., XPATH, CSS, BUTTON, etc.).
     * @param page        The Page object where the locator will be searched.
     * @param locator     The locator string or value.
     * @return A chainable Locator object corresponding to the specified type.
     * @throws IllegalArgumentException if the locator type is unknown.
     */
    public Locator getLocatorForType(String locatorType, Page page, String locator) {
        switch (locatorType.toUpperCase()) {
            case "CSS":
            case "TAG":
            case "XPATH":
                return page.locator(locator);
            case "BUTTON":
                return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(locator));
            case "LINKTEXT":
                return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(locator));
            case "OPTION":
                return page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(locator).setExact(true));
            case "TEXTBOX":
                return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(locator));
            case "CHECKBOX":
                return page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(locator));
            case "RADIOBUTTON":
                return page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(locator));
            case "DROPDOWN":
                return page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(locator));
            case "IMAGE":
                return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(locator));
            case "HEADING":
                return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(locator));
            case "TAB":
                return page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(locator));
            case "LIST":
                return page.getByRole(AriaRole.LIST, new Page.GetByRoleOptions().setName(locator));
            case "LISTBOX":
                return page.getByRole(AriaRole.LISTBOX, new Page.GetByRoleOptions().setName(locator));
            case "LISTITEM":
                return page.getByRole(AriaRole.LISTITEM, new Page.GetByRoleOptions().setName(locator));
            case "TABLE":
                return page.getByRole(AriaRole.TABLE, new Page.GetByRoleOptions().setName(locator));
            case "ROW":
                return page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(locator));
            case "CELL":
                return page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(locator));
            case "BUTTONSUBMIT":
                return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(locator).setPressed(true));
            case "SLIDER":
                return page.getByRole(AriaRole.SLIDER, new Page.GetByRoleOptions().setName(locator));
            case "SPINBUTTON":
                return page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName(locator));
            case "PROGRESSBAR":
                return page.getByRole(AriaRole.PROGRESSBAR, new Page.GetByRoleOptions().setName(locator));
            case "ALERT":
                return page.getByRole(AriaRole.ALERT, new Page.GetByRoleOptions().setName(locator));
            case "ALERTDIALOG":
                return page.getByRole(AriaRole.ALERTDIALOG, new Page.GetByRoleOptions().setName(locator));
            case "DIALOG":
                return page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(locator));
            case "NAVIGATION":
                return page.getByRole(AriaRole.NAVIGATION, new Page.GetByRoleOptions().setName(locator));
            case "MENU":
                return page.getByRole(AriaRole.MENU, new Page.GetByRoleOptions().setName(locator));
            case "MENUITEM":
                return page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName(locator));
            case "MENUITEMCHECKBOX":
                return page.getByRole(AriaRole.MENUITEMCHECKBOX, new Page.GetByRoleOptions().setName(locator));
            case "MENUITEMRADIO":
                return page.getByRole(AriaRole.MENUITEMRADIO, new Page.GetByRoleOptions().setName(locator));
            case "TREE":
                return page.getByRole(AriaRole.TREE, new Page.GetByRoleOptions().setName(locator));
            case "TREEITEM":
                return page.getByRole(AriaRole.TREEITEM, new Page.GetByRoleOptions().setName(locator));
            case "GRID":
                return page.getByRole(AriaRole.GRID, new Page.GetByRoleOptions().setName(locator));
            case "GRIDCELL":
                return page.getByRole(AriaRole.GRIDCELL, new Page.GetByRoleOptions().setName(locator));
            case "SEPARATOR":
                return page.getByRole(AriaRole.SEPARATOR, new Page.GetByRoleOptions().setName(locator));
            case "SWITCH":
                return page.getByRole(AriaRole.SWITCH, new Page.GetByRoleOptions().setName(locator));
            case "STATUS":
                return page.getByRole(AriaRole.STATUS, new Page.GetByRoleOptions().setName(locator));
            case "BANNER":
                return page.getByRole(AriaRole.BANNER, new Page.GetByRoleOptions().setName(locator));
            case "FOOTER":
            case "CONTENTINFO":
                return page.getByRole(AriaRole.CONTENTINFO, new Page.GetByRoleOptions().setName(locator));
            case "MAIN":
                return page.getByRole(AriaRole.MAIN, new Page.GetByRoleOptions().setName(locator));
            case "COMPLEMENTARY":
                return page.getByRole(AriaRole.COMPLEMENTARY, new Page.GetByRoleOptions().setName(locator));
            case "REGION":
                return page.getByRole(AriaRole.REGION, new Page.GetByRoleOptions().setName(locator));
            case "ARTICLE":
                return page.getByRole(AriaRole.ARTICLE, new Page.GetByRoleOptions().setName(locator));
            case "FORM":
                return page.getByRole(AriaRole.FORM, new Page.GetByRoleOptions().setName(locator));
            case "LOG":
                return page.getByRole(AriaRole.LOG, new Page.GetByRoleOptions().setName(locator));
            case "MARQUEE":
                return page.getByRole(AriaRole.MARQUEE, new Page.GetByRoleOptions().setName(locator));
            case "TIMER":
                return page.getByRole(AriaRole.TIMER, new Page.GetByRoleOptions().setName(locator));
            case "TOOLTIP":
                return page.getByRole(AriaRole.TOOLTIP, new Page.GetByRoleOptions().setName(locator));
            case "TOOLBAR":
                return page.getByRole(AriaRole.TOOLBAR, new Page.GetByRoleOptions().setName(locator));
            case "PRESENTATION":
                return page.getByRole(AriaRole.PRESENTATION, new Page.GetByRoleOptions().setName(locator));
            case "FIGURE":
                return page.getByRole(AriaRole.FIGURE, new Page.GetByRoleOptions().setName(locator));
            case "TEXT":
                return page.getByText(locator);
            case "ROLE":
                return page.getByRole(AriaRole.valueOf(locator.toUpperCase()));
            case "ALTTEXT":
                return page.getByAltText(locator);
            case "TITLE":
                return page.getByTitle(locator);
            case "PLACEHOLDER":
                return page.getByPlaceholder(locator);
            case "LABEL":
                return page.getByLabel(locator);
            case "TESTID":
                return page.getByTestId(locator);
            case "ID":
                return page.locator("#" + locator);
            case "NAME":
                return page.locator("[name='" + locator + "']");
            case "CLASS":
                return page.locator("." + locator);
            default:
                throw new IllegalArgumentException("Unknown locator type: " + locatorType);
        }
    }

    /**
     * Retrieves the locator for a specific type from a FrameLocator.
     *
     * @param locatorType The type of locator (e.g., XPATH, CSS, BUTTON, etc.).
     * @param frame       The FrameLocator object where the locator will be searched.
     * @param locator     The locator string or value.
     * @return A chainable Locator object corresponding to the specified type.
     * @throws IllegalArgumentException if the locator type is unknown.
     */
    public Locator getLocatorForType(String locatorType, FrameLocator frame, String locator) {
        switch (locatorType.toUpperCase()) {
            case "CSS":
            case "TAG":
            case "XPATH":
                return frame.locator(locator);
            case "BUTTON":
                return frame.getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName(locator));
            case "LINKTEXT":
                return frame.getByRole(AriaRole.LINK, new FrameLocator.GetByRoleOptions().setName(locator));
            case "OPTION":
                return frame.getByRole(AriaRole.OPTION, new FrameLocator.GetByRoleOptions().setName(locator).setExact(true));
            case "TEXTBOX":
                return frame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName(locator));
            case "CHECKBOX":
                return frame.getByRole(AriaRole.CHECKBOX, new FrameLocator.GetByRoleOptions().setName(locator));
            case "RADIOBUTTON":
                return frame.getByRole(AriaRole.RADIO, new FrameLocator.GetByRoleOptions().setName(locator));
            case "DROPDOWN":
                return frame.getByRole(AriaRole.COMBOBOX, new FrameLocator.GetByRoleOptions().setName(locator));
            case "IMAGE":
                return frame.getByRole(AriaRole.IMG, new FrameLocator.GetByRoleOptions().setName(locator));
            case "HEADING":
                return frame.getByRole(AriaRole.HEADING, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TAB":
                return frame.getByRole(AriaRole.TAB, new FrameLocator.GetByRoleOptions().setName(locator));
            case "LIST":
                return frame.getByRole(AriaRole.LIST, new FrameLocator.GetByRoleOptions().setName(locator));
            case "LISTBOX":
                return frame.getByRole(AriaRole.LISTBOX, new FrameLocator.GetByRoleOptions().setName(locator));
            case "LISTITEM":
                return frame.getByRole(AriaRole.LISTITEM, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TABLE":
                return frame.getByRole(AriaRole.TABLE, new FrameLocator.GetByRoleOptions().setName(locator));
            case "ROW":
                return frame.getByRole(AriaRole.ROW, new FrameLocator.GetByRoleOptions().setName(locator));
            case "CELL":
                return frame.getByRole(AriaRole.CELL, new FrameLocator.GetByRoleOptions().setName(locator));
            case "BUTTONSUBMIT":
                return frame.getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName(locator).setPressed(true));
            case "SLIDER":
                return frame.getByRole(AriaRole.SLIDER, new FrameLocator.GetByRoleOptions().setName(locator));
            case "SPINBUTTON":
                return frame.getByRole(AriaRole.SPINBUTTON, new FrameLocator.GetByRoleOptions().setName(locator));
            case "PROGRESSBAR":
                return frame.getByRole(AriaRole.PROGRESSBAR, new FrameLocator.GetByRoleOptions().setName(locator));
            case "ALERT":
                return frame.getByRole(AriaRole.ALERT, new FrameLocator.GetByRoleOptions().setName(locator));
            case "ALERTDIALOG":
                return frame.getByRole(AriaRole.ALERTDIALOG, new FrameLocator.GetByRoleOptions().setName(locator));
            case "DIALOG":
                return frame.getByRole(AriaRole.DIALOG, new FrameLocator.GetByRoleOptions().setName(locator));
            case "NAVIGATION":
                return frame.getByRole(AriaRole.NAVIGATION, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MENU":
                return frame.getByRole(AriaRole.MENU, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MENUITEM":
                return frame.getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MENUITEMCHECKBOX":
                return frame.getByRole(AriaRole.MENUITEMCHECKBOX, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MENUITEMRADIO":
                return frame.getByRole(AriaRole.MENUITEMRADIO, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TREE":
                return frame.getByRole(AriaRole.TREE, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TREEITEM":
                return frame.getByRole(AriaRole.TREEITEM, new FrameLocator.GetByRoleOptions().setName(locator));
            case "GRID":
                return frame.getByRole(AriaRole.GRID, new FrameLocator.GetByRoleOptions().setName(locator));
            case "GRIDCELL":
                return frame.getByRole(AriaRole.GRIDCELL, new FrameLocator.GetByRoleOptions().setName(locator));
            case "SEPARATOR":
                return frame.getByRole(AriaRole.SEPARATOR, new FrameLocator.GetByRoleOptions().setName(locator));
            case "SWITCH":
                return frame.getByRole(AriaRole.SWITCH, new FrameLocator.GetByRoleOptions().setName(locator));
            case "STATUS":
                return frame.getByRole(AriaRole.STATUS, new FrameLocator.GetByRoleOptions().setName(locator));
            case "BANNER":
                return frame.getByRole(AriaRole.BANNER, new FrameLocator.GetByRoleOptions().setName(locator));
            case "FOOTER":
            case "CONTENTINFO":
                return frame.getByRole(AriaRole.CONTENTINFO, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MAIN":
                return frame.getByRole(AriaRole.MAIN, new FrameLocator.GetByRoleOptions().setName(locator));
            case "COMPLEMENTARY":
                return frame.getByRole(AriaRole.COMPLEMENTARY, new FrameLocator.GetByRoleOptions().setName(locator));
            case "REGION":
                return frame.getByRole(AriaRole.REGION, new FrameLocator.GetByRoleOptions().setName(locator));
            case "ARTICLE":
                return frame.getByRole(AriaRole.ARTICLE, new FrameLocator.GetByRoleOptions().setName(locator));
            case "FORM":
                return frame.getByRole(AriaRole.FORM, new FrameLocator.GetByRoleOptions().setName(locator));
            case "LOG":
                return frame.getByRole(AriaRole.LOG, new FrameLocator.GetByRoleOptions().setName(locator));
            case "MARQUEE":
                return frame.getByRole(AriaRole.MARQUEE, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TIMER":
                return frame.getByRole(AriaRole.TIMER, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TOOLTIP":
                return frame.getByRole(AriaRole.TOOLTIP, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TOOLBAR":
                return frame.getByRole(AriaRole.TOOLBAR, new FrameLocator.GetByRoleOptions().setName(locator));
            case "PRESENTATION":
                return frame.getByRole(AriaRole.PRESENTATION, new FrameLocator.GetByRoleOptions().setName(locator));
            case "FIGURE":
                return frame.getByRole(AriaRole.FIGURE, new FrameLocator.GetByRoleOptions().setName(locator));
            case "TEXT":
                return frame.getByText(locator);
            case "ROLE":
                return frame.getByRole(AriaRole.valueOf(locator.toUpperCase()));
            case "ALTTEXT":
                return frame.getByAltText(locator);
            case "TITLE":
                return frame.getByTitle(locator);
            case "PLACEHOLDER":
                return frame.getByPlaceholder(locator);
            case "LABEL":
                return frame.getByLabel(locator);
            case "TESTID":
                return frame.getByTestId(locator);
            case "ID":
                return frame.locator("#" + locator);
            case "NAME":
                return frame.locator("[name='" + locator + "']");
            case "CLASS":
                return frame.locator("." + locator);
            default:
                throw new IllegalArgumentException("Unknown locator type: " + locatorType);
        }
    }

    /**
     * NEW METHOD for chaining. It finds a new locator within the context of an existing base locator.
     *
     * @param locatorType The type of the new locator to chain (e.g., "BUTTON").
     * @param baseLocator The existing locator to search within.
     * @param locator     The string value for the new locator.
     * @return A new, chained Locator object.
     */
    public Locator getLocatorForType(String locatorType, Locator baseLocator, String locator) {
        switch (locatorType.toUpperCase()) {
            case "CSS":
            case "TAG":
            case "XPATH":
                return baseLocator.locator(locator);
            case "BUTTON":
                return baseLocator.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName(locator));
            case "LINKTEXT":
                return baseLocator.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(locator));
            case "OPTION":
                return baseLocator.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(locator).setExact(true));
            case "TEXTBOX":
                return baseLocator.getByRole(AriaRole.TEXTBOX, new Locator.GetByRoleOptions().setName(locator));
            case "CHECKBOX":
                return baseLocator.getByRole(AriaRole.CHECKBOX, new Locator.GetByRoleOptions().setName(locator));
            case "RADIOBUTTON":
                return baseLocator.getByRole(AriaRole.RADIO, new Locator.GetByRoleOptions().setName(locator));
            case "DROPDOWN":
                return baseLocator.getByRole(AriaRole.COMBOBOX, new Locator.GetByRoleOptions().setName(locator));
            case "IMAGE":
                return baseLocator.getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName(locator));
            case "HEADING":
                return baseLocator.getByRole(AriaRole.HEADING, new Locator.GetByRoleOptions().setName(locator));
            case "TAB":
                return baseLocator.getByRole(AriaRole.TAB, new Locator.GetByRoleOptions().setName(locator));
            case "LIST":
                return baseLocator.getByRole(AriaRole.LIST, new Locator.GetByRoleOptions().setName(locator));
            case "LISTBOX":
                return baseLocator.getByRole(AriaRole.LISTBOX, new Locator.GetByRoleOptions().setName(locator));
            case "LISTITEM":
                return baseLocator.getByRole(AriaRole.LISTITEM, new Locator.GetByRoleOptions().setName(locator));
            case "TABLE":
                return baseLocator.getByRole(AriaRole.TABLE, new Locator.GetByRoleOptions().setName(locator));
            case "ROW":
                return baseLocator.getByRole(AriaRole.ROW, new Locator.GetByRoleOptions().setName(locator));
            case "CELL":
                return baseLocator.getByRole(AriaRole.CELL, new Locator.GetByRoleOptions().setName(locator));
            case "GRID":
                return baseLocator.getByRole(AriaRole.GRID, new Locator.GetByRoleOptions().setName(locator));
            case "GRIDCELL":
                return baseLocator.getByRole(AriaRole.GRIDCELL, new Locator.GetByRoleOptions().setName(locator));
            case "TEXT":
                return baseLocator.getByText(locator);
            case "ROLE":
                return baseLocator.getByRole(AriaRole.valueOf(locator.toUpperCase()));
            case "ALTTEXT":
                return baseLocator.getByAltText(locator);
            case "TITLE":
                return baseLocator.getByTitle(locator);
            case "PLACEHOLDER":
                return baseLocator.getByPlaceholder(locator);
            case "LABEL":
                return baseLocator.getByLabel(locator);
            case "TESTID":
                return baseLocator.getByTestId(locator);
            default:
                throw new IllegalArgumentException("Unknown chained locator type: " + locatorType);
        }
    }
}