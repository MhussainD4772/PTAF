package com.ptaf.ai.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogRedactorTest {

    @Test
    void redactsGoogleStyleKey() {
        String s = "error key=AIzaSyD1Blc23Gz9TN2eh2WZOpUD21G8Zf9r5rM tail";
        String out = LogRedactor.redact(s);
        assertFalse(out.contains("AIzaSy"));
        assertTrue(out.contains("[REDACTED_API_KEY]"));
    }
}
