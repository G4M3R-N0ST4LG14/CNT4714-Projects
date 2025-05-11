package project_2;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class AccountantApp extends JFrame {

    private JComboBox<String> propertiesFilesList;
    private JComboBox<String> userPropertiesFilesList;
    private JTextField jtfUsername;
    private JPasswordField jpfPassword;

    private JTextArea jtaSqlCommand;
    private JTextArea jtaSqlExecution;

    private JButton jbtConnectToDB;
    private JButton jbtDisconnectFromDB;
    private JButton jbtClearSQLCommand;
    private JButton jbtExecuteSQLCommand;
    private JButton jbtClearResultWindow;

    private ResultSetTableModel tableModel = null;
    private JTable table;

    private Connection connection;
    private boolean connectedToDatabase = false;

    private JLabel jlbConnectionStatus;

    public AccountantApp() throws ClassNotFoundException, SQLException, IOException {
        GUIComponents();
        EventListeners();
        GUISetup();
    }

    private void GUIComponents() throws ClassNotFoundException, SQLException, IOException {
        // Fixed options for properties files
        String[] propertiesFiles = {"operationslog.properties"};
        propertiesFilesList = new JComboBox<>(propertiesFiles);

        String[] userPropertiesFiles = {"theaccountant.properties"};
        userPropertiesFilesList = new JComboBox<>(userPropertiesFiles);

        jtfUsername = new JTextField(20);
        jtfUsername.setBackground(Color.WHITE);

        jpfPassword = new JPasswordField(20);
        jpfPassword.setBackground(Color.WHITE);

        jtaSqlCommand = new JTextArea(5, 75);
        jtaSqlCommand.setWrapStyleWord(true);
        jtaSqlCommand.setLineWrap(true);
        jtaSqlCommand.setBackground(Color.LIGHT_GRAY);

        jtaSqlExecution = new JTextArea(5, 75);
        jtaSqlExecution.setWrapStyleWord(true);
        jtaSqlExecution.setLineWrap(true);
        jtaSqlExecution.setBackground(Color.WHITE);
        jtaSqlExecution.setEditable(false);

        jbtConnectToDB = new JButton("Connect to Database");
        jbtConnectToDB.setBackground(Color.GREEN);

        jbtDisconnectFromDB = new JButton("Disconnect From Database");
        jbtDisconnectFromDB.setBackground(Color.RED);

        jbtClearSQLCommand = new JButton("Clear SQL Command");
        jbtClearSQLCommand.setBackground(Color.ORANGE);

        jbtExecuteSQLCommand = new JButton("Execute SQL Command");
        jbtExecuteSQLCommand.setBackground(Color.YELLOW);

        jbtClearResultWindow = new JButton("Clear Result Window");
        jbtClearResultWindow.setBackground(Color.CYAN);

        jlbConnectionStatus = new JLabel("No Connection Established");
        jlbConnectionStatus.setForeground(Color.RED);

        table = new JTable();
    }

    private void EventListeners() {
        jbtConnectToDB.addActionListener(e -> ConnectToDB());
        jbtDisconnectFromDB.addActionListener(e -> DisconnectFromDB());
        jbtClearSQLCommand.addActionListener(e -> jtaSqlCommand.setText(""));
        jbtExecuteSQLCommand.addActionListener(e -> SQLCommand());
        jbtClearResultWindow.addActionListener(e -> ResultWindows());

        addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent event) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private void GUISetup() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("DB URL Properties"), gbc);

        gbc.gridx = 1;
        topPanel.add(propertiesFilesList, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("User Properties"), gbc);

        gbc.gridx = 1;
        topPanel.add(userPropertiesFilesList, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(jtfUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Password"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        topPanel.add(jpfPassword, gbc);

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(jbtConnectToDB);
        connectionPanel.add(jbtDisconnectFromDB);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Connection Status:"));
        statusPanel.add(jlbConnectionStatus);

        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.add(new JLabel("Enter An SQL Command"), BorderLayout.NORTH);
        sqlPanel.add(new JScrollPane(jtaSqlCommand), BorderLayout.CENTER);

        JPanel sqlButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sqlButtonPanel.add(jbtClearSQLCommand);
        sqlButtonPanel.add(jbtExecuteSQLCommand);
        sqlPanel.add(sqlButtonPanel, BorderLayout.SOUTH);

        JPanel executionPanel = new JPanel(new BorderLayout());
        executionPanel.add(new JLabel("SQL Execution Result"), BorderLayout.NORTH);
        executionPanel.add(new JScrollPane(jtaSqlExecution), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        bottomPanel.add(jbtClearResultWindow, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(connectionPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);
        add(sqlPanel, BorderLayout.CENTER);
        add(executionPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setTitle("Accountant Application - (Project 2 - CNT 4714 - Summer 2024)");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void ConnectToDB() {
        try {
            Properties properties = new Properties();
            String selectedPropertiesFile = String.valueOf(propertiesFilesList.getSelectedItem());
            properties.load(new FileInputStream(selectedPropertiesFile));

            Properties userProperties = new Properties();
            String selectedUserPropertiesFile = String.valueOf(userPropertiesFilesList.getSelectedItem());
            userProperties.load(new FileInputStream(selectedUserPropertiesFile));

            Class.forName(properties.getProperty("MYSQL_DB_DRIVER_CLASS"));
            String dbUrl = properties.getProperty("MYSQL_DB_URL");

            if (connectedToDatabase) {
                connection.close();
                ConnectionStatusUp("No Connection Now", Color.RED);
                connectedToDatabase = false;
                emptyTable();
            }

            String enteredUsername = jtfUsername.getText().trim();
            String enteredPassword = new String(jpfPassword.getPassword());

            String correctUsername = userProperties.getProperty("MYSQL_DB_USERNAME");
            String correctPassword = userProperties.getProperty("MYSQL_DB_PASSWORD");

            if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) {
                connection = DriverManager.getConnection(dbUrl, correctUsername, correctPassword);
                ConnectionStatusUp("Connected to " + dbUrl, Color.GREEN);
                connectedToDatabase = true;
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException | SQLException | ClassNotFoundException e) {
            ConnectionError(e);
        }
    }

    private void DisconnectFromDB() {
        try {
            if (connectedToDatabase) {
                connection.close();
                ConnectionStatusUp("No Connection Now", Color.RED);
                connectedToDatabase = false;
                emptyTable();
            }
        } catch (SQLException e) {
            ConnectionError(e);
        }
    }

    private void SQLCommand() {
        if (!connectedToDatabase) {
            JOptionPane.showMessageDialog(this, "Not Connected to a Database", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sqlCommand = jtaSqlCommand.getText().trim();
        if (sqlCommand.isEmpty()) {
            JOptionPane.showMessageDialog(this, "SQL Command is Empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            tableModel = new ResultSetTableModel(connection, sqlCommand);
            table.setModel(tableModel);
        } catch (SQLException e) {
            SQLError(e);
        }
    }

    private void ResultWindows() {
        jtaSqlExecution.setText("");
        emptyTable();
    }

    private void emptyTable() {
        table.setModel(new DefaultTableModel());
        tableModel = null;
    }

    private void ConnectionStatusUp(String status, Color color) {
        jlbConnectionStatus.setText(status);
        jlbConnectionStatus.setForeground(color);
    }

    private void ConnectionError(Exception e) {
        ConnectionStatusUp("No Connection Now", Color.RED);
        emptyTable();
        e.printStackTrace();
    }

    private void SQLError(Exception e) {
        ConnectionStatusUp("No Connection Now", Color.RED);
        emptyTable();
        JOptionPane.showMessageDialog(null, e.getMessage(), "Database error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new AccountantApp().setVisible(true);
            } catch (ClassNotFoundException | SQLException | IOException e) {
                e.printStackTrace();
            }
        });
    }
}

