package com.ptaf.ai.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptaf.ai.FeatureGeneratorService;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.AiGenerationMode;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.validation.GenerationModeEvaluator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;

/** Minimal localhost JSON API for Phase 1. */
public final class AiGenerateHttpServer {

    private static final ObjectMapper JSON = new ObjectMapper();

    private AiGenerateHttpServer() {
    }

    public static HttpServer createAndStart(int port, Path projectRoot) throws Exception {
        FeatureGeneratorService service = new FeatureGeneratorService(new AiAssistantProperties());
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", port), 0);
        server.createContext("/", new UiPageHandler());
        server.createContext("/health", new HealthHandler());
        server.createContext("/generate", new GenerateHandler(service, projectRoot));
        server.createContext("/generate-write", new GenerateAndWriteHandler(service, projectRoot));
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

    private static final class UiPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws java.io.IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                send(ex, 405, "{\"error\":\"method not allowed\"}");
                return;
            }
            byte[] bytes = UI_HTML.getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
            ex.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
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
                out.set("pageFrameContextGuard", JSON.valueToTree(result.pageFrameContextGuardResult()));
                out.set("runnableFeature", JSON.valueToTree(result.runnableFeatureResult()));
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

    private static final class GenerateAndWriteHandler implements HttpHandler {
        private final FeatureGeneratorService service;
        private final Path projectRoot;

        GenerateAndWriteHandler(FeatureGeneratorService service, Path projectRoot) {
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
                String modeRaw = root.path("mode").asText("preview");
                String outputRaw = root.path("output").asText("target/ai-proposals/generated.feature");
                boolean overwrite = root.path("overwrite").asBoolean(true);
                if (requirement == null || requirement.isBlank()) {
                    send(ex, 400, "{\"error\":\"requirement is required\"}");
                    return;
                }
                AiGenerationMode mode = AiGenerationMode.fromString(modeRaw);
                Path output = projectRoot.resolve(outputRaw).normalize();
                GenerationResult result = service.generate(projectRoot, requirement);
                List<String> blockingErrors = new GenerationModeEvaluator().blockingErrors(mode, result);
                Path written = null;
                if (new GenerationModeEvaluator().shouldWriteFile(mode, blockingErrors)) {
                    written = service.writeFeatureFile(output, result, overwrite);
                }

                ObjectNode out = JSON.createObjectNode();
                out.put("mode", mode.name());
                out.put("fileWritten", written != null);
                out.put("outputPath", written != null ? written.toString() : output.toString());
                out.set("blockingErrors", JSON.valueToTree(blockingErrors));
                out.set("result", JSON.valueToTree(result));
                send(ex, blockingErrors.isEmpty() || mode == AiGenerationMode.PREVIEW ? 200 : 422, JSON.writeValueAsString(out));
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

    private static final String UI_HTML = """
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0" />
              <title>PTAF AI Feature Generator</title>
              <style>
                body { font-family: Arial, sans-serif; margin: 20px; background: #f7f9fc; }
                .card { max-width: 980px; background: #fff; border-radius: 10px; padding: 16px; box-shadow: 0 2px 10px rgba(0,0,0,0.08); }
                textarea, input, select { width: 100%; margin: 6px 0 12px; padding: 8px; }
                button { padding: 10px 14px; margin-right: 8px; }
                pre { background: #0f172a; color: #e2e8f0; padding: 12px; border-radius: 8px; overflow: auto; white-space: pre-wrap; }
                .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
                .muted { color: #475569; font-size: 13px; }
              </style>
            </head>
            <body>
              <div class="card">
                <h2>PTAF AI Feature Generator</h2>
                <p class="muted">Write your prompt here, choose mode, and generate a feature file without CLI flags.</p>
                <label>Requirement Prompt</label>
                <textarea id="requirement" rows="8" placeholder="Describe the feature to generate..."></textarea>
                <div class="row">
                  <div>
                    <label>Mode</label>
                    <select id="mode">
                      <option value="preview">preview</option>
                      <option value="write" selected>write</option>
                      <option value="strict">strict</option>
                    </select>
                  </div>
                  <div>
                    <label>Output Path (relative to project root)</label>
                    <input id="output" value="target/ai-proposals/generated.feature" />
                  </div>
                </div>
                <label><input id="overwrite" type="checkbox" checked /> Overwrite if file exists</label>
                <div style="margin-top: 12px;">
                  <button onclick="generate()">Generate</button>
                </div>
                <h3>Result</h3>
                <pre id="result">No result yet.</pre>
              </div>
              <script>
                async function generate() {
                  const payload = {
                    requirement: document.getElementById("requirement").value,
                    mode: document.getElementById("mode").value,
                    output: document.getElementById("output").value,
                    overwrite: document.getElementById("overwrite").checked
                  };
                  const box = document.getElementById("result");
                  box.textContent = "Generating...";
                  try {
                    const res = await fetch("/generate-write", {
                      method: "POST",
                      headers: { "Content-Type": "application/json" },
                      body: JSON.stringify(payload)
                    });
                    const data = await res.json();
                    box.textContent = JSON.stringify(data, null, 2);
                  } catch (e) {
                    box.textContent = "Request failed: " + e;
                  }
                }
              </script>
            </body>
            </html>
            """;
}
