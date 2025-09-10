package com.quiboysstudio.quicards.server;

//imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountCreationServer {
    //variables
    private static final String username = "QuiCardsCreator1";
    private static final String password = "QuiC4rds!";
    private static String ip;
    private static String port;
    private static String url;
    
    //objects
    public static Connection connection;
    public static Statement statement;
    public static ResultSet result;
    
    public static void leaveServer() {
        
        ip = null;
        port = null;
        url = null;
        result = null;
        statement = null;
        connection = null;
        
        Server.leaveServer();
        
        System.out.println("Left server as account creator");
    }
    
    public static void setServer(String ip, String port) {
        url = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
        
        AccountCreationServer.ip = ip;
        AccountCreationServer.port = port;
        Server.setServer(ip, port);
    }
    
    public static boolean checkServer() {
        try {
            result = statement.executeQuery(
                    "select SCHEMA_NAME from INFORMATION_SCHEMATA where SCHEMA_NAME = 'Server';");
            return result.next();
        } catch (Exception e) {
            System.out.println("Failed to check creator server: " + e);
        }
        return false; //default
    }
    
    public static boolean isHosted() {
        String query;
        String host;
        
        try {
            //get current host of server
            query = "SELECT CurrentHost from ServerDetails LIMIT 1;";
            
            result = statement.executeQuery(query);
            
            if (!result.next()) {
                return false;
            }
            
            //get current host details
            host = result.getString("CurrentHost");
            String[] details = host.split("@");
            
            //check if current host is currently connected to server
            query = String.format(
                "SELECT user, host FROM information_schema.PROCESSLIST " +
                "WHERE user = '%s' AND host LIKE '%s%%';",
                details[0].trim(), details[1].trim()
            );

            result = statement.executeQuery(query);
            return result.next();
        } catch (Exception e) {
            System.out.println("Failed to check if host is online: " + e);
        }
        return false; //default
    }
    
    public static boolean connectServer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            statement = connection.createStatement();
            
            //check if host is currently online
            if (!isHosted()) {
                connection.close();
                leaveServer();
                return false;
            }
        
            System.out.println("Connected to server!");
            return true;
        } catch(Exception e) {
            System.out.println("Failed to connect to creator server: " + e);
            return false;
        }
    }
    
    public static boolean checkStatus() {
        return (isHosted() && checkServer());
    }
    
    public static int createUser(String username, String password) {
        //variables
        String query;
        
        try {
            query = String.format("INSERT INTO AccountCreation(Username, Password) VALUES ('%s', '%s');",
                    username, password);
            statement.executeUpdate(query);
            return 1;
        } catch (SQLException e) {
            System.out.println("User already exists: " + e);
            if (e.getErrorCode() == 1062) return e.getErrorCode();
        } catch (Exception e) {
            System.out.println("Failed to insert user details: " + e);
        }
        return 0;
    }
}