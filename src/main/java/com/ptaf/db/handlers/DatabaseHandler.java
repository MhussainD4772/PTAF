package com.ptaf.db.handlers;

import com.ptaf.ui.utils.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DatabaseHandler is a utility class responsible for managing the lifecycle of a database connection.
 * It uses ThreadLocal to ensure each test thread gets its own isolated connection,
 * preventing conflicts during parallel execution.
 *
 * It securely retrieves database credentials from the configuration file and environment variables.
 */
public class DatabaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHandler.class);
    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private DatabaseHandler() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Retrieves the database connection for the current thread.
     * If a connection does not exist for the thread, it creates a new one.
     *
     * @return A thread-safe java.sql.Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        if (connectionThreadLocal.get() == null || connectionThreadLocal.get().isClosed()) {
            logger.info("No existing connection found for this thread. Creating a new one.");
            connectionThreadLocal.set(createConnection());
        }
        return connectionThreadLocal.get();
    }

    /**
     * Closes the database connection for the current thread and removes it from ThreadLocal.
     * This should be called in an @After hook to ensure resources are released.
     */
    public static void closeConnection() {
        try {
            Connection connection = connectionThreadLocal.get();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection for this thread has been closed.");
            }
        } catch (SQLException e) {
            logger.error("Failed to close database connection.", e);
        } finally {
            // Always remove the thread local variable to prevent memory leaks
            connectionThreadLocal.remove();
        }
    }

    /**
     * Creates a new database connection using credentials from the configuration properties.
     * The password is read securely from a system environment variable.
     *
     * @return A new database Connection object.
     * @throws SQLException if the connection cannot be established.
     */
    private static Connection createConnection() throws SQLException {
        try {
            // Load database connection details from config.yml
            String url = ConfigurationProperties.getValue("database.connection_url");
            String username = ConfigurationProperties.getValue("database.username");
            String passwordEnvVariable = ConfigurationProperties.getValue("database.password_env_variable");

            // Securely retrieve the password from the environment variable
            String password = System.getenv(passwordEnvVariable);

            if (url == null || username == null) {
                throw new IllegalArgumentException("Database URL or username is not set in the configuration file.");
            }
            if (password == null) {
                throw new IllegalArgumentException("Database password environment variable '" + passwordEnvVariable + "' is not set.");
            }

            logger.info("Attempting to connect to database at URL: {}", url);
            return DriverManager.getConnection(url, username, password);

        } catch (IllegalArgumentException e) {
            logger.error("Configuration error for database connection.", e);
            throw e; // Re-throw as a runtime exception to fail the test immediately
        }
    }
}