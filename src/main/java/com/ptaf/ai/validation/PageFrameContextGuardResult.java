package com.ptaf.ai.validation;

import java.util.List;

public record PageFrameContextGuardResult(
        boolean passed,
        List<String> invalidFrameSteps,
        List<String> invalidPageSteps,
        List<String> warnings,
        List<String> blockingErrors,
        int frameStepCount,
        int pageStepCount
) {
}
