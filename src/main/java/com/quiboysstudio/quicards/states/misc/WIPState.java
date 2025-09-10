package com.quiboysstudio.quicards.states.misc;

//imports
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.states.State;
import static com.quiboysstudio.quicards.states.State.cardLayout;
import static com.quiboysstudio.quicards.states.State.cardPanel;
import static com.quiboysstudio.quicards.states.State.frame;
import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class WIPState extends State{
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    
    //objects
    private JPanel panel;
    private JLabel label;
    private JLayeredPane layeredPanel;
    private JPanel firstLayerPanel;
    
    @Override
    public void enter() {
        init();
    }
    
    @Override
    public void update() {
        showWipState();
    }

    private void showWipState() {
        
        if (running) return;
        running = true;
        
        System.out.println("Showing WIP menu");
        
        //add header to first layer
        firstLayerPanel.add(FrameConfig.header, BorderLayout.NORTH);
        
        //add background
        layeredPanel.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "WIP");
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        
        if (initialized) return;
        
        System.out.println("Initializing elements from WIP state");
        
        //initialize layered panel
        layeredPanel = new JLayeredPane();
        layeredPanel.setOpaque(false);
        layeredPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        //initialize first layer
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        //panel
        panel = new JPanel();
        panel.setPreferredSize(FrameUtil.scale(frame, 500, 500));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 30),FrameUtil.scale(frame, 650),0,FrameUtil.scale(frame, 650)));
        
        //imageicon
        ImageIcon icon = new ImageIcon(new ImageIcon("resources//misc//wip.png").getImage().
                getScaledInstance(FrameUtil.scale(frame, 500), FrameUtil.scale(frame, 500), Image.SCALE_SMOOTH));
        
        //label
        label = new JLabel();
        label.setBackground(FrameConfig.BLUE);
        label.setIcon(icon);
        
        //add components
        panel.add(label);
        panel.add(ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 557, previousState));
        
        firstLayerPanel.add(panel);
        layeredPanel.add(firstLayerPanel, Integer.valueOf(1));
        
        //create wip card
        cardPanel.add("WIP", layeredPanel);
        
        System.out.println("Entering WIP state");
        
        initialized = true;
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from WIP State");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}