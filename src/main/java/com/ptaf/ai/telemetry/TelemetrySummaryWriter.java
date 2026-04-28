package com.ptaf.ai.telemetry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Phase 4: builds a simple HTML summary from {@code target/ai-telemetry.jsonl} (if present).
 */
public final class TelemetrySummaryWriter {

    private static final ObjectMapper JSON = new ObjectMapper();

    private TelemetrySummaryWriter() {
    }

    public static Path writeHtml(Path targetDir) throws Exception {
        Files.createDirectories(targetDir);
        Path jsonl = targetDir.resolve("ai-telemetry.jsonl");
        Path out = targetDir.resolve("ai-telemetry-summary.html");
        List<String> lines = Files.exists(jsonl)
                ? Files.readAllLines(jsonl, StandardCharsets.UTF_8)
                : List.of();

        StringBuilder rows = new StringBuilder();
        int n = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            JsonNode o = JSON.readTree(line);
            n++;
            rows.append("<tr><td>")
                    .append(escape(o.path("ts").asText("")))
                    .append("</td><td>")
                    .append(escape(o.path("operation").asText("")))
                    .append("</td><td>")
                    .append(escape(o.path("model").asText("")))
                    .append("</td><td><pre>")
                    .append(escape(o.path("usageMetadata").toPrettyString()))
                    .append("</pre></td></tr>\n");
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>PTAF AI telemetry</title>");
        html.append("<style>body{font-family:sans-serif;margin:1rem;} table{border-collapse:collapse;}");
        html.append("td,th{border:1px solid #ccc;padding:6px;vertical-align:top;} pre{white-space:pre-wrap;margin:0;max-width:480px;}</style>");
        html.append("</head><body><h1>Gemini telemetry (local)</h1>");
        html.append("<p>Rows: ").append(n).append(" — source: ai-telemetry.jsonl</p>");
        html.append("<table><thead><tr><th>Time</th><th>Operation</th><th>Model</th><th>usageMetadata</th></tr></thead><tbody>");
        html.append(rows);
        html.append("</tbody></table></body></html>");
        Files.writeString(out, html.toString(), StandardCharsets.UTF_8);
        return out;
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
