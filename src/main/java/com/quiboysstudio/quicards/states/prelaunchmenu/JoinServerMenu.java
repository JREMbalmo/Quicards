package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.server.AccountCreationServer;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JoinServerMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    private JPanel serverInfoPanel;
    private JPanel buttonPanel;
    private JTextField ipField;
    private JTextField portField;
    private JLabel ipLabel;
    private JLabel portLabel;
    
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
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Join Server Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void attemptConnectServer() { 
        //variables
        String ip = String.valueOf(ipField.getText());
        String port = String.valueOf(portField.getText());
            
        System.out.println(String.format(
                "Connecting to MySQL server hosted at %s with %s as port",
                ip, port));

        AccountCreationServer.setServer(ip, port);
        
        if (AccountCreationServer.connectServer()) {
            //run if connection is successful
            JOptionPane.showMessageDialog(null, "Connected to server");
            
            try {
            AccountCreationServer.connection.close();
            } catch (Exception e) {
                System.out.println("Failed to close connection: " + e);
            }
            
            exit(accountAuthenticationMenu);
        } else {
            //run if connection failed
            JOptionPane.showMessageDialog(null, "Can't connect to server");
        }
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from JoinServerMenu state");
        
        // initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //main panel;
        serverInfoPanel = new JPanel();
        serverInfoPanel.setOpaque(false);
        serverInfoPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        serverInfoPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        
        //text fields
        ipField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        portField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        ipLabel = ComponentFactory.createRoundedLabel("IP",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        portLabel = ComponentFactory.createRoundedLabel("Port",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        
        //add components
        serverInfoPanel.add(ipLabel);
        serverInfoPanel.add(ipField);
        serverInfoPanel.add(portLabel);
        serverInfoPanel.add(portField);
        
        //buttons
        buttonPanel.add(ComponentFactory.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 250, () -> {
            AccountCreationServer.leaveServer(); exit(previousState);}));
        buttonPanel.add(ComponentFactory.createCustomButton("Join", FrameConfig.SATOSHI_BOLD, 250, () -> {attemptConnectServer();}));
        serverInfoPanel.add(buttonPanel);
        
        //subpanels
        firstLayerPanel.add(serverInfoPanel, BorderLayout.CENTER);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Join Server Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering JoinServerMenu state");
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from JoinServerMenu");
        System.out.println("Preparing to transition to next state");
        
        //cleanup
        ipField.setText(null);
        portField.setText(null);
        
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}