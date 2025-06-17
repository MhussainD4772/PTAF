package com.ptaf.db.pages; // Using this package to mirror your UI structure

import com.ptaf.db.implementation.DatabaseActionImpl;
import com.ptaf.db.interfaces.DatabaseAction;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * DatabaseCommonMethods provides a high-level API for interacting with the database during tests.
 * This class translates simple, readable method calls into database actions, which are
 * then used in the step definition files. It's the equivalent of Page/FrameCommonMethods for the DB.
 */
public class DatabaseCommonMethods {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseCommonMethods.class);
    private final DatabaseAction databaseAction;

    public DatabaseCommonMethods() {
        this.databaseAction = new DatabaseActionImpl();
    }

    /**
     * Retrieves a list of records from the database based on a predefined query.
     *
     * @param queryKey The key for the SELECT query in db_queries.yml.
     * @param params   Parameters to pass to the query.
     * @return A List of Maps, where each map represents a row.
     */
    public List<Map<String, Object>> getRecords(String queryKey, Object... params) {
        logger.info("Getting records for query key: {}", queryKey);
        return databaseAction.performQuery(queryKey, params);
    }

    /**
     * Retrieves a single record, expecting only one result. Fails the test if more than one is found.
     *
     * @param queryKey The key for the SELECT query in db_queries.yml.
     * @param params   Parameters to pass to the query.
     * @return A single Map representing the database row.
     */
    public Map<String, Object> getSingleRecord(String queryKey, Object... params) {
        logger.info("Getting single record for query key: {}", queryKey);
        return databaseAction.getSingleRecord(queryKey, params);
    }

    /**
     * Retrieves a single value (from the first column of the first row).
     *
     * @param queryKey The key for the SELECT query in db_queries.yml.
     * @param params   Parameters to pass to the query.
     * @return An Object representing the single value.
     */
    public Object getSingleValue(String queryKey, Object... params) {
        logger.info("Getting single value for query key: {}", queryKey);
        return databaseAction.getSingleValue(queryKey, params);
    }

    /**
     * Verifies that at least one record exists in the database for the given query. Fails the test if not found.
     *
     * @param queryKey The key for the SELECT query in db_queries.yml.
     * @param params   Parameters to pass to the query.
     */
    public void verifyRecordExists(String queryKey, Object... params) {
        logger.info("Verifying record exists for query key: {}", queryKey);
        boolean exists = databaseAction.recordExists(queryKey, params);
        Assert.assertTrue("Verification failed: Record for query '" + queryKey + "' was not found.", exists);
        logger.info("Success: Record for query key '{}' found as expected.", queryKey);
    }

    /**
     * Verifies that no records exist in the database for the given query. Fails the test if any are found.
     *
     * @param queryKey The key for the SELECT query in db_queries.yml.
     * @param params   Parameters to pass to the query.
     */
    public void verifyRecordDoesNotExist(String queryKey, Object... params) {
        logger.info("Verifying record does NOT exist for query key: {}", queryKey);
        boolean exists = databaseAction.recordExists(queryKey, params);
        Assert.assertFalse("Verification failed: Record for query '" + queryKey + "' was found but not expected.", exists);
        logger.info("Success: Record for query key '{}' was not found, as expected.", queryKey);
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE query and verifies that a specific number of rows were affected.
     *
     * @param expectedRowsAffected The exact number of rows that should be changed by the query.
     * @param queryKey             The key for the INSERT/UPDATE/DELETE statement in db_queries.yml.
     * @param params               Parameters to pass to the statement.
     */
    public void verifyRowsAffected(int expectedRowsAffected, String queryKey, Object... params) {
        logger.info("Executing update for query key '{}' and verifying {} rows affected.", queryKey, expectedRowsAffected);
        int actualRowsAffected = databaseAction.performUpdate(queryKey, params);
        Assert.assertEquals("Verification failed: Unexpected number of rows affected.", expectedRowsAffected, actualRowsAffected);
        logger.info("Success: {} rows were affected as expected.", actualRowsAffected);
    }

}