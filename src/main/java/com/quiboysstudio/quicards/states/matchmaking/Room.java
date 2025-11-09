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

        // Force reload to avoid caching issue
        ImageIcon rawIcon = new ImageIcon(new ImageIcon(tablePath).getImage());
        Image bgImage = rawIcon.getImage().getScaledInstance(1920, 1080, Image.SCALE_SMOOTH);

        // === Create main panel that paints the background ===
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // === Load deck and shuffle ===
        deckCards = loadDeckCards(selectedDeck);
        Collections.shuffle(deckCards);

        // === Draw 5 cards for each hand ===
        List<String> playerHand = drawCards(5);
        List<String> opponentHand = drawCards(5);

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
