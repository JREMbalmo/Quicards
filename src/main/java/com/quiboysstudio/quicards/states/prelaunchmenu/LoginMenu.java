package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.CustomButtonFactory;
import com.quiboysstudio.quicards.components.factories.CustomLabelFactory;
import com.quiboysstudio.quicards.components.factories.CustomTextFieldFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
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
        
        frame.add(actionsPanel, BorderLayout.CENTER);
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
            int ID;
            String serverUsername, serverPassword;
            long seed;

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
                passwordField.setText(null);
                return;
            }

            //check if login info is correct
            if (password.equals(serverPassword)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                ID = Server.result.getInt("ID");
                seed = Server.result.getLong("seed");
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
        
        //actions panel
        actionsPanel = new JPanel();
        actionsPanel.setPreferredSize(FrameUtil.scale(frame, 580, 520));
        actionsPanel.setBackground(FrameConfig.BLUE);
        actionsPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //actions buttons
        actionsPanel.add(CustomButtonFactory.createCustomButton("Login Account", FrameConfig.SATOSHI_BOLD, 577, () -> {showLoginMenu();}));
        actionsPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        actionsPanel.add(CustomButtonFactory.createStateChangerButton("Create Account", FrameConfig.SATOSHI_BOLD, 577, registerMenu));
        actionsPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        actionsPanel.add(CustomButtonFactory.createCustomButton("Change Server", FrameConfig.SATOSHI_BOLD, 577,
                () -> {Server.leaveServer(); exit(serverMenu);}));
        actionsPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100))); //padding
        
        //login panel
        loginPanel = new JPanel();
        loginPanel.setPreferredSize(FrameUtil.scale(frame, 557, 500));
        loginPanel.setBackground(FrameConfig.BLUE);
        loginPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //text fields
        usernameField = CustomTextFieldFactory.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        passwordField = CustomTextFieldFactory.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        
        //labels
        usernameLabel = CustomLabelFactory.createRoundedLabel("Username", 200, 50, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        passwordLabel = CustomLabelFactory.createRoundedLabel("Password", 200, 50, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        
        //add components to login panel
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        
        //login button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        
        //login buttons
        buttonPanel.add(CustomButtonFactory.createCustomButton("Back", FrameConfig.SATOSHI_BOLD, 250, () -> {exit(loginMenu);}));
        buttonPanel.add(CustomButtonFactory.createCustomButton("Login", FrameConfig.SATOSHI_BOLD, 250, () -> {loginAttempt();}));
        
        //add button panel to login panel
        loginPanel.add(buttonPanel);
        
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
        
        if (actionsPanel.getParent() != null) frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(actionsPanel));
        if (loginPanel.getParent() != null) frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(loginPanel));
        usernameField.setText(null);
        passwordField.setText(null);
    }
}