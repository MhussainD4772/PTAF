package com.ptaf.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.telemetry.Telemetry;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

/**
 * Calls Google Gemini generateContent over HTTPS; Phase 4 logs usage to {@code target/ai-telemetry.jsonl}.
 */
public final class GeminiClient {

    private static final String DEFAULT_BASE = "https://generativelanguage.googleapis.com";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    private final ObjectMapper json = new ObjectMapper();

    public String generateContent(String systemPrompt, String userPrompt, AiAssistantProperties props) throws Exception {
        return generateForOperation("generate", systemPrompt, userPrompt, props);
    }

    /** Used by triage — telemetry operation {@code triage}. */
    public String generateRaw(String systemPrompt, String userPrompt, AiAssistantProperties props) throws Exception {
        return generateForOperation("triage", systemPrompt, userPrompt, props);
    }

    public String generateForOperation(String operation, String systemPrompt, String userPrompt, AiAssistantProperties props) throws Exception {
        String apiKey = Objects.requireNonNull(props.apiKey(), "API key");
        if (apiKey.isBlank()) {
            throw new IllegalStateException("Set " + props.geminiApiKeyEnvName() + " (Google AI Studio API key)");
        }
        String model = Objects.requireNonNull(props.model(), "model");
        String base = getenvOrDefault("GEMINI_API_BASE", DEFAULT_BASE).replaceAll("/+$", "");

        String url = base + "/v1beta/models/" + urlEncode(model) + ":generateContent?key=" + urlEncode(apiKey);

        ObjectNode body = json.createObjectNode();
        ObjectNode sys = json.createObjectNode();
        ArrayNode sysParts = sys.putArray("parts");
        sysParts.addObject().put("text", systemPrompt);
        body.set("systemInstruction", sys);

        ObjectNode userMsg = json.createObjectNode();
        userMsg.put("role", "user");
        ArrayNode userParts = userMsg.putArray("parts");
        userParts.addObject().put("text", userPrompt);
        ArrayNode contents = body.putArray("contents");
        contents.add(userMsg);

        ObjectNode gen = json.createObjectNode();
        gen.put("temperature", props.temperature());
        gen.put("maxOutputTokens", props.maxOutputTokens());
        body.set("generationConfig", gen);

        String payload = json.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Gemini HTTP " + response.statusCode() + ": " + response.body());
        }

        String responseBody = response.body();
        Telemetry.logGeminiResponse(operation != null ? operation : "unknown", model, responseBody);

        JsonNode root = json.readTree(responseBody);
        JsonNode text = root.at("/candidates/0/content/parts/0/text");
        if (text.isMissingNode() || text.asText("").isBlank()) {
            throw new IllegalStateException("Unexpected Gemini response (no text): " + responseBody);
        }
        return text.asText();
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String getenvOrDefault(String name, String defaultValue) {
        try {
            String v = System.getenv(name);
            return v != null && !v.isBlank() ? v.trim() : defaultValue;
        } catch (SecurityException e) {
            return defaultValue;
        }
    }
}
