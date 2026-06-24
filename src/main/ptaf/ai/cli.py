from __future__ import annotations

import sys
from pathlib import Path

import click

from ptaf.ai.audit import generation_audit_support as audit_support
from ptaf.ai.browser.browser_aware_generation_service import BrowserAwareGenerationService
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.feature_generator_service import FeatureGeneratorService
from ptaf.ai.http.ai_generate_http_server import create_and_start
from ptaf.ai.model.ai_generation_mode import AiGenerationMode
from ptaf.ai.quality.quality_gate_service import QualityGateService
from ptaf.ai.quality.quality_report_writer import write as write_quality_report
from ptaf.ai.telemetry.telemetry_summary_writer import write_html
from ptaf.ai.triage.triage_service import TriageService
from ptaf.ai.validation.generation_mode_evaluator import GenerationModeEvaluator


def _validate_gemini(props: AiAssistantProperties) -> None:
    if not props.api_key():
        raise click.ClickException(
            f"Set {props.gemini_api_key_env_name()} to your Google AI Studio API key"
        )
    if not props.model():
        raise click.ClickException("Set model in ai_assistant.yml or GEMINI_MODEL")


def _print_generation_summary(
    generation_mode: AiGenerationMode,
    written: Path | None,
    result,
    blocking_errors: list[str],
) -> None:
    click.echo("=== Generation Result ===")
    click.echo(f"Mode: {generation_mode.name.lower()}")
    click.echo(f"File written: {written is not None}")
    if written is not None:
        click.echo(f"Path: {written.resolve()}")
    if result is not None:
        click.echo(f"Parse: {'passed' if result.structured_response.parse_successful else 'failed'}")
        click.echo(f"Suggested steps: {len(result.suggested_reusable_steps)}")

    if result is not None and result.step_reuse_validation_result is not None:
        step = result.step_reuse_validation_result
        click.echo("\n=== Step Reuse Validation ===")
        click.echo(f"Total steps: {step.total_steps}")
        click.echo(f"Matched existing steps: {step.matched_count}")
        click.echo(f"New steps needed: {step.unmatched_count}")
        click.echo(f"Reuse percentage: {step.reuse_percentage:.1f}%")
        for warning in step.warnings:
            click.echo(f"- {warning}")

    if result is not None and result.yaml_key_validation_result is not None:
        yaml = result.yaml_key_validation_result
        click.echo("\n=== YAML Key Validation ===")
        click.echo(f"Total keys used: {yaml.total_keys}")
        click.echo(f"Existing keys: {yaml.existing_count}")
        click.echo(f"Missing keys: {yaml.missing_count}")
        for missing in yaml.missing_keys:
            click.echo(f"- {missing}")
            patch = yaml.suggested_patches.get(missing)
            if patch and patch.strip():
                click.echo(patch)
        for warning in yaml.warnings:
            click.echo(f"- {warning}")
        click.echo(f"YAML validation: {yaml.existing_count}/{yaml.total_keys} keys found")

    if result is not None and result.runnable_feature_result is not None:
        runnable = result.runnable_feature_result
        click.echo("\n=== Runnable Feature Gate ===")
        click.echo(f"Runnable: {runnable.runnable}")
        for reason in runnable.blocking_reasons:
            click.echo(f"- {reason}")

    if result is not None and result.allowed_yaml_guard_result is not None:
        guard = result.allowed_yaml_guard_result
        click.echo("\n=== Allowed YAML Guard ===")
        click.echo(f"Passed: {guard.passed}")
        for key in guard.unknown_keys_used:
            click.echo(f"- {key}")
        for err in guard.blocking_errors:
            click.echo(f"- {err}")
        for warning in guard.warnings:
            click.echo(f"- {warning}")

    if result is not None and result.page_frame_context_guard_result is not None:
        page_frame = result.page_frame_context_guard_result
        click.echo("\n=== Page/Frame Context Guard ===")
        click.echo(f"Passed: {page_frame.passed}")
        click.echo(f"Frame steps: {page_frame.frame_step_count}")
        click.echo(f"Page steps: {page_frame.page_step_count}")
        for err in page_frame.blocking_errors:
            click.echo(f"- {err}")
        for warning in page_frame.warnings:
            click.echo(f"- {warning}")

    if result is not None and result.missing_yaml_patch_suggestions:
        click.echo("\n=== Missing YAML Patch Suggestions ===")
        for suggestion in result.missing_yaml_patch_suggestions:
            click.echo(f"Key: {suggestion.missing_key}")
            click.echo(f"Target: {suggestion.target_folder}")
            click.echo("Patch:")
            click.echo(suggestion.suggested_yaml)
            for warning in suggestion.warnings:
                click.echo(f"- {warning}")
            click.echo()

    if not blocking_errors:
        click.echo("\nBlocking errors: none")
        return
    click.echo("\nBlocking errors:")
    for error in blocking_errors:
        click.echo(f"- {error}")


