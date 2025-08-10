package com.quiboysstudio.quicards;

import java.util.Scanner;

public class JoinServerMenu extends State{
    
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
        
        System.out.println("Showing JoinServerMenu menu");
        
        System.out.print("Enter IP Adress of Ubuntu server hosting the MySQL server: ");
        ip = scanner.nextLine();
        System.out.print("Enter port: ");
        port = scanner.nextLine();
        System.out.print("Enter username of MySQL user: ");
        username = scanner.nextLine();
        System.out.print("Enter password of MySQL user: ");
        password = scanner.nextLine();
            
        connectServer(ip, port, username, password);
    }
    
    private void connectServer(String ip, String port, String username, String password) { 
        System.out.println(String.format(
                "Connecting to MySQL server hosted at %s with %s as port using %s user with %s as password",
                ip, port, username, password));
        
        Server.setDatabase(ip, port, username, password);
        if (Server.DBConnect()) {
            currentState = loginMenu; //only run when successfully connected to server
            exit(); //only run when successful
        }
    }

    private void init() {
        System.out.println("Initializing elements from JoinServerMenu state");
        System.out.println("Entering JoinServerMenu state");
    }

    private void exit() {
        System.out.println("Removing elements from JoinServerMenu");
        System.out.println("Preparing to transition to next state");
    }
}