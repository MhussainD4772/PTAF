# PTAF Java → Python migration

The Python framework is the repo root project. Use [uv](https://docs.astral.sh/uv/) for dependencies and test execution.

## Setup

```bash
uv sync
uv run playwright install
cp .env.example .env   # optional: DB_PASSWORD, API keys
```

## Running tests (Java runner equivalents)

| Java | Python |
|------|--------|
| `TestRunner` / `mvn test -Dcucumber.options="--tags @ui"` | `uv run pytest -m ui` |
| `ApiTestRunner` / `--tags @api` | `uv run pytest -m api` |
| `DatabaseTestRunner` / `--tags @db` | `uv run pytest -m db` |
| `RegressionRunner` / `--tags @regression` | `uv run pytest -m regression` |
| `--tags @smoke` | `uv run pytest -m smoke` |
| `ParallelRun` (TestNG parallel) | `uv run pytest -n auto` |

Feature-specific Cucumber tags map to the same pytest marker name (without `@`), for example:

- `@Panda_Page` → `uv run pytest -m Panda_Page`
- `@google` → `uv run pytest -m google`

BDD scenarios without `@api` or `@db` are treated as UI tests and receive the `ui` marker automatically.

## Reporting

HTML reports use **pytest-html**. Default output:

```bash
uv run pytest -m smoke --html=reports/report.html
```

`pytest.ini` also sets `--html=reports/report.html` and `--self-contained-html` so screenshots and assets are embedded in a single file.

Failed UI scenarios capture a full-page screenshot via hooks; the image is attached to the HTML report under **Extras**.

Reports are written to `reports/` (gitignored).

## Parallel execution

```bash
uv run pytest -n auto
uv run pytest -m regression -n auto
```

Requires `pytest-xdist` (included in `pyproject.toml`).
