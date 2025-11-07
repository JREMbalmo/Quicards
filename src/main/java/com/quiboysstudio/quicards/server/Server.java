package com.quiboysstudio.quicards.server;

//imports
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.states.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Server {
    //variables
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
        
        System.out.println("Left server");
    }
    
    public static void setServer(String ip, String port) {
        url = String.format("jdbc:mysql://%s:%s/Server?zeroDateTimeBehavior=CONVERT_TO_NULL", ip, port);
        
        Server.ip = ip;
        Server.port = port;
    }
    
    public static boolean connectServer() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, User.getUsername(), User.getPassword());
            statement = connection.createStatement();
        
            System.out.println("Connected to server!");
            return true;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1045) {
                JOptionPane.showMessageDialog(null, "Incorrent login information");
            }
            else {System.out.println("Failed to validate user login: " + e);}
        } catch(Exception e) {
            System.out.println("Failed to connect to server: " + e);
            State.currentState.exit(State.serverMenu);
        }
        return false;
    }
    
    public static boolean doAction() {
        
        return false;
    }
}