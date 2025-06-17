package com.ptaf.db.interfaces;

import java.util.List;
import java.util.Map;

/**
 * DatabaseAction defines the contract for performing high-level, reusable database operations.
 * This interface abstracts away the underlying SQL, allowing tests to interact with the
 * database using logical actions like "performQuery" or "verifyRecordExists".
 */
public interface DatabaseAction {

    /**
     * Executes a SELECT query identified by a key from the db_queries.yml file.
     *
     * @param queryKey The key corresponding to the SQL query in the YAML file (e.g., "users.get_user_by_email").
     * @param params   A varargs array of parameters to be safely passed to the query.
     * @return A List of Maps, where each map represents a row of the result set. Returns an empty list if no results are found.
     */
    List<Map<String, Object>> performQuery(String queryKey, Object... params);

    /**
     * Executes an INSERT, UPDATE, or DELETE statement identified by a key from the db_queries.yml file.
     *
     * @param queryKey The key corresponding to the SQL statement in the YAML file (e.g., "users.delete_user_by_email").
     * @param params   A varargs array of parameters to be safely passed to the statement.
     * @return The number of rows affected by the execution.
     */
    int performUpdate(String queryKey, Object... params);

    /**
     * A convenience method to verify if at least one record exists for a given query and parameters.
     *
     * @param queryKey The key for the SELECT query in the YAML file.
     * @param params   The parameters for the query.
     * @return true if the query returns one or more records, false otherwise.
     */
    boolean recordExists(String queryKey, Object... params);

    /**
     * A convenience method to retrieve a single record from the database.
     * Useful for queries that are expected to return exactly one result (e.g., find by primary key).
     *
     * @param queryKey The key for the SELECT query in the YAML file.
     * @param params   The parameters for the query.
     * @return A Map representing the single record, or null if no record is found.
     * @throws IllegalStateException if the query returns more than one record.
     */
    Map<String, Object> getSingleRecord(String queryKey, Object... params);

    /**
     * A convenience method to retrieve a single value from a query result.
     * Useful for queries that return one row and one column (e.g., SELECT COUNT(*)...).
     *
     * @param queryKey The key for the SELECT query in the YAML file.
     * @param params   The parameters for the query.
     * @return The single value as an Object, or null if no value is found.
     */
    Object getSingleValue(String queryKey, Object... params);

}