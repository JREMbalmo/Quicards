package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.server.Server;
import java.awt.BorderLayout;
import java.util.Scanner;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class JoinServerMenu extends State{
    
    //variables
    private boolean running = false;
    
    //objects
    private static JPanel buttonPanel;
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showMenu();
    }

    private void showMenu() {
        
        if (running) return;
        running = true;
        
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
        
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
        if (running) return;
        
        System.out.println("Initializing elements from JoinServerMenu state");
        System.out.println("Entering JoinServerMenu state");
        
        //create button panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setPreferredSize(FrameConfig.scale(frame, 557, 520));
        buttonPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 150),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        //buttons
        buttonPanel.add(FrameConfig.createStateChangerButton("adfadfadfa", 557, FrameConfig.ORANGE, hostServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(FrameConfig.createStateChangerButton("Jadfadfa", 557, FrameConfig.ORANGE, joinServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(FrameConfig.createStateChangerButton("adfadf", 557, FrameConfig.ORANGE, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        frame.add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from JoinServerMenu");
        System.out.println("Preparing to transition to next state");
    }
}