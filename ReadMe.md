# PTAF — Playwright Python + pytest-bdd

Unified BDD test automation for **UI, API, and database** tests. Gherkin `.feature` files drive execution; locators and config live in YAML under `resources/`.

## Setup

Requires [uv](https://docs.astral.sh/uv/) and Python 3.11+.

```bash
uv sync
uv run playwright install
cp .env.example .env   # optional: DB_PASSWORD, GEMINI_API_KEY, API tokens
```

## Running tests

| Suite | Command |
|-------|---------|
| All tests | `uv run pytest` |
| UI only | `uv run pytest -m ui` |
| API only | `uv run pytest -m api` |
| Database only | `uv run pytest -m db` |
| Regression | `uv run pytest -m regression` |
| Smoke | `uv run pytest -m smoke` |
| Parallel | `uv run pytest -n auto` |
| Automation Panda (canary) | `uv run pytest steps/ -k panda` |

Cucumber tags map to pytest markers (drop the `@`): e.g. `@Panda_Page` → `uv run pytest -m Panda_Page`.

### Keyword-agnostic steps

Step definitions use pytest-bdd’s keyword-agnostic `keyword_step` decorator (`steps/step_binding.py`), matching Cucumber-JVM behavior: **Given / When / Then / And are interchangeable** for the same step text.

## Reporting

HTML reports are written to `reports/report.html` (pytest-html, self-contained with failure screenshots).

```bash
uv run pytest -m smoke --html=reports/report.html
```

## Configuration

| File | Purpose |
|------|---------|
| `resources/config/config.yml` | Browser, URLs, DB, API service settings |
| `resources/elements/*.yml` | UI locator maps |
| `resources/api_requests/api_requests.yml` | API request templates |
| `resources/queries/db_queries.yml` | SQL query templates |
| `.env` | Secrets (never commit — see `.env.example`) |

## AI feature generation

Gemini-powered Gherkin generation, validation, and quality gates:

```bash
uv run ptaf-ai generate --requirement "verify login page title"
uv run ptaf-ai serve --port 8080
uv run ptaf-ai quality --project-root .
uv run ptaf-ai telemetry-report
```

Configure `resources/config/ai_assistant.yml` and set `GEMINI_API_KEY` in `.env`.

## Project layout

```
ptaf/           # Framework core (UI, API, DB, AI, utils)
steps/          # pytest-bdd step definitions
features/       # Gherkin feature files
resources/      # YAML config, locators, queries
tests/          # Unit/integration tests (including tests/ai/)
conftest.py     # Browser fixtures and hooks
```

See `MIGRATION.md` for Java → Python migration notes and `PARITY_REPORT.md` for parity details.
