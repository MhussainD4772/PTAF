# Runners

The Java framework used runner classes here (`TestRunner`, `ApiTestRunner`,
`Regression_Runner`, etc.). In the Python (pytest-bdd) port there are no runner
classes — test execution is driven by:

- `pytest.ini` (markers, test paths, options) at the repo root
- `conftest.py` (fixtures / hooks) at the repo root

Run suites with markers instead of runners, e.g.:

```bash
uv run pytest -m api        # API tests   (Java ApiTestRunner)
uv run pytest -m db         # DB tests    (Java DatabaseTestRunner)
uv run pytest -m regression # Regression  (Java Regression_Runner)
uv run pytest               # Everything  (Java TestRunner)
```
