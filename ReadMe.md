# FNB PTAF - Unified Playwright Test Automation Framework

## Overview

The **FNB PTAF** is an advanced, unified test automation framework designed for comprehensive end-to-end testing of modern applications. It seamlessly integrates **UI (Web)**, **API**, and **Database** testing into a single, cohesive platform.

It leverages industry-standard libraries like [Playwright](https://playwright.dev/) for browser and API automation and [Java JDBC](https://docs.oracle.com/javase/tutorial/jdbc/basics/index.html) for database connectivity. The framework uses [Cucumber](https://cucumber.io/) for implementing Behavior-Driven Development (BDD) and [TestNG](https://testng.org/)/[JUnit](https://junit.org/junit5/) for test execution.

The framework is built with flexibility, maintainability, and scalability in mind. Detailed reporting and easy-to-understand Gherkin scenarios allow technical and non-technical team members alike to participate in the quality assurance process.

The key aspects of **FNB PTAF** are:
- **Unified Testing**: Write and execute UI, API, and Database tests from a single, consistent project.
- **Cross-browser & Parallel Execution**: Run UI tests concurrently across major browsers to ensure application compatibility and speed up execution.
- **Environment-based configuration**: Easily switch between different environments (e.g., QA, Staging, Development) without modifying the core test logic.
- **Extensive reporting** using ExtentReports for comprehensive, interactive HTML reports that include screenshots and execution details.
- **Reusable utilities** and a layered architecture that makes test development faster and easier to maintain.

---

## Key Features

### 1. UI Automation (Playwright)
- **Cross-Browser Support**: Supports all modern browsers, including Chrome, Firefox, and WebKit.
- **Parallel Execution**: Leverages TestNG and JUnit to run large test suites in parallel, significantly reducing execution time.
- **Rich Actions**: Provides a wide range of supported actions, from basic clicks and fills to visibility checks and dynamic waits, preventing flaky tests.
- **Automatic Screenshots**: Captures screenshots on test failure for easy debugging.

### 2. API Automation
- **Full RESTful Support**: Test all HTTP methods (GET, POST, PUT, DELETE).
- **Stateful Request Building**: Construct complex API requests piece-by-piece using simple, declarative Gherkin steps.
- **Secure Authentication**: Handles API keys and access tokens securely via environment variables, keeping sensitive data out of the codebase.

### 3. Database Automation
- **Direct DB Interaction**: Connect directly to PostgreSQL or MS SQL Server to set up test data or verify application outcomes at the data layer.
- **Secure & Reusable Queries**: Manages SQL queries in external YAML files and uses `PreparedStatement` to prevent SQL injection vulnerabilities.

### 4. Cross-Cutting Features
- **Behavior-Driven Development (BDD)**: Cucumber and Gherkin make tests easy to read and write for everyone, fostering collaboration between business, development, and QA teams.
- **Centralized Configuration**: A single `config.yml` file manages environment URLs, database connections, and API service details, allowing for easy switching between environments.
- **Detailed Reporting**: Generates detailed HTML reports with pass/fail summaries, embedded screenshots for failures, and execution timelines.

---

## Project Structure

The project follows a well-organized directory structure that separates concerns and promotes reusability across UI, API, and DB testing layers.

```plaintext
FNB-PTAF/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/ptaf/             # Core framework classes (handlers, performers, etc.)
│   │           ├── api/              # API request builders and handlers
│   │           ├── db/               # Database connection and query performers
│   │           └── ui/               # Common page methods and UI utilities
│   │
│   └── test/
│       ├── java/
│       │   └── com/ptaf/
│       │       ├── runners/          # Test runners for UI, API, DB (TestNG/JUnit)
│       │       └── stepdefinitions/  # Cucumber step definitions for UI, API, DB
│       └── resources/
│           ├── features/             # Cucumber feature files for all test types
│           ├── elements/             # YAML files for UI element locators
│           ├── queries/              # YAML files for Database queries
│           ├── api_requests/         # YAML files for API request definitions
│           └── config.yml            # Global configuration for URLs, DB, API services
│
├── target/                           # Output directory for reports, logs, and screenshots
│
└── pom.xml                           # Maven configuration file for dependencies and build