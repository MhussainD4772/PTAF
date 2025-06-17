package com.ptaf.runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// Specify the runner to use for executing the tests
@RunWith(Cucumber.class)
@CucumberOptions(
        // The reporting plugins are identical to your UI runner for consistency
        plugin = {"pretty",
                "html:target/api-cucumber-reports.html", // Using a different report file name
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:test-output-thread/"
        },
        // This tag will run only the scenarios you mark as API tests
        tags = "@api",
        // The location of your feature files remains the same
        features = "src/test/resources/features",
        // IMPORTANT: The glue path is updated to find your API step definitions and hooks
        glue = {"com/ptaf/api/stepdefinitions", "com/ptaf/hooks"}
)
public class ApiTestRunner {
    // This class is intentionally empty. It serves as an entry point for the API tests.
}