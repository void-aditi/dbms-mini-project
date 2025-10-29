package com.datawhisperer;

import java.sql.*;
import java.util.*;

/**
 * Executes SQL queries on the connected database and returns results.
 * Uses ResultSetMetaData to dynamically handle column information.
 */
public class QueryExecutor {

    private DatabaseConnector dbConnector;

    public QueryExecutor(DatabaseConnector dbConnector) {
        this.dbConnector = dbConnector;
    }

    /**
     * Executes the SQL query and returns data as Object[][].
     * First row contains column names, subsequent rows contain data.
     * @param sql the SQL SELECT query to execute
     * @return Object[][] with results; first row is column names
     * @throws SQLException if query execution fails
     */
    public Object[][] runSQL(String sql) throws SQLException {
        Connection conn = dbConnector.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        // Extract column names
        String[] columns = new String[colCount];
        for (int i = 0; i < colCount; i++) {
            columns[i] = meta.getColumnName(i + 1);
        }

        // Extract data rows
        List<Object[]> rows = new ArrayList<>();
        while (rs.next()) {
            Object[] row = new Object[colCount];
            for (int i = 0; i < colCount; i++) {
                row[i] = rs.getObject(i + 1);
            }
            rows.add(row);
        }

        rs.close();
        stmt.close();

        // Combine into result array
        Object[][] result = new Object[rows.size() + 1][];
        result[0] = columns;
        for (int i = 0; i < rows.size(); i++) {
            result[i + 1] = rows.get(i);
        }

        return result;
    }
}