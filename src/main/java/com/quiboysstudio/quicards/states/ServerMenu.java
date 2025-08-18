package com.quiboysstudio.quicards.states;

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
        
        System.out.println("Choose Action:");
        System.out.println("1. Host Server");
        System.out.println("2. Join Server");
        System.out.println("3. Exit App");
        input = scanner.nextLine();
            
        switch (input) {
            case "1":
                currentState = hostServerMenu;
                exit();
                break;
            case "2":
                currentState = joinServerMenu;
                exit();
                break;
            case "3":
                currentState = exitState;
                exit();
                break;
            default:
                System.out.println("Invalid input, please try again.");
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