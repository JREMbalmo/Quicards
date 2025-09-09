package com.quiboysstudio.quicards.server.handlers;

import java.security.SecureRandom;

public class AccountCreationHandler {
    
    //objects
    private static final SecureRandom secureRandom = new SecureRandom();
    
    public static long generateSeed() {
        return secureRandom.nextLong();
    }
}
