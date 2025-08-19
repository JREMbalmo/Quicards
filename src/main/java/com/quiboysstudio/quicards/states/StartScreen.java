package com.quiboysstudio.quicards.states;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartScreen extends State {
    
    //variables
    
    
    //objects
    private static JLabel logoLabel;
    private static ImageIcon studioLogo, gameLogo;
    private static JPanel panel;
    
    
    @Override
    public void enter() {
        init();
    }

    @Override
    public void update() {
        showStartScreen();
    }
    
    private void showStartScreen() {
        System.out.println("Showing start screen");
        
        new Thread(() -> {
            try {
                State.frame.repaint();
                System.out.println("running thread");
                State.frame.add(panel, BorderLayout.CENTER);
                Thread.sleep(1000);
                logoLabel.setIcon(studioLogo);
                State.frame.repaint();
                Thread.sleep(1000);
                logoLabel.setIcon(null);
                Thread.sleep(1000);
                logoLabel.setIcon(gameLogo);
                Thread.sleep(1000);
                logoLabel.setIcon(null);
                State.frame.repaint();
                Thread.sleep(1000);
                
                currentState = serverMenu;
                exit();
            } catch (Exception e) {
                
            }
        }).start();
        
//        if (currentState == startScreen) {
//            currentState = serverMenu;
//            exit();
//        }
    }
    
    private void init() {
        System.out.println("Initializing JFrame");
        System.out.println("Initializing elements from StartScreen state");
        
        //init
        logoLabel = new JLabel();
        studioLogo = new ImageIcon(new ImageIcon("resources//logos//black_background_studio.png").getImage().getScaledInstance(720, 720, Image.SCALE_SMOOTH));
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//main_game.png").getImage().getScaledInstance(720, 720, Image.SCALE_SMOOTH));
        
        //panel setup
        panel = new JPanel();
        panel.setBackground(new Color (0x000000));
        panel.setPreferredSize(new Dimension(1,1));
        panel.setLayout(new BorderLayout());
        panel.add(logoLabel, BorderLayout.CENTER);
        
        //label config
        logoLabel.setPreferredSize(new Dimension(1,1));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.CENTER);
        System.out.println("config done");
        
        System.out.println("Entering StartScreen State");
    }
    
    private void exit() {
        System.out.println("Removing elements from StartScreen state");
        System.out.println("Preparing to transition to next state");
    }
}
