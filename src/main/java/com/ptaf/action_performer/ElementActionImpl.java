package com.ptaf.action_performer;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.ptaf.handlers.LocatorHandler;
import com.ptaf.helpers.ElementLocatorHelper;
import com.ptaf.interfaces.ElementAction;
import com.ptaf.page_helper.PageHelper;
import com.ptaf.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * ElementActionImpl is an implementation of the ElementAction interface that provides methods for
 * performing actions and assertions on web elements within an instance of a Playwright Page or FrameLocator.
 * It utilizes the ActionPerformer, LocatorHandler, and ElementLocatorHelper to manage interactions
 * with various elements on a web page.
 * <p>
 * Usage:
 * 1. Instantiate ElementActionImpl with a Page object.
 * 2. Call `performActionPage` or `performActionFrame` to execute specific actions on elements.
 * 3. Use the `assertElementTextPage` or `assertElementTextFrame` methods for validating the text
 * content of elements.
 */
public class ElementActionImpl extends PageHelper implements ElementAction {
    private static final Logger logger = LoggerFactory.getLogger(ElementActionImpl.class);

    private final ActionPerformer actionPerformer = new ActionPerformer(); // Handles action execution on Locators
    private final ElementLocatorHelper elementLocatorHelper = new ElementLocatorHelper(); // Assists in locating elements
    private final LocatorHandler locatorHandler = new LocatorHandler(); // Manages Locator creation based on type

    /**
     * Constructor for ElementActionImpl. Inheritance from PageHelper allows
     * initializing with a Page instance for element interaction.
     *
     * @param page The Playwright Page instance to interact with web elements.
     *             This page will be used to perform various actions and assertions on web elements.
     */
    public ElementActionImpl(Page page) {
        super(page); // Initialize the PageHelper with the provided Page
    }

    /**
     * Perform an action on an element located on the current Page.
     *
     * @param page    The Page where the action is performed. This is necessary to identify the context in which the action occurs.
     * @param action  The action to perform (e.g., "click", "fill"). This defines what operation will be executed on the element.
     * @param element The identifier for the target element. This should correspond to an element defined in the locator.
     * @param key     Additional identifier key for locating the element. This can be used to give context to which element is being referenced.
     * @param value   The value associated with the action (if applicable). For instance, this could be the text to fill into a text input.
     * @return boolean indicating success or failure of the action. This can be checked by the caller to ensure the intended operation succeeded.
     */
    @Override
    public boolean performActionPage(Page page, String action, String element, String key, String value) {
        // Delegates to the private method to perform the desired action on the page with a null frame context
        return performAction(page, null, null, null, action, element, key, value, null);
    }

    /**
     * Perform an action on an element located in a specified Frame.
     *
     * @param frameLocator The FrameLocator for identifying the frame containing the element. This is essential when interacting with nested frames.
     * @param action       The action to perform. Similar to performActionPage, defines what operation will occur on the element.
     * @param element      The identifier for the target element within the frame.
     * @param key          Additional identifier key for locating the element.
     * @param value        The value associated with the action (if applicable).
     * @return boolean indicating success or failure of the action, allowing verification of the outcome to the caller.
     */
    @Override
    public boolean performActionFrame(FrameLocator frameLocator, String action, String element, String key, String value) {
        // Delegates to the private method to perform the desired action with a frame context
        return performAction(null, null, null, null, action, element, key, value, frameLocator);
    }

    /**
     * Perform an action on an element located within nested iframes in a specified Page.
     *
     * @param page         The Playwright Page where the action is performed.
     * @param iFrame       The identifier for the first iframe containing the target element.
     * @param iFrame_2     The identifier for the second nested iframe.
     * @param iFrame_3     The identifier for the third nested iframe, if applicable.
     * @param action       The action to perform.
     * @param element      The identifier for the target element within the nested iframes.
     * @param key          Additional identifier key for locating the element.
     * @param value        The value associated with the action (if applicable).
     * @param frameLocator The FrameLocator for the active frame context.
     * @return boolean indicating success or failure of the action.
     */
    @Override
    public boolean performActionPageFrame(Page page, String iFrame, String iFrame_2, String iFrame_3, String action, String element, String key, String value, FrameLocator frameLocator) {
        // Similar delegation as other performAction methods, specific to nested iframes
        return performAction(page, iFrame, iFrame_2, iFrame_3, action, element, key, value, null);
    }

