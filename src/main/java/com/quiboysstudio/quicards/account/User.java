package com.quiboysstudio.quicards.account;

import com.quiboysstudio.quicards.server.Server;

public class User {
    //variables
    private static String username, password;
    private static int userID = -1; // NEW: Added UserID
    
    public static void setupUser(String username, String password) {
        User.username = username;
        User.password = password;
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