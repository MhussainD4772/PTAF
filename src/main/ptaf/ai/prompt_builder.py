from __future__ import annotations

from typing import TYPE_CHECKING

from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.context.framework_generation_context import FrameworkGenerationContext

if TYPE_CHECKING:
    from ptaf.ai.browser.browser_page_context import BrowserPageContext


class PromptBuilder:
    def __init__(self, properties: AiAssistantProperties) -> None:
        self._properties = properties

    def system_prompt(self) -> str:
        return (
            "You are a senior test automation engineer for a Python + pytest-bdd + Playwright framework.\n"
            "Your job is to generate runnable feature content aligned to existing framework assets."
        )

    def user_prompt(
        self,
        requirement: str,
        context: FrameworkGenerationContext,
        similar_feature_snippets: list[str],
        browser_context: BrowserPageContext | None = None,
    ) -> str:
        similar_limited = _limit(similar_feature_snippets, self._properties.context_max_similar_features())
        steps_limited = _limit(context.existing_step_definitions, self._properties.context_max_step_definitions_in_prompt())
        yaml_limited = _limit(context.existing_yaml_keys, self._properties.context_max_yaml_keys_in_prompt())
        browser_section = (
            browser_context.prompt_section()
            if browser_context is not None
            else "(not collected — text-only generation)"
        )
        browser_rules = ""
        if browser_context is not None:
            browser_rules = """
- BROWSER_CONTEXT contains a live accessibility snapshot from Playwright.
- Map interactive elements to existing elements.* YAML keys when names/roles match.
- If no YAML key exists for a needed element, list it in MISSING_YAML_KEYS (do not invent keys in FEATURE_FILE).
- Prefer roles and names visible in ACCESSIBILITY_SNAPSHOT when choosing locators.
"""
        return f"""REQUIREMENT:
{requirement.strip()}

BROWSER_CONTEXT:
{browser_section}

SIMILAR_FEATURES:
{_render_section(similar_limited)}

ALLOWED_STEP_DEFINITIONS:
{_render_section(steps_limited)}

ALLOWED_YAML_KEYS:
{_render_section(yaml_limited)}

UI_CONTEXT_RULES:
DEFAULT_UI_CONTEXT: {self._properties.default_ui_context()}
FRAME_ALLOWED_PAGES:
{_render_section(self._properties.frame_allowed_pages())}
FRAME_ALLOWED_LOCATORS:
{_render_section(self._properties.frame_allowed_locators())}

RULES:
- You must reuse existing step definitions whenever possible.
- You must use only YAML keys from ALLOWED_YAML_KEYS.
- Do not invent YAML keys.
- If a needed YAML key does not exist, list it in MISSING_YAML_KEYS.
- Do not use a missing YAML key inside FEATURE_FILE as if it exists.
- Default to page steps.
- Use frame steps only when page or locator is explicitly frame-allowed.
- Do not use frame steps for login unless login is frame-allowed.
- Prefer page steps for normal UI actions.
- Prefer patterns from SIMILAR_FEATURES.
{browser_rules}- Return only the structured output contract.
- No markdown outside the required contract.
- Always include all contract sections, even if empty.
- Gherkin must include a Feature and at least one Scenario.

OUTPUT_CONTRACT:
<<<FEATURE_FILE>>>
Feature: ...
  Scenario: ...
    Given ...
<<<END_FEATURE_FILE>>>
<<<REUSED_STEPS>>>
- one bullet per line
<<<END_REUSED_STEPS>>>
<<<NEW_STEPS_NEEDED>>>
- one bullet per line
<<<END_NEW_STEPS_NEEDED>>>
<<<YAML_KEYS_USED>>>
- elements.some.key
<<<END_YAML_KEYS_USED>>>
<<<MISSING_YAML_KEYS>>>
- elements.missing.key
<<<END_MISSING_YAML_KEYS>>>
<<<WARNINGS>>>
- warning text
<<<END_WARNINGS>>>
"""


def _limit(items: list[str] | None, max_items: int) -> list[str]:
    if not items or max_items <= 0:
        return []
    if len(items) <= max_items:
        return items
    out = list(items[:max_items])
    out.append(f"[TRUNCATED: showing {max_items} of {len(items)}]")
    return out


def _render_section(lines: list[str] | None, empty_placeholder: str = "(none)") -> str:
    if not lines:
        return empty_placeholder
    return "\n".join(f"- {line}" for line in lines).strip()
