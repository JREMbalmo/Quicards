package com.quiboysstudio.quicards.states;

import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.configs.ButtonConfig;
import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.configs.LabelConfig;
import com.quiboysstudio.quicards.configs.TextFieldConfig;
import static com.quiboysstudio.quicards.states.State.frame;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class LoginMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel actionsPanel;
    private JPanel loginPanel;
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
        showActionsMenu();
    }
    
    private void showActionsMenu() {
        
        if (running) return;
        running = true;
        
        System.out.println("Showing LoginMenu");
        
        frame.add(actionsPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    private void showLoginMenu() {
        //remove actions panel
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(actionsPanel));
            
        //show login panel
        frame.add(loginPanel);
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
            String serverUsername, serverPassword, seed;
            int ID;

            //check if user actually exists
            Server.result = Server.statement.executeQuery(
                "select ID, username, password, seed from Users where username = '" + username +"';"
            );

            //check if user exists
            if (Server.result.next()) {
                serverUsername = Server.result.getString("username");
                serverPassword = Server.result.getString("password");
            } else {
                JOptionPane.showMessageDialog(null, "Username doesn't exist!");
                return;
            }

            //check if login info is correct
            if (password.equals(serverPassword)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                ID = Server.result.getInt("ID");
                seed = Server.result.getString("seed");
                User user = new User(ID, serverUsername, serverPassword, seed);
                currentState = mainMenu;
                exit();
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect login information!");
            }

        } catch (Exception e) {
            System.out.println("Failed to do login: " + e);
        }
    }
    
    private void init() {
        if (initialized) return;
        initialized = true;
        
        System.out.println("Initializing elements from LoginMenu state");
        System.out.println("Entering LoginMenu state");
        
        //actions panel
        actionsPanel = new JPanel();
        actionsPanel.setPreferredSize(FrameConfig.scale(frame, 557, 520));
        actionsPanel.setBackground(FrameConfig.BLUE);
        actionsPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 150),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        
        //actions buttons
        actionsPanel.add(ButtonConfig.createCustomButton("Login Account", FrameConfig.SATOSHI_BOLD, 577, FrameConfig.ORANGE, () -> {showLoginMenu();}));
        actionsPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        actionsPanel.add(ButtonConfig.createStateChangerButton("Create Account", FrameConfig.SATOSHI_BOLD, 577, FrameConfig.ORANGE, registerMenu));
        actionsPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        actionsPanel.add(ButtonConfig.createCustomButton("Change Server", FrameConfig.SATOSHI_BOLD, 577, FrameConfig.BLACK,
                () -> {Server.leaveServer(); currentState = serverMenu; exit();}));
        actionsPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        
        //login panel
        loginPanel = new JPanel();
        loginPanel.setPreferredSize(FrameConfig.scale(frame, 557, 500));
        loginPanel.setBackground(FrameConfig.BLUE);
        loginPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 150),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        
        //text fields
        usernameField = TextFieldConfig.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        passwordField = TextFieldConfig.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        
        //labels
        usernameLabel = LabelConfig.createRoundedLabel("Username", 200, 50, FrameConfig.BLUE, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        passwordLabel = LabelConfig.createRoundedLabel("Username", 200, 50, FrameConfig.BLUE, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        
        //add components to login panel
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        
        //login button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 50),0,0,0));
        buttonPanel.setPreferredSize(FrameConfig.scale(frame, 556, 150));
        
        //login buttons
        buttonPanel.add(ButtonConfig.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 250, FrameConfig.ORANGE, () -> {exit();}));
        buttonPanel.add(ButtonConfig.createCustomButton("Login", FrameConfig.SATOSHI_BOLD, 250, FrameConfig.ORANGE, () -> {loginAttempt();}));
        
        //add button panel to login panel
        loginPanel.add(buttonPanel);
    }
    
    @Override
    public void exit() {
        System.out.println("Removing elements from LoginMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        if (actionsPanel.getParent() != null) frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(actionsPanel));
        if (loginPanel.getParent() != null) frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(loginPanel));
    }
}