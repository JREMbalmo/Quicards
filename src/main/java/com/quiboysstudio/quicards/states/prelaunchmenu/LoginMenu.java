package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.server.AccountCreationServer;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    private JPanel loginMenuPanel;
    private JPanel buttonPanel;
    private JTextField usernameField;
    private JTextField passwordField;
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
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Login Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void loginAttempt() {
        //variables
        String username = String.valueOf(usernameField.getText());
        String password = String.valueOf(passwordField.getText());

        //send queries for login
        try {
            //variables
            int ID;
            String serverUsername, serverPassword;
            long seed;

            //check if user actually exists
            AccountCreationServer.result = AccountCreationServer.statement.executeQuery(
                "select ID, username, password, seed from Users where username = '" + username +"';"
            );

            //check if user exists
            if (AccountCreationServer.result.next()) {
                serverUsername = AccountCreationServer.result.getString("username");
                serverPassword = AccountCreationServer.result.getString("password");
            } else {
                JOptionPane.showMessageDialog(null, "Username doesn't exist!");
                passwordField.setText(null);
                return;
            }

            //check if login info is correct
            if (password.equals(serverPassword)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                ID = AccountCreationServer.result.getInt("ID");
                seed = AccountCreationServer.result.getLong("seed");
                User user = new User(ID, serverUsername, serverPassword, seed);
                exit(mainMenu);
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect login information!");
            }

        } catch (Exception e) {
            System.out.println("Failed to do login: " + e);
        }
    }
    
    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from LoginMenu state");
        
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
        loginMenuPanel = new JPanel();
        loginMenuPanel.setOpaque(false);
        loginMenuPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        loginMenuPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        
        //text fields
        usernameField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        passwordField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        usernameLabel = ComponentFactory.createRoundedLabel("Username",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        passwordLabel = ComponentFactory.createRoundedLabel("Password",200,50,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        
        //add components
        loginMenuPanel.add(usernameLabel);
        loginMenuPanel.add(usernameField);
        loginMenuPanel.add(passwordLabel);
        loginMenuPanel.add(passwordField);
        
        //buttons
        buttonPanel.add(ComponentFactory.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 250, () -> {
            AccountCreationServer.leaveServer(); exit(previousState);}));
        buttonPanel.add(ComponentFactory.createCustomButton("Login", FrameConfig.SATOSHI_BOLD, 250, () -> {loginAttempt();}));
        loginMenuPanel.add(buttonPanel);
        
        //subpanels
        firstLayerPanel.add(loginMenuPanel, BorderLayout.CENTER);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Login Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering LoginMenu state");
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from LoginMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
        
        usernameField.setText(null);
        passwordField.setText(null);
    }
}