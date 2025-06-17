package com.ptaf.db.performer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseActionPerformer contains the low-level logic for executing SQL queries
 * and updates against a database connection.
 *
 * It uses PreparedStatement to ensure all queries are parameterized and secure
 * against SQL injection. It handles processing ResultSets into a generic format.
 */
public class DatabaseActionPerformer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseActionPerformer.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    public DatabaseActionPerformer() {
        // This class is designed to be used statically or instantiated by DatabaseActionImpl
    }

    /**
     * Executes a SELECT query and returns the results as a list of maps.
     * Each map represents a row, with column names as keys.
     *
     * @param conn   The active database connection.
     * @param sql    The SQL query string with '?' placeholders.
     * @param params A list of parameters to be safely substituted into the query.
     * @return A List of Maps, where each map is a row of the result set. Returns an empty list if no results are found.
     * @throws SQLException if a database access error occurs.
     */
    public List<Map<String, Object>> executeQuery(Connection conn, String sql, List<Object> params) throws SQLException {
        logger.debug("Executing Query: {}", sql);
        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            // Safely set the parameters for the query
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Iterate through each row of the result set
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        // Use column label (name) as the key
                        row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(row);
                }
            }
        }
        logger.debug("Query returned {} rows.", results.size());
        return results;
    }

    /**
     * Executes an INSERT, UPDATE, or DELETE statement.
     *
     * @param conn   The active database connection.
     * @param sql    The SQL statement with '?' placeholders.
     * @param params A list of parameters to be safely substituted into the statement.
     * @return The number of rows affected by the execution.
     * @throws SQLException if a database access error occurs.
     */
    public int executeUpdate(Connection conn, String sql, List<Object> params) throws SQLException {
        logger.debug("Executing Update: {}", sql);
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            // Safely set the parameters
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
            int affectedRows = preparedStatement.executeUpdate();
            logger.debug("{} rows were affected.", affectedRows);
            return affectedRows;
        }
    }
}