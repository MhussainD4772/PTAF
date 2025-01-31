package com.ptaf.utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * BrowserFactory is a utility class for creating Playwright Browser instances.
 * It abstracts the browser creation logic, allowing users to easily instantiate
 * different types of browsers (Chrome, Firefox, WebKit) based on their requirements.
 */
public class BrowserFactory {

    /**
     * Enum representing the supported browser types.
     */
    public enum BrowserTypeEnum {
        CHROME,   // Google Chrome browser
        FIREFOX,  // Mozilla Firefox browser
        WEBKIT    // Apple WebKit browser (Safari)
    }

    /**
     * Creates and launches a Playwright Browser instance based on the specified browser type.
     *
     * @param browserTypeEnum The type of browser to create (CHROME, FIREFOX, WEBKIT).
     * @return A Playwright Browser instance of the requested type.
     * @throws IllegalArgumentException if an unsupported browser type is specified.
     */
    public static Browser createBrowser(BrowserTypeEnum browserTypeEnum) {
        Playwright playwright = Playwright.create(); // Initialize Playwright
        BrowserType browserType; // Declare a BrowserType variable

        // Determine the browser type to launch based on the provided enum value
        switch (browserTypeEnum) {
            case CHROME:
                browserType = playwright.chromium(); // Get Chromium browser instance
                break;
            case FIREFOX:
                browserType = playwright.firefox(); // Get Firefox browser instance
                break;
            case WEBKIT:
                browserType = playwright.webkit(); // Get WebKit browser instance
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser type"); // Handle unsupported types
        }

        // Launch the browser in headless mode (set to false for visible mode)
        Browser browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(false));
        return browser; // Return the launched browser instance
    }
}