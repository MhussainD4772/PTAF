# PTAF Java → Python Parity Report (M10)

**Branch:** `python-migration`  
**Baseline:** M0–M9 committed (`46bf4d1` and prior)  
**Date:** 2026-06-23  
**Runner:** `uv` from `python/`

## Executive summary

The Python port reproduces core framework behavior (hooks, UI/API/DB layers, AI pipeline, pytest-bdd wiring). Representative BDD runs execute end-to-end where external dependencies allow. Gaps are concentrated in Excel utilities, bank/SSL environments, intentionally stale API status assertions in one feature, and incomplete regex step binding in frame/new-page step modules.

---

## Representative feature parity

| Feature | Java expectation | Python result | Notes |
|---------|------------------|---------------|-------|
| `automation_panda.feature` (`-m Panda_Page`) | Pass against public Automation Panda site | **FAIL** | Navigate + step defs OK after M10 fixes; fails on `click` for `panda_page` / `home_tab` (`RuntimeError: Action 'click' failed`). Likely locator/YAML or site drift — needs human verification. |
| `google.feature` (`-m google`) | Pass Google search smoke | **Not run** (same step stack as Panda; `automation_panda` chosen as primary UI smoke) | Navigate step wired; expected to behave like Panda for page interactions. |
| `create_post_workflow_api.feature` (`-m api`) | Feature file asserts POST **500**, GET **404** | **FAIL** | All API steps bind and execute. Fails on JSONPath/title assertion: live JSONPlaceholder returns **201** on POST and **200** on GET with canned body — feature expectations do not match live API. |
| `api_test.feature` (BDD, no `@api` tag) | GET post 1 → 200 + JSON assertions | **PASS** | `uv run pytest steps/scenario_bindings.py::test_retrieve_a_specific_blog_post_and_verify_its_title` |
| `create_and_delete_user.feature` (`-m db`) | CRUD user in configured DB | **SKIP** | `DB_PASSWORD` unset in parity run; conftest auto-skips `@db` scenarios when DB not configured. |
| `uv run pytest -m smoke` | Java `@smoke` subset | **5 passed, 5 failed** | Passes are fixture unit tests (`tests/test_fixtures.py`). Five `@smoke` personal-bank BDD scenarios fail with `net::ERR_SSL_VERSION_OR_CIPHER_MISMATCH` against `qa.fnbonline.fnb-online.com` (environment/credentials, not framework wiring). |

---

## Smoke suite (`uv run pytest -m smoke`)

```
5 passed, 5 failed, 353 deselected
```

| Result | Tests |
|--------|-------|
| Pass | `test_browser_fixtures_provide_live_page`, `TestParallelSafeFixtures::*`, `TestLastScenarioFeatureReuse::*` |
| Fail | Five `personaBank_*` / `personaBank_Saving` smoke BDD scenarios (SSL to FNB QA) |

---

## API feature status

| Command | Result |
|---------|--------|
| `uv run pytest -m api` | 1 passed, 1 failed |
| Pass | `tests/test_api_client.py::test_jsonplaceholder_get_all_posts_integration` (unit/integration) |
| Pass | `api_test.feature` BDD scenario (see above) |
| Fail | `create_post_workflow_api.feature` — live status codes and response body differ from feature assertions |

**Known gap:** `create_post_workflow_api.feature` documents demo status codes (500/404) that do not match JSONPlaceholder live behavior (201/200). Align feature expectations or mark as mock-only before expecting green parity.

---

## AI pipeline

| Check | Result |
|-------|--------|
| `uv run pytest tests/ai/ -q` | **18 passed** |
| `uv run ptaf-ai generate --mode preview` | **Blocked** — `GEMINI_API_KEY` unset (`Error: Set GEMINI_API_KEY to your Google AI Studio API key`) |
| `uv run ptaf-ai quality --project-root .` | **OK** — 0 syntax issues; duplicate-step report written to `target/ai-quality-report.{json,html}` |
| CLI surface | `generate`, `quality`, `serve`, `telemetry-report`, `triage` (`ptaf-ai --help`) |

AI unit coverage mirrors Java validators, parser, generation mode evaluator, runnable gate, step reuse, and feature generator service smoke.

---

## Test counts

| Category | Java | Python |
|----------|------|--------|
| Unit / integration (`@Test` / `tests/`) | ~88 AI-focused `@Test` methods in `src/test/java` | **337** collected under `tests/` |
| AI unit tests | (subset of above) | **18** under `tests/ai/` |
| BDD scenarios | **13** `Scenario:` lines across `src/test/resources/features` | **26** collected BDD tests (`steps/scenario_bindings.py`; outlines expand rows) |
| **Total pytest collection** | — | **363** |

---

## Module coverage matrix

