package com.quiboysstudio.quicards.states.matchmaking;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class PlayerArea extends JPanel {
    private final HealthCircle healthCircle;
    private final Map<String, CardSlot> cardSlots = new HashMap<>();
    private final JLabel deckCountLabel;

    public PlayerArea(boolean isTopPlayer) {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        healthCircle = new HealthCircle();
        Color borderColor = isTopPlayer ? Color.GRAY : UIConfig.ORANGE;

        // === Health Circle ===
        gbc.gridx = 3;
        gbc.gridy = isTopPlayer ? 0 : 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(healthCircle, gbc);

        // === Card Slots container ===
        JPanel cardsPanel = new JPanel(new BorderLayout(20, 0));
        cardsPanel.setOpaque(false);

        // Create all slots
        CardSlot deckSlot = new CardSlot("Deck", borderColor);
        CardSlot discardSlot = new CardSlot("Discard", borderColor);

        // Add deck count label overlay
        deckCountLabel = new JLabel("30", SwingConstants.CENTER);
        deckCountLabel.setForeground(Color.WHITE);
        deckCountLabel.setFont(new Font("Arial", Font.BOLD, 20));
        deckSlot.setLayout(new BorderLayout());
        deckSlot.add(deckCountLabel, BorderLayout.CENTER);

        cardSlots.put("Deck", deckSlot);
        cardSlots.put("Discard", discardSlot);

        // Create 3 field slots - IMPORTANT: Give them identifying names!
        for (int i = 1; i <= 3; i++) {
            cardSlots.put("Field " + i, new CardSlot("Field " + i, borderColor));
        }

        // === Middle field area ===
        JPanel fieldSlotsPanel = new JPanel(new GridLayout(1, 0, 10, 10));
        fieldSlotsPanel.setOpaque(false);
        fieldSlotsPanel.add(cardSlots.get("Field 1"));
        fieldSlotsPanel.add(cardSlots.get("Field 2"));
        fieldSlotsPanel.add(cardSlots.get("Field 3"));

        // === Arrange deck/discard depending on player position ===
        if (isTopPlayer) {
            cardsPanel.add(deckSlot, BorderLayout.WEST);
            cardsPanel.add(fieldSlotsPanel, BorderLayout.CENTER);
            cardsPanel.add(discardSlot, BorderLayout.EAST);
        } else {
            cardsPanel.add(discardSlot, BorderLayout.WEST);
            cardsPanel.add(fieldSlotsPanel, BorderLayout.CENTER);
            cardsPanel.add(deckSlot, BorderLayout.EAST);
        }

        gbc.gridx = 0;
        gbc.gridy = isTopPlayer ? 1 : 0;
        gbc.gridwidth = 9;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(cardsPanel, gbc);
    }

    /** Update deck count label dynamically */
    public void updateDeckCount(int count) {
        deckCountLabel.setText(String.valueOf(count));
    }

    public CardSlot getDeckSlot() {
        return cardSlots.get("Deck");
    }
    
    public Map<String, CardSlot> getCardSlots() {
        return cardSlots;
    }
}
