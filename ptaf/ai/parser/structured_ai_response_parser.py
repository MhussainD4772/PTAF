"""Parses strict marker-based output into a structured response."""

from __future__ import annotations

import re

from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse

_FEATURE_FILE = "FEATURE_FILE"
_REUSED_STEPS = "REUSED_STEPS"
_NEW_STEPS_NEEDED = "NEW_STEPS_NEEDED"
_YAML_KEYS_USED = "YAML_KEYS_USED"
_MISSING_YAML_KEYS = "MISSING_YAML_KEYS"
_WARNINGS = "WARNINGS"


def parse(raw: str | None) -> AiGenerationStructuredResponse:
    response = AiGenerationStructuredResponse()
    errors: list[str] = []

    response.feature_file = _extract_text_section(raw, _FEATURE_FILE, errors)
    response.reused_steps = _parse_bullet_section(raw, _REUSED_STEPS, errors)
    response.new_steps_needed = _parse_bullet_section(raw, _NEW_STEPS_NEEDED, errors)
    response.yaml_keys_used = _parse_bullet_section(raw, _YAML_KEYS_USED, errors)
    response.missing_yaml_keys = _parse_bullet_section(raw, _MISSING_YAML_KEYS, errors)
    response.warnings = _parse_bullet_section(raw, _WARNINGS, errors)
    response.parse_errors = errors
    response.parse_successful = not errors
    return response


def _extract_text_section(raw: str | None, name: str, errors: list[str]) -> str:
    block = _extract_block(raw, name, errors)
    return block.strip() if block else ""


def _parse_bullet_section(raw: str | None, name: str, errors: list[str]) -> list[str]:
    block = _extract_block(raw, name, errors)
    if not block or not block.strip():
        return []
    values: list[str] = []
    for line in block.splitlines():
        trimmed = line.strip()
        if trimmed.startswith("- "):
            value = trimmed[2:].strip()
        elif trimmed.startswith("* "):
            value = trimmed[2:].strip()
        else:
            continue
        if value:
            values.append(value)
    return values


def _extract_block(raw: str | None, name: str, errors: list[str]) -> str | None:
    if raw is None:
        errors.append("Raw response is null")
        return None
    pattern = re.compile(
        rf"<<<{re.escape(name)}>>>(.*?)<<<END_{re.escape(name)}>>>",
        re.DOTALL,
    )
    match = pattern.search(raw)
    if not match:
        errors.append(f"Missing required section: {name}")
        return None
    return match.group(1)
