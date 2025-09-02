package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.server.Server;
import com.quiboysstudio.quicards.account.User;
import com.quiboysstudio.quicards.components.factories.CustomButtonFactory;
import com.quiboysstudio.quicards.components.factories.CustomLabelFactory;
import com.quiboysstudio.quicards.components.factories.CustomTextFieldFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class RegisterMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel registerPanel;
    private JPanel buttonPanel;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField confirmField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confirmLabel;
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showRegisterMenu();
    }
    
    private void showRegisterMenu() {
        
        if (running) return;
        running = true;
        
        frame.add(registerPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    private void registerAttempt() {
        //variables
        String username = String.valueOf(usernameField.getText()).trim();
        String password = String.valueOf(passwordField.getText()).trim();
        String cPassword = String.valueOf(confirmField.getText()).trim();
        int id = 0;
        long seed;
        
        //password confirmation
        if (!password.equals(cPassword)) {
            JOptionPane.showMessageDialog(null, "Password doesn't match!");
            passwordField.setText(null);
            confirmField.setText(null);
            return;
        }
        
        //generate seed
        seed = User.generateSeed();
        
        //check if username exists on database
        try {
            Server.result = Server.statement.executeQuery(
                    "select username from Users where username = '" + username + "';"
            );
            
            if (!Server.result.next()) {
                //insert user details to database if username is unique
                Server.statement.executeUpdate(
                        "insert into Users (username, password, seed) values " +
                        "('" + username + "','" + password + "','" + seed + "');"
                );
                Server.result = Server.statement.executeQuery(
                        "select ID from Users where username = '" + username + "';");
                
                if (Server.result.next()) {
                    id = Server.result.getInt("ID");
                }
                
                //setup user class
                User user = new User(id, username, password, seed);
                exit(mainMenu);
            } else {
                JOptionPane.showMessageDialog(null, "Username is already taken!");
                passwordField.setText(null);
                confirmField.setText(null);
            }
        } catch (Exception e) {
            System.out.println("Failed to search username: " + e);
        }
    }
    
    private void init() {
        //only run once
        if (initialized) return;
        
        System.out.println("Initializing elements from RegisterMenu state");
        
        //register panel
        registerPanel = new JPanel();
        registerPanel.setPreferredSize(FrameUtil.scale(frame, 557, 520));
        registerPanel.setBackground(FrameConfig.BLUE);
        registerPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //text fields
        usernameField = CustomTextFieldFactory.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        passwordField = CustomTextFieldFactory.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        confirmField = CustomTextFieldFactory.createRoundedTextField(350, 50, FrameConfig.WHITE, FrameConfig.BLACK, FrameConfig.SATOSHI);
        
        //labels
        usernameLabel = CustomLabelFactory.createRoundedLabel("Username", 200, 50, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        passwordLabel = CustomLabelFactory.createRoundedLabel("Password", 200, 50, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        confirmLabel = CustomLabelFactory.createRoundedLabel("Confirm", 200, 50, FrameConfig.WHITE, FrameConfig.SATOSHI_BOLD, FrameConfig.WHITE);
        
        //button panel
        buttonPanel = new JPanel();
        buttonPanel.setBackground(FrameConfig.BLUE);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50),0,0,0));
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 556, 150));
        
        //register buttons
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 250, loginMenu));
        buttonPanel.add(CustomButtonFactory.createCustomButton("Register", FrameConfig.SATOSHI_BOLD, 250, () -> {registerAttempt();}));
        
        //add components
        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(confirmLabel);
        registerPanel.add(confirmField);
        registerPanel.add(buttonPanel);
        
        initialized = true;
        
        System.out.println("Entering RegisterMenu state");
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from RegisterMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
        
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(registerPanel));
        usernameField.setText(null);
        passwordField.setText(null);
        confirmField.setText(null);
    }
}
