package com.ptaf.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

// Specify the runner to use for executing the tests
@RunWith(Cucumber.class)
@CucumberOptions(
        // Define the output formats for the test reports
        plugin = {"pretty",                       // Print the test results in a readable format
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",  // Generate an HTML report in the specified location
                "timeline:test-output-thread/"
        },
        tags = "@regression",
        // Specify the location of the feature files
        features = "src/test/resources/features",
        // Specify the packages containing the step definitions and hooks
        glue = {"com/ptaf/stepdefinitions", "com/ptaf/hooks"}
)
public class Regression_Runner {
}
