package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.account.User;
import java.util.Scanner;

public class LoginMenu extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showLoginMenu();
    }
    
    private void showLoginMenu() {
        //variables
        int input;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Showing LoginMenu");
        
        System.out.println("Choose Action:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Back");
            
        input = Integer.parseInt(scanner.nextLine().trim());
        
        switch (input) {
            case 1:
                loginPrompt();
                break;
            case 2:
                currentState = registerMenu;
                exit();
                break;
            case 3:
                currentState = serverMenu;
                exit();
                break;
            default:
                System.out.println("Invalid action!");
        }
    }
    
    private void loginPrompt() {
        //variables
        String username, password;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter username: ");
        username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        password = scanner.nextLine();
        
        //send queries for login
        try {
            //variables
            String serverUsername, serverPassword, seed;
            int ID;
            
            //check if user actually exists
            Server.result = Server.statement.executeQuery(
                "select ID, username, password, seed from Users where username = '" + username +"';"
            );
            
            //check if user exists
            if (Server.result.next()) {
                serverUsername = Server.result.getString("username");
                serverPassword = Server.result.getString("password");
            } else {
                System.out.println("Username doesn't exist!");
                return;
            }
            
            //check if login info is correct
            if (password.equals(serverPassword)) {
                System.out.println("Login successful!");
                ID = Server.result.getInt("ID");
                seed = Server.result.getString("seed");
                User user = new User(ID, serverUsername, serverPassword, seed);
                currentState = mainMenu;
                exit();
            } else {
                System.out.println("Incorrect login information!");
            }
            
        } catch (Exception e) {
            System.out.println("Failed to do login: " + e);
        }
    }
    
    private void init() {
        System.out.println("Initializing elements from LoginMenu state");
        System.out.println("Entering LoginMenu state");
    }
    
    private void exit() {
        System.out.println("Removing elements from LoginMenu");
        System.out.println("Preparing to transition to next state");
    }
}
