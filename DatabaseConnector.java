package com.datawhisperer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles database connections using JDBC.
 * Manages connection lifecycle and credentials.
 */
public class DatabaseConnector {

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public DatabaseConnector() {
        // Credentials will be set via setCredentials method
    }

    /**
     * Sets database credentials.
     * @param host database host (e.g., localhost:3306)
     * @param db database name (e.g., datawhisperer_db)
     * @param user username
     * @param pass password
     */
    public void setCredentials(String host, String db, String user, String pass) {
        this.url = "jdbc:mysql://" + host + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        this.user = user;
        this.password = pass;
    }

    /**
     * Gets or creates a database connection.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Note: MySQL JDBC driver must be in classpath (mysql-connector-java.jar)
            // In Java 8+, driver loading is automatic, but ensure jar is present
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}