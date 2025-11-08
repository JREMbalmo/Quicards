package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class HandPanel extends JPanel {
    private final boolean showFront;
    private final List<String> cardImageNames; // e.g. "Common - Bandit.png"

    public HandPanel(boolean showFront, List<String> cardImageNames) {
        this.showFront = showFront;
        this.cardImageNames = cardImageNames;

        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        initCards();
    }

    private void initCards() {
        removeAll();

        for (String fileName : cardImageNames) {
            JLabel cardLabel = new JLabel();
            cardLabel.setPreferredSize(new Dimension(100, 150));
            cardLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

            if (showFront) {
                // Load card using proxy
                Icon cardIcon = new CardImageProxy("/resources/" + fileName, 100, 150);
                cardLabel.setIcon(cardIcon);
            } else {
                // Black placeholder for card backs
                cardLabel.setOpaque(true);
                cardLabel.setBackground(Color.BLACK);
            }

            add(cardLabel);
        }

        revalidate();
        repaint();
    }
}
