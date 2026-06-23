package com.ptaf.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {
                "pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:",
                "timeline:test-output-thread/"
        },
        tags = "@regression",
        features = "src/test/resources/features",
        glue = {"com/ptaf/stepdefinitions", "com/ptaf/hooks"}
)
public class RegressionRunner {
}
