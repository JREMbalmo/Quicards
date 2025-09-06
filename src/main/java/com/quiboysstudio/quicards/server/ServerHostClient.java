package com.quiboysstudio.quicards.server;

import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

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
    private static JScrollPane logsPanel;
    private static JTable logsTable;
    private static JTable usersTable;
    private static JButton printButton;
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
        
        //ensure nobody is currently hosting the server
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
            hosting = true;
            System.out.println("Connected to server!");
            
            if (checkHost()) {
                leaveServer();
                return false;
            };
            
            initHostedServer();
            runHostedServer();
            
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
        
    }
    
    private static void runHostedServer() {
        serverHostClientFrame = new JFrame("Host Server");
        serverHostClientFrame.setSize(1080, 720);
        serverHostClientFrame.setResizable(false);
        serverHostClientFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        serverHostClientFrame.setLayout(new BorderLayout());
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
                        "Action VARCHAR(128) NOT NULL," +
                        "Status TINYINT(1) NOT NULL," +
                        "FOREIGN KEY (User) REFERENCES Users(Username)" +
                        ") AUTO_INCREMENT = 1;"
                );
                
            }
        } catch (Exception e) {
            System.out.println("Failed to check server: " + e);
        }
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
                "WHERE user = '%s' AND host LIKE '%s%%';",
                user, ip
            );

            result = statement.executeQuery(query);
            return result.next();

        } catch (Exception e) {
            System.out.println("Failed to check host: " + e);
        }

        return false; //default
    }
}
