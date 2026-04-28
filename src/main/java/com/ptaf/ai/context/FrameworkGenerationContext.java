package com.ptaf.ai.context;

import java.util.List;

public record FrameworkGenerationContext(
        List<String> existingFeatureSnippets,
        List<String> existingStepDefinitions,
        List<String> existingYamlKeys,
        List<String> uiElementKeys,
        List<String> apiRequestKeys,
        List<String> dbQueryKeys
) {
    public int featureSnippetCount() {
        return existingFeatureSnippets != null ? existingFeatureSnippets.size() : 0;
    }

    public int stepDefinitionCount() {
        return existingStepDefinitions != null ? existingStepDefinitions.size() : 0;
    }

    public int yamlKeyCount() {
        return existingYamlKeys != null ? existingYamlKeys.size() : 0;
    }
}
