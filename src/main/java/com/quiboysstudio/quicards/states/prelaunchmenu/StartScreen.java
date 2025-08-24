package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class StartScreen extends State {
    
    //variables
    private int phase = 0;
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLabel logoLabel;
    private JPanel panel;
    private ImageIcon studioLogo, gameLogo;
    private Timer splashScreen;
    
    @Override
    public void enter() {
        init();
    }

    @Override
    public void update() {
        showStartScreen();
    }
    
    private void showStartScreen() {
        
        //only run once when entering state
        if (running) return;
        running = true;
        
        System.out.println("Showing start screen");
        splashScreen.start();
    }
    
    private void init() {
        
        //stops initializing again if done once
        if (initialized) return;
        
        System.out.println("Initializing JFrame");
        System.out.println("Initializing elements from StartScreen state");
        
        //init
        //logo setup
        logoLabel = new JLabel();
        studioLogo = new ImageIcon(new ImageIcon("resources//logos//studio_logo_white_text.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 720), FrameConfig.scale(frame, 720), Image.SCALE_SMOOTH));
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//game_logo_orange_notext.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 500), FrameConfig.scale(frame, 500), Image.SCALE_SMOOTH));
        
        //panel setup
        panel = new JPanel();
        panel.setBackground(FrameConfig.BLACK);
        panel.setSize(FrameConfig.scale(frame, 1920, 1080));
        panel.setLayout(new BorderLayout());
        panel.add(logoLabel, BorderLayout.CENTER);
        
        //label config
        logoLabel.setPreferredSize(FrameConfig.scale(frame, 1, 1));
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.CENTER);
        
        //timer setup
        splashScreen = new Timer(0, e -> {
            System.out.println("running timer phase: " + phase);
            switch(phase) {
                //add panel (black screen) to frame for 2s
                case 0:
                    frame.add(panel, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                    splashScreen.setDelay(3000);
                    phase++;
                    break;
                //add studio logo to panel for 3s
                case 1:
                    logoLabel.setIcon(studioLogo);
                    frame.revalidate();
                    frame.repaint();
                    splashScreen.setDelay(2000);
                    phase++;
                    break;
                //black screen for 2s
                case 2:
                    logoLabel.setIcon(null);
                    frame.revalidate();
                    frame.repaint();
                    splashScreen.setDelay(3000);
                    phase++;
                    break;
                //add game logo to panel for 3s
                case 3:
                    logoLabel.setIcon(gameLogo);
                    frame.revalidate();
                    frame.repaint();
                    splashScreen.setDelay(2000);
                    phase++;
                    break;
                //black screen for 2s
                case 4:
                    logoLabel.setIcon(null);
                    frame.revalidate();
                    frame.repaint();
                    splashScreen.setDelay(2000);
                    phase++;
                    break;
                //move to servermenu state
                case 5:
                    splashScreen.stop();
                    currentState = serverMenu;
                    exit();
            }
        });
        
        System.out.println("Entering StartScreen State");
        
        initialized = true;
    }
    
    @Override
    public void exit() {
        System.out.println("Removing elements from StartScreen state");
        System.out.println("Preparing to transition to next state");
        
        // clear everything from the frame before going to next state
        running = false;
        frame.getContentPane().removeAll();
        panel.removeAll();
        logoLabel = null;
        panel = null;
        studioLogo = null;
        gameLogo = null;
        splashScreen = null;
        
        frame.revalidate();
        frame.repaint();
    }
}