"""Facade for browser context collection."""

from __future__ import annotations

from ptaf.ai.browser.browser_context_factory import create_browser_context_provider
from ptaf.ai.browser.browser_context_provider import BrowserContextProvider
from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.utils import config


class PageContextCollector:
    """Resolve a start URL and collect live page context for AI generation."""

    def __init__(
        self,
        provider: BrowserContextProvider | None = None,
        properties: AiAssistantProperties | None = None,
    ) -> None:
        props = properties or AiAssistantProperties()
        self._provider = provider or create_browser_context_provider(props)

    def collect_for_config_key(
        self,
        url_config_key: str,
        *,
        max_snapshot_chars: int = 12_000,
    ) -> BrowserPageContext:
        base_url = config.get_base_url(url_config_key)
        if not base_url:
            raise ValueError(
                f"URL config key not found or empty: {url_config_key!r} "
                "(check src/test/resources/config/config.yml)"
            )
        return self._provider.collect(
            base_url,
            start_url_key=url_config_key,
            max_snapshot_chars=max_snapshot_chars,
        )

    def collect_for_url(
        self,
        url: str,
        *,
        max_snapshot_chars: int = 12_000,
    ) -> BrowserPageContext:
        if not url.strip():
            raise ValueError("URL must not be empty")
        return self._provider.collect(url.strip(), max_snapshot_chars=max_snapshot_chars)
