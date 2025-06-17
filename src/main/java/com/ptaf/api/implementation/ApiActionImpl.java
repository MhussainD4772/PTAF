package com.ptaf.api.implementation;

import com.jayway.jsonpath.JsonPath;
import com.ptaf.api.handlers.ApiRequestHandler;
import com.ptaf.api.interfaces.ApiAction;
import com.ptaf.api.performer.ApiActionPerformer;
import com.ptaf.api.wrapper.ApiResponseWrapper;
import com.ptaf.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements the ApiAction interface to provide concrete methods for building,
 * sending, and verifying API requests.
 *
 * This class manages the state of a request (headers, params, body) and the last response
 * in a thread-safe manner, allowing for a clean, stateful testing workflow.
 */
public class ApiActionImpl implements ApiAction {

    private static final Logger logger = LoggerFactory.getLogger(ApiActionImpl.class);

    private final ApiActionPerformer apiPerformer;

    // ThreadLocal variables to hold the state for the NEXT request to be sent.
    private final ThreadLocal<Map<String, String>> headers = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<String, String>> pathParams = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Map<String, Object>> queryParams = ThreadLocal.withInitial(HashMap::new);
    private final ThreadLocal<Object> requestBody = new ThreadLocal<>();

    // ThreadLocal to hold the LAST response received, so subsequent steps can validate it.
    private final ThreadLocal<ApiResponseWrapper> lastResponse = new ThreadLocal<>();

    public ApiActionImpl() {
        this.apiPerformer = new ApiActionPerformer();
        // NOTE: For getValueFromResponse to work, you will need the Jayway JsonPath library.
        // In your pom.xml, add:
        // <dependency>
        //     <groupId>com.jayway.jsonpath</groupId>
        //     <artifactId>json-path</artifactId>
        //     <version>2.9.0</version>
        // </dependency>
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.get().put(key, value);
    }

    @Override
    public void setPathParameter(String key, String value) {
        this.pathParams.get().put(key, value);
    }



    @Override
    public void setQueryParameter(String key, Object value) {
        this.queryParams.get().put(key, value);
    }

    @Override
    public void setRequestBody(Object body) {
        this.requestBody.set(body);
    }

    @Override
    public ApiResponseWrapper sendRequest(String serviceName, String requestKey) {
        // Read request definition from YAML
        String method = (String) YamlReader.get(requestKey + ".method");
        String endpoint = (String) YamlReader.get(requestKey + ".endpoint");

        if (method == null || endpoint == null) {
            throw new IllegalArgumentException("Request definition for key '" + requestKey + "' not found or is incomplete in api_requests.yml.");
        }

        // Send the request using the performer and the current state
        ApiResponseWrapper response = apiPerformer.sendRequest(
                ApiRequestHandler.getContext(serviceName),
                method,
                endpoint,
                headers.get(),
                queryParams.get(),
                pathParams.get(),
                requestBody.get()
        );

        // Store the response in ThreadLocal for subsequent validation steps
        this.lastResponse.set(response);

        // IMPORTANT: Clear the request state so it doesn't leak into the next API call
        clearRequestState();

        return response;
    }

    @Override
    public ApiResponseWrapper getLastResponse() {
        ApiResponseWrapper response = this.lastResponse.get();
        if (response == null) {
            throw new IllegalStateException("No API request has been sent yet in this scenario. Cannot get a response.");
        }
        return response;
    }

    @Override
    public int getResponseStatusCode() {
        return getLastResponse().getStatusCode();
    }

    @Override
    public String getResponseBody() {
        return getLastResponse().getBody();
    }

    @Override
    public Object getValueFromResponse(String jsonPath) {
        String body = getResponseBody();
        if (body == null || body.isEmpty()) {
            logger.warn("Cannot get value from JSONPath because response body is empty.");
            return null;
        }
        try {
            return JsonPath.read(body, jsonPath);
        } catch (Exception e) {
            logger.error("Failed to read JSONPath '{}' from response body.", jsonPath, e);
            throw new RuntimeException("Invalid JSONPath expression or body format.", e);
        }
    }

    /**
     * Clears all request-specific state (headers, params, body) for the current thread.
     * This is called automatically after each sendRequest to ensure a clean slate.
     */
    private void clearRequestState() {
        this.headers.get().clear();
        this.pathParams.get().clear();
        this.queryParams.get().clear();
        this.requestBody.remove();
        logger.debug("Request state cleared for the current thread.");
    }
}