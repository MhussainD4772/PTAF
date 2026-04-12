package com.ptaf.ai.security;

import java.util.regex.Pattern;

/** Phase 4: strips Google-style API keys and obvious secrets from text before sending to Gemini. */
public final class LogRedactor {

    private static final Pattern GOOGLE_API_KEY = Pattern.compile("AIza[0-9A-Za-z_-]{20,}");
    private static final Pattern BEARER = Pattern.compile("(?i)bearer\\s+[0-9A-Za-z._-]+");
    private static final Pattern GENERIC_LONG_SECRET = Pattern.compile("(?i)(api[_-]?key|token|password|secret)\\s*[:=]\\s*\\S{12,}");

    private LogRedactor() {
    }

    public static String redact(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String s = GOOGLE_API_KEY.matcher(text).replaceAll("[REDACTED_API_KEY]");
        s = BEARER.matcher(s).replaceAll("Bearer [REDACTED]");
        s = GENERIC_LONG_SECRET.matcher(s).replaceAll("$1: [REDACTED]");
        return s;
    }
}
