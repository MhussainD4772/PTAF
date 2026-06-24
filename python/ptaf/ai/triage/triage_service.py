from __future__ import annotations

from datetime import datetime, timezone
from pathlib import Path
from uuid import uuid4

from ptaf.ai.audit.ai_generation_audit_logger import AiGenerationAuditLogger
from ptaf.ai.audit.ai_generation_audit_record import AiGenerationAuditRecord
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.gemini_client import GeminiClient
from ptaf.ai.policy.ai_policy import AiPolicy


class TriageService:
    def __init__(self, client: GeminiClient | None = None) -> None:
        self._client = client or GeminiClient()

    def triage(
        self,
        log_excerpt: str,
        props: AiAssistantProperties,
        policy: AiPolicy | None = None,
    ) -> str:
        active_policy = policy or AiPolicy()
        safe = active_policy.maybe_redact_triage_input(log_excerpt or "") or ""
        system = (
            "You are a test automation triage assistant for Python, pytest-bdd, Playwright, and uv.\n"
            "Analyze the log excerpt. Be concise and practical.\n\n"
            "Respond with EXACTLY these markers:\n"
            "<<<LIKELY_AREA>>>\n"
            "(one of: UI | API | DB | CONFIG | ENV | FLAKY | UNKNOWN)\n"
            "<<<END_LIKELY_AREA>>>\n"
            "<<<SUMMARY>>>\n"
            "2-4 sentences: what failed and the most probable cause.\n"
            "<<<END_SUMMARY>>>\n"
            "<<<SUGGESTED_FILES>>>\n"
            "- optional bullet lines: relative paths in the repo to inspect (guess from stack traces)\n"
            "<<<END_SUGGESTED_FILES>>>"
        )
        user = f"LOG EXCERPT:\n{safe}"
        out = self._client.generate_raw(system, user, props)
        audit_record = AiGenerationAuditRecord(
            request_id=str(uuid4()),
            timestamp=datetime.now(timezone.utc).isoformat(),
            command="triage",
            mode="service",
            model=props.model(),
            prompt_version=props.prompt_version(),
            requirement_hash=AiGenerationAuditLogger.sha256(safe),
            output_path="n/a",
            parse_successful=True,
            step_validation_passed=True,
            yaml_validation_passed=True,
            file_written=False,
            reused_steps_count=0,
            new_steps_count=0,
            missing_yaml_keys_count=0,
            warnings=[],
            blocking_errors=[],
        )
        AiGenerationAuditLogger().append(
            Path.cwd(),
            props.audit_enabled(),
            props.audit_output_path(),
            audit_record,
        )
        return out
