package com.quiboysstudio.quicards.states.postlaunchmenu;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.states.State;
import static com.quiboysstudio.quicards.states.State.frame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class SettingsMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JLayeredPane layeredPane;
    private JScrollPane scrollPane;
    private JPanel mainPanel;
    private JPanel firstLayerPanel;
    private JPanel backButtonPanel;
    private JPanel header;
    private JLabel headerLabel;
    private JButton backButton;
    private JButton themeButton;

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
        
        System.out.println("Showing settings menu");
        
        //add background
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Settings Menu");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from SettingsMenu state");
        
        //initialize layered panel
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //back button panel
        backButtonPanel = ComponentFactory.createMenuPanel(FrameUtil.scale(frame, 1920), 100);
        backButtonPanel.setOpaque(false);
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 25), FrameUtil.scale(frame, 25), 0, 0));
        
        //header
        header = ComponentFactory.createMenuPanel(0, 0);
        header.setBounds(0, 0, frame.getWidth(), 100);
        header.setLayout(new FlowLayout(FlowLayout.LEFT));
        header.setBorder(new EmptyBorder(FrameUtil.scale(frame, 5), FrameUtil.scale(frame, 5), 0, 0));
        
        //main panel
        mainPanel = ComponentFactory.createMenuPanel(FrameUtil.scale(frame, 1920), FrameUtil.scale(frame, 1500));
        mainPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 175), 0, 0, 0));
        
        //header label
        headerLabel = ComponentFactory.createTextLabel("Settings", FrameConfig.SATOSHI_BOLD);
        headerLabel.setBounds(0, 0, frame.getWidth(), 100);
        headerLabel.setVerticalTextPosition(JLabel.CENTER);
        headerLabel.setHorizontalTextPosition(JLabel.CENTER);
        
        //back button
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        //theme button
        themeButton = ComponentFactory.createToggleButton("Switch to Light Theme", "Switch to Dark Theme", FrameConfig.SATOSHI_BOLD, 577, () -> {
            ThemeManager.getInstance().toggleTheme();
        });
        
        //scroll pane
        scrollPane = new CustomScrollPane(mainPanel);
        
        //add components
        backButtonPanel.add(backButton);
        header.add(backButtonPanel);
        layeredPane.add(header, Integer.valueOf(2));
        
        layeredPane.add(headerLabel, Integer.valueOf(3));
        
        mainPanel.add(themeButton);
        firstLayerPanel.add(scrollPane, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        //create server menu card
        cardPanel.add("Settings Menu", layeredPane);
        
        initialized = true;
        
        System.out.println("Entering SettingsMenu state");
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from SettingsMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}
