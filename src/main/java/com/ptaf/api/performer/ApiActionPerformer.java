package com.ptaf.api.performer;

import com.google.gson.Gson;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.ptaf.api.wrapper.ApiResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ApiActionPerformer contains the low-level logic for sending HTTP requests
 * using a given APIRequestContext. It constructs the request with all necessary
 * parts and processes the response into a standard ApiResponseWrapper.
 */
public class ApiActionPerformer {

    private static final Logger logger = LoggerFactory.getLogger(ApiActionPerformer.class);
    private final Gson gson = new Gson(); // For serializing request bodies to JSON

    public ApiActionPerformer() {
        // NOTE: For this to work, you will need the Google Gson library dependency.
    }

    /**
     * Sends an API request with the specified details.
     *
     * @param context       The APIRequestContext, containing base URL and auth.
     * @param method        The HTTP method (GET, POST, PUT, DELETE).
     * @param endpoint      The endpoint path, may contain placeholders like {userId}.
     * @param headers       A map of custom request headers.
     * @param queryParams   A map of query parameters to append to the URL.
     * @param pathParams    A map of path parameters to replace placeholders in the endpoint.
     * @param body          The request body object (will be serialized to JSON for POST/PUT).
     * @return An ApiResponseWrapper containing the status, body, and headers of the response.
     */
    public ApiResponseWrapper sendRequest(APIRequestContext context, String method, String endpoint,
                                          Map<String, String> headers, Map<String, Object> queryParams,
                                          Map<String, String> pathParams, Object body) {

        String processedEndpoint = replacePathParameters(endpoint, pathParams);
        logger.info("Sending {} request to endpoint: {}", method.toUpperCase(), processedEndpoint);

        RequestOptions options = RequestOptions.create();

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(options::setHeader);
        }

        // We must iterate and explicitly convert each query parameter value to a String.
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                // String.valueOf() safely converts any object to its string representation.
                options.setQueryParam(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        if (body != null) {
            String jsonBody = gson.toJson(body);
            options.setData(jsonBody);
            if (headers == null || !headers.containsKey("Content-Type")) {
                options.setHeader("Content-Type", "application/json");
            }
            logger.debug("Request Body: {}", jsonBody);
        }

        APIResponse response;
        long startTime = System.currentTimeMillis();

        switch (method.toUpperCase()) {
            case "GET":
                response = context.get(processedEndpoint, options);
                break;
            case "POST":
                response = context.post(processedEndpoint, options);
                break;
            case "PUT":
                response = context.put(processedEndpoint, options);
                break;
            case "DELETE":
                response = context.delete(processedEndpoint, options);
                break;
            case "PATCH":
                response = context.patch(processedEndpoint, options);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        long duration = System.currentTimeMillis() - startTime;
        String responseBody = response.text();
        logger.info("Received response with Status: {} in {}ms", response.status(), duration);
        logger.debug("Response Body: {}", responseBody);

        return new ApiResponseWrapper(response.status(), responseBody, response.headers());
    }

    /**
     * Replaces placeholders in the endpoint string with actual values.
     */
    private String replacePathParameters(String endpoint, Map<String, String> pathParams) {
        if (pathParams == null || pathParams.isEmpty()) {
            return endpoint;
        }
        String processedEndpoint = endpoint;
        for (Map.Entry<String, String> param : pathParams.entrySet()) {
            String placeholder = "{" + param.getKey() + "}";
            processedEndpoint = processedEndpoint.replace(placeholder, param.getValue());
        }
        return processedEndpoint;
    }
}