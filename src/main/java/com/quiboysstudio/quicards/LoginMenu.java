package com.quiboysstudio.quicards;

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
        String username, password;

        //objects
        ActiveUser activeUser;
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Showing LoginMenu");
        
        System.out.println("Enter username: ");
        username = scanner.nextLine();
        System.out.println("Enter password: ");
        password = scanner.nextLine();
        
        activeUser = new ActiveUser(username, password);
        
        currentState = mainMenu;
        exit();
    }
    
    private void exit() {
        System.out.println("Removing elements from LoginMenu");
        System.out.println("Preparing to transition to next state");
    }

    private void init() {
        System.out.println("Initializing elements from LoginMenu state");
        System.out.println("Entering LoginMenu state");
    }

}
