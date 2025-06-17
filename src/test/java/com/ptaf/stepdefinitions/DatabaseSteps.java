package com.ptaf.stepdefinitions;

import com.ptaf.db.pages.DatabaseCommonMethods;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DatabaseSteps contains the Gherkin step definitions for interacting with the database.
 * It uses DatabaseCommonMethods to perform high-level, reusable database operations.
 */
public class DatabaseSteps {

    private final DatabaseCommonMethods dbMethods;

    public DatabaseSteps() {
        this.dbMethods = new DatabaseCommonMethods();
    }

    /**
     * A generic step to verify that one or more records exist in the database.
     * Fails the test if no records are found.
     *
     * @param queryKey The key from db_queries.yml (e.g., "users.get_user_by_email").
     * @param params   A comma-separated list of parameters for the query (e.g., "test@example.com, 123").
     */
    @Given("the database does not contain a record for query {string} with parameters {string}")
    public void the_database_does_not_contain_a_record_for_query(String queryKey, String params) {
        dbMethods.verifyRecordDoesNotExist(queryKey, parseParameters(params));
    }

    /**
     * A generic step to verify that one or more records exist in the database.
     * Fails the test if no records are found.
     *
     * @param queryKey The key from db_queries.yml (e.g., "users.get_user_by_email").
     * @param params   A comma-separated list of parameters for the query (e.g., "test@example.com, 123").
     */
    @Then("I verify the database contains a record for query {string} with parameters {string}")
    public void i_verify_the_database_contains_a_record_for_query_with_parameters(String queryKey, String params) {
        dbMethods.verifyRecordExists(queryKey, parseParameters(params));
    }

    /**
     * A generic step to verify that no records exist for a given query.
     * Fails the test if any records are found.
     *
     * @param queryKey The key from db_queries.yml.
     * @param params   A comma-separated list of parameters.
     */
    @Then("I verify the database does not contain a record for query {string} with parameters {string}")
    public void i_verify_the_database_does_not_contain_a_record_for_query_with_parameters(String queryKey, String params) {
        dbMethods.verifyRecordDoesNotExist(queryKey, parseParameters(params));
    }

    /**
     * A generic step to execute an INSERT statement and verify that one row was created.
     *
     * @param queryKey The key from db_queries.yml for the INSERT statement.
     * @param params   A comma-separated list of parameters for the new record.
     */
    @When("I insert a new record using query {string} with parameters {string}")
    public void i_insert_a_new_record_using_query_with_parameters(String queryKey, String params) {
        dbMethods.verifyRowsAffected(1, queryKey, parseParameters(params));
    }

    /**
     * A generic step to execute an UPDATE statement and verify that one row was updated.
     *
     * @param queryKey The key from db_queries.yml for the UPDATE statement.
     * @param params   A comma-separated list of parameters.
     */
    @When("I update a record using query {string} with parameters {string}")
    public void i_update_a_record_using_query_with_parameters(String queryKey, String params) {
        dbMethods.verifyRowsAffected(1, queryKey, parseParameters(params));
    }

    /**
     * A generic step to execute a DELETE statement and verify that a specific number of rows were deleted.
     *
     * @param expectedRows The number of rows you expect to be deleted.
     * @param queryKey     The key from db_queries.yml for the DELETE statement.
     * @param params       A comma-separated list of parameters.
     */
    @When("I delete {int} record\\(s) using query {string} with parameters {string}")
    public void i_delete_records_using_query_with_parameters(int expectedRows, String queryKey, String params) {
        dbMethods.verifyRowsAffected(expectedRows, queryKey, parseParameters(params));
    }

    /**
     * A helper method to parse the comma-separated parameter string from Gherkin steps.
     * It handles trimming whitespace and returns an array of Objects.
     *
     * @param paramString The string from the feature file, e.g., "test@example.com, 123, true"
     * @return An array of Objects.
     */
    private Object[] parseParameters(String paramString) {
        if (paramString == null || paramString.trim().isEmpty()) {
            return new Object[0]; // Return empty array if no params are provided
        }
        // Split by comma and trim whitespace from each resulting string
        return Arrays.stream(paramString.split(","))
                .map(String::trim)
                .toArray();
    }
}