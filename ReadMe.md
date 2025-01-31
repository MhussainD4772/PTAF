
---

# FNB PTAF - Playwright Test Automation Framework

## Overview

The **FNB PTAF** (Playwright Test Automation Framework) is an advanced test automation framework designed for automated end-to-end testing of web applications. It leverages [Playwright](https://playwright.dev/) for browser automation, [Cucumber](https://cucumber.io/) for implementing Behavior-Driven Development (BDD), and [TestNG](https://testng.org/) or [JUnit](https://junit.org/junit5/) for test execution. The framework is built with flexibility, maintainability, and scalability in mind, allowing tests to run in multiple environments (e.g., QA, staging, development) across different browsers (e.g., Chrome, Firefox, WebKit). Detailed reporting and easy-to-understand Gherkin scenarios allow non-technical team members to participate in the testing process.

The key aspects of **FNB PTAF** are:
- **Cross-browser testing** to ensure your application works across all major browsers.
- **Parallel execution** to speed up test execution, allowing tests to run concurrently in different environments or on different browsers.
- **Extensive reporting** using ExtentReports for comprehensive HTML reports that include screenshots, execution timelines, and pass/fail status for each test.
- **Environment-based configuration**, allowing easy switching between different URLs (e.g., staging, production) without modifying the core test logic.
- **Reusable utilities** for common actions (e.g., click, type, select) and assertions, making test development faster and easier to maintain.

---

## Key Features

### 1. Cross-Browser Support
The framework supports multiple browsers (Chrome, Firefox, and WebKit) using Playwright. Tests can be configured to run on different browsers depending on the needs of the testing phase.

### 2. Parallel Execution
By leveraging TestNG and JUnit, the framework allows tests to run in parallel, significantly reducing the time required to execute large test suites.

### 3. Behavior-Driven Development (BDD)
Cucumber is used to write test scenarios in Gherkin syntax, which makes the tests easier to understand for non-technical team members. Each scenario is linked to a step definition in Java, which implements the test logic using Playwright.

### 4. Environment-Based Configuration
Using YAML configuration files, the framework allows easy switching between environments (e.g., `rnd_url`, `qa_url`, `prod_url`) without altering the test code. This allows tests to be run in multiple environments, from development to production.

### 5. Detailed Reporting
The framework generates detailed HTML reports using ExtentReports, providing:
- A summary of passed, failed, and skipped tests.
- Screenshots of failures or specific steps.
- Execution timelines to visualize test progress and parallel execution.

---

## Project Structure

The **FNB PTAF** project follows a well-organized directory structure that separates test logic from configuration and reports. Here’s a breakdown of the structure:

```plaintext
FNB-PTAF/
│
├── src/
│   └── test/
│       ├── java/
│       │   ├── com/
│       │   │   └── ptaf/
│       │   │       ├── runners/             # Test runners (TestNG/JUnit)
│       │   │       └── stepdefinitions/     # Cucumber step definitions
│       │   └── resources/
│       │       └── features/                # Cucumber feature files
│       │       └── elements/                # YAML files for locators and configurations
│
├── target/                                   # Output directory for reports and logs
│   └── screenshots/                          # Captured screenshots of test failures
│
└── pom.xml                                   # Maven configuration file
```

---

## Key Features

### 1. Action Performer

**Description**: The Action Performer class encapsulates various methods for interacting with UI elements on a web page. This includes basic actions such as clicking, filling in forms, and advanced interactions like drag-and-drop.

**Example**: Clicking a button on a web page.

```java
ActionPerformer actionPerformer = new ActionPerformer();
Locator myLocator = page.locator("button#submit"); // Selecting the button locator
actionPerformer.performAction("click", myLocator, null); // Performing click action
```

### 2. Element Locator Helper

**Description**: This feature enables the retrieval of locators for web elements defined in external YAML configuration files. It allows better management of element definitions and promotes maintainability.

**Example**: Retrieving a locator for a username input field from a YAML file.

```yaml
# elements.yml
elements:
  loginForm:
    usernameInput: "CSS_input.username"  # CSS locator for username input
```

```java
// Using the ElementLocatorHelper to fetch the locator value
ElementLocatorHelper locatorHelper = new ElementLocatorHelper();
String usernameLocator = locatorHelper.getElement("loginForm", "usernameInput"); // Fetching locator value
Locator myLocator = page.locator(usernameLocator); // Using fetched locator
```

### 3. Page Helper

**Description**: The Page Helper class serves as a utility to assist with operations on the Playwright Page object, encapsulating common functionalities related to page interactions.

**Example**: Navigating to a specific URL.

```java
PageHelper pageHelper = new PageHelper(page); // Creating Page Helper instance
pageHelper.page.navigate("http://example.com/login"); // Navigating to URL
```

### 4. Cucumber Hooks Integration

**Description**: This feature manages the setup and teardown processes for Cucumber scenarios, ensuring the browser context is correctly initialized and cleaned up after each test execution.

**Example**: Setting up the browser context before a scenario runs.

```java
@Before
public void setUp(Scenario scenario) {
    // Initialize browser and context
    Hooks.setUp(scenario);
}

// Teardown after the scenario execution
@After
public void tearDown(Scenario scenario) {
    Hooks.tearDown(scenario);
}
```

### 5. Comprehensive Logging and Reporting

**Description**: The framework uses SLF4J for structured logging, helping keep track of actions and error states during test executions. Error logging is accompanied by details and screenshots when failures occur.

**Example**: Logging an error during a test execution.

```java
try {
    actionPerformer.performAction("click", myLocator, null); // Attempting to click
} catch (Exception e) {
    logger.error("Error while clicking on the button: {}", e.getMessage()); // Logging error details
}
```

### 6. Dynamic Content Waits

**Description**: The framework supports waiting mechanisms for elements to become visible or to transition states (like appearing or disappearing) to prevent flaky tests.

**Example**: Waiting for an element to appear before interacting with it.

```java
myLocator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE)); // Waiting for visibility
actionPerformer.performAction("click", myLocator, null);
```

### 7. File Upload and Download Support

**Description**: Simplifies file upload and download actions, allowing tests to manage files as part of the testing process.

**Example**: Uploading a file through an input field.

```java
Locator fileInputLocator = page.locator("input[type='file']"); // Locating file input
actionPerformer.performAction("uploadfile", fileInputLocator, "/path/to/file"); // Uploading the file
```

### 8. Error Handling

**Description**: Robust error handling throughout the framework ensures proper logging and recovery actions, improving test reliability.

**Example**: Capturing a screenshot and logging an error on test failure.

```java
try {
    // Perform test actions
} catch (Exception e) {
    logger.error("Test failed: {}", e.getMessage());
    byte[] screenshot = page.screenshot(); // Capture screenshot
    scenario.attach(screenshot, "image/png", "Screenshot for failure"); // Attach screenshot to report
}
```

---

## Conclusion

This Playwright Automation Framework is designed to offer a comprehensive, feature-rich solution for automating web testing. Each key feature is structured for easy integration and is supported by thorough example usages. Following this guide will enable you to maximize the efficiency and effectiveness of your automated testing strategies.

These professional excerpts can be copy-pasted directly into your documentation or README file, making it easy for users to understand and utilize the features of your automation framework.
### Technologies Used

- **Playwright**: A modern automation library for browser testing that supports multiple languages and frameworks.
- **JUnit/Cucumber**: For managing and running tests in a BDD style. This enables writing tests in a human-readable format and facilitates collaboration between technical and non-technical stakeholders.
- **YAML**: Used for configuration management, allowing for flexible and externalized element locator definitions.
- **Maven/Gradle**: Package management and build automation tools to manage dependencies and build processes.

### How to Use

#### 1. **Setup Your Environment**:
- Ensure you have Java Development Kit (JDK) and Maven/Gradle installed.
- Clone the repository or download the source code containing the framework.

#### 2. **Add Dependencies**:
Update your `pom.xml` (for Maven) or `build.gradle` (for Gradle) to include Playwright and necessary dependencies.

**Maven Example**:
   ```xml
   <dependencies>
       <dependency>
           <groupId>com.microsoft.playwright</groupId>
           <artifactId>playwright</artifactId>
           <version>1.16.0</version> <!-- Check for the latest version -->
       </dependency>
       <dependency>
           <groupId>io.cucumber</groupId>
           <artifactId>cucumber-java</artifactId>
           <version>6.10.4</version>
       </dependency>
       <!-- Additional dependencies -->
   </dependencies>
   ```

#### 3. **Define Configuration**:
Create a YAML configuration file in the `elements` directory that defines the locators for your elements. Ensure the structure is as follows:

**Example: elements.yml**
   ```yaml
   elements:
     loginForm:
       usernameInput: "CSS_username"
       passwordInput: "CSS_password"
       submitButton: "BUTTON_submit"
   ```

#### 4. **Create Test Scenarios**:
Write your feature files in Cucumber syntax, focusing on the business logic in human-readable language.

**Example: login.feature**
   ```gherkin
   Feature: User Login

     Scenario: Successful Login
       Given I navigate to the login page
       When I enter "testUser" in the username field
       And I enter "password123" in the password field
       And I click on the submit button
       Then I should see the welcome message
   ```

#### 5. **Implement Step Definitions**:
Create step definition classes that map to the steps defined in your feature files, utilizing the `PageCommonMethods` and other helper classes to perform actions.

**Example: LoginSteps.java**
   ```java
   public class LoginSteps {
       private final PageCommonMethods pageMethods;

       public LoginSteps(Page page) {
           this.pageMethods = new PageCommonMethods(page);
       }

       @Given("I navigate to the login page")
       public void iNavigateToTheLoginPage() {
           pageMethods.navigateTo("http://example.com/login"); // Implement navigateTo method accordingly
       }

       @When("I enter {string} in the username field")
       public void iEnterInTheUsernameField(String username) {
           pageMethods.fill(page, "Username Input", "usernameInput", username);
       }

       @When("I enter {string} in the password field")
       public void iEnterInThePasswordField(String password) {
           pageMethods.fill(page, "Password Input", "passwordInput", password);
       }

       @When("I click on the submit button")
       public void iClickOnTheSubmitButton() {
           pageMethods.click(page, "Submit Button", "submitButton");
       }

       @Then("I should see the welcome message")
       public void iShouldSeeTheWelcomeMessage() {
           Assert.assertTrue(pageMethods.assertElementTextPage(page, "Welcome Message", "welcomeText", "Welcome to the application!"));
       }
   }
   ```

#### 6. **Run Your Tests**:
Use your preferred test runner to execute your Cucumber tests. If configured properly, your automated tests will run against a real browser instance using the defined actions and configurations.

### Supported Actions

The framework supports a versatile set of actions that can be performed on UI elements:

- **Basic Interactions**:
   - Click/Double Click/Right Click
   - Fill input fields
   - Upload files
   - Select options from dropdown elements

- **Visibility Checks**:
   - Assert that the element is visible, enabled, or checked.

- **Dynamic Content Waits**:
   - Wait for elements to appear/disappear based on state or text content, preventing flaky tests.

### Error Handling

Robust error handling mechanisms are embedded throughout the framework. They include:

- **Logging**: Detailed logs are generated for each action taken, alongside error messages when exceptions occur. This provides context for debugging during test runs.

- **Screenshot Capture**: Automatically captures screenshots when a scenario fails, aiding in post-mortem analysis and visibility into the application state at the point of failure.

### Conclusion

This Playwright Automation Framework provides an extensive, feature-rich solution for automated web application testing. Leveraging modern practices and technology stacks, it allows QA teams to write clean, maintainable tests that are easy to extend and robust against changes in application behavior. By following the sections outlined in this README, you can effectively set up and run automation tests for your web applications, significantly enhancing your testing capabilities.

### Future Improvements

- **Integration with Continuous Integration (CI) Tools**: Potential integration with Jenkins, Travis CI, or GitHub Actions for automated execution of tests on code changes.
- **Cross-Browser Testing**: Extending support for multiple browsers and devices to ensure the application behaves consistently across various platforms.
- **Parallel Execution**: Implementing parallel test execution to speed up the testing process, thereby reducing the feedback time for developers.
- **Test Reporting**: Enhancing the reporting capabilities to generate more comprehensive test reports, including metrics on test execution times and success rates.

---

## Test Execution Flow

1. **Define Behavior in Feature Files:**
    - Test cases are written in Gherkin syntax within `.feature` files located in `src/test/resources/features/`. These feature files describe the application's expected behavior in a readable format.

   Example:
   ```gherkin
   Scenario: User navigates to login page
     Given we navigate to "qa_url"
     Then we should see the login form
   ```

2. **Step Definitions:**
    - Each Gherkin step in the feature file maps to a method in the `stepdefinitions/` folder. For example, the step "we navigate to 'qa_url'" is implemented in `CommonSteps.java` as follows:
      ```java
      @Given("we navigate to {string} url")
      public void weNavigateToURL(String url) {
          page.navigate(ConfigurationProperties.getBaseUrl(url));
      }
      ```

3. **Run the Tests:**
    - The tests can be executed using either TestNG or JUnit, depending on the runner specified in the `runners/` folder.
    - Use Maven to run tests:
      ```bash
      mvn clean test
      ```

4. **View Reports:**
    - After the test execution completes, detailed HTML reports are generated in the `target/` directory. ExtentReports provides interactive reports with execution details, screenshots of failed tests, and a timeline of the test execution.

---

## Environment-Specific Configuration

The framework supports environment-based configurations through YAML files. This enables easy switching between different environments (e.g., development, QA, production).

Example configuration in

`config.yml`:
```yaml
rnd_url: "https://pdscawebdev01.myfnb.us/Account/Login?ReturnUrl=%2F"
qa_url: "https://qa.fnbonline.fnb-online.com/documentcenter/Account/Login#"
```

- **Switching Environments:**
    - To change the environment for the tests (e.g., from QA to production), simply modify the feature files or the test runner to use the appropriate URL key from the configuration file. This avoids hardcoding URLs in the tests and ensures that the same test suite can be executed across different environments.

---

## Reporting

### ExtentReports
The framework integrates with **ExtentReports**, which generates rich, interactive HTML reports after each test execution. The reports include:
- **Pass/Fail Summary**: A breakdown of which tests passed, failed, or were skipped.
- **Detailed Steps**: Every step in each scenario is logged, showing exactly what was executed.
- **Screenshots**: If a test fails, a screenshot is automatically captured and embedded in the report.
- **Parallel Execution Timeline**: If tests are executed in parallel, the report will display a visual timeline of the execution.

Reports are stored in:
- **HTML Report**: `target/cucumber-reports.html`
- **Extent Report**: `target/extent-reports/`
- **Screenshots**: `target/screenshots/`

### Example of a Report Entry:
```plaintext
Test Case: User Login
Status: Failed
Step: User enters "wrong_password" in the password field
Screenshot: [embedded]
```

---

## How to Run Tests

### Prerequisites
- **Java**: Install [Java 11+](https://adoptopenjdk.net/).
- **Maven**: Install [Maven](https://maven.apache.org/install.html).
- **Playwright**: Install Playwright dependencies by running:
  ```bash
  mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install-deps"
  ```

### Running All Tests
To execute all Cucumber scenarios in parallel, run the following command:
```bash
mvn clean test
```

### Running Specific Tags
To run tests tagged with a specific keyword (e.g., `@regression`), use the following Maven command:
```bash
mvn test -Dcucumber.options="--tags @regression"
```

### Viewing Reports
After the test execution is complete, open the HTML report located at `target/cucumber-reports.html` in a browser to view the results.

---

## Adding New Features and Tests

### 1. Adding a New Feature
1. Create a new `.feature` file under `src/test/resources/features/`.
2. Define scenarios in Gherkin syntax that describe the new feature's behavior.

### 2. Adding New Step Definitions
1. If the steps in the feature file are not already defined, add corresponding methods in `src/test/java/com/ptaf/stepdefinitions/`.

### 3. Adding Locators for New Elements
1. Add new element locators to `src/test/resources/elements/elements.yml`.
2. Update step definitions to use the new locators.

---

## Conclusion

The **FNB PTAF (Playwright Test Automation Framework)** is designed to be flexible, scalable, and easy to maintain. With its environment-based configuration, parallel execution, detailed reporting, and reusable utilities, it significantly improves the speed and reliability of testing web applications. Whether you're testing locally or in a CI/CD pipeline, this framework provides the tools and structure needed to ensure your application is thoroughly tested across multiple environments and browsers.

Feel free to contribute to the project or raise issues as you continue to expand the test suite!