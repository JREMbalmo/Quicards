package com.quiboysstudio.quicards.states;

//imports
import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.configs.ButtonConfig;
import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ServerMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel header;
    private JPanel buttonPanel;
    private JLabel logoLabel;
    private ImageIcon gameLogo;
    
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
        
        frame.add(header, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();

    }

    private void init() {
        if (initialized) return;
        initialized = true;
        
        System.out.println("initializing elements from server menu");
        
        //change frame background
        frame.getContentPane().setBackground(FrameConfig.BLUE);
        
        //create header panel
        header = new JPanel();
        header.setPreferredSize(FrameConfig.scale(frame,1920,203));
        header.setBackground(FrameConfig.BLUE);
        header.setLayout(new BorderLayout());
        
        //create logo label
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//game_logo_orange_text.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 622), FrameConfig.scale(frame, 150), Image.SCALE_SMOOTH));
        logoLabel = new JLabel();
        logoLabel.setIcon(gameLogo);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.BOTTOM);
        logoLabel.setPreferredSize(FrameConfig.scale(frame, 622, 202));
        header.add(logoLabel, BorderLayout.CENTER);
        frame.add(header, BorderLayout.NORTH);
        
        //create button panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setPreferredSize(FrameConfig.scale(frame, 557, 520));
        buttonPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 150),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        //buttons
        buttonPanel.add(ButtonConfig.createStateChangerButton("Host Server", FrameConfig.SATOSHI_BOLD, 557, FrameConfig.ORANGE, hostServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(ButtonConfig.createStateChangerButton("Join Server", FrameConfig.SATOSHI_BOLD, 557, FrameConfig.ORANGE, joinServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        buttonPanel.add(ButtonConfig.createStateChangerButton("Exit", FrameConfig.SATOSHI_BOLD, 557, FrameConfig.BLACK, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        frame.add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
        running = false;
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(buttonPanel));
    }
}