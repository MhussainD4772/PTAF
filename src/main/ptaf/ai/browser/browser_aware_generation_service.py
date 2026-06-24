"""Explore a live page, then generate a feature file with browser context."""

from __future__ import annotations

from pathlib import Path

from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.ai.browser.page_context_collector import PageContextCollector
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.ai.feature_generator_service import FeatureGeneratorService
from ptaf.ai.model.generation_result import GenerationResult


class BrowserAwareGenerationService:
    """Phase 1: collect Playwright accessibility snapshot, then call Gemini."""

    def __init__(
        self,
        properties: AiAssistantProperties,
        collector: PageContextCollector | None = None,
        generator: FeatureGeneratorService | None = None,
    ) -> None:
        self._properties = properties
        self._collector = collector or PageContextCollector(properties=properties)
        self._generator = generator or FeatureGeneratorService(properties)

    def explore_and_generate(
        self,
        project_root: Path,
        requirement: str,
        *,
        url_config_key: str | None = None,
        url: str | None = None,
        max_snapshot_chars: int = 12_000,
    ) -> tuple[GenerationResult, BrowserPageContext]:
        if url_config_key and url:
            raise ValueError("Provide either url_config_key or url, not both")
        if url_config_key:
            browser_context = self._collector.collect_for_config_key(
                url_config_key,
                max_snapshot_chars=max_snapshot_chars,
            )
        elif url:
            browser_context = self._collector.collect_for_url(
                url,
                max_snapshot_chars=max_snapshot_chars,
            )
        else:
            raise ValueError("Provide --start-url-key or --url")

        result = self._generator.generate(
            project_root,
            requirement,
            browser_context=browser_context,
        )
        return result, browser_context
