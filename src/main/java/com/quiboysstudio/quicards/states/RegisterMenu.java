package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.account.User;
import java.util.Scanner;

public class RegisterMenu extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showRegisterMenu();
    }
    
    private void showRegisterMenu() {
        //variables
        String username, password;
        long seed;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Showing RegisterMenu");
        
        System.out.println("Enter username: ");
        username = scanner.nextLine().trim();
        System.out.println("Enter password: ");
        password = scanner.nextLine();
        System.out.println("Confirm password: ");
        
        //password confirmation
        if (!password.equals(scanner.nextLine())) {
            System.out.println("Password doesn't match!");
            return;
        }
        
        //generate seed
        seed = User.generateSeed();
        
        //check if username exists on database
        try {
            Server.result = Server.statement.executeQuery(
                    "select username from Users where username = '" + username + "';"
            );
            
            if (!Server.result.next()) {
                //insert user details to database if username is unique
                Server.statement.executeUpdate(
                        "insert into Users (username, password, seed) values " +
                        "('" + username + "','" + password + "','" + seed + "');"
                );
                currentState = mainMenu;
                exit();
            } else {
                System.out.println("Username is already taken!");
            }
        } catch (Exception e) {
            System.out.println("Failed to search username: " + e);
        }
    }
    
    private void init() {
        System.out.println("Initializing elements from RegisterMenu state");
        System.out.println("Entering RegisterMenu state");
    }
    
    private void exit() {
        System.out.println("Removing elements from RegisterMenu");
        System.out.println("Preparing to transition to next state");
    }
}
