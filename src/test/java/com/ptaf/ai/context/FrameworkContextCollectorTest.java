package com.ptaf.ai.context;

import com.ptaf.ai.config.AiAssistantProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FrameworkContextCollectorTest {

    @TempDir
    Path tempDir;

    @Test
    void collectsStepDefinitionsYamlKeysAndFeatureSnippets() throws Exception {
        prepareFrameworkFiles(tempDir);
        FrameworkContextCollector collector = new FrameworkContextCollector(defaultProps(tempDir, 20));

        FrameworkGenerationContext context = collector.collect(tempDir);

        assertTrue(context.existingStepDefinitions().contains("user is on the login page"));
        assertTrue(context.existingYamlKeys().contains("elements.login.username.input"));
        assertTrue(context.uiElementKeys().contains("elements.login.username.input"));
        assertTrue(context.apiRequestKeys().contains("api_requests.users.create.method"));
        assertTrue(context.dbQueryKeys().contains("queries.user.getbyid"));
        assertFalse(context.existingFeatureSnippets().isEmpty());
    }

    @Test
    void separatesYamlKeyCategories() throws Exception {
        prepareFrameworkFiles(tempDir);
        FrameworkContextCollector collector = new FrameworkContextCollector(defaultProps(tempDir, 20));
        FrameworkGenerationContext context = collector.collect(tempDir);

        assertTrue(context.uiElementKeys().stream().allMatch(k -> k.startsWith("elements.")));
        assertTrue(context.apiRequestKeys().stream().allMatch(k -> k.startsWith("api_requests.")));
        assertTrue(context.dbQueryKeys().stream().allMatch(k -> k.startsWith("queries.")));
    }

    @Test
    void respectsMaxFeatureSnippetsConfig() throws Exception {
        prepareFrameworkFiles(tempDir);
        Path featuresDir = tempDir.resolve("src/test/resources/features");
        Files.writeString(featuresDir.resolve("another.feature"), """
                Feature: Another
                  Scenario: Two
                    Given x
                """, StandardCharsets.UTF_8);

        FrameworkContextCollector collector = new FrameworkContextCollector(defaultProps(tempDir, 1));
        FrameworkGenerationContext context = collector.collect(tempDir);
        assertEquals(1, context.featureSnippetCount());
    }

    @Test
    void handlesMissingFeatureFolderGracefully() throws Exception {
        Path stepDir = tempDir.resolve("src/test/java/com/ptaf/stepdefinitions");
        Path elementsDir = tempDir.resolve("src/test/resources/elements");
        Files.createDirectories(stepDir);
        Files.createDirectories(elementsDir);

        Files.writeString(stepDir.resolve("LoginSteps.java"), """
                package com.ptaf.stepdefinitions;
                import io.cucumber.java.en.Given;
                public class LoginSteps {
                    @Given("user is on the login page")
                    public void onPage() {}
                }
                """, StandardCharsets.UTF_8);
        Files.writeString(elementsDir.resolve("login.yml"), """
                login:
                  username:
                    input: "#username"
                """, StandardCharsets.UTF_8);

        FrameworkContextCollector collector = new FrameworkContextCollector(defaultProps(tempDir, 20));
        FrameworkGenerationContext context = collector.collect(tempDir);

        assertTrue(context.existingFeatureSnippets().isEmpty());
        assertFalse(context.existingStepDefinitions().isEmpty());
        assertFalse(context.existingYamlKeys().isEmpty());
    }

    private static void prepareFrameworkFiles(Path root) throws Exception {
        Path stepDir = root.resolve("src/test/java/com/ptaf/stepdefinitions");
        Path elementsDir = root.resolve("src/test/resources/elements");
        Path apiDir = root.resolve("src/test/resources/api_requests");
        Path queriesDir = root.resolve("src/test/resources/queries");
        Path featuresDir = root.resolve("src/test/resources/features");

        Files.createDirectories(stepDir);
        Files.createDirectories(elementsDir);
        Files.createDirectories(apiDir);
        Files.createDirectories(queriesDir);
        Files.createDirectories(featuresDir);

        Files.writeString(stepDir.resolve("LoginSteps.java"), """
                package com.ptaf.stepdefinitions;
                import io.cucumber.java.en.Given;
                import io.cucumber.java.en.When;
                public class LoginSteps {
                    @Given("user is on the login page")
                    public void onPage() {}
                    @When("user enters {string} into {string}")
                    public void enters(String v, String f) {}
                }
                """, StandardCharsets.UTF_8);

        Files.writeString(elementsDir.resolve("login.yml"), """
                login:
                  username:
                    input: "#username"
                """, StandardCharsets.UTF_8);
        Files.writeString(apiDir.resolve("users.yaml"), """
                users:
                  create:
                    method: "POST"
                """, StandardCharsets.UTF_8);
        Files.writeString(queriesDir.resolve("user.yml"), """
                user:
                  getById: "SELECT * FROM users WHERE id = ?"
                """, StandardCharsets.UTF_8);
        Files.writeString(featuresDir.resolve("login.feature"), """
                @smoke
                Feature: Login
                  Scenario: Valid login
                    Given user is on the login page
                    When user enters "abc" into "username"
                """, StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private static AiAssistantProperties defaultProps(Path root, int maxFeatureSnippets) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load("""
                ai_assistant:
                  context:
                    featurePaths:
                      - src/test/resources/features
                    stepDefinitionPaths:
                      - src/test/java
                    yamlPaths:
                      elements: src/test/resources/elements
                      api_requests: src/test/resources/api_requests
                      queries: src/test/resources/queries
                      config: src/test/resources/config
                    maxFeatureSnippets: %d
                """.formatted(maxFeatureSnippets));
        try {
            Constructor<AiAssistantProperties> ctor = AiAssistantProperties.class.getDeclaredConstructor(Map.class);
            ctor.setAccessible(true);
            return ctor.newInstance((Map<String, Object>) data);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create AiAssistantProperties for test", e);
        }
    }
}
