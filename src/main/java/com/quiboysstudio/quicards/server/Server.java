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
    
    //objects
    public static Connection connection;
    public static Statement statement;
    public static ResultSet result;
    
    public static void setDatabase(String ip, String port, String username, String password) {
        database = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
        Server.username = username;
        Server.password = password;
    }
    
    public static boolean DBConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(database, username, password);
            statement = connection.createStatement();
            System.out.println("Connected to server!");
            return true;
        } catch(Exception e) {
            System.out.println("Failed to connect to server: " + e);
            return false;
        }
    }
}
