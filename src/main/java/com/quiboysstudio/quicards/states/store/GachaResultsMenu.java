package com.quiboysstudio.quicards.states.store;

import com.quiboysstudio.quicards.components.CustomScrollPane;
import com.quiboysstudio.quicards.components.FrameConfig;
import com.quiboysstudio.quicards.components.factories.ComponentFactory;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
// REMOVED: java.awt.Image (no longer needed here)
import javax.swing.BoxLayout; 
import javax.swing.Box;
// REMOVED: javax.swing.ImageIcon (no longer needed here)
import javax.swing.Icon; // NEW IMPORT
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;


public class GachaResultsMenu extends State {
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    private String[] pulledCardFileNames;
    private boolean animationPlayed = false; // Track if animation has been shown

    //objects
    private JLayeredPane layeredPane;
    private JScrollPane packOpeningScrollPane;
    private JPanel firstLayerPanel;
    private JPanel topBarPanel;
    private JPanel navigationPanel;
    private JPanel packCardsPanel;
    private JButton backButton;
    
    // For animation
    private JLabel[] cardLabels;
    private Icon[] frontIcons;

    public GachaResultsMenu(String[] cardFileNames) {
        this.pulledCardFileNames = cardFileNames;
    }
    
    public GachaResultsMenu() {};

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
        
        System.out.println("Showing Gacha Results menu");
        
        layeredPane.add(FrameConfig.backgroundPanel, Integer.valueOf(0));
        
        cardLayout.show(cardPanel, "Gacha Results");
        frame.revalidate();
        frame.repaint();
        
        // Start flip animation after menu is shown
        if (!animationPlayed && cardLabels != null && frontIcons != null) {
            startFlipAnimation();
            animationPlayed = true;
        }
    }

    private void init() {
        if (initialized) return;
        
        System.out.println("Initializing elements from GachaResultsMenu state");
        
        // Reset animation flag when reinitializing
        animationPlayed = false;
        
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
        
        backButton = ComponentFactory.createStateChangerButton("Back", FrameConfig.SATOSHI_BOLD, 150, previousState);
        backButton.setVerticalAlignment(JButton.CENTER);
        backButton.setHorizontalAlignment(JButton.CENTER);
        
        navigationPanel.add(backButton);
        topBarPanel.add(navigationPanel, BorderLayout.WEST);
        
        packCardsPanel = new JPanel();
        packCardsPanel.setOpaque(false);
        packCardsPanel.setLayout(new BoxLayout(packCardsPanel, BoxLayout.Y_AXIS));
        packCardsPanel.setBorder(new EmptyBorder(FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50), FrameUtil.scale(frame, 20), FrameUtil.scale(frame, 50)));
        
        JPanel topRowPanel = createRowPanel();
        JPanel bottomRowPanel = createRowPanel();
        
        // Initialize arrays for animation
        if (this.pulledCardFileNames != null && this.pulledCardFileNames.length > 0) {
            cardLabels = new JLabel[this.pulledCardFileNames.length];
            frontIcons = new Icon[this.pulledCardFileNames.length];
            
            // Populate the rows
            for (int i = 0; i < this.pulledCardFileNames.length; i++) {
                // Create card item and store references for animation
                JPanel cardItem = createPackCardItemWithAnimation(this.pulledCardFileNames[i], i);
                
                if (i < 5) {
                    topRowPanel.add(cardItem);
                } else {
                    bottomRowPanel.add(cardItem);
                }
            }
        } else if (this.pulledCardFileNames != null && this.pulledCardFileNames.length == 0) {
            packCardsPanel.add(ComponentFactory.createTextLabel("No cards found.", FrameConfig.SATOSHI_BOLD));
        } else {
            System.out.println("No pulled cards to display.");
            packCardsPanel.add(ComponentFactory.createTextLabel("Error: No cards were found.", FrameConfig.SATOSHI_BOLD));
        }
        
        packCardsPanel.add(topRowPanel);
        packCardsPanel.add(bottomRowPanel);

        packOpeningScrollPane = new CustomScrollPane(packCardsPanel);
        
        firstLayerPanel.add(topBarPanel, BorderLayout.NORTH);
        firstLayerPanel.add(packOpeningScrollPane, BorderLayout.CENTER);
        
        layeredPane.add(firstLayerPanel, Integer.valueOf(1));
        
        cardPanel.add("Gacha Results", layeredPane);
        
        initialized = true;
        
        System.out.println("Entering GachaResultsMenu state");
    }

    private JPanel createRowPanel() {
        JPanel rowPanel = new JPanel();
        rowPanel.setOpaque(false);
        rowPanel.setLayout(new FlowLayout(FlowLayout.CENTER, FrameUtil.scale(frame, 30), FrameUtil.scale(frame, 30)));
        return rowPanel;
    }

    private JPanel createPackCardItemWithAnimation(String cardFileName, int index) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320)));
        
        // ---- IMAGE DIMENSIONS ----
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);

        // ---- CARD BACK (shown initially) ----
        Icon cardBackIcon = CardFlipAnimation.createCardBackIcon(imgWidth, imgHeight);
        
        // ---- CARD FRONT (for animation reveal) ----
        String imagePath = "resources/cards/Fantasy Card Pack/" + cardFileName;
        Icon frontIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);
        
        // Store the front icon for later animation
        frontIcons[index] = frontIcon;
        
        // Create JLabel with card back initially
        JLabel imageLabel = new JLabel(cardBackIcon);
        imageLabel.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        // Store reference to label for animation
        cardLabels[index] = imageLabel;
        
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
    
    private void startFlipAnimation() {
        if (cardLabels == null || frontIcons == null) return;
        
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);
        Icon cardBackIcon = CardFlipAnimation.createCardBackIcon(imgWidth, imgHeight);
        
        // Animate all cards with 200ms stagger between each
        CardFlipAnimation.animateFlipSequence(
            cardLabels,
            cardBackIcon,
            frontIcons,
            200, // Stagger delay in ms
            () -> System.out.println("All cards revealed!")
        );
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from GachaResultsMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        
        // Reset animation state for next time
        animationPlayed = false;
        
        previousState = currentState;
        currentState = nextState;
    }
}