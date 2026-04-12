package com.ptaf.ai.triage;

import com.ptaf.ai.GeminiClient;
import com.ptaf.ai.audit.AuditLog;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.policy.AiPolicy;

/**
 * Phase 4: sends failure logs / stack traces to Gemini for a short structured triage note.
 */
public final class TriageService {

    private final GeminiClient client = new GeminiClient();

    public String triage(String logExcerpt, AiAssistantProperties props) throws Exception {
        return triage(logExcerpt, props, new AiPolicy());
    }

    public String triage(String logExcerpt, AiAssistantProperties props, AiPolicy policy) throws Exception {
        String safe = policy.maybeRedactTriageInput(logExcerpt != null ? logExcerpt : "");
        String system = """
                You are a test automation triage assistant for Java, Cucumber, Playwright, and Maven.
                Analyze the log excerpt. Be concise and practical.

                Respond with EXACTLY these markers:
                <<<LIKELY_AREA>>>
                (one of: UI | API | DB | CONFIG | ENV | FLAKY | UNKNOWN)
                <<<END_LIKELY_AREA>>>
                <<<SUMMARY>>>
                2-4 sentences: what failed and the most probable cause.
                <<<END_SUMMARY>>>
                <<<SUGGESTED_FILES>>>
                - optional bullet lines: relative paths in the repo to inspect (guess from stack traces)
                <<<END_SUGGESTED_FILES>>>
                """.strip();

        String user = "LOG EXCERPT:\n" + safe;
        String out = client.generateRaw(system, user, props);
        AuditLog.append("triage", props.model(), "success", AuditLog.sha256Prefix(safe, 16));
        return out;
    }
}
