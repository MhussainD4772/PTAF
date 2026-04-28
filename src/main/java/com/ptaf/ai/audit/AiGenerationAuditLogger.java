package com.ptaf.ai.audit;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Append-only JSONL logger for AI generation audit records.
 */
public final class AiGenerationAuditLogger {
    private static final ObjectMapper JSON = new ObjectMapper();

    public record WriteResult(boolean written, String outputPath, String warningMessage) {
    }

    public WriteResult append(Path projectRoot, boolean enabled, String outputPath, AiGenerationAuditRecord record) {
        if (!enabled) {
            return new WriteResult(false, outputPath, null);
        }
        try {
            Path file = projectRoot.resolve(outputPath).normalize();
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    file,
                    JSON.writeValueAsString(record) + "\n",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            return new WriteResult(true, file.toString(), null);
        } catch (Exception e) {
            return new WriteResult(false, outputPath, "Warning: audit log could not be written");
        }
    }

    public static String sha256(String text) {
        if (text == null) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception ignored) {
            return "";
        }
    }
}
