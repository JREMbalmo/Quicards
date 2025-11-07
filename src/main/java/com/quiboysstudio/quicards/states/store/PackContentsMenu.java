package com.quiboysstudio.quicards.states.store;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout; 
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

/**
 * New state to display *all* cards in a pack.
 * This is used for the "Check Cards" button.
 * It is modeled directly on GachaResultsMenu and uses the same CardImageProxy.
 */
public class PackContentsMenu extends State {
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    private String[] allCardFileNames; // Holds all card filenames from the pack

    //objects
    private JLayeredPane layeredPane;
    private JScrollPane packOpeningScrollPane;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel packCardsPanel;
    private JButton backButton;

    public PackContentsMenu(String[] cardFileNames) {
        this.allCardFileNames = cardFileNames;
    }
    
    public PackContentsMenu() {};

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
        
        System.out.println("Showing Pack Contents menu");
        
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        // Use a unique name for this card panel
        cardLayout.show(cardPanel, "Pack Contents"); 
        frame.revalidate();
        frame.repaint();
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from PackContentsMenu state");
        
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        
        firstLayerPanel = new JPanel();
        firstLayerPanel.setOpaque(false);
        firstLayerPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        firstLayerPanel.setLayout(new BorderLayout());
        
        topBarPanel = new JPanel();
        topBarPanel.setOpaque(false);
        topBarPanel.setLayout(new BorderLayout());
        topBarPanel.setPreferredSize(new Dimension(frame.getWidth(), FrameUtil.scale(frame, 150)));
        
        navigationPanel = new JPanel();
        navigationPanel.setOpaque(false);
        navigationPanel.setLayout(new FlowLayout(FlowLayout.LEFT, FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 20)));
        
        // This button's previousState will be the StoreMenu
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        navigationPanel.add(backButton);
        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        
        packCardsPanel = new JPanel();
        packCardsPanel.setOpaque(false);
        packCardsPanel.setLayout(new BoxLayout(packCardsPanel, BoxLayout.Y_AXIS));
        packCardsPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        // Populate the rows dynamically
        if (this.allCardFileNames != null && this.allCardFileNames.length > 0) {
            
            JPanel currentRowPanel = createRowPanel(); // Start with the first row
            packCardsPanel.add(currentRowPanel);

            for (int i = 0; i < this.allCardFileNames.length; i++) {
                // When a row is full (5 cards), create a new one
                if (i > 0 && i % 5 == 0) {
                    currentRowPanel = createRowPanel();
                    packCardsPanel.add(currentRowPanel);
                }
                JPanel cardItem = createPackCardItem(this.allCardFileNames[i]);
                currentRowPanel.add(cardItem);
            }
        } else {
            System.out.println("No cards found to display.");
            packCardsPanel.add(ComponentFactory.createTextLabel("Error: No cards were found.", FrameConfig.SATOSHI_BOLD));
        }

        // The CustomScrollPane will now scroll over all the rows
        packOpeningScrollPane = new CustomScrollPane(packCardsPanel);
        
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(packOpeningScrollPane, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        // Use a unique name for this card panel
        cardPanel.add("Pack Contents", layeredPane); 
        
        initialized = true;
        
        System.out.println("Entering PackContentsMenu state");
    }

    /**
     * Helper method to create an invisible panel with a FlowLayout.
     * (Copied from GachaResultsMenu)
     */
    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        return rowPanel;
    }

    /**
     * Creates the visual representation for a single card.
     * (Copied directly from GachaResultsMenu)
     */
    private JPanel createPackCardItem(String cardFileName) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320)));
        
        // ---- IMAGE PROXY ----
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);
        String imagePath = "resources/cards/Fantasy Card Pack/" + cardFileName;

        Icon proxyIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);
        
        JLabel imageLabel = new JLabel(proxyIcon);
        imageLabel.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // ---- NAME PARSING ----
        String cardName;
        try {
            String displayName = cardFileName.substring(0, cardFileName.lastIndexOf('.'));
            String[] parts = displayName.split(" - ");
            cardName = parts.length > 1 ? parts[1] : displayName;
        } catch (Exception e) {
            cardName = "Error";
        }
        
        JLabel nameLabel = ComponentFactory.createTextLabel(cardName, FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // ---- ADDING COMPONENTS ----
        card.add(imageLabel); 
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel); 
        
        return card;
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from PackContentsMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        previousState = currentState;
        currentState = nextState;
    }
}