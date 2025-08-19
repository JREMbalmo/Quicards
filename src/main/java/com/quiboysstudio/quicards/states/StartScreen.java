package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.configs.FrameConfig;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartScreen extends State {
    
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
        
        try {
            //black screen for 2s
            frame.add(panel, BorderLayout.CENTER);
            frame.repaint();
            Thread.sleep(2000);
            
            //show studio logo for 3s
            logoLabel.setIcon(studioLogo);
            frame.revalidate();
            frame.repaint();
            Thread.sleep(3000);
                
            //remove studio logo for 2s
            logoLabel.setIcon(null);
            frame.revalidate();
            frame.repaint();
            Thread.sleep(2000);
                
            //show game logo for 3s
            logoLabel.setIcon(gameLogo);
            frame.revalidate();
            frame.repaint();
            Thread.sleep(3000);
                
            //remove game logo for 2s
            logoLabel.setIcon(null);
            frame.revalidate();
            frame.repaint();
            Thread.sleep(2000);
                
            //go to server menu state
            currentState = serverMenu;
            exit();
        } catch (Exception e) {
                System.out.println("Failed to load splash screen: " + e);
        }
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
        panel.setBackground(FrameConfig.BLACK);
        panel.setSize(new Dimension(1920,1080));
        panel.setLayout(new BorderLayout());
        panel.add(logoLabel, BorderLayout.CENTER);
        
        //label config
        logoLabel.setPreferredSize(new Dimension(1,1));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.CENTER);
        
        System.out.println("Entering StartScreen State");
    }
    
    private void exit() {
        System.out.println("Removing elements from StartScreen state");
        System.out.println("Preparing to transition to next state");
        // clear everything from the frame before going to next state
        frame.getContentPane().removeAll();
        frame.revalidate();
        frame.repaint();
    }
    
    //objects
    private static JLabel logoLabel;
    private static JPanel panel;
    private static ImageIcon studioLogo, gameLogo;
}
