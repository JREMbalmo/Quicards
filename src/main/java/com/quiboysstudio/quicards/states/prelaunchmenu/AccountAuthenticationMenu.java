package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.server.AccountCreationServer;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class AccountAuthenticationMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel buttonPanel;
    private JPanel firstLayerPanel;   
    private JLayeredPane layeredPanel;
    
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showAuthenticationMenu();
    }
    
    private void showAuthenticationMenu() {
        
        if (running) return;
        running = true;
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Account Authentication Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from AuthenticationMenu state");
        
        // initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 580, 520));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //actions buttons
        buttonPanel.add(ComponentFactory.createStateChangerButton("Login Account", FrameConfig.SATOSHI_BOLD, 577, loginMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Create Account", FrameConfig.SATOSHI_BOLD, 577, registerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        buttonPanel.add(ComponentFactory.createCustomButton("Change Server", FrameConfig.SATOSHI_BOLD, 577,
                () -> {AccountCreationServer.leaveServer(); exit(serverMenu);}));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        
        //subpanels
        firstLayerPanel.add(buttonPanel);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Account Authentication Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering AuthenticationMenu state");
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from LoginMenu");
        System.out.println("Preparing to transition to next state");
        
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}