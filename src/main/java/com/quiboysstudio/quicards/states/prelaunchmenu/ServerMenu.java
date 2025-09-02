package com.quiboysstudio.quicards.states.prelaunchmenu;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.CustomButtonFactory;
import com.quiboysstudio.quicards.components.factories.CustomLabelFactory;
import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ServerMenu extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel header;
    private JPanel buttonPanel;
    private JLabel logoLabel;
    private ImageIcon gameLogo;
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
        
        System.out.println("Showing server menu");
        
        cardLayout.show(cardPanel, "Server Menu");
        frame.revalidate();
        frame.repaint();

    }

    private void init() {
        if (initialized) return;

        System.out.println("initializing elements from server menu");

        // initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        // create header panel
        header = new JPanel();
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(FrameUtil.scale(frame, 50), 0, 0, 0));
        gameLogo = new ImageIcon(new ImageIcon("resources//logos//game_logo_orange_text.png").getImage()
                .getScaledInstance(FrameUtil.scale(frame, 622), FrameUtil.scale(frame, 150), Image.SCALE_SMOOTH));
        logoLabel = CustomLabelFactory.createRoundedLabel(null, 921, 228, FrameConfig.BLACK, 200, FrameConfig.SATOSHI, FrameConfig.WHITE);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        logoLabel.setVerticalAlignment(JLabel.CENTER);
        logoLabel.setIcon(gameLogo);
        header.add(logoLabel);

        //add header to first layer
        firstLayerPanel.add(header, BorderLayout.NORTH);
        
        //create button panel with buttons
        buttonPanel = new JPanel();
        buttonPanel.setBackground(null);
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 150),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        buttonPanel.setPreferredSize(FrameUtil.scale(frame, 580, 520));
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Host Server", FrameConfig.SATOSHI_BOLD, 557, hostServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Join Server", FrameConfig.SATOSHI_BOLD, 557, joinServerMenu));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        buttonPanel.add(CustomButtonFactory.createStateChangerButton("Exit App", FrameConfig.SATOSHI_BOLD, 557, exitState));
        buttonPanel.add(Box.createVerticalStrut(FrameUtil.scale(frame, 100)));
        
        
        //add button panel to first layer
        firstLayerPanel.add(buttonPanel, BorderLayout.CENTER);
        
        //add components
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0)); //background
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create server menu card
        cardPanel.add("Server Menu", layeredPanel);

        initialized = true;
    }

    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from ServerMenu state");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}