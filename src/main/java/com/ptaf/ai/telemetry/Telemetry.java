package com.ptaf.ai.telemetry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptaf.ai.BuildInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

/**
 * Phase 4: append-only JSON lines to {@code target/ai-telemetry.jsonl} (usage + operation + version).
 */
public final class Telemetry {

    private static final ObjectMapper JSON = new ObjectMapper();

    private Telemetry() {
    }

    public static void logGeminiResponse(String operation, String model, String responseBody) {
        try {
            JsonNode root = JSON.readTree(responseBody);
            ObjectNode line = JSON.createObjectNode();
            line.put("ts", Instant.now().toString());
            line.put("moduleVersion", BuildInfo.VERSION);
            line.put("operation", operation != null ? operation : "unknown");
            line.put("model", model);
            JsonNode usage = root.get("usageMetadata");
            if (usage != null && !usage.isMissingNode()) {
                line.set("usageMetadata", usage);
            } else {
                line.putNull("usageMetadata");
            }
            Path target = Path.of("target");
            Files.createDirectories(target);
            Path log = target.resolve("ai-telemetry.jsonl");
            Files.writeString(
                    log,
                    JSON.writeValueAsString(line) + "\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
            // never fail generation on telemetry
        }
    }
}
