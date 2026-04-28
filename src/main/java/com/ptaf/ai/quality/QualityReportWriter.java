package com.ptaf.ai.quality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/** Writes {@code target/ai-quality-report.json} and {@code target/ai-quality-report.html}. */
public final class QualityReportWriter {

    private static final ObjectMapper JSON = new ObjectMapper();

    private QualityReportWriter() {
    }

    public static void write(Path targetDir, QualityReport report) throws Exception {
        Files.createDirectories(targetDir);
        Path jsonPath = targetDir.resolve("ai-quality-report.json");
        Path htmlPath = targetDir.resolve("ai-quality-report.html");

        ObjectNode root = JSON.createObjectNode();
        root.put("generatedAt", Instant.now().toString());
        root.put("warnThreshold", report.warnThreshold());
        root.put("failedStrict", report.failedStrict());
        ArrayNode syn = root.putArray("syntaxIssues");
        for (String s : report.syntaxIssues()) {
            syn.add(s);
        }
        ArrayNode dups = root.putArray("duplicateStepGroups");
        for (DuplicateStepDetector.DuplicateGroup g : report.duplicateGroups()) {
            ObjectNode go = dups.addObject();
            go.put("normalized", g.normalizedStep());
            go.put("count", g.count());
            ArrayNode occ = go.putArray("occurrences");
            for (StepOccurrence o : g.occurrences()) {
                ObjectNode oo = occ.addObject();
                oo.put("file", o.relativePath());
                oo.put("line", o.lineNumber());
                oo.put("raw", o.rawLine());
            }
        }
        Files.writeString(jsonPath, JSON.writerWithDefaultPrettyPrinter().writeValueAsString(root), StandardCharsets.UTF_8);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>PTAF AI quality</title></head><body>\n");
        html.append("<h1>AI quality report</h1><p>").append(Instant.now()).append("</p>\n");
        html.append("<h2>Syntax / structure</h2><ul>\n");
        for (String s : report.syntaxIssues()) {
            html.append("<li>").append(escapeHtml(s)).append("</li>\n");
        }
        html.append("</ul>\n<h2>Duplicate step groups (≥ ").append(report.warnThreshold()).append(")</h2>\n");
        for (DuplicateStepDetector.DuplicateGroup g : report.duplicateGroups()) {
            html.append("<h3>").append(escapeHtml(g.normalizedStep())).append(" (").append(g.count()).append(")</h3><ul>\n");
            for (StepOccurrence o : g.occurrences()) {
                html.append("<li>").append(escapeHtml(o.relativePath())).append(":").append(o.lineNumber()).append("</li>\n");
            }
            html.append("</ul>\n");
        }
        html.append("</body></html>");
        Files.writeString(htmlPath, html.toString(), StandardCharsets.UTF_8);
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
