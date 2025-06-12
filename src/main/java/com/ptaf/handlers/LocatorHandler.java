package com.ptaf.handlers;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * LocatorHandler provides methods for retrieving Locators based on various locator types
 * from a given Playwright Page or FrameLocator. It serves as a utility class for abstraction
 * around how different types of elements are located within the UI.
 * <p>
 * Usage:
 * - Use the getLocatorForType method to obtain a Locator based on its type and context (Page or Frame).
 */
public class LocatorHandler {

    /**
     * Retrieves the locator for a specific type from a Playwright Page.
     *
     * @param locatorType The type of locator (e.g., XPATH, CSS, BUTTON, etc.).
     * @param page        The Page object where the locator will be searched.
     * @param locator     The locator string or value.
     * @return The located element as a Locator object corresponding to the specified type.
     * @throws IllegalArgumentException if the locator type is unknown.
     */
    public Locator getLocatorForType(String locatorType, Page page, String locator) {
        switch (locatorType.toUpperCase()) {
            case "CSS":
            case "TAG":
            case "XPATH":
                return page.locator(locator).first(); // Return Locator using CSS, TAG, or XPATH
            case "BUTTON":
                return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(locator)).first(); // Get button by name
            case "LINKTEXT":
                return page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(locator)).first(); // Get link by visible text
            case "OPTION":
                return page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(locator).setExact(true)).first(); // Select option with exact matching
            case "TEXTBOX":
                return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(locator)).first(); // Get textbox by name
            case "CHECKBOX":
                return page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(locator)).first(); // Get checkbox by name
            case "RADIOBUTTON":
                return page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(locator)).first(); // Get radio button by name
            case "DROPDOWN":
                return page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName(locator)).first(); // Get dropdown by name
            case "IMAGE":
                return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(locator)).first(); // Get image by name
            case "HEADING":
                return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(locator)).first(); // Get heading by name
            case "TAB":
                return page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(locator)).first(); // Get tab by name
            case "LIST":
                return page.getByRole(AriaRole.LIST, new Page.GetByRoleOptions().setName(locator)).first(); // Get list by name
            case "LISTBOX":
                return page.getByRole(AriaRole.LISTBOX, new Page.GetByRoleOptions().setName(locator)).first(); // Get list by name
            case "LISTITEM":
                return page.getByRole(AriaRole.LISTITEM, new Page.GetByRoleOptions().setName(locator)).first(); // Get list item by name
            case "TABLE":
                return page.getByRole(AriaRole.TABLE, new Page.GetByRoleOptions().setName(locator)).first(); // Get table by name
            case "ROW":
                return page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(locator)).first(); // Get row by name
            case "CELL":
                return page.getByRole(AriaRole.CELL, new Page.GetByRoleOptions().setName(locator)).first(); // Get cell by name
            case "BUTTONSUBMIT":
                return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(locator).setPressed(true)).first(); // Get pressed submit button
            case "SLIDER":
                return page.getByRole(AriaRole.SLIDER, new Page.GetByRoleOptions().setName(locator)).first(); // Get slider by name
            case "SPINBUTTON":
                return page.getByRole(AriaRole.SPINBUTTON, new Page.GetByRoleOptions().setName(locator)).first(); // Get spin button by name
            case "PROGRESSBAR":
                return page.getByRole(AriaRole.PROGRESSBAR, new Page.GetByRoleOptions().setName(locator)).first(); // Get progress bar by name
            case "ALERT":
                return page.getByRole(AriaRole.ALERT, new Page.GetByRoleOptions().setName(locator)).first(); // Get alert by name
            case "ALERTDIALOG":
                return page.getByRole(AriaRole.ALERTDIALOG, new Page.GetByRoleOptions().setName(locator)).first(); // Get alert dialog by name
            case "DIALOG":
                return page.getByRole(AriaRole.DIALOG, new Page.GetByRoleOptions().setName(locator)).first(); // Get dialog by name
            case "NAVIGATION":
                return page.getByRole(AriaRole.NAVIGATION, new Page.GetByRoleOptions().setName(locator)).first(); // Get navigation by name
            case "MENU":
                return page.getByRole(AriaRole.MENU, new Page.GetByRoleOptions().setName(locator)).first(); // Get menu by name
            case "MENUITEM":
                return page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName(locator)).first(); // Get menu item by name
            case "MENUITEMCHECKBOX":
                return page.getByRole(AriaRole.MENUITEMCHECKBOX, new Page.GetByRoleOptions().setName(locator)).first(); // Get menu item with checkbox by name
            case "MENUITEMRADIO":
                return page.getByRole(AriaRole.MENUITEMRADIO, new Page.GetByRoleOptions().setName(locator)).first(); // Get menu item with radio button by name
            case "TREE":
                return page.getByRole(AriaRole.TREE, new Page.GetByRoleOptions().setName(locator)).first(); // Get tree structure by name
            case "TREEITEM":
                return page.getByRole(AriaRole.TREEITEM, new Page.GetByRoleOptions().setName(locator)).first(); // Get item in tree by name
            case "GRID":
                return page.getByRole(AriaRole.GRID, new Page.GetByRoleOptions().setName(locator)).first(); // Get grid by name
            case "GRIDCELL":
                return page.getByRole(AriaRole.GRIDCELL, new Page.GetByRoleOptions().setName(locator)).first(); // Get cell in grid by name
            case "SEPARATOR":
                return page.getByRole(AriaRole.SEPARATOR, new Page.GetByRoleOptions().setName(locator)).first(); // Get separator by name
            case "SWITCH":
                return page.getByRole(AriaRole.SWITCH, new Page.GetByRoleOptions().setName(locator)).first(); // Get switch by name
            case "STATUS":
                return page.getByRole(AriaRole.STATUS, new Page.GetByRoleOptions().setName(locator)).first(); // Get status by name
            case "BANNER":
                return page.getByRole(AriaRole.BANNER, new Page.GetByRoleOptions().setName(locator)).first(); // Get banner by name
            case "FOOTER":
                return page.getByRole(AriaRole.CONTENTINFO, new Page.GetByRoleOptions().setName(locator)).first(); // Get footer content by name
            case "MAIN":
                return page.getByRole(AriaRole.MAIN, new Page.GetByRoleOptions().setName(locator)).first(); // Get main content area by name
            case "COMPLEMENTARY":
                return page.getByRole(AriaRole.COMPLEMENTARY, new Page.GetByRoleOptions().setName(locator)).first(); // Get complementary content
            case "REGION":
                return page.getByRole(AriaRole.REGION, new Page.GetByRoleOptions().setName(locator)).first(); // Get region by name
            case "ARTICLE":
                return page.getByRole(AriaRole.ARTICLE, new Page.GetByRoleOptions().setName(locator)).first(); // Get article section by name
            case "FORM":
                return page.getByRole(AriaRole.FORM, new Page.GetByRoleOptions().setName(locator)).first(); // Get form by name
            case "LOG":
                return page.getByRole(AriaRole.LOG, new Page.GetByRoleOptions().setName(locator)).first(); // Get log section by name
            case "MARQUEE":
                return page.getByRole(AriaRole.MARQUEE, new Page.GetByRoleOptions().setName(locator)).first(); // Get marquee element by name
            case "TIMER":
                return page.getByRole(AriaRole.TIMER, new Page.GetByRoleOptions().setName(locator)).first(); // Get timer by name
            case "TOOLTIP":
                return page.getByRole(AriaRole.TOOLTIP, new Page.GetByRoleOptions().setName(locator)).first(); // Get tooltip by name
            case "TOOLBAR":
                return page.getByRole(AriaRole.TOOLBAR, new Page.GetByRoleOptions().setName(locator)).first(); // Get toolbar by name
            case "PRESENTATION":
                return page.getByRole(AriaRole.PRESENTATION, new Page.GetByRoleOptions().setName(locator)).first(); // Get presentation content
            case "FIGURE":
                return page.getByRole(AriaRole.FIGURE, new Page.GetByRoleOptions().setName(locator)).first(); // Get figure content
            case "CONTENTINFO":
                return page.getByRole(AriaRole.CONTENTINFO, new Page.GetByRoleOptions().setName(locator)).first(); // Get content information by name
            case "TEXT":
                return page.getByText(locator).first(); // Get element by visible text
            case "ROLE":
                return page.getByRole(AriaRole.valueOf(locator)).first(); // Get element by ARIA role
            case "ALTTEXT":
                return page.getByAltText(locator).first(); // Get element by alt text
            case "TITLE":
                return page.getByTitle(locator).first(); // Get element by title attribute
            case "PLACEHOLDER":
                return page.getByPlaceholder(locator).first(); // Get element by placeholder attribute
            case "LABEL":
                return page.getByLabel(locator).first(); // Get element by label
            case "TESTID":
                return page.getByTestId(locator).first(); // Get element by data-testid attribute
            case "ID":
                return page.locator("#" + locator).first(); // Get element by ID
            case "NAME":
                return page.locator("[name='" + locator + "']").first(); // Get element by NAME attribute
            case "CLASS":
                return page.locator("." + locator).first(); // Get element by CLASS
            default:
                throw new IllegalArgumentException("Unknown locator type: " + locatorType); // Handle unknown locator types
        }
    }

    /**
     * Retrieves the locator for a specific type from a FrameLocator.
     *
     * @param locatorType The type of locator (e.g., XPATH, CSS, BUTTON, etc.).
     * @param frame       The FrameLocator object where the locator will be searched.
     * @param locator     The locator string or value.
     * @return The located element as a Locator object corresponding to the specified type.
     * @throws IllegalArgumentException if the locator type is unknown.
     */
    public Locator getLocatorForType(String locatorType, FrameLocator frame, String locator) {
        switch (locatorType.toUpperCase()) {
            case "XPATH":
            case "CSS":
            case "TAG":
                return frame.locator(locator).first(); // Return Locator using CSS, TAG, or XPATH
            case "BUTTON":
                return frame.getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get button by name in frame
            case "LINKTEXT":
                return frame.getByRole(AriaRole.LINK, new FrameLocator.GetByRoleOptions().setName(locator)).first().first(); // Get link by visible text in frame
            case "OPTION":
                return frame.getByRole(AriaRole.OPTION, new FrameLocator.GetByRoleOptions().setName(locator).setExact(true)).first(); // Select option with exact match in frame
            case "TEXTBOX":
                return frame.getByRole(AriaRole.TEXTBOX, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get textbox by name in frame
            case "CHECKBOX":
                return frame.getByRole(AriaRole.CHECKBOX, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get checkbox by name in frame
            case "RADIOBUTTON":
                return frame.getByRole(AriaRole.RADIO, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get radio button by name in frame
            case "DROPDOWN":
                return frame.getByRole(AriaRole.COMBOBOX, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get dropdown by name in frame
            case "IMAGE":
                return frame.getByRole(AriaRole.IMG, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get image by name in frame
            case "HEADING":
                return frame.getByRole(AriaRole.HEADING, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get heading by name in frame
            case "TAB":
                return frame.getByRole(AriaRole.TAB, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get tab by name in frame
            case "LIST":
                return frame.getByRole(AriaRole.LIST, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get list by name in frame
            case "LISTBOX":
                return frame.getByRole(AriaRole.LISTBOX, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get list by name in frame
            case "LISTITEM":
                return frame.getByRole(AriaRole.LISTITEM, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get list item by name in frame
            case "TABLE":
                return frame.getByRole(AriaRole.TABLE, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get table by name in frame
            case "ROW":
                return frame.getByRole(AriaRole.ROW, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get row by name in frame
            case "CELL":
                return frame.getByRole(AriaRole.CELL, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get cell by name in frame
            case "BUTTONSUBMIT":
                return frame.getByRole(AriaRole.BUTTON, new FrameLocator.GetByRoleOptions().setName(locator).setPressed(true)).first(); // Get pressed submit button in frame
            case "SLIDER":
                return frame.getByRole(AriaRole.SLIDER, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get slider by name in frame
            case "SPINBUTTON":
                return frame.getByRole(AriaRole.SPINBUTTON, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get spin button by name in frame
            case "PROGRESSBAR":
                return frame.getByRole(AriaRole.PROGRESSBAR, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get progress bar by name in frame
            case "ALERT":
                return frame.getByRole(AriaRole.ALERT, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get alert by name in frame
            case "ALERTDIALOG":
                return frame.getByRole(AriaRole.ALERTDIALOG, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get alert dialog by name in frame
            case "DIALOG":
                return frame.getByRole(AriaRole.DIALOG, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get dialog by name in frame
            case "NAVIGATION":
                return frame.getByRole(AriaRole.NAVIGATION, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get navigation by name in frame
            case "MENU":
                return frame.getByRole(AriaRole.MENU, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get menu by name in frame
            case "MENUITEM":
                return frame.getByRole(AriaRole.MENUITEM, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get menu item by name in frame
            case "MENUITEMCHECKBOX":
                return frame.getByRole(AriaRole.MENUITEMCHECKBOX, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get menu item with checkbox by name in frame
            case "MENUITEMRADIO":
                return frame.getByRole(AriaRole.MENUITEMRADIO, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get menu item with radio button by name in frame
            case "TREE":
                return frame.getByRole(AriaRole.TREE, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get tree structure by name in frame
            case "TREEITEM":
                return frame.getByRole(AriaRole.TREEITEM, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get item in tree by name in frame
            case "GRID":
                return frame.getByRole(AriaRole.GRID, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get grid by name in frame
            case "GRIDCELL":
                return frame.getByRole(AriaRole.GRIDCELL, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get cell in grid by name in frame
            case "SEPARATOR":
                return frame.getByRole(AriaRole.SEPARATOR, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get separator by name in frame
            case "SWITCH":
                return frame.getByRole(AriaRole.SWITCH, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get switch by name in frame
            case "STATUS":
                return frame.getByRole(AriaRole.STATUS, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get status by name in frame
            case "BANNER":
                return frame.getByRole(AriaRole.BANNER, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get banner by name in frame
            case "FOOTER":
                return frame.getByRole(AriaRole.CONTENTINFO, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get footer content by name in frame
            case "MAIN":
                return frame.getByRole(AriaRole.MAIN, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get main content area by name in frame
            case "COMPLEMENTARY":
                return frame.getByRole(AriaRole.COMPLEMENTARY, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get complementary content in frame
            case "REGION":
                return frame.getByRole(AriaRole.REGION, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get region by name in frame
            case "ARTICLE":
                return frame.getByRole(AriaRole.ARTICLE, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get article section by name in frame
            case "FORM":
                return frame.getByRole(AriaRole.FORM, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get form by name in frame
            case "LOG":
                return frame.getByRole(AriaRole.LOG, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get log section by name in frame
            case "MARQUEE":
                return frame.getByRole(AriaRole.MARQUEE, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get marquee element by name in frame
            case "TIMER":
                return frame.getByRole(AriaRole.TIMER, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get timer by name in frame
            case "TOOLTIP":
                return frame.getByRole(AriaRole.TOOLTIP, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get tooltip by name in frame
            case "TOOLBAR":
                return frame.getByRole(AriaRole.TOOLBAR, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get toolbar by name in frame
            case "PRESENTATION":
                return frame.getByRole(AriaRole.PRESENTATION, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get presentation content in frame
            case "FIGURE":
                return frame.getByRole(AriaRole.FIGURE, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get figure content in frame
            case "CONTENTINFO":
                return frame.getByRole(AriaRole.CONTENTINFO, new FrameLocator.GetByRoleOptions().setName(locator)).first(); // Get content information by name in frame
            case "TEXT":
                return frame.getByText(locator).first(); // Get element by visible text in frame
            case "ROLE":
                return frame.getByRole(AriaRole.valueOf(locator)).first(); // Get element by ARIA role in frame
            case "ALTTEXT":
                return frame.getByAltText(locator).first(); // Get element by alt text in frame
            case "TITLE":
                return frame.getByTitle(locator).first(); // Get element by title attribute in frame
            case "PLACEHOLDER":
                return frame.getByPlaceholder(locator).first(); // Get element by placeholder attribute in frame
            case "LABEL":
                return frame.getByLabel(locator).first(); // Get element by label in frame
            case "TESTID":
                return frame.getByTestId(locator).first(); // Get element by data-testid attribute in frame
            case "ID":
                return frame.locator("#" + locator).first(); // Get element by ID in frame
            case "NAME":
                return frame.locator("[name='" + locator + "']").first(); // Get element by NAME attribute in frame
            case "CLASS":
                return frame.locator("." + locator).first(); // Get element by CLASS in frame
            default:
                throw new IllegalArgumentException("Unknown locator type: " + locatorType); // Handle unknown locator types
        }
    }

    /**
     * Retrieves a Locator object based on the provided locator type from a given page,
     * potentially traversing through one to three levels of iFrames.
     *
     * This method allows for versatile locator retrieval using various types such as
     * XPATH, CSS, ROLE, and others, while descending through the specified iFrames.
     *
     * If the specified locator type is valid, the method returns the corresponding Locator
     * found either in the context of the provided FrameLocator or the main page (if provided).
     *
     * The method includes error handling to provide informative messages in case
     * the locator cannot be found or if an invalid locator type is requested.
     *
     * @param page The main Page to search for locators if the specified frame is not provided.
     * @param iFrame The first level iFrame locator to search within.
     * @param iFrame_2 An optional second level iFrame for deeper searches.
     * @param iFrame_3 An optional third level iFrame for deeper searches.
     * @param locatorType The type of locator to be retrieved (e.g., "BUTTON", "LINKTEXT", "XPATH", etc.).
     * @param locator The specific identifier for the locator (e.g., an element's name).
     * @return The Locator object for the specified type and identifier.
     * @throws IllegalArgumentException if the locator type is unknown or the Locator cannot be found.
     */
    public Locator getLocatorForTypeFrame(Page page, String iFrame, String iFrame_2, String iFrame_3, String locatorType, String locator) {
        try {
            // Start with the first iFrame and access its content frame
            FrameLocator frameLocator = page.locator(iFrame).contentFrame();

            // If a second iFrame is specified, navigate deeper
            if (iFrame_2 != null) {
                frameLocator = frameLocator.locator(iFrame_2).contentFrame();
            }

            // If a third iFrame is specified, navigate deeper
            if (iFrame_3 != null) {
                frameLocator = frameLocator.locator(iFrame_3).contentFrame();
            }

            FrameLocator.GetByRoleOptions options = new FrameLocator.GetByRoleOptions().setName(locator);

            switch (locatorType.toUpperCase()) {
                case "XPATH":
                case "CSS":
                case "TAG":
                    return frameLocator.locator(locator).first(); // Return Locator using CSS, TAG, or XPATH

                case "BUTTON":
                    return frameLocator.getByRole(AriaRole.BUTTON, options).first(); // Get button by name in iFrame

                case "LINKTEXT":
                    return frameLocator.getByRole(AriaRole.LINK, options).first(); // Get link by visible text in iFrame

                case "OPTION":
                    return frameLocator.getByRole(AriaRole.OPTION, options).first(); // Get option by name in iFrame

                case "TEXTBOX":
                    return frameLocator.getByRole(AriaRole.TEXTBOX, options).first(); // Get textbox by name in iFrame

                case "CHECKBOX":
                    return frameLocator.getByRole(AriaRole.CHECKBOX, options).first(); // Get checkbox by name in iFrame

                case "RADIOBUTTON":
                    return frameLocator.getByRole(AriaRole.RADIO, options).first(); // Get radio button by name in iFrame

                case "DROPDOWN":
                    return frameLocator.getByRole(AriaRole.COMBOBOX, options).first(); // Get dropdown by name in iFrame

                case "IMAGE":
                    return frameLocator.getByRole(AriaRole.IMG, options).first(); // Get image by name in iFrame

                case "HEADING":
                    return frameLocator.getByRole(AriaRole.HEADING, options).first(); // Get heading by name in iFrame

                case "TAB":
                    return frameLocator.getByRole(AriaRole.TAB, options).first(); // Get tab by name in iFrame

                case "LIST":
                    return frameLocator.getByRole(AriaRole.LIST, options).first(); // Get list by name in iFrame

                case "LISTBOX":
                    return frameLocator.getByRole(AriaRole.LISTBOX, options).first(); // Get list by name in iFrame

                case "LISTITEM":
                    return frameLocator.getByRole(AriaRole.LISTITEM, options).first(); // Get list item by name in iFrame

                case "TABLE":
                    return frameLocator.getByRole(AriaRole.TABLE, options).first(); // Get table by name in iFrame

                case "ROW":
                    return frameLocator.getByRole(AriaRole.ROW, options).first(); // Get row by name in iFrame

                case "CELL":
                    return frameLocator.getByRole(AriaRole.CELL, options).first(); // Get cell by name in iFrame

                case "SLIDER":
                    return frameLocator.getByRole(AriaRole.SLIDER, options).first(); // Get slider by name in iFrame

                case "SPINBUTTON":
                    return frameLocator.getByRole(AriaRole.SPINBUTTON, options).first(); // Get spin button by name in iFrame

                case "PROGRESSBAR":
                    return frameLocator.getByRole(AriaRole.PROGRESSBAR, options).first(); // Get progress bar by name in iFrame

                case "ALERT":
                    return frameLocator.getByRole(AriaRole.ALERT, options).first(); // Get alert by name in iFrame

                case "ALERTDIALOG":
                    return frameLocator.getByRole(AriaRole.ALERTDIALOG, options).first(); // Get alert dialog by name in iFrame

                case "DIALOG":
                    return frameLocator.getByRole(AriaRole.DIALOG, options).first(); // Get dialog by name in iFrame

                case "NAVIGATION":
                    return frameLocator.getByRole(AriaRole.NAVIGATION, options).first(); // Get navigation by name in iFrame

                case "MENU":
                    return frameLocator.getByRole(AriaRole.MENU, options).first(); // Get menu by name in iFrame

                case "MENUITEM":
                    return frameLocator.getByRole(AriaRole.MENUITEM, options).first(); // Get menu item by name in iFrame

                case "MENUITEMCHECKBOX":
                    return frameLocator.getByRole(AriaRole.MENUITEMCHECKBOX, options).first(); // Get menu item with checkbox by name in iFrame

                case "MENUITEMRADIO":
                    return frameLocator.getByRole(AriaRole.MENUITEMRADIO, options).first(); // Get menu item with radio button by name in iFrame

                case "TREE":
                    return frameLocator.getByRole(AriaRole.TREE, options).first(); // Get tree structure by name in iFrame

                case "TREEITEM":
                    return frameLocator.getByRole(AriaRole.TREEITEM, options).first(); // Get item in tree by name in iFrame

                case "GRID":
                    return frameLocator.getByRole(AriaRole.GRID, options).first(); // Get grid by name in iFrame

                case "GRIDCELL":
                    return frameLocator.getByRole(AriaRole.GRIDCELL, options).first(); // Get cell in grid by name in iFrame

                case "SEPARATOR":
                    return frameLocator.getByRole(AriaRole.SEPARATOR, options).first(); // Get separator by name in iFrame

                case "SWITCH":
                    return frameLocator.getByRole(AriaRole.SWITCH, options).first(); // Get switch by name in iFrame

                case "STATUS":
                    return frameLocator.getByRole(AriaRole.STATUS, options).first(); // Get status by name in iFrame

                case "BANNER":
                    return frameLocator.getByRole(AriaRole.BANNER, options).first(); // Get banner by name in iFrame

                case "FOOTER":
                    return frameLocator.getByRole(AriaRole.CONTENTINFO, options).first(); // Get footer by name in iFrame

                case "MAIN":
                    return frameLocator.getByRole(AriaRole.MAIN, options).first(); // Get main area by name in iFrame

                case "COMPLEMENTARY":
                    return frameLocator.getByRole(AriaRole.COMPLEMENTARY, options).first(); // Get complementary content in iFrame

                case "REGION":
                    return frameLocator.getByRole(AriaRole.REGION, options).first(); // Get region by name in iFrame

                case "ARTICLE":
                    return frameLocator.getByRole(AriaRole.ARTICLE, options).first(); // Get article section by name in iFrame

                case "FORM":
                    return frameLocator.getByRole(AriaRole.FORM, options).first(); // Get form by name in iFrame

                case "LOG":
                    return frameLocator.getByRole(AriaRole.LOG, options).first(); // Get log section by name in iFrame

                case "MARQUEE":
                    return frameLocator.getByRole(AriaRole.MARQUEE, options).first(); // Get marquee by name in iFrame

                case "TIMER":
                    return frameLocator.getByRole(AriaRole.TIMER, options).first(); // Get timer by name in iFrame

                case "TOOLTIP":
                    return frameLocator.getByRole(AriaRole.TOOLTIP, options).first(); // Get tooltip by name in iFrame

                case "TOOLBAR":
                    return frameLocator.getByRole(AriaRole.TOOLBAR, options).first(); // Get toolbar by name in iFrame

                case "PRESENTATION":
                    return frameLocator.getByRole(AriaRole.PRESENTATION, options).first(); // Get presentation content in iFrame

                case "FIGURE":
                    return frameLocator.getByRole(AriaRole.FIGURE, options).first(); // Get figure content in iFrame

                case "CONTENTINFO":
                    return frameLocator.getByRole(AriaRole.CONTENTINFO, options).first(); // Get content information by name in iFrame

                case "TEXT":
                    return frameLocator.getByText(locator).first(); // Get element by visible text in iFrame

                case "ROLE":
                    return frameLocator.getByRole(AriaRole.valueOf(locator)).first(); // Get element by ARIA role in iFrame

                case "ALTTEXT":
                    return frameLocator.getByAltText(locator).first(); // Get by alt text in iFrame

                case "TITLE":
                    return frameLocator.getByTitle(locator).first(); // Get element by title in iFrame

                case "PLACEHOLDER":
                    return frameLocator.getByPlaceholder(locator).first(); // Get element by placeholder in iFrame

                case "LABEL":
                    return frameLocator.getByLabel(locator).first(); // Get element by label in iFrame

                case "TESTID":
                    return frameLocator.getByTestId(locator).first(); // Get element by data-testid in iFrame

                case "ID":
                    return frameLocator.locator("#" + locator).first(); // Get element by ID in iFrame

                case "NAME":
                    return frameLocator.locator("[name='" + locator + "']").first(); // Get element by NAME in iFrame

                case "CLASS":
                    return frameLocator.locator("." + locator).first(); // Get element by CLASS in iFrame

                default:
                    throw new IllegalArgumentException("Unknown locator type: " + locatorType); // Handle unknown locator types in iFrame
            }
        } catch (Exception e) {
            // Handle exceptions and provide feedback if the locator cannot be found
            System.out.println("Locator not found on the page: " + e.getMessage());
        }

        throw new IllegalArgumentException("Locator not found for type: " + locatorType + " with name: " + locator); // Fallback error
    }
}