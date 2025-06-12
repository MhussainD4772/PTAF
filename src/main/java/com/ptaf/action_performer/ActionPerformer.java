package com.ptaf.action_performer;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * ActionPerformer is a utility class for performing a wide variety of actions
 * on Playwright Locators within a Page. It handles clicks, form input, attribute manipulation,
 * element validation, scrolling, screenshots, and much more.
 *
 * It also supports returning values such as text content, attributes, or boolean results
 * when applicable, while performing non-returning actions like click or hover as void-equivalent.
 */
public class ActionPerformer {

    private static final Logger logger = LoggerFactory.getLogger(ActionPerformer.class);

    /**
     * Utility to format text content by placing each word on a new line.
     * Useful for improving text readability in logs or reports.
     */
    private static String formatTextForNewLine(String text) {
        return text.replaceAll(" ", "\n");
    }

    /**
     * Performs the specified action on a target Locator and returns the result if applicable.
     *
     * @param page          Playwright Page instance
     * @param action        The action name (e.g., "click", "gettext", "select")
     * @param targetLocator The element to perform the action on
     * @param value         An optional value for the action (e.g., text to fill, attribute name)
     * @return              A String result if the action yields one (e.g., gettext), otherwise null
     */
    public String performActionWithReturn(Page page, String action, Locator targetLocator, String value) {
        return performAction(page, action, targetLocator, value);
    }

    /**
     * Performs the specified action on a target Locator and returns the result if applicable.
     */
    public String performAction(Page page, String action, Locator targetLocator, String value) {
        try {
            switch (action.toLowerCase()) {
                case "click": targetLocator.click(); return null;
                case "fill": targetLocator.fill(value); return null;
                case "select": targetLocator.selectOption(value); return null;
                case "selectmultiple": targetLocator.selectOption(value.split(",")); return null;
                case "check": targetLocator.check(); return null;
                case "uncheck": targetLocator.uncheck(); return null;
                case "hover": targetLocator.hover(); return null;
                case "type": targetLocator.type(value); return null;
                case "press": targetLocator.press(value); return null;
                case "dblclick": targetLocator.dblclick(); return null;
                case "rightclick": targetLocator.click(new Locator.ClickOptions().setButton(MouseButton.valueOf("right"))); return null;
                case "tap": targetLocator.tap(); return null;
                case "input": targetLocator.evaluate("(element, val) => element.value = val", value); return null;
                case "screenshot": targetLocator.screenshot(new Locator.ScreenshotOptions().setPath(Paths.get(value))); return null;
                case "scroll": targetLocator.evaluate("element => element.scrollIntoView({ behavior: 'smooth', block: 'center' })"); return null;
                case "focus": targetLocator.focus(); return null;
                case "blur": targetLocator.evaluate("element => element.blur()"); return null;
                case "clear": targetLocator.clear(); return null;
                case "drag": Locator target = targetLocator.page().locator(value); targetLocator.dragTo(target); return null;
                case "dragstart": targetLocator.dispatchEvent("dragstart"); return null;
                case "dragend": targetLocator.dispatchEvent("dragend"); return null;
                case "uploadfile":
                case "selectfile": targetLocator.setInputFiles(Paths.get(value)); return null;
                case "file_chooser_for_upload": page.waitForFileChooser(() -> click(targetLocator)); return null;
                case "getattribute": return targetLocator.getAttribute(value);
                case "setattribute": targetLocator.evaluate("(el, val) => el.setAttribute('value', val)", value); return null;
                case "removeattribute": targetLocator.evaluate("(el, attr) => el.removeAttribute(attr)", value); return null;
                case "gettext": return targetLocator.textContent();
                case "get_and_contain_text": String getText = targetLocator.textContent(); assertCondition(getText.contains(getText), "Element does not contain expected text."); return getText;
                case "getvalue": return targetLocator.inputValue();
                case "hasvalue": String currentValue = targetLocator.inputValue(); assertCondition(currentValue.equals(value), "Expected: " + value + ", but found: " + currentValue); return currentValue;
                case "isvisible": boolean isVisible = targetLocator.isVisible(); assertCondition(isVisible, "Element is not visible."); return String.valueOf(isVisible);
                case "isenabled": boolean isEnabled = targetLocator.isEnabled(); assertCondition(isEnabled, "Element is not enabled."); return String.valueOf(isEnabled);
                case "ischecked": boolean isChecked = targetLocator.isChecked(); assertCondition(isChecked, "Element is not checked."); return String.valueOf(isChecked);
                case "isdisabled": boolean isDisabled = targetLocator.isDisabled(); assertCondition(isDisabled, "Element is not disabled."); return String.valueOf(isDisabled);
                case "ishidden": boolean isHidden = targetLocator.isHidden(); assertCondition(isHidden, "Element is not hidden."); return String.valueOf(isHidden);
                case "exists": boolean exists = targetLocator.count() > 0; assertCondition(exists, "Element does not exist."); return String.valueOf(exists);
                case "not_exists": boolean notExists = targetLocator.count() == 0; assertCondition(notExists, "Element exists but should not."); return String.valueOf(notExists);
                case "hastext": String locatorText = targetLocator.textContent(); assertCondition(locatorText.contains(value), "Text mismatch."); return locatorText;
                case "hasclass": boolean hasClass = targetLocator.getAttribute("class").contains(value); assertCondition(hasClass, "Class mismatch."); return String.valueOf(hasClass);
                case "hasequalvalue": String actualValue = targetLocator.inputValue(); assertCondition(actualValue.equals(value), "Value mismatch."); return actualValue;
                case "isempty": String inputValue = targetLocator.inputValue(); assertCondition(inputValue.isEmpty(), "Element is not empty."); return inputValue;
                case "waitforelement": targetLocator.waitFor(); return null;
                case "waitforstate": targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.valueOf(value.toUpperCase()))); return null;
                case "waitfortext": targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)); if (!targetLocator.textContent().contains(value)) throw new AssertionError("Text not found: " + value); return targetLocator.textContent();
                case "waitforvalue": targetLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)); if (!targetLocator.inputValue().equals(value)) throw new AssertionError("Value mismatch."); return targetLocator.inputValue();
                case "evaluate": targetLocator.evaluate(value); return null;
                default: throw new IllegalArgumentException("Unknown action: " + action);
            }
        } catch (Exception e) {
            logger.error("Error while performing action: {} for Target Locator {}", action, targetLocator, e);
            throw new RuntimeException("Action failed: " + action + " for Target Locator: " + targetLocator + " - " + e.getMessage(), e);
        }
    }

    private void click(Locator targetLocator) {
        try {
            targetLocator.click();
        } catch (Exception e) {
            logger.error("Error while clicking on target locator: {}", e.getMessage());
            throw new RuntimeException("Click action failed: " + e.getMessage(), e);
        }
    }

    private void assertCondition(boolean condition, String errorMessage) {
        if (!condition) {
            throw new AssertionError(errorMessage);
        }
    }

    public void waitForLocator(Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(60000.0));
        } catch (Exception e) {
            logger.error("Failed to wait for the element to be displayed", e);
        }
    }
}