package com.quiboysstudio.quicards.states;

import java.util.Scanner;

//TO DO:
//ADD CANCEL FEATURE (go back to server menu)

public class HostServerMenu extends State{
    
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
        String ip, port, username, password;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Showing HostServerMenu menu");
        
        System.out.print("Enter IP Adress of Ubuntu server hosting the MySQL server: ");
        ip = scanner.nextLine();
        System.out.print("Enter port: ");
        port = scanner.nextLine();
        System.out.print("Enter username of MySQL user: ");
        username = scanner.nextLine();
        System.out.print("Enter password of MySQL user: ");
        password = scanner.nextLine();
            
        connectServer(ip, port, username, password); //only proceed if successfully connected otherwise return
            
        System.out.println("Choose Action:");
        System.out.println("1. Link to a master server");
        System.out.println("2. Host independent server");
        System.out.println("3. Go Back");
            
        switch(scanner.nextLine()) {
            case "1":
                linkServer();
                exit();
                break;
            case "2":
                setupServer(ip, port, username, password);
                exit();
                break;
            case "3":
                break;
            default:
                System.out.println("Invalid input, please try again.");
        }
    }

    private void connectServer(String ip, String port, String username, String password) {
        System.out.println(
                String.format(
                    "Connecting to MySQL server hosted at %s with %s as port using %s user with %s as password",
                    ip, port, username, password)
        );
    }

    private void linkServer() {
        //variables
        String ip, port, username, password;
        
        //objects
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter IP Adress of Ubuntu server hosting the MySQL server: ");
        ip = scanner.nextLine();
        System.out.print("Enter port: ");
        port = scanner.nextLine();
        System.out.print("Enter username of MySQL user: ");
        username = scanner.nextLine();
        System.out.print("Enter password of MySQL user: ");
        password = scanner.nextLine();
            
        connectServer(ip, port, username, password);
        currentState = loginMenu; //temp, only run when client successfully connects to server
    }
    
    private void setupServer(String ip, String port, String username, String password) {
        if (checkServer(ip, port, username, password)) {
            
            System.out.println(
                    String.format(
                        "Setting up server hosted at %s with %s as port using %s user with %s as password",
                        ip, port, username, password)
            );
            currentState = registerMenu; //temp
        } else {
            
            System.out.println("Someone else owns that server!");
        }
    }
    
    //check if mysql server is already master hosted by someone else
    private boolean checkServer(String ip, String port, String username, String password) {
        //variables
        boolean vacant = false; //temp
        
        System.out.println("Checking if server is valid not hosted by anyone else");
        
        return vacant;
    }
    
    private void init() {
        System.out.println("Initializing elements from HostServerMenu state");
        System.out.println("Entering HostServerMenu state");
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from HostServerMenuState");
        System.out.println("Preparing to transition to next state");
    }
}