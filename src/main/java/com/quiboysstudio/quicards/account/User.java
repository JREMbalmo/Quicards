package com.quiboysstudio.quicards.account;

import java.security.SecureRandom;

public class User {
    //variables
    private static int ID = 0;
    private static String username, password;
    private static long seed;
    
    //objects
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public User(int ID, String username, String password, long seed) {
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
    
    public static long getSeed() {
        return seed;
    }
    
    public static int getID() {
        return ID;
    }
    
    public static void logout() {
        username = null;
        password = null;
        seed = 0;
        ID = 0;
    }
        
    public static long generateSeed() {
        return secureRandom.nextLong();
    }
}