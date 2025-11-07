package com.quiboysstudio.quicards.states.postlaunchmenu;

//imports
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MainMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel buttonPanel;
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    
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
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Main Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void init() {
        
        if (initialized) return;
        
        System.out.println("Initializing elements from MainMenu state");
        
        //initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //buttonPanel
        buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 10),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //buttons
        buttonPanel.add(ComponentFactory.createStateChangerButton("Join Room", FrameConfig.SATOSHI_BOLD, 557, joinRoomMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Create Room", FrameConfig.SATOSHI_BOLD, 557, createRoomMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Store", FrameConfig.SATOSHI_BOLD, 557, storeMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Inventory", FrameConfig.SATOSHI_BOLD, 557, inventoryMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Settings", FrameConfig.SATOSHI_BOLD, 557, settingsMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createCustomButton("Log Out", FrameConfig.SATOSHI_BOLD, 557, () -> {
            User.logout(); exit(accountAuthenticationMenu);}));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        buttonPanel.add(ComponentFactory.createStateChangerButton("Exit App", FrameConfig.SATOSHI_BOLD, 557, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 90))); //padding
        
        //add components
        
        //add button panel to firstlayerpanel
        firstLayerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        //add firstlayerpanel to layeredpanel
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create main menu card
        cardPanel.add("Main Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering MainMenu state");
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from MainMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}