| Java package / class | Python module | Status |
|----------------------|---------------|--------|
| `com.ptaf.hooks.Hooks` | `ptaf/hooks.py` + `conftest.py` | Ported |
| `com.ptaf.utils.ConfigurationProperties` | `ptaf/utils/config.py` | Ported |
| `com.ptaf.utils.BrowserFactory` | `ptaf/utils/browser_factory.py` | Ported |
| `com.ptaf.utils.YamlReader` | `ptaf/utils/yaml_reader.py` | Ported |
| `com.ptaf.utils.ScenarioUtil` | `ptaf/utils/scenario_util.py` | Ported |
| `com.ptaf.utils.ExcelReader/Writer/ExcelToYaml` | — | **Not ported** |
| `com.ptaf.ui.pages.PageCommonMethods` | `ptaf/ui/page_common.py` | Ported |
| `com.ptaf.ui.pages.FrameCommonMethods` | `ptaf/ui/frame_common.py` | Ported |
| `com.ptaf.ui.action_performer.*` | `ptaf/ui/action_performer.py`, `assertion_performer.py`, `element_action.py` | Ported |
| `com.ptaf.ui.handlers.LocatorHandler` | `ptaf/ui/locator_handler.py` | Ported |
| `com.ptaf.ui.helpers.ElementLocatorHelper` | `ptaf/ui/locator_helper.py` | Ported |
| `com.ptaf.ui.page_helper.PageHelper` | — | **Not ported** |
| `com.ptaf.api.*` | `ptaf/api/api_client.py`, `api_common.py` | Ported |
| `com.ptaf.db.*` | `ptaf/db/db_handler.py`, `db_common.py`, `pages/database_common_methods.py` | Ported |
| `com.ptaf.stepdefinitions.*` | `python/steps/*_steps.py` | Ported (see gaps below) |
| `com.ptaf.ai.FeatureGeneratorService` | `ptaf/ai/feature_generator_service.py` | Ported |
| `com.ptaf.ai.GeminiClient` / `GeminiModelClient` | `ptaf/ai/gemini_client.py` | Ported |
| `com.ptaf.ai.PromptBuilder` | `ptaf/ai/prompt_builder.py` | Ported |
| `com.ptaf.ai.validation.*` | `ptaf/ai/validation/*` | Ported |
| `com.ptaf.ai.quality.*` | `ptaf/ai/quality/*` | Ported |
| `com.ptaf.ai.context.*` | `ptaf/ai/context/*` | Ported |
| `com.ptaf.ai.index.*` | `ptaf/ai/index/*` | Ported |
| `com.ptaf.ai.parser.*` | `ptaf/ai/parser/*` | Ported |
| `com.ptaf.ai.audit.*` | `ptaf/ai/audit/*` | Ported |
| `com.ptaf.ai.telemetry.*` | `ptaf/ai/telemetry/*` | Ported |
| `com.ptaf.ai.triage.TriageService` | `ptaf/ai/triage/triage_service.py` | Ported |
| `com.ptaf.ai.http.AiGenerateHttpServer` | `ptaf/ai/http/ai_generate_http_server.py` | Ported |
| `com.ptaf.ai.cli.AiAssistantCli` | `ptaf/ai/cli.py` | Ported |
| `com.ptaf.ai.cli.AiUiLauncher` | — | **Not ported** (desktop UI launcher) |
| `com.ptaf.ai.model.SourceChunk/ScoredPattern` | — | **Partial** (core generation models only) |

---

## `TODO(migration)` inventory

Collected via `grep -r 'TODO(migration)'` across the repo:

| File | Line | Item |
|------|------|------|
| `python/conftest.py` | 38 | Confirm `@LastScenario` reuse semantics for pytest-bdd feature tags. |

---

## Known gaps

1. **Excel utilities** — `ExcelReader`, `ExcelWriter`, `ExcelToYaml` not migrated; config comment references Excel paths only.
2. **Live API status codes** — `create_post_workflow_api.feature` expects 500/404; JSONPlaceholder returns 201/200.
3. **Bank / SSL environments** — Personal-bank smoke scenarios require reachable FNB QA with valid TLS; parity run hit `ERR_SSL_VERSION_OR_CIPHER_MISMATCH`.
4. **DB parity** — Set `DB_PASSWORD` (and PostgreSQL/SQL Server per `resources/config/config.yml`) to execute `@db` scenarios.
5. **pytest-bdd regex steps** — `page_common_steps.py` updated with `(?P<name>…)` groups (M10 blocker fix). `frame_common_steps.py` and `new_page_common_steps.py` still use unnamed `(.*?)` groups and may hit `fixture 'element' not found` when those steps are exercised.
6. **BDD step loading** — `conftest.py` `pytest_plugins` registers all `steps/*_steps.py` modules (M10 blocker fix).
7. **AI live preview** — Requires `GEMINI_API_KEY`; pipeline validated via 18 unit tests and `ptaf-ai quality` offline.
8. **`AiUiLauncher`** — Java desktop helper not ported.
9. **`PageHelper`** — Java UI helper not ported.

---

## M10 blocker fixes applied (Python only)

1. `conftest.py` — `pytest_plugins` to load Gherkin step modules.
2. `steps/frame_common_steps.py` — `@given` + `parsers.parse` for `we navigate to {config_key} url`.
3. `steps/page_common_steps.py` — named regex groups for pytest-bdd 8.x compatibility.

Java sources were **not** modified.

---

## Commands reference

```bash
cd python
uv sync && uv run playwright install

uv run pytest -m smoke -q
uv run pytest -m Panda_Page -q
uv run pytest -m api -q
uv run pytest -m db -q          # skip if DB_PASSWORD unset
uv run pytest tests/ai/ -q
uv run ptaf-ai quality --project-root .
uv run ptaf-ai generate -r "..." --mode preview   # needs GEMINI_API_KEY
```
