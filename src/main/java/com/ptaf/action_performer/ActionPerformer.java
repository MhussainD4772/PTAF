package com.ptaf.action_performer;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * ActionPerformer is a utility class that provides methods for interacting with UI elements
 * using Playwright's Locator API. This class encapsulates various actions that can be
 * performed on web elements, making it easier to automate user interactions.
 *
 * <h2>Usage:</h2>
 * <ol>
 *     <li>Create an instance of ActionPerformer.</li>
 *     <li>Use the 'performAction' method to execute a specific action on a Locator.</li>
 *     <li>Call 'waitForLocator' to ensure an element is visible before performing actions on it.</li>
 * </ol>
 *
 * <h2>Example:</h2>
 * <pre>
 * ActionPerformer actionPerformer = new ActionPerformer();
 * Locator myLocator = page.locator("selector-for-element");
 * actionPerformer.waitForLocator(myLocator);
 * actionPerformer.performAction("click", myLocator, null);
 * </pre>
 */
public class ActionPerformer {

    private static final Logger logger = LoggerFactory.getLogger(ActionPerformer.class);

    /**
     * Performs the specified action on the given Locator.
     *
     * @param page          The Playwright Page instance on which the action is performed.
     * @param action        The action to execute (e.g., "click", "fill", "select").
     * @param targetLocator The Locator that represents the target UI element.
     * @param value         The value used for the action, which can vary based on the action type.
     *                      For example, it can be text to fill an input or an attribute for
     *                      setting or clearing. It can also be null if not applicable.
     */
    public void performAction(Page page, String action, Locator targetLocator, String value) {
        try {
            switch (action.toLowerCase()) {
                case "click":
                    targetLocator.click(); // Clicks the element represented by the Locator
                    break;
                case "fill":
                    targetLocator.fill(value); // Fill the input field with the specified value
                    break;
                case "select":
                    targetLocator.selectOption(value); // Selects a single option from a dropdown
                    break;
                case "selectmultiple":
                    targetLocator.selectOption(value.split(",")); // Selects multiple options from a dropdown
                    break;
                case "check":
                    targetLocator.check(); // Checks a checkbox
                    break;
                case "uncheck":
                    targetLocator.uncheck(); // Unchecks a checkbox
                    break;
                case "hover":
                    targetLocator.hover(); // Hovers over the element
                    break;
                case "type":
                    targetLocator.type(value); // Types the specified value into an input field
                    break;
                case "press":
                    targetLocator.press(value); // Simulates pressing a keyboard key
                    break;
                case "dblclick":
                    targetLocator.dblclick(); // Double-clicks the element
                    break;
                case "screenshot":
                    // Takes a screenshot of the element and saves it to the specified file path
                    targetLocator.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(value)));
                    break;
                case "scroll":
                    // Scrolls the element into view smoothly
                    targetLocator.evaluate("element => element.scrollIntoView({ behavior: 'smooth', block: 'center' })");
                    break;
                case "focus":
                    targetLocator.focus(); // Sets focus on the element
                    break;
                case "blur":
                    targetLocator.evaluate("element => element.blur()"); // Removes focus from the element
                    break;
                case "clear":
                    targetLocator.clear(); // Clears the text input field
                    break;
                case "drag":
                    // Drags the target element to another specified element
                    Locator target = targetLocator.page().locator(value);
                    targetLocator.dragTo(target);
                    break;
                case "getattribute":
                    // Retrieves the value of a specified attribute and prints it
                    System.out.println("Attribute value: " + targetLocator.getAttribute(value));
                    break;
                case "setattribute":
                    // Sets a specified attribute value for the element
                    targetLocator.evaluate("(element, attributeValue) => element.setAttribute('value', attributeValue)", value);
                    break;
                case "removeattribute":
                    // Removes a specified attribute from the element
                    targetLocator.evaluate("(element, attributeName) => element.removeAttribute(attributeName)", value);
                    break;
                case "gettext":
                    // Retrieves and prints the text content of the element
                    String text = targetLocator.textContent();
                    System.out.println("Element text content: " + text);
                    break;

                case "get_and_contain_text":
                    // Retrieves and prints the text content of the element
                    String get_text = targetLocator.textContent();
                    System.out.println("Element text is contains to: " + get_text);
                    String elementText = targetLocator.textContent();
                    assertCondition(elementText.contains(get_text), "Element does not contain text: " + get_text);
                    break;
                case "getvalue":
                    // Retrieves and prints the current value of the element (for input fields)
                    String valueContent = targetLocator.inputValue();
                    System.out.println("Element value: " + valueContent);
                    break;
                case "hasvalue":
                    // Checks if the current value of the input element matches the expected value
                    String currentValue = targetLocator.inputValue();
                    if (currentValue != null && currentValue.equals(value)) {
                        System.out.println("Element has the expected value: " + value);
                    } else {
                        throw new AssertionError("Element does not have the expected value. Expected: " + value + ", but found: " + currentValue);
                    }
                    break;
                case "isvisible":
                    // Asserts that the element is visible on the page
                    boolean isVisible = targetLocator.isVisible();
                    assertCondition(isVisible, "Element is not visible.");
                    break;
                case "isenabled":
                    // Asserts that the element is enabled (interactable)
                    boolean isEnabled = targetLocator.isEnabled();
                    assertCondition(isEnabled, "Element is not enabled.");
                    break;
                case "ischecked":
                    // Asserts that a checkbox is checked
                    boolean isChecked = targetLocator.isChecked();
                    assertCondition(isChecked, "Element is not checked.");
                    break;
                case "isdisabled":
                    // Asserts that a checkbox is checked
                    boolean isDisabled = targetLocator.isDisabled();
                    assertCondition(isDisabled, "Element is not disabled.");
                    break;
                case "ishidden":
                    // Asserts that a checkbox is checked
                    boolean isHidden = targetLocator.isHidden();
                    assertCondition(isHidden, "Element is not disabled.");
                    break;
                case "exists":
                    // Asserts that the element exists in the DOM
                    boolean exists = targetLocator.count() > 0;
                    assertCondition(exists, "Element does not exist in the DOM.");
                    break;
                case "not_exists":
                    // Asserts that the element does not exist in the DOM
                    boolean notExists = targetLocator.count() == 0;
                    assertCondition(notExists, "Element exists in the DOM, but it should not.");
                    break;
                case "evaluate":
                    // Evaluates a custom JavaScript expression within the context of the element
                    targetLocator.evaluate(value);
                    break;
                case "waitforelement":
                    // Waits for the element to be present in the DOM and visible
                    targetLocator.waitFor();
                    break;
                case "waitforstate":
                    // Waits for the specified state (e.g., visible, hidden) of the element
                    targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.valueOf(value.toUpperCase())));
                    break;
                case "waitfortext":
                    // Waits until the specified text is present in the element’s content
                    targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                    if (!targetLocator.textContent().contains(value)) {
                        throw new AssertionError("Text '" + value + "' not found in element.");
                    }
                    break;
                case "waitforvalue":
                    // Waits until the input value matches the specified value
                    targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
                    if (!targetLocator.inputValue().equals(value)) {
                        throw new AssertionError("Expected value '" + value + "' not found in element.");
                    }
                    break;
                case "rightclick":
                    // Simulates a right-click on the element
                    targetLocator.click(new Locator.ClickOptions().setButton(MouseButton.valueOf("right")));
                    break;
                case "tap":
                    // Simulates a tap action on touch devices
                    targetLocator.tap();
                    break;
                case "uploadfile":
                    // Uploads a file to the element (input type='file')
                    targetLocator.setInputFiles(Paths.get(value)); // Assuming value is a file path
                    break;
                case "dragstart":
                    // Dispatches the dragstart event on the element
                    targetLocator.dispatchEvent("dragstart");
                    break;
                case "dragend":
                    // Dispatches the dragend event on the element
                    targetLocator.dispatchEvent("dragend");
                    break;
                case "input":
                    // Directly sets the input field's value
                    targetLocator.evaluate("(element, val) => element.value = val", value);
                    break;
                case "selectfile":
                    // Selects a file to upload, similar to uploadfile but for <input type="file">
                    targetLocator.setInputFiles(Paths.get(value)); // Selects file to upload
                    break;
                // More Playwright assertions can be included as required
                case "hastext":
                    // Asserts that the element contains the specified text
                    String locatorText = targetLocator.textContent();
                    assertCondition(locatorText.contains(value), "Element does not contain text: " + value);
                    break;
                case "hasclass":
                    // Asserts that the element has the specified CSS class
                    String className = value; // Assuming value is the class name
                    boolean hasClass = targetLocator.getAttribute("class").contains(className);
                    assertCondition(hasClass, "Element does not have expected class: " + className);
                    break;
                case "hasequalvalue":
                    // Asserts that the element's value equals the expected value
                    String actualValue = targetLocator.inputValue();
                    assertCondition(actualValue.equals(value), "Element's value is not equal to expected value: " + value);
                    break;
                case "isempty":
                    // Asserts that the input field is empty
                    String inputValue = targetLocator.inputValue();
                    assertCondition(inputValue.isEmpty(), "Element is not empty.");
                    break;
                case "file_chooser_for_upload":
                    // Wait for a file chooser to be displayed before invoking the click action
                    page.waitForFileChooser(() -> click(targetLocator)); // Assuming value is not needed here.
                    break;

                default:
                    throw new IllegalArgumentException("Unknown action: " + action); // Throws an error if action is not recognized
            }
        } catch (Exception e) {
            // Logs the error and throws a runtime exception if action execution fails
            logger.error("Error while performing action: {} for Target Locator " + targetLocator, e.getMessage());
            throw new RuntimeException("Action failed: for Target Locator " + targetLocator + e.getMessage(), e);
        }
    }

    /**
     * Clicks the specified Locator while handling exceptions.
     *
     * @param targetLocator The Locator that represents the UI element to be clicked.
     */
    private void click(Locator targetLocator) {
        try {
            targetLocator.click(); // Clicks the specified Locator
        } catch (Exception e) {
            logger.error("Error while clicking on target locator: {}", e.getMessage());
            throw new RuntimeException("Click action failed: " + e.getMessage(), e); // Raises error for failed click action
        }
    }

    /**
     * Asserts a condition and raises an AssertionError if the condition is false.
     *
     * @param condition    The condition to evaluate.
     * @param errorMessage The error message to display if the assertion fails.
     */
    private void assertCondition(boolean condition, String errorMessage) {
        if (!condition) {
            throw new AssertionError(errorMessage); // Throws an assertion error with the provided message
        }
    }

    /**
     * Waits for a specified Locator to become visible within a timeout.
     *
     * @param locator The Locator to wait for.
     *                The method will time out after 60 seconds if the element is not visible.
     */
    public void waitForLocator(Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE) // Wait until the element is visible
                    .setTimeout(60000)); // Timeout set to 60 seconds
        } catch (Exception e) {
            // Logs the error if waiting for the element fails
            logger.error("Failed to wait for the element to be displayed", e);
        }
    }

    /**
     * Skips the step if the Locator is not visible.
     *
     * @param locator The Locator to check for visibility.
     *                If the element is not visible, the method will log and pass the step without performing any action.
     */
    public void skipIfNotVisible(Locator locator) {
        try {
            // Check if the locator is visible
            if (locator.isVisible()) {
                logger.info("The element is visible. Proceeding with the step.");
                // You can add further actions here if needed
            } else {
                logger.warn("The element is not visible. Skipping the step.");
                // Skip logic, no further action taken
            }
        } catch (Exception e) {
            // Logs the error if checking the element's visibility fails
            logger.error("Failed to check the element's visibility. Skipping the step.", e);
        }
    }
}