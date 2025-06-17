package com.ptaf.api.wrapper;

import java.util.Map;

/**
 * A wrapper class to store the results of an API call in a standardized format.
 * This decouples the framework from Playwright's specific APIResponse object and provides
 * easy access to the most important parts of the response.
 */
public class ApiResponseWrapper {

    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;

    public ApiResponseWrapper(int statusCode, String body, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;
    }

    /**
     * @return The HTTP status code of the response (e.g., 200, 404, 500).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return The full response body as a String. Can be parsed as JSON or other formats.
     */
    public String getBody() {
        return body;
    }

    /**
     * @return A map of all response headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String toString() {
        return "ApiResponseWrapper{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}