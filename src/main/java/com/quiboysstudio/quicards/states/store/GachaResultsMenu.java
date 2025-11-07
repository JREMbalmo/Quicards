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
import javax.swing.BoxLayout; 
import javax.swing.Box;
import javax.swing.Icon; // NEW IMPORT
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;


public class GachaResultsMenu extends State {
    
    //variables
    private boolean running = false;
    private boolean initialized = false;
    private String[] pulledCardFileNames;
    private boolean animationPlayed = false;
    
    public GachaResultsMenu(String[] pulledCardFileNames) {
        this.pulledCardFileNames = pulledCardFileNames;
    }   
    
    
    public GachaResultsMenu() {
        this.pulledCardFileNames = new String[0]; // Default for StateManager
    }

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
    private JLabel[] nameLabels; // NEW: Store name labels for reveal
    private Icon[] frontIcons;
    
    // Animation constants
    private static final int CARD_REVEAL_DELAY = 200;
    private static final int FLIP_DURATION = 600;

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
            nameLabels = new JLabel[this.pulledCardFileNames.length]; // NEW
            frontIcons = new Icon[this.pulledCardFileNames.length];
            
            // Populate the rows
            for (int i = 0; i < this.pulledCardFileNames.length; i++) {
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

    /**
     * NEW: Creates card item with animation support and hidden name
     */
    private JPanel createPackCardItemWithAnimation(String cardFileName, int index) {
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(FrameUtil.scale(frame, 200), FrameUtil.scale(frame, 320)));
        
        int imgWidth = FrameUtil.scale(frame, 180);
        int imgHeight = FrameUtil.scale(frame, 260);

        // Card back (shown initially)
        Icon cardBackIcon = CardFlipAnimation.createCardBackIcon(imgWidth, imgHeight);
        
        // Card front (for animation reveal)
        String imagePath = "resources/cards/Fantasy Card Pack/" + cardFileName;
        Icon frontIcon = new CardImageProxy(imagePath, imgWidth, imgHeight);
        
        frontIcons[index] = frontIcon;
        
        // Create JLabel with card back initially
        JLabel imageLabel = new JLabel(cardBackIcon);
        imageLabel.setPreferredSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setMaximumSize(new Dimension(imgWidth, imgHeight));
        imageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        
        cardLabels[index] = imageLabel;
        
        // Parse card name
        String cardName = parseCardName(cardFileName);
        
        // Create name label (initially showing ??? and transparent)
        JLabel nameLabel = ComponentFactory.createTextLabel("???", FrameConfig.SATOSHI_BOLD);
        nameLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(255, 255, 255, 0)); // Fully transparent
        
        // Store name label and card data for reveal
        nameLabels[index] = nameLabel;
        nameLabel.putClientProperty("cardFileName", cardFileName);
        nameLabel.putClientProperty("cardName", cardName);
        
        card.add(imageLabel);
        card.add(Box.createRigidArea(new Dimension(0, FrameUtil.scale(frame, 10))));
        card.add(nameLabel);
        
        return card;
    }
    
    /**
     * NEW: Starts the sequential card reveal animation
     */
    private void startFlipAnimation() {
        if (cardLabels == null || frontIcons == null || nameLabels == null) return;
        
        for (int i = 0; i < cardLabels.length; i++) {
            final int index = i;
            
            // Schedule each card's reveal with a delay
            Timer delayTimer = new Timer(CARD_REVEAL_DELAY * i, e -> {
                revealCard(index);
                ((Timer) e.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    
    /**
     * NEW: Reveals a single card with flip animation
     */
    private void revealCard(int index) {
        JLabel imageLabel = cardLabels[index];
        Icon backIcon = imageLabel.getIcon();
        Icon frontIcon = frontIcons[index];
        JLabel nameLabel = nameLabels[index];
        
        // Animate flip
        CardFlipAnimation.animateFlip(imageLabel, backIcon, frontIcon, () -> {
            // After flip completes, reveal the name with color
            String cardFileName = (String) nameLabel.getClientProperty("cardFileName");
            String cardName = (String) nameLabel.getClientProperty("cardName");
            revealCardName(nameLabel, cardName, cardFileName);
        });
    }
    
    
    /**
     * NEW: Reveals card name with fade-in and rarity color
     */
    private void revealCardName(JLabel nameLabel, String cardName, String cardFileName) {
        Color rarityColor = getRarityColor(cardFileName);
        
        nameLabel.setText(cardName);
        
        // Animate fade-in
        Timer fadeTimer = new Timer(20, null);
        final int[] alpha = {0};
        
        fadeTimer.addActionListener(e -> {
            alpha[0] += 15;
            if (alpha[0] >= 255) {
                alpha[0] = 255;
                fadeTimer.stop();
            }
            
            nameLabel.setForeground(new Color(
                rarityColor.getRed(),
                rarityColor.getGreen(),
                rarityColor.getBlue(),
                alpha[0]
            ));
            nameLabel.repaint();
        });
        
        fadeTimer.start();
    }
    
    /**
     * NEW: Determines rarity color based on card filename
     */
    private Color getRarityColor(String cardFileName) {
        String rarity = "Common";
        
        if (cardFileName.contains(" - ")) {
            rarity = cardFileName.split(" - ")[0].trim();
        }
        
        switch (rarity.toLowerCase()) {
            case "common":
                return new Color(200, 200, 200);
            case "uncommon":
                return new Color(30, 255, 0);
            case "rare":
                return new Color(0, 112, 221);
            case "epic":
                return new Color(163, 53, 238);
            case "legendary":
                return new Color(255, 128, 0);
            default:
                return Color.WHITE;
        }
    }
    
    /**
     * Helper method to parse card name from filename
     */
    private String parseCardName(String cardFileName) {
        try {
            String displayName = cardFileName.substring(0, cardFileName.lastIndexOf('.'));
            String[] parts = displayName.split(" - ");
            return parts.length > 1 ? parts[1] : displayName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
    
    @Override
    public void exit(State nextState) {
        System.out.println("Removing elements from GachaResultsMenu");
        System.out.println("Preparing to transition to next state");
        running = false;
        
        animationPlayed = false;
        
        previousState = currentState;
        currentState = nextState;
    }
}
