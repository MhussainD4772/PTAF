package com.ptaf.ai.validation;

import java.util.List;

public record AllowedYamlGuardResult(
        boolean passed,
        List<String> allowedKeysUsed,
        List<String> unknownKeysUsed,
        List<String> missingKeysDeclared,
        List<String> missingKeysUsedInFeature,
        List<String> warnings,
        List<String> blockingErrors
) {
}
