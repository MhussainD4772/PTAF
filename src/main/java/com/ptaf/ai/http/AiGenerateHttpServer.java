package com.ptaf.ai.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptaf.ai.FeatureGeneratorService;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.GenerationResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Executors;

/** Minimal localhost JSON API for Phase 1. */
public final class AiGenerateHttpServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private AiGenerateHttpServer() {
    }

    public static HttpServer createAndStart(int port, Path projectRoot) throws Exception {
        FeatureGeneratorService service = new FeatureGeneratorService(new AiAssistantProperties());
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/health", new HealthHandler());
        server.createContext("/generate", new GenerateHandler(service, projectRoot));
        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
        return server;
    }

    private static final class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws java.io.IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, "{\"error\":\"method not allowed\"}");
                return;
            }
            send(ex, 200, "{\"status\":\"ok\"}");
        }
    }

    private static final class GenerateHandler implements HttpHandler {
        private final FeatureGeneratorService service;
        private final Path projectRoot;

        GenerateHandler(FeatureGeneratorService service, Path projectRoot) {
            this.service = service;
            this.projectRoot = projectRoot;
        }

        @Override
        public void handle(HttpExchange ex) throws java.io.IOException {
            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, "{\"error\":\"method not allowed\"}");
                return;
            }
            try (InputStream in = ex.getRequestBody()) {
                JsonNode root = JSON.readTree(in);
                String requirement = root.path("requirement").asText(null);
                if (requirement == null || requirement.isBlank()) {
                    send(ex, 400, "{\"error\":\"requirement is required\"}");
                    return;
                }
                GenerationResult result = service.generate(projectRoot, requirement);
                ObjectNode out = JSON.createObjectNode();
                out.put("featureGherkin", result.featureGherkin());
                out.set("suggestedReusableSteps", JSON.valueToTree(result.suggestedReusableSteps()));
                out.put("rawModelResponse", result.rawModelResponse());
                out.set("structuredResponse", JSON.valueToTree(result.structuredResponse()));
                out.set("stepReuseValidation", JSON.valueToTree(result.stepReuseValidationResult()));
                out.set("yamlKeyValidation", JSON.valueToTree(result.yamlKeyValidationResult()));
                out.set("allowedYamlGuard", JSON.valueToTree(result.allowedYamlGuardResult()));
                out.set("missingYamlPatchSuggestions", JSON.valueToTree(result.missingYamlPatchSuggestions()));
                ArrayNode trace = JSON.createArrayNode();
                for (var sp : result.reuseTrace()) {
                    ObjectNode row = JSON.createObjectNode();
                    row.put("pattern", sp.pattern());
                    row.put("sourceRelativePath", sp.sourceRelativePath());
                    row.put("score", sp.score());
                    trace.add(row);
                }
                out.set("reuseTrace", trace);
                send(ex, 200, JSON.writeValueAsString(out));
            } catch (Exception e) {
                ObjectNode err = JSON.createObjectNode();
                err.put("error", e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
                send(ex, 500, JSON.writeValueAsString(err));
            }
        }
    }

    private static void send(HttpExchange ex, int status, String body) throws java.io.IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
