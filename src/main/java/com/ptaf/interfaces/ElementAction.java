package com.ptaf.interfaces;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.FrameLocator;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.util.List;

/**
 * The ElementAction interface defines a set of methods for interacting with web elements
 * on a page or within a frame using the Playwright framework. This interface abstracts
 * common actions and assertions, allowing for easier automation of user interactions
 * and content verification within web applications.
 */
public interface ElementAction {

    /**
     * Performs a specified action on a web element located on the main page.
     *
     * @param page   The Playwright Page instance representing the current web page.
     * @param action The action to be performed on the element (e.g., "click", "type").
     * @param element The logical name or identifier of the element on which the action will be performed.
     * @param key    The locator string used to identify the specific element.
     * @param value  The value to be used with the action (e.g., text to be entered during typing).
     * @return true if the action was performed successfully; false otherwise.
     */
    boolean performActionPage(Page page, String action, String element, String key, String value);

    /**
     * Performs an action and returns a String result when applicable (e.g., gettext, getvalue).
     * If the action does not yield a result, return null.
     *
     * @param page    The Playwright Page instance.
     * @param action  The action to perform.
     * @param element Logical name of the element.
     * @param key     Locator string.
     * @param value   Optional value needed for the action.
     * @return The resulting String value, or null if not applicable.
     */
    String performActionPageWithReturn(Page page, String action, String element, String key, String value);

    /**
     * Performs a specified action on a web element located within a specified frame.
     *
     * @param frameLocator The Playwright FrameLocator instance used to identify the frame containing the element.
     * @param action The action to be performed on the element (e.g., "click", "type").
     * @param element The logical name or identifier of the element on which the action will be performed.
     * @param key    The locator string used to identify the specific element within the frame.
     * @param value  The value to be used with the action (e.g., text to be entered during typing).
     * @return true if the action was performed successfully; false otherwise.
     */
    boolean performActionFrame(FrameLocator frameLocator, String action, String element, String key, String value);

    /**
     * Performs a specified action on an element located in multiple nested frames if applicable.
     *
     * @param page        The Playwright Page instance representing the current web page.
     * @param iFrame      The locator string for the first iframe.
     * @param iFrame_2    The locator string for the second iframe, if applicable.
     * @param iFrame_3    The locator string for the third iframe, if applicable.
     * @param action      The action to be performed on the element (e.g., "click", "type").
     * @param element     The logical name or identifier of the element on which the action will be performed.
     * @param key         The locator string used to identify the specific element.
     * @param value       The value to be used with the action (e.g., text to be entered during typing).
     * @param frameLocator The Playwright FrameLocator instance when needed for frame operations.
     * @return true if the action was performed successfully; false otherwise.
     */
    boolean performActionPageFrame(Page page, String iFrame, String iFrame_2, String iFrame_3, String action, String element, String key, String value, FrameLocator frameLocator);

    String performActionPageFrameWithReturn(Page page, String iFrame, String iFrame_2, String iFrame_3, String action, String element, String key, String value, FrameLocator frameLocator);


    /**
     * Retrieves the ElementHandle for a specified element on the main page.
     *
     * @param page   The Playwright Page instance representing the current web page.
     * @param element The logical name or identifier of the element to retrieve.
     * @param key    The locator string used to identify the specific element.
     * @return true if the ElementHandle was obtained successfully; false otherwise.
     */
    boolean getElementHandlePage(Page page, String element, String key);

    /**
     * Retrieves the ElementHandle for a specified element within a frame.
     *
     * @param frameLocator The Playwright FrameLocator instance used to identify the frame.
     * @param element     The logical name or identifier of the element to retrieve.
     * @param key         The locator string used to identify the specific element.
     * @return true if the ElementHandle was obtained successfully; false otherwise.
     */
    boolean getElementHandleFrame(FrameLocator frameLocator, String element, String key);

    /**
     * Asserts that the text of a web element on the main page matches the expected text.
     *
     * @param page         The Playwright Page instance representing the current web page.
     * @param element      The logical name or identifier of the element whose text is being asserted.
     * @param key          The locator string used to identify the specific element.
     * @param expectedText The expected text that should match the element's actual text.
     * @return true if the element's text matches the expected text; false otherwise.
     */
    boolean assertElementTextPage(Page page, String element, String key, String expectedText);

    /**
     * Asserts that the text of a web element within a frame matches the expected text.
     *
     * @param frameLocator  The Playwright FrameLocator instance used to identify the frame containing the element.
     * @param element      The logical name or identifier of the element whose text is being asserted.
     * @param key          The locator string used to identify the specific element within the frame.
     * @param expectedText The expected text that should match the element's actual text.
     * @return true if the element's text matches the expected text; false otherwise.
     */
    boolean assertElementTextFrame(FrameLocator frameLocator, String element, String key, String expectedText);

    /**
     * Uploads a file to a specified element on the main page.
     *
     * @param page      The Playwright Page instance representing the current web page.
     * @param file_name The name of the file to be uploaded.
     * @param element   The logical name or identifier of the file upload element.
     * @param key       The locator string used to identify the specific element.
     */
    void uploadFile(Page page, String file_name, String element, String key);

    /**
     * Clicks on a document link identified by its name on the main page.
     *
     * @param page    The Playwright Page instance representing the current web page.
     * @param element The logical name or identifier of the document link to click.
     * @param key     The locator string used to identify the specific element.
     */
    void clickOnDocumentLinkName(Page page, String element, String key);

    /**
     * Retrieves the locator string for a specified element.
     *
     * @param element The logical name of the element.
     * @param key     The locator string used to identify the specific element.
     * @return The locator string corresponding to the specified element.
     */
    String getElement(String element, String key);

    /**
     * Retrieves a list of ElementHandles for a specified element on the main page or within a frame.
     *
     * @param page     The Playwright Page instance representing the current web page.
     * @param element  The logical name or identifier of the element.
     * @param key      The locator string used to identify the specific element.
     * @param frameLocator The Playwright FrameLocator instance if working within a frame.
     * @return A list of ElementHandles for the specified element.
     */
    List<ElementHandle> getElementHandleList(Page page, String element, String key, FrameLocator frameLocator);

    /**
     * Retrieves a Locator object for a specified element, considering the possible presence of nested frames.
     *
     * @param iFrame     The locator string for the first iframe, if applicable.
     * @param iFrame_2   The locator string for the second iframe, if applicable.
     * @param iFrame_3   The locator string for the third iframe, if applicable.
     * @param element    The logical name or identifier of the element.
     * @param key        The locator string used to identify the specific element.
     * @param page       The Playwright Page instance representing the current web page.
     * @param frameLocator The Playwright FrameLocator instance when needed for frame operations.
     * @return A Locator object for the specified element, considering nested frames.
     */
    Locator getLocator(String iFrame, String iFrame_2, String iFrame_3, String element, String key, Page page, FrameLocator frameLocator);

    /**
     * Retrieves the exact locator string for a specified element.
     *
     * @param element The logical name of the element.
     * @param key     The locator string used to identify the specific element.
     * @return The exact locator string corresponding to the specified element.
     */
    String getExactLocator(String element, String key);
}