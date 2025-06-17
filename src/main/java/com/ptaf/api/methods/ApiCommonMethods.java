package com.ptaf.api.methods;

import com.ptaf.api.implementation.ApiActionImpl;
import com.ptaf.api.interfaces.ApiAction;
import org.junit.jupiter.api.Assertions; // Using JUnit 5 for assertions
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * ApiCommonMethods provides a high-level API for interacting with web services during tests.
 * This class translates simple, readable method calls into API actions, which are
 * then used in the step definition files.
 */
public class ApiCommonMethods {

    private static final Logger logger = LoggerFactory.getLogger(ApiCommonMethods.class);
    private final ApiAction apiAction;

    public ApiCommonMethods() {
        this.apiAction = new ApiActionImpl();
    }

    // --- Request Building Methods ---

    public void setHeader(String key, String value) {
        logger.info("Setting header: {} = {}", key, value);
        apiAction.setHeader(key, value);
    }

    public void setPathParameter(String key, String value) {
        logger.info("Setting path parameter: {} = {}", key, value);
        apiAction.setPathParameter(key, value);
    }

    public void setQueryParameter(String key, Object value) {
        logger.info("Setting query parameter: {} = {}", key, value);
        apiAction.setQueryParameter(key, value);
    }

    public void setRequestBody(Object body) {
        logger.info("Setting request body.");
        logger.debug("Request body content: {}", body);
        apiAction.setRequestBody(body);
    }

    // --- Request Sending Method ---

    public void sendRequest(String serviceName, String requestKey) {
        logger.info("Sending request for key '{}' to service '{}'", requestKey, serviceName);
        apiAction.sendRequest(serviceName, requestKey);
    }

    // --- Response Verification Methods ---

    /**
     * Verifies that the status code of the last API response matches the expected value.
     *
     * @param expectedStatusCode The expected HTTP status code (e.g., 200, 201, 404).
     */
    public void verifyResponseStatusCode(int expectedStatusCode) {
        int actualStatusCode = apiAction.getResponseStatusCode();
        logger.info("Verifying response status code. Expected: {}, Actual: {}", expectedStatusCode, actualStatusCode);
        Assertions.assertEquals(expectedStatusCode, actualStatusCode, "Response status code mismatch.");
    }

    /**
     * Verifies that the response body from the last API call contains a specific piece of text.
     *
     * @param expectedText The text to search for in the response body.
     */
    public void verifyResponseBodyContains(String expectedText) {
        String responseBody = apiAction.getResponseBody();
        logger.info("Verifying response body contains text: '{}'", expectedText);
        Assertions.assertTrue(responseBody.contains(expectedText),
                "Response body did not contain the expected text. Expected: " + expectedText);
    }

    /**
     * Verifies the value of a specific header from the last API response.
     *
     * @param headerName     The name of the header to check (case-insensitive).
     * @param expectedValue  The expected value of the header.
     */
    public void verifyResponseHeader(String headerName, String expectedValue) {
        Map<String, String> headers = apiAction.getLastResponse().getHeaders();
        // Header names are often case-insensitive, so we check for the key's presence carefully
        String actualValue = headers.entrySet().stream()
                .filter(entry -> headerName.equalsIgnoreCase(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);

        logger.info("Verifying header '{}'. Expected: '{}', Actual: '{}'", headerName, expectedValue, actualValue);
        Assertions.assertNotNull(actualValue, "Header '" + headerName + "' not found in response.");
        Assertions.assertEquals(expectedValue, actualValue, "Response header value mismatch.");
    }

    /**
     * Verifies that a value extracted from the JSON response body via JSONPath matches an expected value.
     *
     * @param jsonPath       The JSONPath expression to find the value (e.g., "$.data.email").
     * @param expectedValue  The expected value (as a String, which will be compared).
     */
    public void verifyJsonPathValue(String jsonPath, String expectedValue) {
        Object actualValueObj = getValueByJsonPath(jsonPath);
        String actualValue = Objects.toString(actualValueObj, null); // Convert actual value to string for comparison

        logger.info("Verifying JSONPath '{}'. Expected: '{}', Actual: '{}'", jsonPath, expectedValue, actualValue);
        Assertions.assertEquals(expectedValue, actualValue, "Value from JSONPath did not match expected value.");
    }

    // --- Response Data Getter Method ---

    /**
     * Retrieves a value from the last response using a JSONPath expression.
     *
     * @param jsonPath The JSONPath expression.
     * @return The extracted value as an Object.
     */
    public Object getValueByJsonPath(String jsonPath) {
        logger.info("Getting value from response using JSONPath: {}", jsonPath);
        return apiAction.getValueFromResponse(jsonPath);
    }
}