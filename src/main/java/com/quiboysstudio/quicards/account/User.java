package com.quiboysstudio.quicards.account;

import java.security.SecureRandom;

public class User {
    //variables
    private static String username, password, seed;
    private static int ID = 0;
    
    //objects
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public User(int ID, String username, String password, String seed) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.seed = seed;
    }
    
    public static String getUsername() {
        return username;
    }
    
    public static String getPassword() {
        return password;
    }
    
    public static String getSeed() {
        return seed;
    }
    
    public static int getID() {
        return ID;
    }
    
    public static void logout() {
        username = null;
        password = null;
        seed = null;
        ID = 0;
    }
        
    public static long generateSeed() {
        return secureRandom.nextLong();
    }
}