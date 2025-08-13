package com.quiboysstudio.quicards;

public class ActiveUser {
    private String username, password;
    
    ActiveUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
}
