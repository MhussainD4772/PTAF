# Browser context for AI generation (Phase 1)

Phase 1 uses the **official Microsoft Playwright MCP server** (`@playwright/mcp`) as the
default browser context provider.

## Setup (one-time)

From the repo root:

```bash
npm install
npm run mcp:install-browser
```

This installs `@playwright/mcp` locally (no `npx` download prompt) and pulls the Chromium
binary the MCP server uses. Python UI tests still use `uv run playwright install` separately.

## How it works

1. `ptaf-ai explore-generate` starts `node_modules/.bin/playwright-mcp` over stdio
2. Calls `browser_navigate` → `browser_snapshot` → `browser_close`
3. Parses the MCP snapshot (YAML accessibility tree with `[ref=eN]` refs)
4. Sends that as `BROWSER_CONTEXT` to Gemini with your manual scenario

## Configuration (`ai_assistant.yml`)

```yaml
browser_context:
  provider: mcp          # default — official Playwright MCP server
  mcp_command: npx     # used only if node_modules/.bin/playwright-mcp is missing
  mcp_server_args: []    # empty = auto from config.yml browser + headless
```

Set `provider: playwright` to use direct Python Playwright `page.aria_snapshot()` instead
(fallback / offline debugging only).
