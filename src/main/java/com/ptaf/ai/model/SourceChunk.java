package com.ptaf.ai.model;

/** One file of text tagged for ranking (features, steps, hooks, pages, YAML, etc.). */
public record SourceChunk(String relativePath, String kind, String content) {
}
