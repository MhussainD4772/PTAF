package com.ptaf.ai;

import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.parse.AiResponseParser;
import com.ptaf.ai.audit.AuditLog;
import com.ptaf.ai.policy.AiPolicy;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Wires context → prompts → Gemini → parse → optional file write. */
public final class FeatureGeneratorService {

    private final AiAssistantProperties properties;
    private final GeminiClient geminiClient;
    private final ContextCollector contextCollector;
    private final PromptBuilder promptBuilder;
    private final AiPolicy policy;

    public FeatureGeneratorService(AiAssistantProperties properties) {
        this(properties, new AiPolicy());
    }

    public FeatureGeneratorService(AiAssistantProperties properties, AiPolicy policy) {
        this.properties = properties;
        this.policy = policy;
        this.geminiClient = new GeminiClient();
        this.contextCollector = new ContextCollector(properties);
        this.promptBuilder = new PromptBuilder(properties);
    }

    public GenerationResult generate(Path projectRoot, String requirement) throws Exception {
        String blocked = policy.validateRequirement(requirement);
        if (blocked != null) {
            throw new IllegalStateException("Policy rejected requirement: " + blocked);
        }
        var ctx = contextCollector.collect(projectRoot, requirement);
        String system = promptBuilder.systemPrompt();
        String user = promptBuilder.userPrompt(requirement, ctx);
        String raw = geminiClient.generateContent(system, user, properties);
        var parsed = AiResponseParser.parse(raw);
        var gen = new GenerationResult(
                parsed.featureGherkin(),
                parsed.suggestedReusableSteps(),
                parsed.rawModelResponse(),
                ctx.rankedStepPatterns()
        );
        AuditLog.append("generate", properties.model(), "success", AuditLog.sha256Prefix(requirement, 16));
        return gen;
    }

    public Path writeFeatureFile(Path outputFile, GenerationResult result) throws IOException {
        Path parent = outputFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        String content = result.featureGherkin().trim() + "\n";
        Files.writeString(outputFile, content, StandardCharsets.UTF_8);
        return outputFile;
    }
}
