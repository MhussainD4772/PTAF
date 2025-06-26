package com.ptaf.runners;

import org.junit.runner.RunWith;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

// Specify the runner to use for executing the tests
@RunWith(Cucumber.class)
@CucumberOptions(
        // The reporting plugins are identical for consistency
        plugin = {"pretty",
                "html:target/db-cucumber-reports.html", // Using a unique report file name
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:test-output-thread/"
        },
        // This tag will run only the scenarios you mark as database tests
        tags = "@db",
        // The location of your feature files remains the same
        features = "src/test/resources/features",
        // IMPORTANT: The glue path is updated to find your database step definitions and hooks
        glue = {"com/ptaf/stepdefinitions", "com/ptaf/hooks"}
)
public class DatabaseTestRunner {
    // This class is intentionally empty. It serves as an entry point for the database tests.
}