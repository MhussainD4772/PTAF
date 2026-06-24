# Database testing in PTAF (Python)

PTAF runs database checks alongside UI and API tests via pytest-bdd. Playwright does not talk to the database directly; the framework uses **psycopg** (PostgreSQL) and **pyodbc** (SQL Server) through `ptaf/db/`.

## Configuration

Connection settings live in `resources/config/config.yml`:

```yaml
database:
  connection_url: "jdbc:postgresql://localhost:5432/your-database"
  username: "postgres"
  password_env_variable: "DB_PASSWORD"
```

Set the password in `.env` (see `.env.example`):

```bash
DB_PASSWORD=your_secret
```

SQL templates are defined in `resources/queries/db_queries.yml`.

## Step definitions

Database Gherkin steps are in `steps/database_steps.py` and delegate to `ptaf/db/db_handler.py` and `DatabaseCommonMethods`.

Example feature usage:

```gherkin
@db
Scenario: Verify user record
  Given I connect to the database
  When I execute the query "select_user_by_id" with parameters "1"
  Then the query result should contain "username" with value "alice"
```

Run DB scenarios:

```bash
uv run pytest -m db
```

## Dependencies

Database drivers are declared in `pyproject.toml` (`psycopg[binary]`, `pyodbc`). Install with:

```bash
uv sync
```

## Key points

- Store credentials in environment variables, not in YAML.
- Use parameterized queries from `db_queries.yml` rather than inline SQL in features.
- Tag scenarios with `@db` so they can be run independently of UI tests.
