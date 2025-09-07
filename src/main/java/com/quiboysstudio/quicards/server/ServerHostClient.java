package com.quiboysstudio.quicards.server;

import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ServerHostClient {
    //variables
    private static String username;
    private static String password;
    private static String url;
    private static String ip;
    private static String port;
    private static boolean hosting = false;
    
    //objects
    private static Connection connection;
    private static Statement statement;
    private static ResultSet result;
    
    private static JFrame serverHostClientFrame;
    private static JScrollPane logsPane;
    private static JScrollPane usersPane;
    private static JPanel tablesPanel;
    private static JPanel commandPanel;
    private static JPanel usersPanel;
    private static JPanel exportPanel;
    private static JPanel logsPanel;
    private static JTable logsTable;
    private static JTable usersTable;
    private static JButton exportLogsButton;
    private static JButton runCommandButton;
    private static JTextField commandField;
    
    public static void setServer(String ip, String port, String username, String password) {
        url = String.format("jdbc:mysql://%s:%s/?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
            
        ServerHostClient.ip = ip;
        ServerHostClient.port = port;
        ServerHostClient.username = username;
        ServerHostClient.password = password;
    }
    
    public static boolean connectServer() {
        try {
            //connect to server
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            
            //ensure Server database setup
            checkServer();
            connection.close();
            
            //connect to server using Server database
            url = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            
            //check if someone is already hosting the server
            if (checkHost()) {
                leaveServer();
                return false;
            };
            
            initHostedServer();
            runHostedServer();
            
            hosting = true;
            System.out.println("Connected to server!");
            
            return true;
        } catch(Exception e) {
            System.out.println("Failed to connect to server: " + e);
            return false;
        }
    }
        
    public static void leaveServer() {
        //variables
        String query;
        
        System.out.println("Leaving Server");
        ip = null;
        port = null;
        url = null;
        username = null;
        password = null;
        hosting = false;
        
        //reset server host
        query = "update ServerDetails set CurrentHost = null;";
        
        try {
            statement.executeUpdate(query);
        } catch (Exception e) {
            System.out.println("Failed to reset server host: " + e);
        }
    }
    
    private static void initHostedServer() {
        //frame
        serverHostClientFrame = new JFrame("Admin Menu (" + ip + ")");
        serverHostClientFrame.setSize(1280, 720);
        serverHostClientFrame.setResizable(false);
        serverHostClientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        serverHostClientFrame.setLayout(new BorderLayout());
        serverHostClientFrame.setLocationRelativeTo(null);
        serverHostClientFrame.setIconImage(new ImageIcon("resources//logos//game_logo_appicon.png").getImage());
        
        //panels
        
        //tables panel
        tablesPanel = new JPanel();
        tablesPanel.setPreferredSize(new Dimension(1280,500));
        
        //command panel
        commandPanel = new JPanel();
        commandPanel.setPreferredSize(new Dimension(1280, 150));
        
        //logs panel
        logsPanel = new JPanel();
        logsPanel.setPreferredSize(new Dimension (1000, 480));
        
        //users panel
        usersPanel = new JPanel();
        usersPanel.setPreferredSize(new Dimension (200, 480));
        
        //export panel
        exportPanel = new JPanel();
        exportPanel.setPreferredSize(new Dimension(250,150));
        exportPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        
        //setup tables
        
        //logs table
        DefaultTableModel model = new DefaultTableModel(new Object[][] {},
                new String[] {"Action ID", "User", "Action", "Status"});
        logsTable = new JTable(model);
        logsTable.setPreferredSize(new Dimension(1000, 480));
        logsPane = new JScrollPane(logsTable);
        logsPane.setPreferredSize(new Dimension(1000, 480));
        
        //users table
        model = new DefaultTableModel(new Object[][] {}, new String[] {"Active Users"});
        usersTable = new JTable(model);
        usersTable.setPreferredSize(new Dimension(200, 480));
        usersPane = new JScrollPane(usersTable);
        usersPane.setPreferredSize(new Dimension(200, 480));
        
        //command text field
        commandField = new JTextField();
        commandField.setBackground(Color.white);
        commandField.setPreferredSize(new Dimension (1000, 18));
        
        //buttons
        
        //run command button
        runCommandButton = new JButton("Run");
        runCommandButton.setPreferredSize(new Dimension(100, 18));
        runCommandButton.addActionListener(e -> {
            runCommand(String.valueOf(commandField.getText()));
            commandField.setText(null);
        });
        
        //export logs button
        exportLogsButton = new JButton("Export Logs");
        exportLogsButton.setPreferredSize(new Dimension(200, 18));
        exportLogsButton.addActionListener(e -> {
            exportLogs();
        });
        
        //add components
        exportPanel.add(exportLogsButton);
        
        commandPanel.add(commandField);
        commandPanel.add(runCommandButton);
        commandPanel.add(exportPanel);
        
        logsPanel.add(logsPane);
        usersPanel.add(usersPane);
        
        tablesPanel.add(logsPanel);
        tablesPanel.add(usersPanel);
        
        serverHostClientFrame.add(tablesPanel, BorderLayout.NORTH);
        serverHostClientFrame.add(commandPanel, BorderLayout.SOUTH);
    }
    
    private static void runHostedServer() {
        serverHostClientFrame.setVisible(true);
    }
    
    private static void exitHostedServer() {
        leaveServer();
    }
    
    private static void checkServer() {
        try {
            //check if Server database exists on server
            result = statement.executeQuery(
                    "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME = 'Server'");
            
            if (!result.next()) {
                
                //create Server database if it doesn't exist on server
                statement.executeUpdate(
                        "CREATE DATABASE Server;"
                );
                
                statement.executeUpdate(
                        "USE Server;"
                );
                
                //create ServerDetails table
                statement.executeUpdate(
                        "CREATE TABLE ServerDetails (" +
                        "ID TINYINT(1) PRIMARY KEY AUTO_INCREMENT," +
                        "ServerName VARCHAR(128)," +
                        "CurrentHost VARCHAR(128)," +
                        "CONSTRAINT ck_single_row CHECK (ID = 1)" +
                        ") AUTO_INCREMENT = 1;"
                );
                
                //create Users table
                statement.executeUpdate(
                        "CREATE TABLE Users (" +
                        "ID INT PRIMARY KEY AUTO_INCREMENT," +
                        "Username VARCHAR(32) UNIQUE NOT NULL," +
                        "Password TEXT NOT NULL," +
                        "Seed BIGINT NOT NULL" +
                        ") AUTO_INCREMENT = 100000;"
                );
                
                //create Actions table
                statement.executeUpdate(
                        "CREATE TABLE Actions (" +
                        "ActionsID INT PRIMARY KEY AUTO_INCREMENT," +
                        "User VARCHAR(32) NOT NULL," +
                        "Password TEXT NOT NULL," +
                        "Action VARCHAR(128) NOT NULL," +
                        "Status TINYINT(1) NOT NULL," +
                        "FOREIGN KEY (User) REFERENCES Users(Username)," +
                        "FOREIGN KEY (Password) REFERENCES Users(Password)" +
                        ") AUTO_INCREMENT = 1;"
                );
            }
        } catch (Exception e) {
            System.out.println("Failed to check server: " + e);
        }
    }
    
    private static void runCommand(String command) {
        JOptionPane.showMessageDialog(null, command);
    }
    
    private static void exportLogs() {
        JOptionPane.showMessageDialog(null, "Exporting Logs");
    }
    
    private static boolean checkHost() {
        String query;
        String user;
        String ip;

        try {
            //check if there is already a stored host
            result = statement.executeQuery("SELECT CurrentHost FROM ServerDetails LIMIT 1");

            if (!result.next() || result.getString("CurrentHost") == null) {
                //use current user as host if no current host
                query = "SELECT CURRENT_USER;";
                result = statement.executeQuery(query);

                if (result.next()) {
                    String currentUser = result.getString("CURRENT_USER");

                    if (!result.isBeforeFirst()) {
                        query = String.format(
                            "INSERT INTO ServerDetails(CurrentHost) VALUES ('%s')",
                            currentUser
                        );
                    } else {
                        query = String.format(
                            "UPDATE ServerDetails SET CurrentHost = '%s'",
                            currentUser
                        );
                    }

                    statement.executeUpdate(query);
                }

                return false;
            }

            String hostEntry = result.getString("CurrentHost");

            //get CURRENT_USER
            query = "SELECT CURRENT_USER;";
            result = statement.executeQuery(query);

            if (result.next()) {
                String currentUser = result.getString("CURRENT_USER");

                if (hostEntry.equalsIgnoreCase(currentUser)) {
                    
                    query = String.format(
                        "UPDATE ServerDetails SET CurrentHost = '%s'",
                        currentUser
                    );
                    statement.executeUpdate(query);
                    return false;
                }
            }

            //get user and ip
            String[] parts = hostEntry.split("@");
            user = parts[0].trim();
            ip = parts[1].trim();

            //check if current host is currently connected
            query = String.format(
                "SELECT user, host FROM information_schema.PROCESSLIST " +
                "WHERE user = '%s';",
                user
            );

            result = statement.executeQuery(query);
            return result.next();

        } catch (Exception e) {
            System.out.println("Failed to check host: " + e);
        }

        return false; //default
    }
}
