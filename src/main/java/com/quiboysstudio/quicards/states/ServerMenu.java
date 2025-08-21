package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.configs.FrameConfig;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
        
        if (running) return;
        running = true;
        
        System.out.println("Showing server menu");
        
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
        
        System.out.println("Showing ServerMenu menu");
        
        System.out.println("Choose Action:");
        System.out.println("1. Host Server");
        System.out.println("2. Join Server");
        System.out.println("3. Exit App");

    }

    private void init() {
        if (running) return;
        
        System.out.println("initializing elements from server menu");
        
        //create panel
        panel = new JPanel();
        panel.setSize(FrameConfig.scale(frame, 1920, 1080));
        panel.setBackground(FrameConfig.BLUE);
        panel.setLayout(new BorderLayout());
        
        //create logo panel
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//main_game.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 360), FrameConfig.scale(frame, 360), Image.SCALE_SMOOTH));
        logoLabel = new JLabel();
        logoLabel.setIcon(gameLogo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.CENTER);
        logoLabel.setPreferredSize(FrameConfig.scale(frame, 360, 360));
        panel.add(logoLabel, BorderLayout.NORTH);
        
        //buttons
    }

    private void exit() {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
    }
    
    //variables
    private boolean running = false;
    
    //objects
    private static JPanel panel;
    private static JLabel logoLabel;
    private static JPanel buttonPanel;
    private static ImageIcon gameLogo;
}