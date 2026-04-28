package com.ptaf.ai.model;

/** Cucumber step regex with declaring file and keyword-overlap score vs the requirement. */
public record ScoredPattern(String pattern, String sourceRelativePath, double score) {
}
