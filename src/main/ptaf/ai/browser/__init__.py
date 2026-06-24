"""Browser context collection for AI-assisted feature generation."""

from ptaf.ai.browser.browser_aware_generation_service import BrowserAwareGenerationService
from ptaf.ai.browser.browser_context_factory import create_browser_context_provider
from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.ai.browser.mcp_browser_context_provider import McpBrowserContextProvider
from ptaf.ai.browser.page_context_collector import PageContextCollector
from ptaf.ai.browser.playwright_context_provider import PlaywrightBrowserContextProvider

__all__ = [
    "BrowserAwareGenerationService",
    "BrowserPageContext",
    "PageContextCollector",
    "PlaywrightBrowserContextProvider",
    "McpBrowserContextProvider",
    "create_browser_context_provider",
]
