package com.quiboysstudio.quicards.states.misc;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.CustomButtonFactory;
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
        panel.setPreferredSize(FrameUtil.scale(frame, 500, 500));
        panel.setBackground(FrameConfig.BLUE);
        panel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 30),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //imageicon
        ImageIcon icon = new ImageIcon(new ImageIcon("resources//misc//wip.png").getImage().
                getScaledInstance(FrameUtil.scale(frame, 500), FrameUtil.scale(frame, 500), Image.SCALE_SMOOTH));
        
        //label
        label = new JLabel();
        label.setBackground(FrameConfig.BLUE);
        label.setIcon(icon);
        
        //add components
        panel.add(label);
        panel.add(CustomButtonFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 557, mainMenu));
        
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