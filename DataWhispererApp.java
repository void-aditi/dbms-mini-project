package com.datawhisperer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Main application class for Data Whisperer.
 * Provides a Swing-based UI for converting natural language queries to SQL and executing them.
 */
public class DataWhispererApp extends JFrame {

    private JTextField nlInput;
    private JButton convertBtn;
    private JTextArea sqlArea;
    private JButton runBtn;
    private JTable resultTable;
    private JScrollPane tableScroll;
    private JList<String> historyList;
    private JScrollPane historyScroll;
    private DefaultListModel<String> historyModel;

    private DatabaseConnector dbConnector;
    private QueryTranslator translator;
    private QueryExecutor executor;

    public DataWhispererApp() {
        // Authenticate database connection
        AuthDialog dialog = new AuthDialog(this);
        dbConnector = dialog.showDialog();
        if (dbConnector == null) {
            System.exit(0);
        }

        // Initialize backend components
        translator = new QueryTranslator();
        executor = new QueryExecutor(dbConnector);

        initUI();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes the user interface components.
     */
    private void initUI() {
        setTitle("Data Whisperer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel (North)
        JPanel inputPanel = new JPanel(new BorderLayout());
        nlInput = new JTextField();
        convertBtn = new JButton("Convert to SQL");
        inputPanel.add(nlInput, BorderLayout.CENTER);
        inputPanel.add(convertBtn, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        // Center panel: SQL area and results table
        JPanel centerPanel = new JPanel(new BorderLayout());
        sqlArea = new JTextArea(5, 50);
        sqlArea.setEditable(false);
        JScrollPane sqlScroll = new JScrollPane(sqlArea);
        runBtn = new JButton("Run SQL");
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.add(sqlScroll, BorderLayout.CENTER);
        sqlPanel.add(runBtn, BorderLayout.SOUTH);

        resultTable = new JTable();
        tableScroll = new JScrollPane(resultTable);

        centerPanel.add(sqlPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // History panel (East)
        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        historyScroll = new JScrollPane(historyList);
        historyScroll.setPreferredSize(new Dimension(200, 0));
        add(historyScroll, BorderLayout.EAST);

        // Event listeners
        convertBtn.addActionListener(e -> convertToSQL());
        runBtn.addActionListener(e -> runSQL());

        pack();
        setVisible(true);
    }

    /**
     * Converts natural language input to SQL.
     */
    private void convertToSQL() {
        String nl = nlInput.getText().trim();
        if (nl.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a natural language query.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sql = translator.convertNLtoSQL(nl);
        sqlArea.setText(sql);
        // TODO: Add to history
    }

    /**
     * Executes the SQL query and displays results.
     */
    private void runSQL() {
        String sql = sqlArea.getText().trim();
        if (sql.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate SQL first.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isSafeQuery(sql)) {
            JOptionPane.showMessageDialog(this, "Only SELECT queries are allowed for safety.", "Security Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Object[][] data = executor.runSQL(sql);
            if (data.length == 0) {
                JOptionPane.showMessageDialog(this, "No results found.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] columns = (String[]) data[0];
            Object[][] rows = new Object[data.length - 1][];
            System.arraycopy(data, 1, rows, 0, rows.length);
            resultTable.setModel(new DefaultTableModel(rows, columns));
            // TODO: Add query to history
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error executing query: " + ex.getMessage(), "Execution Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Checks if the SQL query is safe (SELECT only).
     * @param sql the SQL query
     * @return true if safe, false otherwise
     */
    private boolean isSafeQuery(String sql) {
        String upper = sql.toUpperCase().trim();
        return upper.startsWith("SELECT") &&
               !upper.contains("INSERT") &&
               !upper.contains("UPDATE") &&
               !upper.contains("DELETE") &&
               !upper.contains("DROP") &&
               !upper.contains("ALTER");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataWhispererApp());
    }
}