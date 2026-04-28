package com.ptaf.ai.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ptaf.ai.BuildInfo;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Phase 4: append-only audit trail (no raw requirements or logs) — {@code target/ai-audit.jsonl}.
 */
public final class AuditLog {

    private static final ObjectMapper JSON = new ObjectMapper();

    private AuditLog() {
    }

    public static void append(String operation, String model, String outcome, String contentFingerprintHex) {
        try {
            ObjectNode line = JSON.createObjectNode();
            line.put("ts", Instant.now().toString());
            line.put("moduleVersion", BuildInfo.VERSION);
            line.put("operation", operation);
            line.put("model", model != null ? model : "");
            line.put("outcome", outcome);
            line.put("contentSha256Prefix", contentFingerprintHex != null ? contentFingerprintHex : "");
            Path target = Path.of("target");
            Files.createDirectories(target);
            Path log = target.resolve("ai-audit.jsonl");
            Files.writeString(
                    log,
                    JSON.writeValueAsString(line) + "\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
            // never fail main flow
        }
    }

    public static String sha256Prefix(String text, int hexChars) {
        if (text == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            String hex = HexFormat.of().formatHex(hash);
            return hex.substring(0, Math.min(hexChars, hex.length()));
        } catch (Exception e) {
            return "";
        }
    }
}
