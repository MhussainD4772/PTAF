package com.ptaf.ai.validation;

import com.ptaf.ai.model.AiGenerationStructuredResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageFrameContextGuardTest {
    private final PageFrameContextGuard guard = new PageFrameContextGuard();

    @Test
    void pageStepsPassByDefault() {
        PageFrameContextGuardResult result = guard.validate(
                structured("""
                        Feature: Login
                          Scenario: Valid
                            And we enter value on page login locator username value "testuser"
                            And we click on page login locator loginbutton
                        """),
                "page",
                List.of(),
                List.of()
        );
        assertTrue(result.passed());
        assertEquals(0, result.frameStepCount());
        assertEquals(2, result.pageStepCount());
    }

    @Test
    void frameStepsFailWhenPageNotAllowed() {
        PageFrameContextGuardResult result = guard.validate(
                structured("And we click on frame login locator loginbutton"),
                "page",
                List.of(),
                List.of()
        );
        assertFalse(result.passed());
        assertEquals(1, result.frameStepCount());
        assertTrue(result.blockingErrors().stream().anyMatch(e -> e.contains("page 'login' locator 'loginbutton'")));
    }

    @Test
    void frameStepsPassWhenPageAllowed() {
        PageFrameContextGuardResult result = guard.validate(
                structured("And we click on frame paymentIframe locator submit"),
                "page",
                List.of("paymentIframe"),
                List.of()
        );
        assertTrue(result.passed());
    }

    @Test
    void frameStepsPassWhenLocatorAllowed() {
        PageFrameContextGuardResult result = guard.validate(
                structured("And we enter value on frame checkout locator secureFrame value \"4111\""),
                "page",
                List.of(),
                List.of("checkout.secureFrame")
        );
        assertTrue(result.passed());
    }

    @Test
    void mixedPageAndFrameStepsCountedCorrectly() {
        PageFrameContextGuardResult result = guard.validate(
                structured("""
                        And we click on page login locator username
                        And we click on frame login locator password
                        Then we verify on page homepage of locator logout_btn is visible
                        """),
                "page",
                List.of(),
                List.of()
        );
        assertEquals(1, result.frameStepCount());
        assertEquals(2, result.pageStepCount());
    }

    @Test
    void blockingErrorContainsPageAndLocator() {
        PageFrameContextGuardResult result = guard.validate(
                structured("Then we verify on frame homepage of locator logout_btn is visible"),
                "page",
                List.of(),
                List.of()
        );
        assertTrue(result.blockingErrors().stream().anyMatch(e -> e.contains("page 'homepage' locator 'logout_btn'")));
    }

    private static AiGenerationStructuredResponse structured(String featureText) {
        AiGenerationStructuredResponse response = new AiGenerationStructuredResponse();
        response.setParseSuccessful(true);
        response.setFeatureFile(featureText);
        response.setWarnings(List.of());
        response.setParseErrors(List.of());
        return response;
    }
}
