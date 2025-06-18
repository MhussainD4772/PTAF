package com.ptaf.ui.action_performer;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AssertionPerformer {

    private static final Logger logger = LoggerFactory.getLogger(AssertionPerformer.class);

    public void performAssertion(Page page, String action, Locator targetLocator, String value) {
        try {
            switch (action.toLowerCase()) {
                // --- Text-related ---
                case "contain":
                    assertThat(targetLocator).containsText(value);
                    break;
                case "notcontain":
                    assertThat(targetLocator).not().containsText(value);
                    break;
                case "hastext":
                    assertThat(targetLocator).hasText(value);
                    break;
                case "hastextexactly":
                    assertThat(targetLocator).hasText(value, new LocatorAssertions.HasTextOptions().setUseInnerText(true));
                    break;
                case "nothastext":
                    assertThat(targetLocator).not().hasText(value);
                    break;

                // --- Visibility ---
                case "isvisible":
                    assertThat(targetLocator).isVisible();
                    break;
                case "notvisible":
                    assertThat(targetLocator).not().isVisible();
                    break;
                case "ishidden":
                    assertThat(targetLocator).isHidden();
                    break;
                case "isattached":
                    assertThat(targetLocator).isAttached();
                    break;
                case "detached":
                    assertThat(targetLocator).not().isAttached();
                    break;

                // --- State ---
                case "enabled":
                    assertThat(targetLocator).isEnabled();
                    break;
                case "disabled":
                    assertThat(targetLocator).isDisabled();
                    break;
                case "checked":
                    assertThat(targetLocator).isChecked();
                    break;
                case "notchecked":
                    assertThat(targetLocator).not().isChecked();
                    break;
                case "focused":
                    assertThat(targetLocator).isFocused();
                    break;
                case "notfocused":
                    assertThat(targetLocator).not().isFocused();
                    break;

                // --- Attributes ---
                case "hasattribute":
                    String[] attrParts = value.split("=", 2);
                    assertThat(targetLocator).hasAttribute(attrParts[0], attrParts[1]);
                    break;
                case "nothasattribute":
                    String[] notAttrParts = value.split("=", 2);
                    assertThat(targetLocator).not().hasAttribute(notAttrParts[0], notAttrParts[1]);
                    break;
                case "hasclass":
                    assertThat(targetLocator).hasClass(value);
                    break;
                case "nothasclass":
                    assertThat(targetLocator).not().hasClass(value);
                    break;
                case "hasvalue":
                    assertThat(targetLocator).hasValue(value);
                    break;

                // --- Page-level ---
                case "hastitle":
                    assertThat(page).hasTitle(value);
                    break;
                case "hasurl":
                    assertThat(page).hasURL(value);
                    break;

                // --- Collections ---
                case "hascount":
                    assertThat(targetLocator).hasCount(Integer.parseInt(value));
                    break;

                // --- Regex & Pattern Matching (Optional)
                case "matchregex":
                    assertThat(targetLocator).hasText(Pattern.compile(value));
                    break;
                case "textstartswith":
                    assertThat(targetLocator).hasText(Pattern.compile("^" + Pattern.quote(value)));
                    break;
                case "textendswith":
                    assertThat(targetLocator).hasText(Pattern.compile(Pattern.quote(value) + "$"));
                    break;

                default:
                    throw new IllegalArgumentException("Unknown Assertion: " + action);
            }
        } catch (Exception e) {
            logger.error("Error while performing assertion: {} for Target Locator {}", action, targetLocator, e);
            throw new AssertionError("Assertion failed for action: " + action + ", Locator: " + targetLocator + ", Reason: " + e.getMessage(), e);
        }
    }
}
