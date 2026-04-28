package com.ptaf.ai.index;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StepDefinitionIndexTest {

    @TempDir
    Path tempDir;

    @Test
    void extractsAnnotationBasedStepDefinitions() throws Exception {
        Path src = tempDir.resolve("src/test/java/com/example");
        Files.createDirectories(src);
        Path stepFile = src.resolve("LoginSteps.java");
        Files.writeString(stepFile, """
                package com.example;

                import io.cucumber.java.en.Given;
                import io.cucumber.java.en.Then;
                import io.cucumber.java.en.When;

                public class LoginSteps {
                    @Given("user is on the login page")
                    public void onPage() {}

                    @When("user enters {string} into {string}")
                    public void enters(String value, String field) {}

                    @Then("^user should see success$")
                    public void success() {}
                }
                """, StandardCharsets.UTF_8);

        StepDefinitionIndex index = StepDefinitionIndex.build(tempDir, List.of("src/test/java"));
        assertTrue(index.knownSteps().contains("user is on the login page"));
        assertTrue(index.knownSteps().contains("user enters {string} into {string}"));
        assertTrue(index.knownSteps().contains("^user should see success$"));
    }
}
