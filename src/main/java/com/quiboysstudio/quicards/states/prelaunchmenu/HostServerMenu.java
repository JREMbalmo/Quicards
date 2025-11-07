package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.server.ServerHostClient;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class HostServerMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel serverInfoPanel;
    private JPanel buttonPanel;
    private JTextField ipField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
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
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Host Server Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void attemptConnectServer() { 
        //variables
        String ip = String.valueOf(ipField.getText());
        String port = String.valueOf(portField.getText());
        String username = String.valueOf(usernameField.getText());
        String password = String.valueOf(passwordField.getText());

            
        System.out.println(String.format(
                "Connecting to MySQL server hosted at %s with %s as port using %s user with %s as password",
                ip, port, username, password));

        ServerHostClient.setServer(ip, port, username, password);
        
        if (ServerHostClient.connectServer()) {
            //run if connection is successful
            JOptionPane.showMessageDialog(null, "Connected to server");
            exit(serverMenu);
        }
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from HostServerMenu state");
        
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
        usernameField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        passwordField = ComponentFactory.createRoundedPasswordField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        ipLabel = ComponentFactory.createRoundedLabel("IP",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        portLabel = ComponentFactory.createRoundedLabel("Port",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        usernameLabel = ComponentFactory.createRoundedLabel("Username",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        passwordLabel = ComponentFactory.createRoundedLabel("Password",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        
        //add components
        serverInfoPanel.add(ipLabel);
        serverInfoPanel.add(ipField);
        serverInfoPanel.add(portLabel);
        serverInfoPanel.add(portField);
        serverInfoPanel.add(usernameLabel);
        serverInfoPanel.add(usernameField);
        serverInfoPanel.add(passwordLabel);
        serverInfoPanel.add(passwordField);
        
        //buttons
        buttonPanel.add(ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 250, previousState));
        buttonPanel.add(ComponentFactory.createCustomButton("Host", FrameConfig.SATOSHI_BOLD, 250, () -> {attemptConnectServer();}));
        serverInfoPanel.add(buttonPanel);
        
        //subpanels
        firstLayerPanel.add(serverInfoPanel, BorderLayout.CENTER);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Host Server Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering HostServerMenu state");
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from HostServerMenu");
        System.out.println("Preparing to transition to next state");
        
        //cleanup
        ipField.setText(null);
        portField.setText(null);
        usernameField.setText(null);
        passwordField.setText(null);
        
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}