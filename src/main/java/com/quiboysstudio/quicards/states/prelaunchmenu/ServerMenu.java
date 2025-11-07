package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.Logo;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ServerMenu extends State{
    
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
        showMenu();
    }
    
    private void showMenu() {
        
        if (running) return;
        running = true;
        
        System.out.println("Showing server menu");
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Server Menu");
        frame.revalidate();
        frame.repaint();

    }

    private void init() {
        
        if (initialized) return;

        System.out.println("initializing elements from server menu");

        //initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //create header panel
        FrameConfig.header = new JPanel();
        FrameConfig.header.setOpaque(false);
        FrameConfig.header.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50), 0, 0, 0));
        FrameConfig.gameLogo = new Logo(721, 171);
        FrameConfig.logoLabel = ComponentFactory.createRoundedLabel(null, 921, 228, FrameConfig.BLACK, 200, FrameConfig.SATOSHI, FrameConfig.WHITE);
        FrameConfig.logoLabel.setHorizontalAlignment(JLabel.CENTER);
        FrameConfig.logoLabel.setVerticalAlignment(JLabel.CENTER);
        FrameConfig.logoLabel.setIcon(FrameConfig.gameLogo);
        FrameConfig.header.add(FrameConfig.logoLabel);
        
        //create button panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 580, 520));
        buttonPanel.add(ComponentFactory.createStateChangerButton("Host Server", FrameConfig.SATOSHI_BOLD, 557, hostServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        buttonPanel.add(ComponentFactory.createStateChangerButton("Join Server", FrameConfig.SATOSHI_BOLD, 557, joinServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        buttonPanel.add(ComponentFactory.createStateChangerButton("Exit App", FrameConfig.SATOSHI_BOLD, 557, mainMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        
        //add button panel to first layer
        firstLayerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        //add components
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create server menu card
        cardPanel.add("Server Menu", layeredPanel);

        initialized = true;
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}