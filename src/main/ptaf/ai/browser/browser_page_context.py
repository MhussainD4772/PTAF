"""Captured browser state fed into AI prompts."""

from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class BrowserPageContext:
    """Live page context from Playwright (same technique as Playwright MCP snapshots)."""

    url: str
    title: str
    aria_snapshot: str
    start_url_key: str | None = None
    provider: str = "playwright"

    def prompt_section(self) -> str:
        """Render for inclusion in the generation prompt."""
        lines = [
            f"URL: {self.url}",
            f"TITLE: {self.title}",
            f"PROVIDER: {self.provider}",
        ]
        if self.start_url_key:
            lines.append(f"START_URL_CONFIG_KEY: {self.start_url_key}")
        lines.append("")
        lines.append("ACCESSIBILITY_SNAPSHOT:")
        lines.append(self.aria_snapshot.strip() or "(empty)")
        return "\n".join(lines)
