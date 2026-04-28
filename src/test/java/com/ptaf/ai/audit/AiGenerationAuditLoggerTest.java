package com.ptaf.ai.audit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiGenerationAuditLoggerTest {
    private static final ObjectMapper JSON = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void auditRecordCreationAndJsonlWrite() throws Exception {
        AiGenerationAuditRecord record = sampleRecord("abc123");
        AiGenerationAuditLogger logger = new AiGenerationAuditLogger();
        AiGenerationAuditLogger.WriteResult result =
                logger.append(tempDir, true, "target/ai-audit/generation-audit.jsonl", record);

        assertTrue(result.written());
        Path logPath = tempDir.resolve("target/ai-audit/generation-audit.jsonl");
        assertTrue(Files.exists(logPath));
        List<String> lines = Files.readAllLines(logPath, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        JsonNode json = JSON.readTree(lines.get(0));
        assertEquals("generate", json.path("command").asText());
        assertEquals("PREVIEW", json.path("mode").asText());
        assertEquals("abc123", json.path("requirementHash").asText());
    }

    @Test
    void doesNotStoreRawRequirementText() throws Exception {
        String requirement = "user logs in with account 123";
        String hash = AiGenerationAuditLogger.sha256(requirement);
        AiGenerationAuditRecord record = sampleRecord(hash);
        new AiGenerationAuditLogger().append(tempDir, true, "target/ai-audit/generation-audit.jsonl", record);

        String line = Files.readAllLines(
                tempDir.resolve("target/ai-audit/generation-audit.jsonl"),
                StandardCharsets.UTF_8
        ).get(0);
        assertFalse(line.contains(requirement));
        assertTrue(line.contains(hash));
    }

    @Test
    void failedGenerationStillLogs() throws Exception {
        AiGenerationAuditRecord record = new AiGenerationAuditRecord(
                "req-2",
                "2026-01-01T00:00:00Z",
                "generate",
                "STRICT",
                "gemini-2.5-flash",
                "phase1-v1",
                "hash-2",
                "target/ai-proposals/generated.feature",
                false,
                false,
                false,
                false,
                0,
                2,
                1,
                List.of("warn"),
                List.of("Missing YAML key: elements.login.submitButton")
        );

        AiGenerationAuditLogger.WriteResult result =
                new AiGenerationAuditLogger().append(tempDir, true, "target/ai-audit/generation-audit.jsonl", record);
        assertTrue(result.written());
        JsonNode json = JSON.readTree(Files.readAllLines(
                tempDir.resolve("target/ai-audit/generation-audit.jsonl"),
                StandardCharsets.UTF_8
        ).get(0));
        assertFalse(json.path("fileWritten").asBoolean());
        assertEquals(1, json.path("blockingErrors").size());
    }

    @Test
    void auditLoggerFailureDoesNotThrow() throws Exception {
        Path conflictFile = tempDir.resolve("target");
        Files.writeString(conflictFile, "not-a-directory", StandardCharsets.UTF_8);
        AiGenerationAuditLogger.WriteResult result = new AiGenerationAuditLogger()
                .append(tempDir, true, "target/ai-audit/generation-audit.jsonl", sampleRecord("hash"));
        assertFalse(result.written());
        assertNotNull(result.warningMessage());
    }

    @Test
    void auditDisabledSkipsWriting() {
        AiGenerationAuditLogger.WriteResult result = new AiGenerationAuditLogger()
                .append(tempDir, false, "target/ai-audit/generation-audit.jsonl", sampleRecord("hash"));
        assertFalse(result.written());
        assertFalse(Files.exists(tempDir.resolve("target/ai-audit/generation-audit.jsonl")));
    }

    private static AiGenerationAuditRecord sampleRecord(String requirementHash) {
        return new AiGenerationAuditRecord(
                "req-1",
                "2026-01-01T00:00:00Z",
                "generate",
                "PREVIEW",
                "gemini-2.5-flash",
                "phase1-v1",
                requirementHash,
                "target/ai-proposals/generated.feature",
                true,
                true,
                true,
                false,
                3,
                1,
                0,
                List.of(),
                List.of()
        );
    }
}
