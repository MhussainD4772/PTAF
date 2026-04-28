package com.ptaf.ai.model;

import java.util.Locale;

public enum AiGenerationMode {
    PREVIEW,
    WRITE,
    STRICT;

    public static AiGenerationMode fromString(String value) {
        if (value == null || value.isBlank()) {
            return PREVIEW;
        }
        return switch (value.trim().toUpperCase(Locale.ROOT)) {
            case "PREVIEW" -> PREVIEW;
            case "WRITE" -> WRITE;
            case "STRICT" -> STRICT;
            default -> throw new IllegalArgumentException("Unsupported mode: " + value + ". Use preview|write|strict");
        };
    }
}