@click.group()
@click.version_option("1.0", prog_name="ptaf-ai")
def cli() -> None:
    """PTAF AI assistant — Gemini Gherkin + quality gate + triage."""


@cli.command("generate")
@click.option("-r", "--requirement", help="Requirement / user story")
@click.option("--requirement-file", type=click.Path(path_type=Path), help="UTF-8 file with the requirement")
@click.option("-o", "--output", type=click.Path(path_type=Path), help="Output path")
@click.option("--mode", default="preview", show_default=True, help="Generation mode: preview|write|strict")
@click.option("--overwrite", is_flag=True, help="Allow overwriting an existing output file in write mode")
@click.option("--project-root", type=click.Path(path_type=Path), default=".", show_default=True)
def generate_command(
    requirement: str | None,
    requirement_file: Path | None,
    output: Path | None,
    mode: str,
    overwrite: bool,
    project_root: Path,
) -> None:
    project_root = project_root.resolve()
    props = AiAssistantProperties()
    _validate_gemini(props)
    req = _resolve_requirement(requirement, requirement_file)
    out = output or Path("target/ai-proposals/generated.feature")
    generation_mode = AiGenerationMode.from_string(mode)
    service = FeatureGeneratorService(props)
    result = None
    blocking_errors: list[str] = []
    written: Path | None = None
    exit_code = 0
    try:
        result = service.generate(project_root, req)
        evaluator = GenerationModeEvaluator()
        blocking_errors = evaluator.blocking_errors(generation_mode, result)
        if evaluator.should_write_file(generation_mode, blocking_errors):
            written = service.write_feature_file(out, result, overwrite)
        if generation_mode != AiGenerationMode.PREVIEW and blocking_errors:
            exit_code = 1
    except Exception as exc:
        blocking_errors = [str(exc) or exc.__class__.__name__]
        exit_code = 1

    _print_generation_summary(generation_mode, written, result, blocking_errors)
    audit_write = audit_support.append(
        project_root,
        props,
        generation_mode,
        req,
        out,
        written,
        result,
        blocking_errors,
    )
    if audit_write.written:
        click.echo("\n=== Audit ===")
        click.echo("Audit log written:")
        click.echo(audit_write.output_path)
    elif audit_write.warning_message:
        click.echo(audit_write.warning_message)
    raise SystemExit(exit_code)


@cli.command("explore-generate")
@click.option("-r", "--requirement", help="Manual test scenario / requirement")
@click.option("--requirement-file", type=click.Path(path_type=Path), help="UTF-8 file with the scenario")
@click.option(
    "--start-url-key",
    help="Config URL key from config.yml (e.g. google_url, panda_url)",
)
@click.option("--url", help="Direct URL to explore (alternative to --start-url-key)")
@click.option("-o", "--output", type=click.Path(path_type=Path), help="Output path")
@click.option("--mode", default="preview", show_default=True, help="Generation mode: preview|write|strict")
@click.option("--overwrite", is_flag=True, help="Allow overwriting an existing output file in write mode")
@click.option(
    "--max-snapshot-chars",
    default=12_000,
    show_default=True,
    help="Max accessibility snapshot chars sent to the model",
)
@click.option("--project-root", type=click.Path(path_type=Path), default=".", show_default=True)
def explore_generate_command(
    requirement: str | None,
    requirement_file: Path | None,
    start_url_key: str | None,
    url: str | None,
    output: Path | None,
    mode: str,
    overwrite: bool,
    max_snapshot_chars: int,
    project_root: Path,
) -> None:
    """Explore a live page via @playwright/mcp, then generate a feature file."""
    project_root = project_root.resolve()
    props = AiAssistantProperties()
    _validate_gemini(props)
    req = _resolve_requirement(requirement, requirement_file)
    if not start_url_key and not url:
        raise click.ClickException("Provide --start-url-key or --url")
    if start_url_key and url:
        raise click.ClickException("Use either --start-url-key or --url, not both")

    out = output or Path("target/ai-proposals/explore-generated.feature")
    generation_mode = AiGenerationMode.from_string(mode)
    service = BrowserAwareGenerationService(props)
    result = None
    browser_context = None
    blocking_errors: list[str] = []
    written: Path | None = None
    exit_code = 0
    try:
        result, browser_context = service.explore_and_generate(
            project_root,
            req,
            url_config_key=start_url_key,
            url=url,
            max_snapshot_chars=max_snapshot_chars,
        )
        evaluator = GenerationModeEvaluator()
        blocking_errors = evaluator.blocking_errors(generation_mode, result)
        generator = FeatureGeneratorService(props)
        if evaluator.should_write_file(generation_mode, blocking_errors):
            written = generator.write_feature_file(out, result, overwrite)
        if generation_mode != AiGenerationMode.PREVIEW and blocking_errors:
            exit_code = 1
    except Exception as exc:
        blocking_errors = [str(exc) or exc.__class__.__name__]
        exit_code = 1

    if browser_context is not None:
        click.echo("=== Browser Context ===")
        click.echo(f"Provider: {browser_context.provider}")
        click.echo(f"URL: {browser_context.url}")
        click.echo(f"Title: {browser_context.title}")
        if browser_context.start_url_key:
            click.echo(f"Start URL key: {browser_context.start_url_key}")
        click.echo(f"Snapshot chars: {len(browser_context.aria_snapshot)}")

    _print_generation_summary(generation_mode, written, result, blocking_errors)
    audit_write = audit_support.append(
        project_root,
        props,
        generation_mode,
        req,
        out,
        written,
        result,
        blocking_errors,
    )
    if audit_write.written:
        click.echo("\n=== Audit ===")
        click.echo("Audit log written:")
        click.echo(audit_write.output_path)
    elif audit_write.warning_message:
        click.echo(audit_write.warning_message)
    raise SystemExit(exit_code)


