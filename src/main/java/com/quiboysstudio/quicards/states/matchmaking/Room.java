package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import javax.swing.border.EmptyBorder;


public class Room extends JFrame {

    private PlayerArea topPlayerArea;
    private PlayerArea bottomPlayerArea;
    private JPanel topHandPanel;
    private JPanel bottomHandPanel;
    private String tableName;
    private List<String> deckCards;  // the full deck
    private final Random random = new Random();
    private CardAnimationSystem animationSystem;
    private JPanel animationLayer;

    public Room(String selectedDeck) {
        setTitle("Card Room - " + selectedDeck);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        // === Randomly pick a background table image ===
        File tableDir = new File("resources/tables");
        File[] tableFiles = tableDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

        if (tableFiles == null || tableFiles.length == 0) {
            System.err.println("No table images found in: " + tableDir.getAbsolutePath());
            tableName = "default";
        } else {
            int randomIndex = (int) (Math.random() * tableFiles.length);
            File chosenTable = tableFiles[randomIndex];
            tableName = chosenTable.getName();
            System.out.println("Selected table: " + tableName);
        }

        String tablePath = "resources/tables/" + tableName;

        // === Use CardImageProxy for lazy loading of the background ===
        CardImageProxy bgImageProxy = new CardImageProxy(tablePath, 1920, 1080);

        // === Create main panel that paints the background using the proxy ===
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Use the proxy to paint the background image
                bgImageProxy.paintIcon(this, g, 0, 0);
            }
        };
        
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);
        
        // === Create animation layer (overlay for card animations) ===
        animationLayer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (animationSystem != null) {
                    animationSystem.paintAnimations((Graphics2D) g);
                }
            }
        };
        animationLayer.setOpaque(false);
        animationLayer.setLayout(null);
        
        animationSystem = new CardAnimationSystem(animationLayer);

        // === Load deck and shuffle ===
        deckCards = loadDeckCards(selectedDeck);
        Collections.shuffle(deckCards);

        // === Prepare cards to draw (don't add to hand yet) ===
        List<String> playerHand = new ArrayList<>();
        List<String> opponentHand = new ArrayList<>();

        // === Create both player areas ===
        topPlayerArea = new PlayerArea(true);
        bottomPlayerArea = new PlayerArea(false);
        
        // === Create hands ===
        topHandPanel = new HandPanel(false, opponentHand);
        bottomHandPanel = new HandPanel(true, playerHand);
        
        // === Setup click listeners for bottom field slots (ONLY ONCE) ===
        for (Map.Entry<String, CardSlot> entry : bottomPlayerArea.getCardSlots().entrySet()) {
            CardSlot slot = entry.getValue();

            // Only add listeners to field slots
            if (!slot.isFieldSlot()) continue;
            
            slot.setOnSlotClicked(clickedSlot -> {
                // Check if slot already has a card
                if (clickedSlot.hasCard()) {
                    System.out.println("Slot already occupied! Cannot replace card.");
                    return;
                }
                
                HandPanel hand = (HandPanel) bottomHandPanel;
                String selectedCard = hand.getSelectedCard();
                
                if (selectedCard == null) {
                    System.out.println("No card selected!");
                    return;
                }

                System.out.println("Deploying card: " + selectedCard + " to " + entry.getKey());
                clickedSlot.setDeployedCard(selectedCard);
                hand.removeCard(selectedCard);
                hand.clearSelection();
            });
        }
        
        // === Update deck count on UI ===
        bottomPlayerArea.updateDeckCount(deckCards.size());
        topPlayerArea.updateDeckCount(deckCards.size());

        // === Create center play area ===
        JPanel playArea = new JPanel(new BorderLayout());
        playArea.setOpaque(false);

        // === Assemble top and bottom containers ===
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(topHandPanel, BorderLayout.NORTH);
        topContainer.add(topPlayerArea, BorderLayout.CENTER);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setOpaque(false);
        bottomContainer.add(bottomPlayerArea, BorderLayout.CENTER);
        bottomContainer.add(bottomHandPanel, BorderLayout.SOUTH);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(playArea, BorderLayout.CENTER);
        mainPanel.add(bottomContainer, BorderLayout.SOUTH);
        
        // === Add animation layer on top ===
        setGlassPane(animationLayer);
        animationLayer.setVisible(true);
        
        // === Start card draw animation after UI is visible ===
        SwingUtilities.invokeLater(() -> {
            // Wait a moment for UI to settle
            Timer delayTimer = new Timer(500, e -> {
                ((Timer)e.getSource()).stop();
                animateInitialCardDraw(playerHand, opponentHand);
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        });
    }

    /**
     * Animates the initial card draw from deck to both hands.
     */
    private void animateInitialCardDraw(List<String> playerHand, List<String> opponentHand) {
        // Get deck positions in screen coordinates
        Point bottomDeckPos = getDeckScreenPosition(bottomPlayerArea);
        Point topDeckPos = getDeckScreenPosition(topPlayerArea);
        
        int cardWidth = 120;
        int cardSpacing = 40;
        int handCenterY = getHeight() - 130; // Bottom hand Y position
        int topHandCenterY = 100; // Top hand Y position
        
        // Animate player (bottom) cards
        for (int i = 0; i < 5; i++) {
            if (deckCards.isEmpty()) break;
            
            String cardName = deckCards.remove(0);
            
            // Calculate end position in hand
            int totalWidth = (5 - 1) * cardSpacing + cardWidth;
            int startX = (getWidth() - totalWidth) / 2;
            int endX = startX + i * cardSpacing;
            int endY = handCenterY;
            
            int delay = i * 150; // Stagger animations
            final String finalCardName = cardName;  // ← ADD THIS LINE

            animationSystem.animateCardDraw(
                cardName,
                bottomDeckPos.x,
                bottomDeckPos.y,
                endX,
                endY,
                delay,
                () -> {
                    // When animation completes, add THIS card to hand
                    playerHand.add(finalCardName);  // ← ADD THIS LINE
                    HandPanel hand = (HandPanel) bottomHandPanel;
                    hand.setCards(new ArrayList<>(playerHand));

                    // Update deck count
                    bottomPlayerArea.updateDeckCount(deckCards.size());
                }
            );
        }
        
        // Animate opponent (top) cards
        for (int i = 0; i < 5; i++) {
            if (deckCards.isEmpty()) break;
            
            String cardName = deckCards.remove(0);
            
            // Calculate end position in hand
            int totalWidth = (5 - 1) * cardSpacing + cardWidth;
            int startX = (getWidth() - totalWidth) / 2;
            int endX = startX + i * cardSpacing;
            int endY = topHandCenterY;
            
            int delay = i * 150; // Stagger animations
            final String finalCardName = cardName;
            animationSystem.animateCardDraw(
                cardName,
                topDeckPos.x,
                topDeckPos.y,
                endX,
                endY,
                delay,
                () -> {
                    // When animation completes, add THIS card to hand
                    opponentHand.add(finalCardName);  // ← ADD THIS LINE
                    HandPanel hand = (HandPanel) topHandPanel;
                    hand.setCards(new ArrayList<>(opponentHand));

                    // Update deck count
                    topPlayerArea.updateDeckCount(deckCards.size());
                }
            );
        }
    }
    
    /**
     * Gets the screen position of the deck slot (centered on the top card of the stack).
     */
    private Point getDeckScreenPosition(PlayerArea playerArea) {
        CardSlot deckSlot = playerArea.getDeckSlot();
        Point slotPos = deckSlot.getLocationOnScreen();
        Point framePos = this.getLocationOnScreen();
        
        // Convert to frame-relative coordinates
        int x = slotPos.x - framePos.x;
        int y = slotPos.y - framePos.y;
        
        // Adjust to center of the card stack (accounting for stack offset)
        int cardWidth = (int)(deckSlot.getWidth() * 0.7);
        int cardHeight = (int)(deckSlot.getHeight() * 0.85);
        int offsetX = (deckSlot.getWidth() - cardWidth) / 2;
        int offsetY = (deckSlot.getHeight() - cardHeight) / 2;
        
        // Position at top card of stack (last card drawn)
        int stackOffset = 4 * 2; // 5 cards with 2px offset each
        
        x += offsetX + stackOffset;
        y += offsetY - stackOffset;
        
        return new Point(x, y);
    }

    /**
     * Draws N random cards from the deck (removes them).
     */
    private List<String> drawCards(int count) {
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < count && !deckCards.isEmpty(); i++) {
            hand.add(deckCards.remove(0));
        }
        return hand;
    }

    /**
     * Reads the card list from a .txt deck file.
     */
    private List<String> loadDeckCards(String deckName) {
        List<String> cards = new ArrayList<>();
        File deckFile = new File("decks/" + deckName + ".txt");

        if (!deckFile.exists()) {
            System.err.println("⚠ Deck not found: " + deckFile.getAbsolutePath());
            return cards;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(deckFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    cards.add(line.split("#")[0].trim()); // remove unique ID suffix
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cards;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Room room = new Room("Test1");
            room.setVisible(true);
        });
    }
}
