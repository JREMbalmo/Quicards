package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.Collections;
import java.util.Random;
import javax.swing.border.EmptyBorder;

public class Room extends JFrame {

    private PlayerArea topPlayerArea;
    private PlayerArea bottomPlayerArea;
    private JPanel topHandPanel;
    private JPanel bottomHandPanel;

    private List<String> deckCards;  // the full deck
    private final Random random = new Random();

    public Room(String selectedDeck) {
        setTitle("Card Room - " + selectedDeck);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConfig.BLUE);
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

        // === Update deck count on UI ===
        bottomPlayerArea.updateDeckCount(deckCards.size());
        topPlayerArea.updateDeckCount(deckCards.size());

        // === Create center play area ===
        JPanel playArea = new JPanel(new BorderLayout());
        playArea.setOpaque(false);

        // === Create hands ===
        topHandPanel = createPlayerHand(opponentHand, false);   // hidden cards
        bottomHandPanel = createPlayerHand(playerHand, true);   // visible cards

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
     * Creates a player's hand (top or bottom).
     */
    private JPanel createPlayerHand(List<String> cardFiles, boolean showFront) {
        JPanel handPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        handPanel.setOpaque(false);
        handPanel.setPreferredSize(new Dimension(1920, 200));

        for (String cardFile : cardFiles) {
            JLabel cardLabel = new JLabel();
            cardLabel.setPreferredSize(new Dimension(120, 180));
            cardLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));

            if (showFront) {
                String imagePath = "resources/cards/Fantasy Card Pack/" + cardFile;
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    cardLabel.setIcon(new CardImageProxy(imagePath, 120, 180));
                } else {
                    cardLabel.setOpaque(true);
                    cardLabel.setBackground(Color.GRAY);
                    cardLabel.setText("Missing");
                    cardLabel.setForeground(Color.WHITE);
                    cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
                }
            } else {
                cardLabel.setOpaque(true);
                cardLabel.setBackground(Color.BLACK);
            }

            handPanel.add(cardLabel);
        }

        return handPanel;
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
