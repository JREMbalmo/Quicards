package com.quiboysstudio.quicards.states.postlaunchmenu;

//imports
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.CustomButtonFactory;
import com.quiboysstudio.quicards.states.State;
import static com.quiboysstudio.quicards.states.State.frame;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel buttonPanel;
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showMainMenu();
    }
    
    private void showMainMenu() {
        
        if (running) return;
        running = true;
        
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    private void init() {
        
        if (initialized) return;
        
        System.out.println("Initializing elements from MainMenu state");
        System.out.println("Entering MainMenu state");
        
        //buttonPanel
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 30),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //buttons
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Join Room", FrameConfig.SATOSHI_BOLD, 557, wipState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Create Room", FrameConfig.SATOSHI_BOLD, 557, wipState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Store", FrameConfig.SATOSHI_BOLD, 557, wipState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Inventory", FrameConfig.SATOSHI_BOLD, 557, wipState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Settings", FrameConfig.SATOSHI_BOLD, 557, wipState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createCustomButton("Log Out", FrameConfig.SATOSHI_BOLD, 557, () -> {
            User.logout(); currentState = loginMenu; exit();}));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Exit App", FrameConfig.SATOSHI_BOLD, 557, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        
        initialized = true;
    }
    
    @Override
    public void exit() {
        System.out.println("Removing elements from MainMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(buttonPanel));
    }
}
