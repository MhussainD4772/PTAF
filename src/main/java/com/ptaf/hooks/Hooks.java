package com.ptaf.hooks;

import com.ptaf.pages.PageCommonMethods;
import com.ptaf.utils.BrowserFactory;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hooks class manages the lifecycle of the browser and page instances for Cucumber scenarios.
 * This includes setup and teardown operations, ensuring proper resource management
 * throughout the execution of test scenarios.
 */
public class Hooks {

    // Thread-local variables to ensure each thread has its own instance of Browser, Page, etc.
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Scenario> scenarioThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<PageCommonMethods> pageCommonMethodsThreadLocal = new ThreadLocal<>();

    private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

    // Flags to track scenario states
    private static boolean isLastScenarioFeature = false;
    private static boolean isFirstScenarioInFeature = true;

    /**
     * Called before each scenario execution.
     * Initializes the browser and page context if not already done.
     *
     * @param scenario The Cucumber Scenario being executed.
     */
    @Before
    public void setUp(Scenario scenario) {
        scenarioThreadLocal.set(scenario); // Store the current scenario

        // Check if the current scenario is marked as "@LastScenario"
        if (scenario.getSourceTagNames().contains("@LastScenario")) {
            isLastScenarioFeature = true; // Set flag indicating this is the last scenario
        }

        // If this is the last scenario and it's not the first one, reuse the browser
        if (isLastScenarioFeature && !isFirstScenarioInFeature) {
            logger.info("Reusing browser instance for feature with @LastScenario tag.");
            return; // Skip browser reinitialization
        }

        try {
            // Define the type of browser to initialize
            BrowserFactory.BrowserTypeEnum browserTypeEnum = BrowserFactory.BrowserTypeEnum.CHROME;
            Browser browser = BrowserFactory.createBrowser(browserTypeEnum); // Create a new browser instance
            browserThreadLocal.set(browser); // Store browser instance in thread-local variable

            // Create a new browser context
            BrowserContext context = browser.newContext();
            contextThreadLocal.set(context); // Store browser context in thread-local variable

            // Open a new page in the context
            Page page = context.newPage();
            pageThreadLocal.set(page); // Store page instance in thread-local variable

            // Initialize PageCommonMethods with the current page
            PageCommonMethods pageCommonMethods = new PageCommonMethods(page);
            pageCommonMethodsThreadLocal.set(pageCommonMethods);

            logger.info("Browser setup completed for scenario: {}", scenario.getName()); // Log successful setup
        } catch (Exception e) {
            // Log any errors during the browser setup process
            logger.error("Error setting up the browser for scenario: {}", e.getMessage());
            throw new RuntimeException("Browser setup failed", e); // Rethrow with failure message
        }
    }

    /**
     * Called after each scenario execution.
     * Finalizes the scenario and cleans up resources if necessary.
     *
     * @param scenario The Cucumber Scenario that has just completed executing.
     */
    @After
    public void tearDown(Scenario scenario) {
        try {
            // If the scenario passed, finalize it
            if (scenario.getStatus() == io.cucumber.java.Status.PASSED) {
                PageCommonMethods pageCommonMethods = pageCommonMethodsThreadLocal.get();
                if (pageCommonMethods != null) {
                    pageCommonMethods.finalizeScenario(); // Perform finalization steps for the scenario
                }
            }
        } catch (Exception e) {
            logger.error("Error during scenario teardown: {}", e.getMessage(), e); // Log any errors during teardown
        } finally {
            // If it's the last scenario in the feature, skip closing the browser
            if (isLastScenarioFeature) {
                logger.info("Skipping browser closure for feature with @LastScenario tag.");
                isFirstScenarioInFeature = false;
            } else {
                closeBrowserResources(); // Always close browser resources for non-last scenarios
            }
        }
    }

    /**
     * Closes the browser, context, and page resources.
     * This method is called during the teardown process.
     */
    private void closeBrowserResources() {
        try {
            Page page = pageThreadLocal.get(); // Retrieve the current page instance
            if (page != null && !page.isClosed()) {
                page.close(); // Close the current page
//                logger.info("Page closed."); // Log page closure
            }
        } catch (Exception e) {
            logger.error("Error closing the page: {}", e.getMessage(), e); // Log any errors during page closure
        } finally {
            pageThreadLocal.remove(); // Remove page from thread-local storage
        }

        try {
            BrowserContext context = contextThreadLocal.get(); // Retrieve the browser context
            if (context != null) {
                context.close(); // Close the browser context
//                logger.info("Browser context closed."); // Log context closure
            }
        } catch (Exception e) {
            logger.error("Error closing the browser context: {}", e.getMessage(), e); // Log any errors during context closure
        } finally {
            contextThreadLocal.remove(); // Remove context from thread-local storage
        }

        try {
            Browser browser = browserThreadLocal.get(); // Retrieve the browser instance
            if (browser != null) {
                browser.close(); // Close the browser
                logger.info("Browser closed."); // Log browser closure
            }
        } catch (Exception e) {
            logger.error("Error closing the browser: {}", e.getMessage(), e); // Log any errors during browser closure
        } finally {
            browserThreadLocal.remove(); // Remove browser from thread-local storage
        }
    }

    /**
     * Retrieves the current Page instance.
     *
     * @return The current Page object.
     * @throws IllegalStateException If the page is closed or not initialized.
     */
    public static Page getPage() {
        Page page = pageThreadLocal.get(); // Get the current page instance
        if (page == null || page.isClosed()) {
            throw new IllegalStateException("The page is closed or not initialized."); // Raise error if page is uninitialized
        }
        return page; // Return the page instance
    }

    /**
     * Retrieves the current Browser instance.
     *
     * @return The current Browser object.
     * @throws IllegalStateException If the browser is not initialized.
     */
    public static Browser getBrowser() {
        Browser browser = browserThreadLocal.get(); // Get the current browser
        if (browser == null) {
            throw new IllegalStateException("The browser is not initialized."); // Raise error if browser is uninitialized
        }
        return browser; // Return the browser instance
    }

    /**
     * Retrieves the current Scenario being executed.
     *
     * @return The current Scenario object.
     */
    public static Scenario getCurrentScenario() {
        return scenarioThreadLocal.get(); // Return the current scenario instance
    }

    /**
     * Sets the current Scenario being executed.
     *
     * @param scenario The Cucumber Scenario to be set.
     */
    public static void setCurrentScenario(Scenario scenario) {
        scenarioThreadLocal.set(scenario); // Store the current scenario instance
    }
}