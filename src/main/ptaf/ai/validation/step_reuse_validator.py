from __future__ import annotations

import re

from ptaf.ai.index.step_definition_index import StepDefinitionIndex
from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.step_reuse_validation_result import StepReuseValidationResult

_STEP_LINE = re.compile(r"^(Given|When|Then|And|But)\b.*", re.IGNORECASE)
_STEP_PREFIX = re.compile(r"^(Given|When|Then|And|But)\s+", re.IGNORECASE)
_LOW_REUSE_THRESHOLD = 50.0


class StepReuseValidator:
    def validate(
        self,
        structured_response: AiGenerationStructuredResponse,
        step_definition_index: StepDefinitionIndex,
    ) -> StepReuseValidationResult:
        feature_steps = _extract_feature_steps(structured_response.feature_file)
        matched: list[str] = []
        unmatched: list[str] = []

        for feature_step in feature_steps:
            if _matches_any_known_step(feature_step, step_definition_index.known_steps()):
                matched.append(feature_step)
            else:
                unmatched.append(feature_step)

        claimed_reused_but_not_found: list[str] = []
        for claimed in structured_response.reused_steps:
            if not _matches_any_known_step(claimed, step_definition_index.known_steps()):
                claimed_reused_but_not_found.append(claimed)

        claimed_new_but_already_exists: list[str] = []
        for claimed_new in structured_response.new_steps_needed:
            if _matches_any_known_step(claimed_new, step_definition_index.known_steps()):
                claimed_new_but_already_exists.append(claimed_new)

        normalized_claimed_new = {_normalize_step(step) for step in structured_response.new_steps_needed}
        all_unmatched_claimed_as_new = all(
            _normalize_step(step) in normalized_claimed_new for step in unmatched
        )

        total = len(feature_steps)
        matched_count = len(matched)
        unmatched_count = len(unmatched)
        reuse_percentage = 0.0 if total == 0 else (matched_count * 100.0) / total
        passed = all_unmatched_claimed_as_new

        warnings: list[str] = []
        for step in claimed_reused_but_not_found:
            warnings.append(f"AI claimed reused step not found: {step}")
        for step in claimed_new_but_already_exists:
            warnings.append(f"AI claimed new step already exists: {step}")
        if total > 0 and reuse_percentage < _LOW_REUSE_THRESHOLD:
            warnings.append(f"Low step reuse percentage: {reuse_percentage:.1f}%")

        return StepReuseValidationResult(
            feature_steps=feature_steps,
            matched_existing_steps=matched,
            unmatched_steps=unmatched,
            claimed_reused_but_not_found=claimed_reused_but_not_found,
            claimed_new_but_already_exists=claimed_new_but_already_exists,
            total_steps=total,
            matched_count=matched_count,
            unmatched_count=unmatched_count,
            reuse_percentage=reuse_percentage,
            passed=passed,
            warnings=warnings,
        )


def _extract_feature_steps(feature_file: str | None) -> list[str]:
    if not feature_file or not feature_file.strip():
        return []
    steps: list[str] = []
    for line in feature_file.splitlines():
        trimmed = line.strip()
        if _STEP_LINE.match(trimmed):
            steps.append(trimmed)
    return steps


def _matches_any_known_step(candidate_step: str, known_steps: list[str]) -> bool:
    normalized_candidate = _normalize_step(candidate_step)
    candidate_no_keyword_raw = _remove_keyword_and_trim(candidate_step)
    candidate_no_keyword_collapsed = _collapse_spaces(candidate_no_keyword_raw)

    for known_step in known_steps:
        normalized_known = _normalize_step(known_step)
        if normalized_known == normalized_candidate:
            return True
        if _matches_parameterized_pattern(normalized_known, normalized_candidate):
            return True
        if _matches_raw_regex_pattern(_remove_keyword_and_trim(known_step), candidate_no_keyword_collapsed):
            return True
    return False


def _matches_parameterized_pattern(known_pattern: str, normalized_candidate: str) -> bool:
    regex = _build_parameterized_regex(known_pattern)
    return re.match(regex, normalized_candidate, re.IGNORECASE) is not None


def _build_parameterized_regex(known_pattern: str) -> str:
    # pytest-bdd patterns often use value "{value}" — treat as a quoted-string slot.
    pattern = re.sub(
        r'"(\{([^}]+)\})"',
        lambda match: "{" + f"quoted:{match.group(2)}" + "}",
        known_pattern,
    )
    regex = ["^"]
    index = 0
    while index < len(pattern):
        if pattern[index] == "{":
            close = pattern.find("}", index)
            if close == -1:
                char = pattern[index]
                if char in ".\\^$|?*+()[]{}:":
                    regex.append("\\")
                regex.append(char)
                index += 1
                continue
            param = pattern[index + 1 : close]
            if param.startswith("quoted:"):
                regex.append('"[^"]*"')
            elif param.endswith(":d") or param.endswith(":i"):
                regex.append("-?\\d+")
            elif param.endswith(":f"):
                regex.append("-?\\d+(?:\\.\\d+)?")
            elif param in ("string",):
                regex.append('("[^"]*"|\'[^\']*\'|\\S+)')
            elif param in ("int",):
                regex.append("-?\\d+")
            elif param in ("double",):
                regex.append("-?\\d+(?:\\.\\d+)?")
            else:
                # pytest-bdd parse placeholders: {element}, {config_key}, {value}, etc.
                regex.append('(?:\"[^\"]*\"|\'[^\']*\'|[^\\s"]+)')
            index = close + 1
            continue

        if pattern.startswith("{string}", index):
            regex.append('("[^"]*"|\'[^\']*\'|\\S+)')
            index += len("{string}")
        elif pattern.startswith("{int}", index):
            regex.append("-?\\d+")
            index += len("{int}")
        elif pattern.startswith("{double}", index):
            regex.append("-?\\d+(?:\\.\\d+)?")
            index += len("{double}")
        else:
            char = pattern[index]
            if char in ".\\^$|?*+()[]{}:":
                regex.append("\\")
            regex.append(char)
            index += 1
    regex.append("$")
    return "".join(regex)


def _matches_raw_regex_pattern(known_pattern: str | None, candidate: str) -> bool:
    if not known_pattern or not known_pattern.strip():
        return False
    regex = _strip_regex_anchors(_collapse_spaces(known_pattern))
    try:
        return re.match(f"^{regex}$", candidate, re.IGNORECASE) is not None
    except re.error:
        return False


def _normalize_step(step: str) -> str:
    without_keyword = _remove_keyword_and_trim(step)
    without_anchors = _strip_regex_anchors(without_keyword)
    return _collapse_spaces(without_anchors).lower()


def _remove_keyword_and_trim(step: str | None) -> str:
    if step is None:
        return ""
    return _STEP_PREFIX.sub("", step.strip()).strip()


def _strip_regex_anchors(value: str | None) -> str:
    if value is None:
        return ""
    out = value
    if out.startswith("^"):
        out = out[1:].strip()
    if out.endswith("$"):
        out = out[:-1].strip()
    return out


def _collapse_spaces(value: str | None) -> str:
    if value is None:
        return ""
    return " ".join(value.strip().split())
