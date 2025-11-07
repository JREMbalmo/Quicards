package com.quiboysstudio.quicards.account;

import com.quiboysstudio.quicards.server.Server;

public class User {
    //variables
    private static String username, password;
    private static int userID = -1; // NEW: Added UserID
    private static int money = 0;
    
    public static void updateMoney() {
        try {
            Server.result = Server.statement.executeQuery("SELECT Money from Users where UserID = " + User.getUserID());
            if (Server.result.next()) {
                money = Server.result.getInt("Money");
            }
        }   catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void setupUser(String username, String password) {
        User.username = username;
        User.password = password;
    }
    
    public static void setupID() {
        try {
            Server.result = Server.statement.executeQuery("SELECT UserID from Users where Username = '" + username + "';");
            
            if (Server.result.next()) {
                userID = Server.result.getInt("UserID");
                System.out.println("updated user id");
            }
            
        } catch (Exception e) {
            System.out.println("failed to setup user id: " + e);
        }
    }
    
    // NEW: Setter for UserID (to be called on login)
    public static void setUserID(int id) {
        User.userID = id;
    }
    
    // NEW: Getter for UserID
    public static int getUserID() {
        return userID;
    }
    
    public static String getUsername() {
        return username;
    }
    
    public static String getPassword() {
        return password;
    }
    
    public static int getMoney() {
        return money;
    }
    
    public static void setPassword(String password) {
        User.password = password;
    }
    
    public static boolean isActive() {
        return (username != null || password != null);
    }
    
    public static void logout() {
        try {
        Server.connection.close();
        } catch (Exception e) {
            System.out.println("Failed to close server connection: " + e);
        }
        
        username = null;
        password = null;
        userID = -1; // Reset UserID on logout
        Server.result = null;
        Server.statement = null;
        Server.connection = null;
    }
}