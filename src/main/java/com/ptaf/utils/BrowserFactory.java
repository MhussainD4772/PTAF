package com.ptaf.utils;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * BrowserFactory is a utility class for creating Playwright Browser instances.
 * It abstracts the browser creation logic, allowing users to easily instantiate
 * different types of browsers (Chrome, Firefox, WebKit, Microsoft Edge) based on their requirements.
 */
public class BrowserFactory {

    private static final Logger logger = LoggerFactory.getLogger(BrowserFactory.class);
    static String headlessMode = ConfigurationProperties.getHeadlessMode();
    static String videoCapture = ConfigurationProperties.getVideoCapture(); // New property
    private static final String VIDEO_DIR = "test-output/captured-videos";


    /**
     * Enum representing the supported browser types.
     */
    public enum BrowserTypeEnum {
        CHROME,
        FIREFOX,
        WEBKIT,
        EDGE
    }

    /**
     * Creates and launches a Playwright Browser instance based on the specified browser type.
     *
     * @param browserTypeEnum The type of browser to create.
     * @return A Playwright Browser instance.
     */
    public static Browser createBrowser(BrowserTypeEnum browserTypeEnum) {
        Playwright playwright = Playwright.create();
        return switch (browserTypeEnum) {
            case CHROME -> {
                BrowserType browserType = playwright.chromium();
                yield launchBrowser(browserType);
            }
            case FIREFOX -> {
                BrowserType browserType = playwright.firefox();
                yield launchBrowser(browserType);
            }
            case WEBKIT -> {
                BrowserType browserType = playwright.webkit();
                yield launchBrowser(browserType);
            }
            case EDGE -> {
                boolean headless = Boolean.parseBoolean(headlessMode);
                logger.info("Launching Microsoft Edge with headless mode: {}", headless);
                yield playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setChannel("msedge")
                        .setHeadless(headless));
            }
        };
    }

    /**
     * Launches the specified browser type with the configured headless mode.
     *
     * @param browserType The BrowserType instance.
     * @return The launched Browser.
     */
    private static Browser launchBrowser(BrowserType browserType) {
        boolean headless = Boolean.parseBoolean(headlessMode);
        logger.info("Launching browser: {} with headless mode: {}", browserType.name().toUpperCase(), headless);
        return browserType.launch(new BrowserType.LaunchOptions().setHeadless(headless));
    }

    /**
     * Creates a browser context with optional video capture.
     *
     * @param browser The Browser instance.
     * @return A BrowserContext with or without video enabled.
     */
    public static BrowserContext createContextWithVideo(Browser browser) {
        boolean recordVideo = Boolean.parseBoolean(videoCapture);
        Browser.NewContextOptions contextOptions = new Browser.NewContextOptions();

        if (recordVideo) {
            logger.info("Video capture enabled.");
            contextOptions.setRecordVideoDir(Paths.get(VIDEO_DIR))
                    .setRecordVideoSize(1280, 720);
        } else {
            logger.info("Video capture disabled.");
        }

        return browser.newContext(contextOptions);
    }
}
