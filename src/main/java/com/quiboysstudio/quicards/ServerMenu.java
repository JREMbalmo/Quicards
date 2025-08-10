package com.quiboysstudio.quicards;

import java.util.Scanner;

public class ServerMenu extends State{
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showMenu();
    }
    
    private void showMenu() {
        //variables
        String input;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Showing ServerMenu menu");
        
        while (true) {
            System.out.println("Choose Action:");
            System.out.println("1. Host Server");
            System.out.println("2. Join Server");
            System.out.println("3. Exit App");
            input = scanner.nextLine();
            
            switch (input) {
                case "1":
                    currentState = hostServerMenu;
                    exit();
                    return;
                case "2":
                    currentState = joinServerMenu;
                    exit();
                    return;
                case "3":
                    currentState = exitState;
                    exit();
                    return;
                default:
                    System.out.println("Invalid input, please try again.");
            }
        }
    }

    private void init() {
        System.out.println("Initializing elements from ServerMenu state");
        System.out.println("Entering ServerMenu state");
    }

    private void exit() {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
    }
}