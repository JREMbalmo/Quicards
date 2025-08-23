package com.quiboysstudio.quicards.states;

//imports
import com.quiboysstudio.quicards.configs.FrameConfig;
import com.quiboysstudio.quicards.configs.ButtonConfig;
import com.quiboysstudio.quicards.configs.LabelConfig;
import com.quiboysstudio.quicards.configs.TextFieldConfig;
import com.quiboysstudio.quicards.server.Server;
import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JoinServerMenu extends State{
    
    //variables
    private boolean running = false;
    
    //objects
    private static JPanel serverInfoPanel;
    
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
    
    private Runnable connectServer(JTextField ip, JTextField port, JTextField username, JTextField password) { 
        return () -> {
            System.out.println(String.format(
                    "Connecting to MySQL server hosted at %s with %s as port using %s user with %s as password",
                    String.valueOf(ip.getText()), String.valueOf(port.getText()),
                    String.valueOf(username.getText()), String.valueOf(password.getText())));

            Server.setDatabase(String.valueOf(ip.getText()), String.valueOf(port.getText()),
                    String.valueOf(username.getText()), String.valueOf(password.getText()));
            if (Server.DBConnect()) {
                JOptionPane.showMessageDialog(null, "Connected to server");
                currentState = loginMenu;
                exit();
            } else {
                JOptionPane.showMessageDialog(null, "Can't connect to server");
                ip.setText(null);
                port.setText(null);
                username.setText(null);
                password.setText(null);
            }
        };
    }

    private void init() {
        if (running) return;
        
        System.out.println("Initializing elements from JoinServerMenu state");
        System.out.println("Entering JoinServerMenu state");
        
        //main pane2
        serverInfoPanel = new JPanel();
        serverInfoPanel.setBackground(FrameConfig.BLUE);
        serverInfoPanel.setPreferredSize(FrameConfig.scale(frame, 557, 520));
        serverInfoPanel.setBorder(new EmptyBorder(FrameConfig.scale(frame, 150),FrameConfig.scale(frame, 650),0,FrameConfig.scale(frame, 650)));
        
        //text fields
        JTextField ipField = TextFieldConfig.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        JTextField portField = TextFieldConfig.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        JTextField usernameField = TextFieldConfig.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        JTextField passwordField = TextFieldConfig.createRoundedTextField(350,50,FrameConfig.WHITE,FrameConfig.BLACK,FrameConfig.SATOSHI);
        
        //labels
        JLabel ipLabel = LabelConfig.createRoundedLabel("IP",200,50,FrameConfig.BLUE,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        JLabel portLabel = LabelConfig.createRoundedLabel("Port",200,50,FrameConfig.BLUE,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        JLabel usernameLabel = LabelConfig.createRoundedLabel("Username",200,50,FrameConfig.BLUE,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        JLabel passwordLabel = LabelConfig.createRoundedLabel("Password",200,50,FrameConfig.BLUE,FrameConfig.WHITE,FrameConfig.SATOSHI_BOLD,FrameConfig.WHITE);
        
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
        serverInfoPanel.add(ButtonConfig.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 250, FrameConfig.ORANGE, serverMenu));
        serverInfoPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        serverInfoPanel.add(ButtonConfig.createCustomButton("Join", FrameConfig.SATOSHI_BOLD, 250, FrameConfig.ORANGE,
                connectServer(ipField,portField,usernameField,passwordField)));
        serverInfoPanel.add(Box.createVerticalStrut(FrameConfig.scale(frame, 100))); //padding
        frame.add(serverInfoPanel, BorderLayout.CENTER);
    }

    @Override
    public void exit() {
        System.out.println("Removing elements from JoinServerMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        frame.getContentPane().remove(frame.getContentPane().getComponentZOrder(serverInfoPanel));
        frame.revalidate();
        frame.repaint();
    }
}