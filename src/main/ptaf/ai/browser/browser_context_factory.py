"""Create browser context providers from ai_assistant.yml."""

from __future__ import annotations

from ptaf.ai.browser.browser_context_provider import BrowserContextProvider
from ptaf.ai.browser.mcp_browser_context_provider import McpBrowserContextProvider
from ptaf.ai.browser.playwright_context_provider import PlaywrightBrowserContextProvider
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties


def create_browser_context_provider(
    properties: AiAssistantProperties | None = None,
) -> BrowserContextProvider:
    props = properties or AiAssistantProperties()
    provider = props.browser_context_provider().strip().lower()
    if provider == "playwright":
        return PlaywrightBrowserContextProvider()
    if provider not in ("mcp", "playwright-mcp"):
        raise ValueError(
            f"Unknown browser_context.provider: {provider!r} (use 'mcp' or 'playwright')"
        )
    return McpBrowserContextProvider(props)