    /**
     * Retrieves a list of element handles based on a specified element located within a Page context,
     * and returns a boolean indicating if any matching elements were found.
     *
     * @param page    The Playwright Page instance on which to locate the element. This provides context for the search.
     * @param element The identifier for the target element. It should correspond to a defined element name in the locator.
     * @param key     An additional key used to assist in locating the element. This adds specificity to the search parameters.
     * @return boolean indicating whether one or more element handles were located on the Page. This aids in the validation of element availability.
     */
    @Override
    public boolean getElementHandlePage(Page page, String element, String key) {
        // Use the getElementHandleList method to retrieve all ElementHandles matching the specified element in the Page context
        List<ElementHandle> elementHandles = getElementHandleList(page, element, key, null);

        // Returns true if element handles were found; otherwise, returns false
        return !elementHandles.isEmpty();
    }

    /**
     * Retrieves a list of element handles based on a specified element located within a Frame context,
     * and returns a boolean indicating if any matching elements were found.
     *
     * @param frameLocator The FrameLocator instance for identifying the frame containing the target element. Necessary for the correct context.
     * @param element      The identifier for the target element within the frame.
     * @param key          An additional key used to assist in locating the element.
     * @return boolean indicating whether one or more element handles were located within the specified frame.
     */
    @Override
    public boolean getElementHandleFrame(FrameLocator frameLocator, String element, String key) {
        // Use the getElementHandleList method to retrieve all ElementHandles matching the specified element in the Frame context
        List<ElementHandle> elementHandles = getElementHandleList(null, element, key, frameLocator);

        // Returns true if element handles were found; otherwise, returns false
        return !elementHandles.isEmpty();
    }

    /**
     * Asserts that the text content of an element on the current Page matches the expected text.
     *
     * @param page         The Page where the assertion is performed. It provides context for locating the element.
     * @param element      The identifier for the target element. Should match a previously defined element in the locator.
     * @param key          Additional identifier key for locating the element.
     * @param expectedText The expected text to match against the element's content. This is the value that we expect to see in the element.
     * @return boolean indicating whether the text matches the expected value. This can be used to verify correctness in UI testing.
     */
    @Override
    public boolean assertElementTextPage(Page page, String element, String key, String expectedText) {
        // Delegates to the assertion helper method with null frame context
        return assertElementText(page, element, key, expectedText, null);
    }

