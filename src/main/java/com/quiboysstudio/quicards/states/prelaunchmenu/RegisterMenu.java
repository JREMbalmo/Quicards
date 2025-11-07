package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.server.AccountCreationServer;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RegisterMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    private JPanel registerMenuPanel;
    private JPanel buttonPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confirmLabel;
    
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
        
        //connect to server as account creator user
        AccountCreationServer.connectServer();
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Register Menu");
        frame.revalidate();
        frame.repaint();
    }
    
    private void loginAttempt() {
        //variables
        String username = String.valueOf(usernameField.getText()).trim();
        String password = String.valueOf(passwordField.getText()).trim();
        
        try {
        Thread.sleep(100);
        } catch (Exception e) {
            System.out.println("Failed to pause thread: " + e);
        }
        
        User.setupUser(username, password);
        
        Server.connectServer();
        
        User.setupID();
        User.updateMoney();
    }
    
    private void registerAttempt() {
        //variables
        String username = String.valueOf(usernameField.getText()).trim();
        String password = String.valueOf(passwordField.getText()).trim();
        String cPassword = String.valueOf(confirmField.getText()).trim();
        
        //password confirmation
        if (!password.equals(cPassword)) {
            JOptionPane.showMessageDialog(null, "Password doesn't match!");
            passwordField.setText(null);
            confirmField.setText(null);
            return;
        }
        
        switch (AccountCreationServer.createUser(username, password)) {
            case 1 -> {
                JOptionPane.showMessageDialog(null, "Account created successfully!");
                while (!User.isActive()) {
                    //connect to server using newly registered user
                    loginAttempt();
                }
                exit(mainMenu);
            }
            case 1062 -> {
                JOptionPane.showMessageDialog(null, "Username is taken!");
                clearFields();
                return;
            }
            case 0 -> {
                JOptionPane.showMessageDialog(null, "Can't register account");
                clearFields();
                return;
            }
        }
        
        //check if username exists on database
//        try {
//            AccountCreationServer.result = AccountCreationServer.statement.executeQuery(
//                    "select Username from Users where username = '" + username + "';"
//            );
            
//            if (!AccountCreationServer.result.next()) {
//                //insert user details to database if username is unique
//                AccountCreationServer.statement.executeUpdate(
//                        "insert into AccountCreation (Username, Password) values " +
//                        "('" + username + "','" + password + "');"
//                );
//                AccountCreationServer.result = AccountCreationServer.statement.executeQuery(
//                        "select ID from Users where username = '" + username + "';");
//                
//                if (AccountCreationServer.result.next()) {
//                    id = AccountCreationServer.result.getInt("ID");
//                }
                
                //setup user class
                //User user = new User(id, username, password, seed);
//                exit(mainMenu);
//            } else {
//                JOptionPane.showMessageDialog(null, "Username is already taken!");
//                passwordField.setText(null);
//                confirmField.setText(null);
//            }
//        } catch (Exception e) {
//            System.out.println("Failed to search username: " + e);
//        }
    }
    
    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from RegisterMenu state");
        
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
        registerMenuPanel = new JPanel();
        registerMenuPanel.setOpaque(false);
        registerMenuPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        registerMenuPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        
        //text fields
        usernameField = ComponentFactory.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        passwordField = ComponentFactory.createRoundedPasswordField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        confirmField = ComponentFactory.createRoundedPasswordField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        usernameLabel = ComponentFactory.createRoundedLabel("Username",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        passwordLabel = ComponentFactory.createRoundedLabel("Password",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        confirmLabel = ComponentFactory.createRoundedLabel("Confirm",200,50,FrameConfig.BLACK,FrameConfig.SATOSHI_BOLD);
        
        //add components
        registerMenuPanel.add(usernameLabel);
        registerMenuPanel.add(usernameField);
        registerMenuPanel.add(passwordLabel);
        registerMenuPanel.add(passwordField);
        registerMenuPanel.add(confirmLabel);
        registerMenuPanel.add(confirmField);
        
        //buttons
        buttonPanel.add(ComponentFactory.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 250, () -> {
            try {
            AccountCreationServer.connection.close();
            } catch (Exception e) {
                System.out.println("Failed to close connection: " + e);
            }
            exit(previousState);}));
        buttonPanel.add(ComponentFactory.createCustomButton("Register", FrameConfig.SATOSHI_BOLD, 250, () -> {registerAttempt();}));
        registerMenuPanel.add(buttonPanel);
        
        //subpanels
        firstLayerPanel.add(registerMenuPanel, BorderLayout.CENTER);
        
        //panel layers
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create host server menu card
        cardPanel.add("Register Menu", layeredPanel);
        
        initialized = true;
        
        System.out.println("Entering RegisterMenu state");
    }
    
    private void clearFields() {
        //clean up
        usernameField.setText(null);
        passwordField.setText(null);
        confirmField.setText(null);
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from RegisterMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
        
        clearFields();
    }
}
