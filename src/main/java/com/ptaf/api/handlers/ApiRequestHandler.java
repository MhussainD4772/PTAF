package com.ptaf.api.handlers;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.ptaf.ui.utils.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the lifecycle of the Playwright APIRequestContext.
 * This class ensures that each test thread gets its own API context, configured with the correct
 * base URL and authentication headers for the service being tested.
 */
public class ApiRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiRequestHandler.class);
    private static final ThreadLocal<APIRequestContext> apiContextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ApiRequestHandler() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates and retrieves the APIRequestContext for the current thread.
     * If a context for the thread does not exist, it creates a new one based on the
     * service name defined in config.yml.
     *
     * @param serviceName The key for the service in the 'api_services' section of config.yml (e.g., "jsonplaceholder").
     * @return A thread-safe APIRequestContext object.
     */
    public static APIRequestContext getContext(String serviceName) {
        if (apiContextThreadLocal.get() == null) {
            logger.info("Creating new APIRequestContext for service: {}", serviceName);

            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);

            // Read configuration for the specified API service
            String baseUrl = ConfigurationProperties.getValue("api_services." + serviceName + ".base_url");
            String tokenEnvVar = ConfigurationProperties.getValue("api_services." + serviceName + ".auth_token_env");

            if (baseUrl == null) {
                throw new IllegalArgumentException("Base URL for API service '" + serviceName + "' not found in config.yml.");
            }

            APIRequest.NewContextOptions options = new APIRequest.NewContextOptions().setBaseURL(baseUrl);

            // Securely add authentication headers if a token is configured
            if (tokenEnvVar != null && !tokenEnvVar.isEmpty()) {
                String token = System.getenv(tokenEnvVar);
                if (token == null || token.isEmpty()) {
                    throw new IllegalArgumentException("API auth token environment variable '" + tokenEnvVar + "' is not set or is empty.");
                }
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                options.setExtraHTTPHeaders(headers);
                logger.info("API context created with Authorization header.");
            }

            apiContextThreadLocal.set(playwright.request().newContext(options));
        }
        return apiContextThreadLocal.get();
    }

    /**
     * Disposes of the APIRequestContext and closes the Playwright instance for the current thread.
     * This must be called in an @After hook to clean up resources.
     */
    public static void disposeContext() {
        APIRequestContext context = apiContextThreadLocal.get();
        if (context != null) {
            context.dispose();
            apiContextThreadLocal.remove();
            logger.info("APIRequestContext disposed for this thread.");
        }

        Playwright playwright = playwrightThreadLocal.get();
        if (playwright != null) {
            playwright.close();
            playwrightThreadLocal.remove();
        }
    }
}