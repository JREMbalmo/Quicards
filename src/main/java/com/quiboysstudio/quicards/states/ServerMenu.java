package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.configs.FrameConfig;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ServerMenu extends State{
    
    //variables
    private boolean running = false;
    
    //objects
    private static JPanel panel;
    private static JPanel buttonPanel;
    private static JLabel logoLabel;
    private static ImageIcon gameLogo;
    
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

    }

    private void init() {
        if (running) return;
        
        System.out.println("initializing elements from server menu");
        
        //create panel
        panel = new JPanel();
        panel.setPreferredSize(FrameConfig.scale(frame, 1920, 1080));
        panel.setBackground(FrameConfig.BLUE);
        panel.setLayout(new BorderLayout());
        
        //create logo panel
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//game_logo_orange_text.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 622), FrameConfig.scale(frame, 150), Image.SCALE_SMOOTH));
        logoLabel = new JLabel();
        logoLabel.setIcon(gameLogo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.BOTTOM);
        logoLabel.setPreferredSize(FrameConfig.scale(frame, 622, 202));
        panel.add(logoLabel, BorderLayout.NORTH);
        
        //create button panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setPreferredSize(FrameConfig.scale(frame, 557, 520));
        buttonPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 220),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        //buttons
        buttonPanel.add(FrameConfig.createStateChangerButton("Host Server", 557, FrameConfig.ORANGE, hostServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(FrameConfig.createStateChangerButton("Join Server", 557, FrameConfig.ORANGE, joinServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(FrameConfig.createStateChangerButton("Exit", 557, FrameConfig.ORANGE, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        panel.add(buttonPanel);
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
        running = false;
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }
}