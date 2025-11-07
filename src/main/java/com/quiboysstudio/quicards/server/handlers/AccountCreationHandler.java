package com.quiboysstudio.quicards.server.handlers;

import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.Statement;

public class AccountCreationHandler {
    //variables
    private boolean running = false;
    
    //objects
    private final SecureRandom secureRandom = new SecureRandom();
    
    //sql
    private ResultSet result;
    private Statement statement;
    
    public AccountCreationHandler (ResultSet result, Statement statement) {
        this.result = result;
        this.statement = statement;
    }
    
    public void checkActions() {
        createUsers(checkNewRows());
    }
    
    private long generateSeed() {
        return secureRandom.nextLong();
    }
    
    private ResultSet checkNewRows() {
        try {
            result = statement.executeQuery(
            "SELECT * FROM  AccountCreation where Processed = 0;"
            );
        } catch (Exception e) {
            System.out.println("Failed to check new AccountCreation rows: " + e);
        }
        
        return result;
    }
    
    private void createUsers(ResultSet result) {
        //variables
        String username;
        String password;
        long seed;
        int creationID;
        
        //create seed
        seed = generateSeed();
        
        try {
            while(result.next()) {
                //get creationID
                creationID = result.getInt("CreationID");
                
                //get username
                username = result.getString("Username");
                
                //get password
                password = result.getString("Password");
                
                //insert user on Users table
                statement.executeUpdate(
                        String.format(
                        "INSERT INTO Users(Username, Password, Seed)" +
                        "VALUES ('%s', '%s', '%d');",
                        username, password, seed
                        )
                );
                
                //create mysql user
                statement.executeUpdate(
                        String.format(
                        "CREATE USER '%s'@'%%' IDENTIFIED BY '%s';",
                        username, password
                        )
                );
                
                //grant privileges
                statement.executeUpdate(
                        String.format(
                        "GRANT INSERT ON Server.Actions TO '%s'@'%%';",
                        username
                        )
                );
                statement.executeUpdate(
                        String.format(
                        "GRANT SELECT ON Server.Users TO '%s'@'%%';",
                        username
                        )
                );
                
                //update process status on AccountCreation table
                statement.executeUpdate(
                        String.format(
                        "UPDATE AccountCreation set Processed = 1 " +
                        "WHERE CreationID = %d",
                        creationID
                        )
                );
                
                if (username != null) System.out.println("user " + username + " created successfully!");
            }
        } catch (Exception e) {
            System.out.println("Failed to create user: " + e);
        }
    }
}