    /**
     * Asserts that the text content of an element in a specified Frame matches the expected text.
     *
     * @param frameLocator The FrameLocator for identifying the frame containing the element. Essential for correct element context.
     * @param element      The identifier for the target element.
     * @param key          Additional identifier key for locating the element.
     * @param expectedText The expected text to match against the element's content.
     * @return boolean indicating whether the text matches the expected value. Useful for verifying UI correctness in frame contexts.
     */
    @Override
    public boolean assertElementTextFrame(FrameLocator frameLocator, String element, String key, String expectedText) {
        // Delegates with null page context for frame-specific assertion
        return assertElementText(null, element, key, expectedText, frameLocator);
    }
    /**
     * Performs a specified action on an element located on the current Page and returns a string result.
     * This is typically used for actions like "gettext", "getvalue", or "getattribute" where the action
     * returns a value instead of just performing an interaction.
     *
     * @param page    The Playwright Page instance where the action should be executed.
     * @param action  The name of the action to perform (e.g., "gettext", "getattribute").
     * @param element The logical name of the element defined in the configuration (e.g., YAML).
     * @param key     The key used to locate the specific variant of the element.
     * @param value   An optional parameter used by some actions (e.g., attribute name for "getattribute").
     * @return        The result of the action as a String, or null if the locator was not found or an exception occurred.
     */
    @Override
    public String performActionPageWithReturn(Page page, String action, String element, String key, String value) {
        try {
            // Get the locator for the given element and key in the context of the provided page
            Locator targetLocator = getLocatorBasedOnPage(page, element, key);

            // If the locator cannot be resolved, log the error and return null
            if (targetLocator == null) {
                logger.error("Locator not found for element: {} with key: {}", element, key);
                return null;
            }

            // Wait until the element is ready for interaction (e.g., visible or attached to the DOM)
            actionPerformer.waitForLocator(targetLocator);

            // Perform the action and return the result (e.g., text content, value, attribute, etc.)
            return actionPerformer.performActionWithReturn(page, action, targetLocator, value);

        } catch (Exception e) {
            // Log any exception that occurred during execution and return null
            logger.error("Exception in performActionPageWithReturn for element '{}' and action '{}':", element, action, e);
            return null;
        }
    }

    /**
     * Performs the specified action on an element inside nested iframes within a Page context,
     * and returns a string result (if the action provides one, like "gettext" or "getattribute").
     *
     * @param page         The Playwright Page instance to perform the action on.
     * @param iFrame       The first-level iframe name or ID.
     * @param iFrame_2     The second-level nested iframe (optional).
     * @param iFrame_3     The third-level nested iframe (optional).
     * @param action       The action to perform (e.g., "gettext", "getvalue").
     * @param element      The identifier of the element on which the action is to be performed.
     * @param key          The key used to locate the element (from config/YAML).
     * @param value        The value to pass to the action, if needed (e.g., attribute name).
     * @param frameLocator Not used here (should be null); included for signature consistency.
     * @return             A string result from the action, or null if action fails or produces no output.
     */
    @Override
    public String performActionPageFrameWithReturn(Page page, String iFrame, String iFrame_2, String iFrame_3, String action, String element, String key, String value, FrameLocator frameLocator) {
        try {
            Locator targetLocator = getLocatorBasedOnPageFrame(page, iFrame, iFrame_2, iFrame_3, element, key);
            if (targetLocator == null) {
                logger.error("Locator not found for nested frame element: {} with key: {}", element, key);
                return null;
            }

            actionPerformer.waitForLocator(targetLocator);
            return actionPerformer.performActionWithReturn(page, action, targetLocator, value);
        } catch (Exception e) {
            logger.error("Exception in performActionPageFrameWithReturn for element '{}' and action '{}':", element, action, e);
            return null;
        }
    }

