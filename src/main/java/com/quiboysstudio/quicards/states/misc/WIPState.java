package com.quiboysstudio.quicards.states.misc;

import com.quiboysstudio.quicards.configs.*;
import com.quiboysstudio.quicards.states.State;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class WIPState extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel panel;
    private JLabel label;
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showWipState();
    }

    private void showWipState() {
        
        if (running) return;
        running = true;
        
        System.out.println("Showing WIP menu");
        
        frame.add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        
        if (initialized) return;
        
        System.out.println("Initializing elements from WIP state");
        
        //panel
        panel = new JPanel();
        panel.setPreferredSize(FrameConfig.scale(frame, 500, 500));
        panel.setBackground(FrameConfig.BLUE);
        panel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 30),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        
        //imageicon
        ImageIcon icon = new ImageIcon(new ImageIcon("resources//misc//wip.png").getImage().
                getScaledInstance(FrameConfig.scale(frame, 500), FrameConfig.scale(frame, 500), Image.SCALE_SMOOTH));
        
        //label
        label = new JLabel();
        label.setBackground(FrameConfig.BLUE);
        label.setIcon(icon);
        
        //add components
        panel.add(label);
        panel.add(ButtonConfig.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 557, FrameConfig.ORANGE, mainMenu));
        
        System.out.println("Entering WIP state");
        
        initialized = true;
    }
    
    @Override
    public void exit() {
        System.out.println("Removing elements from WIP State");
        System.out.println("Preparing to transition to next state");
        running = false;
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(panel));
    }
}