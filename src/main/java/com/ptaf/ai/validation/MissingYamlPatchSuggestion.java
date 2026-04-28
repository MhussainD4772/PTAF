package com.ptaf.ai.validation;

import java.util.List;

public record MissingYamlPatchSuggestion(
        String missingKey,
        String category,
        String targetFolder,
        String suggestedYaml,
        List<String> warnings
) {
}