    /**
     * Internal method for performing actions on either a Page or FrameLocator.
     *
     * @param page         The Page object (if applicable). Necessary to determine the operation context.
     * @param iFrame       The identifier for the iframe (if applicable). Specifies nested iframes that may contain the target element.
     * @param action       The action to perform. Defines what operation will happen on the identified element.
     * @param element      The identifier for the target element. Used to locate the element for the action.
     * @param key          Additional identifier key for locating the element. Adds specificity to which element is being acted upon.
     * @param value        The value associated with the action (if applicable). For example, the text to be filled or passed in.
     * @param frameLocator The FrameLocator (if applicable). Allows actions on nested frames.
     * @return boolean indicating success or failure of the action. Useful for determining the outcome of the attempted operation.
     */
    private boolean performAction(Page page, String iFrame, String iFrame_2, String iFrame_3, String action, String element, String key, String value, FrameLocator frameLocator) {
        Locator targetLocator = null;

        // Determine the correct Locator based on the context (Page, FrameLocator, or Page with iframe)
        try {
            if (page != null && iFrame == null) {
                targetLocator = getLocatorBasedOnPage(page, element, key); // Retrieve locator in page context
            } else if (frameLocator != null) {
                targetLocator = getLocatorBasedOnFrame(frameLocator, element, key); // Retrieve locator in frame context
            } else if (page != null) {
                targetLocator = getLocatorBasedOnPageFrame(page, iFrame, iFrame_2, iFrame_3, element, key); // Handle nested iframes
            } else {
                throw new IllegalArgumentException("Both page and frameLocator cannot be null."); // Ensure at least some context is provided
            }
            if (targetLocator == null) {
                throw new IllegalStateException("Failed to resolve a target Locator."); // Validate locator resolution
            }
            // Ensure the Locator is ready before performing actions
            actionPerformer.waitForLocator(targetLocator); // Wait for locator to be in state to interact
            // Execute the action using ActionPerformer
            actionPerformer.performAction(page, action, targetLocator, value); // Perform specified action on locator
            return true; // Action was successful
        } catch (IllegalArgumentException e) {
            logger.error("Invalid argument provided: {}", e.getMessage()); // Log error if invalid arguments are detected
        } catch (IllegalStateException e) {
            logger.error("Locator resolution error: {}", e.getMessage()); // Log locator resolution issues
        } catch (Exception e) {
            // Log other errors and return false to indicate failure
            logger.error("Error while performing action '{}' on TargetLocator '{}'", action, targetLocator, e); // Log detailed error for troubleshooting
        }

        return false; // Indicate action failed
    }

    /**
     * Uploads a file through a file chooser dialog by clicking on a specified element.
     *
     * @param page      The Playwright Page instance to perform the upload action on.
     * @param file_name The name of the file to upload, which should correlate with a valid file path.
     * @param element   The identifier for the target element that triggers the file chooser.
     * @param key       Additional identifier key used for locating the file chooser element.
     */
    @Override
    public void uploadFile(Page page, String file_name, String element, String key) {
        // Wait for the file chooser dialog to appear and upload a file using the specified element
        FileChooser fileChooser = page.waitForFileChooser(() ->
                page.click(getElement(element, key))); // Clicks the element to open the file chooser
        fileChooser.setFiles(Paths.get(getElement(element, file_name))); // Uploads the specified file
    }

    /**
     * Clicks on a document link identified by its name.
     *
     * @param page    The Playwright Page instance where the click action is performed.
     * @param element The identifier for the document link element.
     * @param key     Additional identifier key for locating the document link.
     */
    @Override
    public void clickOnDocumentLinkName(Page page, String element, String key) {
        String documentLinkName = getElement(element, key); // Retrieves the link name from the configuration
        String fileName = extractFileName(documentLinkName); // Extracts the actual file name from the input
        System.out.println(fileName); // Output the extracted file name for logging purposes
        try {
            // Click the link by its ARIA role and name using the retrieved selector
            page.getByRole(AriaRole.LINK,
                    new Page.GetByRoleOptions().setName(fileName)).click(); // Perform click action on link
        } catch (Exception e) {
            // Log any exceptions that occur during the role-based click operation
            logger.error("Failed to click on element by Role '{}'", element + key, e); // Log error context
        }
    }

    // Method to extract and return the file name from a given path
    public static String extractFileName(String filePath) {
        // Split the string based on the "/" delimiter to isolate the file name from the full path
        String[] parts = filePath.split("/");

        // The last part of the array is the file name
        return parts[parts.length - 1]; // Return the last segment as the file name
    }

    /**
     * Retrieves the selector for a given element and key from a YAML configuration.
     *
     * @param element The identifier for the target element to be retrieved.
     * @param key     The key used to obtain the specific locator for the element.
     * @return The selector string retrieved from the YAML configuration.
     */
    public String getElement(String element, String key) {
        try {
            return (String) YamlReader.get("elements." + element + "." + key); // Retrieve selector from YAML
        } catch (Exception e) {
            logger.error("Failed to retrieve selector for element '{}'", element + key, e); // Log error on retrieval failure
            throw e; // Rethrow exception for upstream handling
        }
    }

