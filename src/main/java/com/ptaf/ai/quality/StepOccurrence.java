package com.ptaf.ai.quality;

/** One Gherkin step line occurrence inside a feature file. */
public record StepOccurrence(String relativePath, int lineNumber, String rawLine, String normalized) {
}
