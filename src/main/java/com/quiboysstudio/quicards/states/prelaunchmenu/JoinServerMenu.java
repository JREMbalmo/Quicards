package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JoinServerMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel serverInfoPanel;
    private JPanel buttonPanel;
    private JTextField ipField;
    private JTextField portField;
    private JTextField usernameField;
    private JTextField passwordField;
    private JLabel ipLabel;
    private JLabel portLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    
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
        
        frame.add(serverInfoPanel, BorderLayout.CENTER);
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

        Server.setServer(ip, port);
        
        //check if server has correct setup
//        if (!Server.checkServer()) {
//            JOptionPane.showMessageDialog(null, "Server does not have correct setup!");
//            return;
//        }
        
        //check if server is being hosted
        
        
        if (Server.connectServer()) {
            //run if connection is successful
            JOptionPane.showMessageDialog(null, "Connected to server");
            exit(loginMenu);
        } else {
            //run if connection failed
            JOptionPane.showMessageDialog(null, "Can't connect to server");
        }
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from JoinServerMenu state");
        
        //main panel;
        serverInfoPanel = new JPanel();
        serverInfoPanel.setBackground(FrameConfig.BLUE);
        serverInfoPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        serverInfoPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        
        //text fields
        ipField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        portField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        usernameField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        passwordField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        ipLabel = ComponentFactory.createRoundedLabel("IP",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        portLabel = ComponentFactory.createRoundedLabel("Port",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        usernameLabel = ComponentFactory.createRoundedLabel("Username",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        passwordLabel = ComponentFactory.createRoundedLabel("Password",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        
        //add component
        serverInfoPanel.add(ipLabel);
        serverInfoPanel.add(ipField);
        serverInfoPanel.add(portLabel);
        serverInfoPanel.add(portField);
        serverInfoPanel.add(usernameLabel);
        serverInfoPanel.add(usernameField);
        serverInfoPanel.add(passwordLabel);
        serverInfoPanel.add(passwordField);
        
        //buttons
        buttonPanel.add(ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 250, serverMenu));
        buttonPanel.add(ComponentFactory.createCustomButton("Join", FrameConfig.SATOSHI_BOLD, 250, () -> {attemptConnectServer();}));
        serverInfoPanel.add(buttonPanel);
        
        initialized = true;
        
        System.out.println("Entering JoinServerMenu state");
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from JoinServerMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
        
        //cleanup
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(serverInfoPanel));
        ipField.setText(null);
        portField.setText(null);
        usernameField.setText(null);
        passwordField.setText(null);
    }
}