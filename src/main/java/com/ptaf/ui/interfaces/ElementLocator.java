package com.ptaf.ui.interfaces;

import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * ElementLocator interface provides methods for locating web elements based on their locator type
 * (such as XPath or CSS) within both page and frame contexts using Playwright. This interface
 * abstracts the element retrieval mechanism, allowing for flexible interaction with web UI components.
 */
public interface ElementLocator {

    /**
     * Locates a web element in the context of a page based on the specified locator type.
     *
     * @param locatorType The type of locator used to identify the element (e.g., "XPATH", "CSS", "BUTTON").
     * @param page        The Playwright Page object representing the current page where the element is located.
     * @param locator     The locator string used to uniquely identify the element on the page.
     * @return A Locator object that can be used to interact with the specified element.
     * @throws IllegalArgumentException if the locator type is not recognized or invalid.
     */
    Locator getLocatorForType(String locatorType, Page page, String locator);

    /**
     * Locates a web element in the context of a frame based on the specified locator type.
     *
     * @param locatorType The type of locator used to identify the element (e.g., "XPATH", "CSS", "BUTTON").
     * @param frame       The Playwright FrameLocator object representing the current frame where the element is located.
     * @param locator     The locator string used to uniquely identify the element within the frame.
     * @return A Locator object that can be used to interact with the specified element within the frame.
     * @throws IllegalArgumentException if the locator type is not recognized or invalid.
     */
    Locator getLocatorForType(String locatorType, FrameLocator frame, String locator);
}