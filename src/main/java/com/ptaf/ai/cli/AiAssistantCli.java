package com.ptaf.ai.cli;

import com.ptaf.ai.FeatureGeneratorService;
import com.ptaf.ai.config.AiAssistantProperties;
import com.ptaf.ai.http.AiGenerateHttpServer;
import com.ptaf.ai.model.GenerationResult;
import com.ptaf.ai.quality.QualityGateService;
import com.ptaf.ai.quality.QualityReportWriter;
import com.ptaf.ai.telemetry.TelemetrySummaryWriter;
import com.ptaf.ai.triage.TriageService;
import com.sun.net.httpserver.HttpServer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * PTAF AI CLI: generate, serve, quality (Phase 3), triage (Phase 4).
 */
@Command(
        name = "ptaf-ai",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {
                AiAssistantCli.GenerateCommand.class,
                AiAssistantCli.ServeCommand.class,
                AiAssistantCli.QualityCommand.class,
                AiAssistantCli.TriageCommand.class,
                AiAssistantCli.TelemetryReportCommand.class
        },
        description = "PTAF AI assistant — Gemini Gherkin + quality gate + triage"
)
public class AiAssistantCli implements Callable<Integer> {

    public static void main(String[] args) {
        int code = new CommandLine(new AiAssistantCli()).execute(args);
        System.exit(code);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    @Command(
            name = "generate",
            mixinStandardHelpOptions = true,
            description = "Call Gemini and write a .feature draft (Phase 4 policy applies)"
    )
    static final class GenerateCommand implements Callable<Integer> {

        @Option(names = {"-r", "--requirement"}, required = true, description = "Requirement / user story")
        String requirement;

        @Option(names = {"-o", "--output"}, description = "Output path (default: target/ai-proposals/generated.feature)")
        Path output;

        @Option(names = {"--project-root"}, description = "Repo root (default: user.dir)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            Path out = output != null ? output : Path.of("target", "ai-proposals", "generated.feature");
            FeatureGeneratorService service = new FeatureGeneratorService(props);
            GenerationResult result = service.generate(projectRoot, requirement);
            Path written = service.writeFeatureFile(out, result);
            System.out.println("Wrote: " + written.toAbsolutePath());
            System.out.println("Suggested steps: " + result.suggestedReusableSteps().size());
            return 0;
        }
    }

    @Command(
            name = "serve",
            mixinStandardHelpOptions = true,
            description = "Localhost API: GET /health, POST /generate"
    )
    static final class ServeCommand implements Callable<Integer> {

        @Option(names = {"-p", "--port"}, description = "Port")
        int port = 8787;

        @Option(names = {"--project-root"}, description = "Repo root (default: user.dir)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            HttpServer server = AiGenerateHttpServer.createAndStart(port, projectRoot);
            System.out.println("http://127.0.0.1:" + port + "  POST /generate  GET /health");
            System.out.println("Press Enter to stop.");
            System.in.read();
            server.stop(0);
            return 0;
        }
    }

    @Command(
            name = "quality",
            mixinStandardHelpOptions = true,
            description = "Phase 3: scan .feature files — syntax checks + duplicate step report (JSON/HTML in target/)"
    )
    static final class QualityCommand implements Callable<Integer> {

        @Option(names = {"--project-root"}, description = "Repo root")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Option(names = {"--features-dir"}, description = "Relative to project root")
        String featuresDir = "src/test/resources/features";

        @Option(names = {"--strict"}, description = "Exit 1 if syntax errors or duplicate count ≥ fail threshold (see ai_policy.yml)")
        boolean strict;

        @Override
        public Integer call() throws Exception {
            var gate = new QualityGateService();
            var report = gate.run(projectRoot, featuresDir, strict);
            Path target = projectRoot.resolve("target");
            QualityReportWriter.write(target, report);
            System.out.println("Reports: " + target.resolve("ai-quality-report.json").toAbsolutePath());
            System.out.println("         " + target.resolve("ai-quality-report.html").toAbsolutePath());
            System.out.println("Syntax issues: " + report.syntaxIssues().size());
            System.out.println("Duplicate groups (≥ threshold): " + report.duplicateGroups().size());
            return report.failedStrict() ? 1 : 0;
        }
    }

    @Command(
            name = "triage",
            mixinStandardHelpOptions = true,
            description = "Phase 4: send log text to Gemini for likely area + summary (telemetry logged)"
    )
    static final class TriageCommand implements Callable<Integer> {

        @Option(names = {"--log"}, description = "Path to log file (UTF-8)")
        Path logFile;

        @Option(names = {"--text"}, description = "Inline log excerpt (if --log not set)")
        String text;

        @Override
        public Integer call() throws Exception {
            AiAssistantProperties props = new AiAssistantProperties();
            validateGemini(props);
            String excerpt;
            if (logFile != null) {
                excerpt = Files.readString(logFile, StandardCharsets.UTF_8);
            } else if (text != null && !text.isBlank()) {
                excerpt = text;
            } else {
                throw new IllegalStateException("Provide --log or --text");
            }
            if (excerpt.length() > 120_000) {
                excerpt = excerpt.substring(0, 120_000) + "\n...[truncated]";
            }
            var triage = new TriageService();
            String out = triage.triage(excerpt, props);
            System.out.println(out);
            Path tri = Path.of("target", "ai-triage-last.txt");
            Files.createDirectories(tri.getParent());
            Files.writeString(tri, out, StandardCharsets.UTF_8);
            System.out.println("Also saved: " + tri.toAbsolutePath());
            return 0;
        }
    }

    @Command(
            name = "telemetry-report",
            mixinStandardHelpOptions = true,
            description = "Phase 4: build target/ai-telemetry-summary.html from target/ai-telemetry.jsonl"
    )
    static final class TelemetryReportCommand implements Callable<Integer> {

        @Option(names = {"--project-root"}, description = "Repo root (target/ under it)")
        Path projectRoot = Path.of(System.getProperty("user.dir"));

        @Override
        public Integer call() throws Exception {
            Path html = TelemetrySummaryWriter.writeHtml(projectRoot.resolve("target"));
            System.out.println("Wrote: " + html.toAbsolutePath());
            return 0;
        }
    }

    static void validateGemini(AiAssistantProperties props) {
        if (props.apiKey() == null || props.apiKey().isBlank()) {
            throw new IllegalStateException(
                    "Set " + props.geminiApiKeyEnvName() + " to your Google AI Studio API key"
            );
        }
        if (props.model() == null || props.model().isBlank()) {
            throw new IllegalStateException("Set model in ai_assistant.yml or GEMINI_MODEL");
        }
    }
}