@cli.command("serve")
@click.option("-p", "--port", default=8787, show_default=True)
@click.option("--project-root", type=click.Path(path_type=Path), default=".", show_default=True)
def serve_command(port: int, project_root: Path) -> None:
    project_root = project_root.resolve()
    props = AiAssistantProperties()
    _validate_gemini(props)
    server = create_and_start(port, project_root)
    click.echo(f"UI: http://127.0.0.1:{port}/")
    click.echo("API: POST /generate-write (preferred)  POST /generate (deprecated)  GET /health")
    click.echo("Press Enter to stop.")
    input()
    server.shutdown()


@cli.command("quality")
@click.option("--project-root", type=click.Path(path_type=Path), default=".", show_default=True)
@click.option("--features-dir", default="features", show_default=True)
@click.option("--strict", is_flag=True, help="Exit 1 if syntax errors or duplicate count >= fail threshold")
def quality_command(project_root: Path, features_dir: str, strict: bool) -> None:
    gate = QualityGateService()
    report = gate.run(project_root, features_dir, strict)
    target = project_root / "target"
    write_quality_report(target, report)
    click.echo(f"Reports: {(target / 'ai-quality-report.json').resolve()}")
    click.echo(f"         {(target / 'ai-quality-report.html').resolve()}")
    click.echo(f"Syntax issues: {len(report.syntax_issues)}")
    click.echo(f"Duplicate groups (≥ threshold): {len(report.duplicate_groups)}")
    raise SystemExit(1 if report.failed_strict else 0)


@cli.command("triage")
@click.option("--log", "log_file", type=click.Path(path_type=Path))
@click.option("--text")
def triage_command(log_file: Path | None, text: str | None) -> None:
    project_root = project_root.resolve()
    props = AiAssistantProperties()
    _validate_gemini(props)
    if log_file is not None:
        excerpt = log_file.read_text(encoding="utf-8")
    elif text and text.strip():
        excerpt = text
    else:
        raise click.ClickException("Provide --log or --text")
    if len(excerpt) > 120_000:
        excerpt = excerpt[:120_000] + "\n...[truncated]"
    out = TriageService().triage(excerpt, props)
    click.echo(out)
    tri = Path("target/ai-triage-last.txt")
    tri.parent.mkdir(parents=True, exist_ok=True)
    tri.write_text(out, encoding="utf-8")
    click.echo(f"Also saved: {tri.resolve()}")


@cli.command("telemetry-report")
@click.option("--project-root", type=click.Path(path_type=Path), default=".", show_default=True)
def telemetry_report_command(project_root: Path) -> None:
    project_root = project_root.resolve()
    html = write_html(project_root / "target")
    click.echo(f"Wrote: {html.resolve()}")


def _resolve_requirement(requirement: str | None, requirement_file: Path | None) -> str:
    has_file = requirement_file is not None
    has_text = requirement is not None and requirement.strip()
    if has_file and has_text:
        raise click.ClickException("Use either -r/--requirement or --requirement-file, not both")
    if has_file:
        return requirement_file.read_text(encoding="utf-8").strip()
    if has_text:
        return requirement.strip()
    raise click.ClickException("Provide -r/--requirement or --requirement-file")


def main() -> None:
    try:
        cli(prog_name="ptaf-ai")
    except click.ClickException as exc:
        click.echo(str(exc), err=True)
        raise SystemExit(1) from exc


if __name__ == "__main__":
    main()