    /**
     * Internal method to assert the text content of an element, applicable to Page or Frame.
     *
     * @param page         The Page object (if applicable). Defines context for locating the element.
     * @param element      The identifier for the target element. Specifies which element's content is asserted.
     * @param key          Additional identifier key for locating the element.
     * @param expectedText The expected text to match against the element's content. This is the value expected to be present.
     * @param frameLocator The FrameLocator (if applicable). Allows for the assertion to occur in frame context.
     * @return boolean indicating whether the text matches the expected value. Enables verification of text content accuracy.
     */
    private boolean assertElementText(Page page, String element, String key, String expectedText, FrameLocator frameLocator) {
        try {
            // Determine the correct Locator based on the context
            Locator targetLocator = (page != null)
                    ? getLocatorBasedOnPage(page, element, key) // Retrieve locator in page context
                    : getLocatorBasedOnFrame(frameLocator, element, key); // Retrieve locator in frame context
            String actualText = targetLocator.textContent(); // Retrieve the actual text content of the locator
            boolean isTextMatching = expectedText.equals(actualText); // Compare expected text with actual text

            // Log the actual and expected text content for visibility in test execution logs
            logger.info("Asserting text on element '{}': expected '{}', actual '{}'", element, expectedText, actualText);

            if (!isTextMatching) {
                logger.error("Text mismatch: expected '{}' but found '{}'", expectedText, actualText); // Log mismatch error details
            }
            return isTextMatching; // Return the result of the text comparison
        } catch (Exception e) {
            // Log the error details and return false to indicate failure
            logger.error("Error while asserting text on element '{}'", element, e); // Log detailed error for troubleshooting
            return false; // Indicate failure in assertion
        }
    }

