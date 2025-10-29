package com.datawhisperer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dialog for database authentication.
 * Collects connection details and tests the connection.
 */
public class AuthDialog extends JDialog {

    private JTextField hostField;
    private JTextField dbField;
    private JTextField userField;
    private JPasswordField passField;
    private boolean authenticated = false;
    private DatabaseConnector dbConnector;

    public AuthDialog(JFrame parent) {
        super(parent, "Database Authentication", true);
        dbConnector = new DatabaseConnector();
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(5, 2, 5, 5));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        add(new JLabel("Host:"));
        hostField = new JTextField("localhost:3306");
        add(hostField);

        add(new JLabel("Database:"));
        dbField = new JTextField("datawhisperer_db");
        add(dbField);

        add(new JLabel("Username:"));
        userField = new JTextField();
        add(userField);

        add(new JLabel("Password:"));
        passField = new JPasswordField();
        add(passField);

        JButton connectBtn = new JButton("Connect");
        connectBtn.addActionListener(e -> connect());
        add(connectBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> {
            authenticated = false;
            setVisible(false);
        });
        add(cancelBtn);

        pack();
        setLocationRelativeTo(getParent());
    }

    private void connect() {
        String host = hostField.getText().trim();
        String db = dbField.getText().trim();
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        if (host.isEmpty() || db.isEmpty() || user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        dbConnector.setCredentials(host, db, user, pass);
        try {
            dbConnector.getConnection();
            authenticated = true;
            setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Connection failed: " + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public DatabaseConnector showDialog() {
        setVisible(true);
        return authenticated ? dbConnector : null;
    }
}