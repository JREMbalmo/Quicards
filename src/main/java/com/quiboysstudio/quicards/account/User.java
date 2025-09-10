package com.quiboysstudio.quicards.account;

import com.quiboysstudio.quicards.server.Server;

public class User {
    //variables
    private static String username, password;
    
    public static void setupUser(String username, String password) {
        User.username = username;
        User.password = password;
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
        Server.result = null;
        Server.statement = null;
        Server.connection = null;
    }
}