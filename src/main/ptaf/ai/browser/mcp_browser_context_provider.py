"""Collect browser context via Microsoft's @playwright/mcp MCP server."""

from __future__ import annotations

import asyncio
import logging
import shutil
from pathlib import Path

from mcp import ClientSession, StdioServerParameters
from mcp.client.stdio import stdio_client

from ptaf.ai.browser.browser_page_context import BrowserPageContext
from ptaf.ai.browser.mcp_response_parser import (
    extract_page_title,
    extract_page_url,
    extract_snapshot_yaml,
    tool_result_text,
)
from ptaf.ai.config.ai_assistant_properties import AiAssistantProperties
from ptaf.utils import config

logger = logging.getLogger(__name__)

_BROWSER_MAP = {
    "CHROME": "chromium",
    "CHROMIUM": "chromium",
    "FIREFOX": "firefox",
    "WEBKIT": "webkit",
    "EDGE": "chromium",
}


def _trim_snapshot(text: str, max_chars: int) -> str:
    cleaned = text.strip()
    if len(cleaned) <= max_chars:
        return cleaned
    return (
        cleaned[:max_chars]
        + f"\n...[truncated: {len(cleaned) - max_chars} chars omitted]"
    )


def _resolve_mcp_browser() -> str:
    browser_name = (config.get_browser() or "chrome").strip().upper()
    return _BROWSER_MAP.get(browser_name, "chromium")


def _headless_enabled() -> bool:
    raw = config.get_headless_mode() or "false"
    return raw.strip().lower() == "true"


class McpBrowserContextProvider:
    """Official Playwright MCP server (@playwright/mcp) over stdio."""

    def __init__(self, properties: AiAssistantProperties | None = None) -> None:
        self._properties = properties or AiAssistantProperties()

    def collect(
        self,
        url: str,
        *,
        start_url_key: str | None = None,
        max_snapshot_chars: int = 12_000,
    ) -> BrowserPageContext:
        local_mcp = (
            self._properties.project_root() / "node_modules" / ".bin" / "playwright-mcp"
        )
        if shutil.which(self._properties.mcp_command()) is None and not local_mcp.is_file():
            raise RuntimeError(
                "Playwright MCP is not installed. From the repo root run:\n"
                "  npm install\n"
                "  npm run mcp:install-browser"
            )
        return asyncio.run(
            self._collect_async(
                url,
                start_url_key=start_url_key,
                max_snapshot_chars=max_snapshot_chars,
            )
        )

    async def _collect_async(
        self,
        url: str,
        *,
        start_url_key: str | None,
        max_snapshot_chars: int,
    ) -> BrowserPageContext:
        command, args = self._resolve_mcp_launch()
        params = StdioServerParameters(command=command, args=args)
        logger.info("Starting Playwright MCP server: %s %s", params.command, params.args)

        async with stdio_client(params) as (read, write):
            async with ClientSession(read, write) as session:
                await session.initialize()

                navigate = await session.call_tool("browser_navigate", {"url": url})
                if navigate.isError:
                    raise RuntimeError(
                        f"browser_navigate failed: {tool_result_text(navigate.content)}"
                    )

                snapshot = await session.call_tool("browser_snapshot", {})
                if snapshot.isError:
                    raise RuntimeError(
                        f"browser_snapshot failed: {tool_result_text(snapshot.content)}"
                    )

                try:
                    await session.call_tool("browser_close", {})
                except Exception:
                    logger.debug("browser_close failed during MCP cleanup", exc_info=True)

        combined = tool_result_text(snapshot.content)
        navigate_text = tool_result_text(navigate.content)
        page_url = extract_page_url(combined) or extract_page_url(navigate_text) or url
        page_title = extract_page_title(combined) or extract_page_title(navigate_text)
        snapshot_yaml = _trim_snapshot(
            extract_snapshot_yaml(combined),
            max_snapshot_chars,
        )

        return BrowserPageContext(
            url=page_url,
            title=page_title,
            aria_snapshot=snapshot_yaml,
            start_url_key=start_url_key,
            provider="playwright-mcp",
        )

    def _mcp_server_args(self) -> list[str]:
        configured = self._properties.mcp_server_args()
        if configured:
            return list(configured)

        args = ["--isolated", f"--browser={_resolve_mcp_browser()}"]
        if _headless_enabled():
            args.append("--headless")
        return args

    def _resolve_mcp_launch(self) -> tuple[str, list[str]]:
        """Prefer project-local @playwright/mcp (npm install) over npx -y download."""
        server_args = self._mcp_server_args()
        root = self._properties.project_root()
        local_mcp = root / "node_modules" / ".bin" / "playwright-mcp"
        if local_mcp.is_file():
            return str(local_mcp), server_args

        configured_command = self._properties.mcp_command()
        if configured_command != "npx":
            return configured_command, ["-y", "@playwright/mcp@latest", *server_args]

        if shutil.which("npx") is None:
            raise RuntimeError(
                "Playwright MCP is not installed. From the repo root run:\n"
                "  npm install\n"
                "  npm run mcp:install-browser"
            )
        return "npx", ["-y", "@playwright/mcp@latest", *server_args]
