package com.ptaf.api.interfaces;

import com.ptaf.api.wrapper.ApiResponseWrapper;

import java.util.Map;

/**
 * ApiAction defines the contract for performing high-level, reusable API operations.
 * This interface abstracts away the complexities of building and sending HTTP requests,
 * allowing tests to be written in a clean, readable, and stateful manner.
 *
 * The intended workflow is:
 * 1. Set request details (headers, params, body).
 * 2. Send the request using a key from the api_requests.yml file.
 * 3. Use getter/verification methods to assert on the response.
 */
public interface ApiAction {

    // --- Methods for building the next request ---

    /**
     * Adds a custom header to the next request.
     *
     * @param key   The header name (e.g., "x-request-id").
     * @param value The header value.
     */
    void setHeader(String key, String value);

    /**
     * Sets a path parameter to be replaced in the endpoint URL.
     * For an endpoint like "/users/{userId}", key would be "userId".
     *
     * @param key   The name of the placeholder in the endpoint string (without curly braces).
     * @param value The value to substitute.
     */
    void setPathParameter(String key, String value);

    /**
     * Adds a query parameter to the next request's URL.
     *
     * @param key   The query parameter name.
     * @param value The query parameter value.
     */
    void setQueryParameter(String key, Object value);

    /**
     * Sets the request body for the next POST, PUT, or PATCH request.
     * The object provided will be serialized to JSON.
     *
     * @param body A POJO, Map, or String representing the request body.
     */
    void setRequestBody(Object body);

    // --- Method for executing the request ---

    /**
     * Sends the configured API request.
     *
     * @param serviceName The key for the API service in config.yml (e.g., "jsonplaceholder").
     * @param requestKey  The key for the request definition in api_requests.yml (e.g., "jsonplaceholder_requests.get_all_posts").
     * @return An ApiResponseWrapper containing the response details.
     */
    ApiResponseWrapper sendRequest(String serviceName, String requestKey);

    // --- Methods for retrieving data from the last response ---

    /**
     * Retrieves the wrapper object for the most recent API response.
     *
     * @return The last ApiResponseWrapper, or null if no request has been made.
     */
    ApiResponseWrapper getLastResponse();

    /**
     * A convenience method to get the status code from the last response.
     *
     * @return The HTTP status code.
     */
    int getResponseStatusCode();

    /**
     * A convenience method to get the body as a String from the last response.
     *
     * @return The response body.
     */
    String getResponseBody();

    /**
     * Extracts a single value from the last JSON response body using a JSONPath expression.
     *
     * @param jsonPath The JSONPath expression (e.g., "$.data.id", "$[0].name").
     * @return The extracted value as an Object.
     */
    Object getValueFromResponse(String jsonPath);

}