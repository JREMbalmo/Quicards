package com.quiboysstudio.quicards.server;

//imports
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Server {
    //variables
    private static String database;
    private static String username;
    private static String password;
    public static final boolean JOIN = true;
    public static final boolean HOST = false;
    
    //objects
    public static Connection connection;
    public static Statement statement;
    public static ResultSet result;
    
    public static void leaveServer() {
        database = null;
        username = null;
        password = null;
    }
    
    public static void setServer(String ip, String port, String username, String password, boolean type) {
        if (type) {
            database = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
        } else {
            database = String.format("jdbc:mysql://%s:%s/?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
        }
        Server.username = username;
        Server.password = password;
    }
    
    public static boolean checkServer() {
        try {
            result = statement.executeQuery(
                    "select SCHEMA_NAME from INFORMATION_SCHEMATA where SCHEMA_NAME = 'Server';");
            return result.next();
        } catch (Exception e) {
            System.out.println("Failed to check server: " + e);
        }
        return false; //default
    }
    
    public static boolean isHosted() {
        try {
            
        } catch (Exception e) {
            
        }
        
        return false; //default
    }
    
//    public static boolean connectServer() {
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(database, username, password);
//            statement = connection.createStatement();
//            System.out.println("Connected to server!");
//            return true;
//        } catch(Exception e) {
//            System.out.println("Failed to connect to server: " + e);
//            return false;
//        }
//    }
    
        public static boolean connectServer(){
        String db = "Server";
        String uname = "admin";
        String pswd = "]#KCzK9[MeePV8<6YN~o2YOj48dT";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://paydaybank.cluster-chqoc26c4kyy.ap-southeast-1.rds.amazonaws.com:3306/" + db + "?serverTimezone=UTC";
            connection = DriverManager.getConnection(url, uname, pswd);
            statement = connection.createStatement();
            System.out.println("Connected to server!");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to connect to server: " + e);
            return false;
        }
    }
}