    /**
     * Retrieves a list of ElementHandles for a given element, considering page and frame contexts.
     *
     * @param page         The Page object (if applicable). Necessary for establishing the current context for element retrieval.
     * @param element      The identifier for the target element. This helps specify what elements are being sought.
     * @param key          Additional identifier key for locating the element. Increases specificity for element searching.
     * @param frameLocator The FrameLocator (if applicable). Used for retrieving elements from frames, which alters the context of the search.
     * @return List<ElementHandle> containing the element handles, or an empty list if not found. This is useful for bulk operations on elements.
     */
    @Override
    public List<ElementHandle> getElementHandleList(Page page, String element, String key, FrameLocator frameLocator) {
        List<ElementHandle> elementHandles = new ArrayList<>();
        try {
            Locator targetLocator = (page != null)
                    ? getLocatorBasedOnPage(page, element, key) // Retrieve in page context
                    : getLocatorBasedOnFrame(frameLocator, element, key); // Retrieve in frame context

            if (targetLocator != null) {
                elementHandles = targetLocator.elementHandles(); // Gather the list of ElementHandles from the resolved locator
            } else {
                logger.error("Target locator for element '{}' could not be determined.", element); // Log locator resolution issues
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve element handles for '{}'", element, e); // Log failure on handle retrieval
        }
        return elementHandles; // Return the list of ElementHandles (possibly empty)
    }

    /**
     * Retrieves a Locator based on a given Page context.
     *
     * @param page    The Page from which to locate the element. It provides a specific context for element operations.
     * @param element The identifier for the target element. Specifies what element we want to resolve to a Locator.
     * @param key     Additional identifier key for locating the element. Increases specificity for the locator retrieval.
     * @return The Locator for the identified element. This is essential to operate on the desired web element in the context.
     */
    private Locator getLocatorBasedOnPage(Page page, String element, String key) {
        return getLocator(null, null, null, element, key, page, null); // Fetch locator for page context
    }

    /**
     * Retrieves a Locator based on a given Screen context handling nested iframes.
     *
     * @param page     The Page from which to locate the element.
     * @param iFrame   The identifier for the first iframe.
     * @param iFrame_2 The identifier for the second nested iframe.
     * @param iFrame_3 The identifier for the third nested iframe.
     * @param element  The identifier for the target element.
     * @param key      Additional identifier key for locating the element.
     * @return The Locator for the identified element in the context of nested iframes.
     */
    private Locator getLocatorBasedOnPageFrame(Page page, String iFrame, String iFrame_2, String iFrame_3, String element, String key) {
        return getLocator(iFrame, iFrame_2, iFrame_3, element, key, page, null); // Retrieve locator considering iframes in a page
    }

    /**
     * Retrieves a Locator based on a given Frame context.
     *
     * @param frameLocator The FrameLocator containing the element. This is crucial for identifying elements properly within frame context.
     * @param element      The identifier for the target element. Helps in locating the correct element within the frame.
     * @param key          Additional identifier key for locating the element. Provides further specificity in element identification.
     * @return The Locator for the identified element. This is how we fetch the element we want to interact with in the frame context.
     */
    private Locator getLocatorBasedOnFrame(FrameLocator frameLocator, String element, String key) {
        return getLocator(null, null, null, element, key, null, frameLocator); // Fetch locator in frame context
    }

    /**
     * Retrieves a Locator for the specified element by determining its type and context.
     *
     * @param element      The identifier for the target element. Defines which element we are seeking a locator for.
     * @param key          Additional identifier key for locating the element. This allows for finer granularity in locating elements.
     * @param page         The Page object (if applicable). Important for resolving the context of the locator retrieval.
     * @param frameLocator The FrameLocator (if applicable). Alters the context for element resolution if the element is within a frame.
     * @return The Locator for the identified element. This enables interactions with the specified web element.
     * @throws RuntimeException If the context (Page or Frame) is unknown. It ensures that resolution cannot occur without valid context.
     */
    @Override
    public Locator getLocator(String iFrame, String iFrame_2, String iFrame_3, String element, String key, Page page, FrameLocator frameLocator) {
        // Get the locator value based on the provided element and key
        String locatorValue = elementLocatorHelper.getElement(element, key); // Fetch locator configuration
        String locatorType = elementLocatorHelper.getLocatorType(locatorValue); // Identify the type of locator (id, class, etc.)
        String locator = elementLocatorHelper.getLocator(locatorValue); // Extract the locator string for usage

        try {
            // Return the appropriate Locator based on whether being called in a Page or Frame context, or within nested iframes
            if (iFrame == null) {
                return locatorHandler.getLocatorForType(locatorType, page, locator); // Return locator for specified page context
            } else if (frameLocator != null) {
                return locatorHandler.getLocatorForType(locatorType, frameLocator, locator); // Return locator for specified frame context
            } else if (page != null) {
                return locatorHandler.getLocatorForTypeFrame(page, iFrame, iFrame_2, iFrame_3, locatorType, locator); // Handle nested iframes
            }
            // If both page and frameLocator are null, handle the unknown context case
            throw new IllegalArgumentException("Both page and frameLocator cannot be null"); // Ensure context is defined

        } catch (IllegalArgumentException e) {
            // Wrap and throw runtime exception for failed locator resolution
            throw new RuntimeException("Failed to get locator for: " + (page != null ? "Page" : "Frame") + " LocatorType "
                    + locatorType + " Locator " + locator + " Locator Value " + locatorValue, e);
        }
    }

    /**
     * Retrieves the exact locator string corresponding to a given element and key defined in configuration.
     *
     * @param element The identifier for the target element to retrieve.
     * @param key     The key used to obtain the specific locator for the element.
     * @return The exact locator string retrieved from the configuration. This is directly usable in interactions.
     */
    @Override
    public String getExactLocator(String element, String key) {
        // Get the locator value based on the element and key
        String locatorValue = elementLocatorHelper.getElement(element, key); // Resolve the locator configuration
        return elementLocatorHelper.getLocator(locatorValue); // Extract and return the locator string for direct usage
    }

}