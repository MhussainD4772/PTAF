package com.ptaf.db.implementation;

import com.ptaf.db.handlers.DatabaseHandler;
import com.ptaf.db.interfaces.DatabaseAction;
import com.ptaf.db.performer.DatabaseActionPerformer;
import com.ptaf.utils.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Implements the DatabaseAction interface to provide concrete methods for interacting with the database.
 * This class orchestrates getting connections, reading queries from YAML, and executing them
 * via the DatabaseActionPerformer.
 */
public class DatabaseActionImpl implements DatabaseAction {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseActionImpl.class);
    private final DatabaseActionPerformer dbPerformer;

    public DatabaseActionImpl() {
        this.dbPerformer = new DatabaseActionPerformer();
    }

    @Override
    public List<Map<String, Object>> performQuery(String queryKey, Object... params) {
        // NOTE: This assumes your YamlReader is configured to load 'db_queries.yml'.
        // You may need to create a separate reader or adjust the existing one.
        String sql = (String) YamlReader.get(queryKey);
        if (sql == null) {
            logger.error("Query key '{}' not found in any YAML files.", queryKey);
            throw new IllegalArgumentException("Query key not found: " + queryKey);
        }

        logger.info("Performing query for key '{}' with parameters: {}", queryKey, Arrays.toString(params));

        try {
            Connection conn = DatabaseHandler.getConnection();
            return dbPerformer.executeQuery(conn, sql, Arrays.asList(params));
        } catch (SQLException e) {
            logger.error("Failed to execute query for key '{}'", queryKey, e);
            // Return empty list on failure so tests can assert on empty results
            return Collections.emptyList();
        }
    }

    @Override
    public int performUpdate(String queryKey, Object... params) {
        String sql = (String) YamlReader.get(queryKey);
        if (sql == null) {
            logger.error("Query key '{}' not found in any YAML files.", queryKey);
            throw new IllegalArgumentException("Query key not found: " + queryKey);
        }

        logger.info("Performing update for key '{}' with parameters: {}", queryKey, Arrays.toString(params));

        try {
            Connection conn = DatabaseHandler.getConnection();
            return dbPerformer.executeUpdate(conn, sql, Arrays.asList(params));
        } catch (SQLException e) {
            logger.error("Failed to execute update for key '{}'", queryKey, e);
            return -1; // Return -1 to indicate failure
        }
    }

    @Override
    public boolean recordExists(String queryKey, Object... params) {
        List<Map<String, Object>> results = performQuery(queryKey, params);
        boolean exists = !results.isEmpty();
        logger.info("Verifying record existence for key '{}'. Result: {}", queryKey, exists);
        return exists;
    }

    @Override
    public Map<String, Object> getSingleRecord(String queryKey, Object... params) {
        List<Map<String, Object>> results = performQuery(queryKey, params);
        if (results.isEmpty()) {
            logger.warn("Query for key '{}' returned no results.", queryKey);
            return null;
        }
        if (results.size() > 1) {
            logger.error("Query for key '{}' returned {} records, but only one was expected.", queryKey, results.size());
            throw new IllegalStateException("Expected single record, but found " + results.size());
        }
        return results.get(0);
    }

    @Override
    public Object getSingleValue(String queryKey, Object... params) {
        Map<String, Object> record = getSingleRecord(queryKey, params);
        if (record == null || record.isEmpty()) {
            logger.warn("Cannot get single value, as no record was found for key '{}'", queryKey);
            return null;
        }
        if (record.size() > 1) {
            logger.warn("Record has multiple columns, returning the first value only for key '{}'", queryKey);
        }
        // Return the first value from the map's entry set
        return record.values().iterator().next();
    }
}