package com.ptaf.ai.validation;

import java.util.List;
import java.util.Map;

public record YamlKeyValidationResult(
        List<String> yamlKeysUsed,
        List<String> existingKeys,
        List<String> missingKeys,
        Map<String, String> suggestedPatches,
        int totalKeys,
        int existingCount,
        int missingCount,
        boolean passed,
        List<String> warnings
) {
}
