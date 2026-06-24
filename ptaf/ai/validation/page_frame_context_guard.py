from __future__ import annotations

import re

from ptaf.ai.model.ai_generation_structured_response import AiGenerationStructuredResponse
from ptaf.ai.validation.page_frame_context_guard_result import PageFrameContextGuardResult

_PAGE_FRAME_STEP = re.compile(
    r".*on\s+(page|frame)\s+([a-zA-Z0-9_.-]+)\s+(?:locator|of\s+locator)\s+([a-zA-Z0-9_.-]+).*",
    re.IGNORECASE,
)
_UI_CONTEXT_HINT = re.compile(r".*on\s+(page|frame)\b.*", re.IGNORECASE)


class PageFrameContextGuard:
    def validate(
        self,
        structured_response: AiGenerationStructuredResponse,
        default_ui_context: str,
        frame_allowed_pages: list[str],
        frame_allowed_locators: list[str],
    ) -> PageFrameContextGuardResult:
        allowed_pages = _normalize_set(frame_allowed_pages)
        allowed_locators = _normalize_set(frame_allowed_locators)
        feature_file = structured_response.feature_file if structured_response else ""

        invalid_frame_steps: list[str] = []
        invalid_page_steps: list[str] = []
        warnings: list[str] = []
        blocking_errors: list[str] = []
        frame_count = 0
        page_count = 0

        for raw_line in feature_file.splitlines():
            line = raw_line.strip()
            if not line:
                continue
            match = _PAGE_FRAME_STEP.match(line)
            if not match:
                if _UI_CONTEXT_HINT.match(line) and " on frame " in line.lower():
                    warnings.append(f"Could not parse frame step context cleanly: {line}")
                continue

            context = match.group(1).lower()
            page = match.group(2).lower()
            locator = match.group(3).lower()

            if context == "frame":
                frame_count += 1
                compound = f"{page}.{locator}"
                allowed = page in allowed_pages or compound in allowed_locators
                if not allowed:
                    invalid_frame_steps.append(line)
                    blocking_errors.append(
                        f"Frame step is not allowed for page '{page}' locator '{locator}'. Use page step instead."
                    )
            else:
                page_count += 1
                if default_ui_context != "page":
                    invalid_page_steps.append(line)
                    warnings.append(
                        f"Encountered page step while defaultUiContext is '{default_ui_context}': {line}"
                    )

        return PageFrameContextGuardResult(
            passed=not blocking_errors,
            invalid_frame_steps=list(invalid_frame_steps),
            invalid_page_steps=list(invalid_page_steps),
            warnings=list(warnings),
            blocking_errors=list(blocking_errors),
            frame_step_count=frame_count,
            page_step_count=page_count,
        )


def _normalize_set(values: list[str] | None) -> set[str]:
    if not values:
        return set()
    out: set[str] = set()
    for value in values:
        if value and value.strip():
            out.add(value.strip().lower())
    return out
