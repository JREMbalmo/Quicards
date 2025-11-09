package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.function.Consumer;
import java.util.HashMap;
import java.util.Map;

public class CardSlot extends JLabel {
    private final String slotName;
    private final Color borderColor;
    private boolean isHovered = false;
    private float glowAlpha = 0.0f;
    private final Timer hoverTimer;
    private Consumer<CardSlot> onSlotClicked;
    private String deployedCardName;
    private Map<String, Icon> cardIconCache = new HashMap<>();  // Cache for card icons

    public void setOnSlotClicked(Consumer<CardSlot> listener) {
        this.onSlotClicked = listener;
        System.out.println("Listener set for slot: " + slotName);
    }

    public boolean isFieldSlot() {
        return slotName.startsWith("Field");
    }

    public void setDeployedCard(String cardName) {
        System.out.println("setDeployedCard() called on slot '" + slotName + "' with card: " + cardName);
        this.deployedCardName = cardName;
        repaint();
    }

    public String getDeployedCard() {
        return deployedCardName;
    }

    public CardSlot(String name, Color border) {
        this.slotName = name;
        this.borderColor = border;

        setPreferredSize(UIConfig.SLOT_SIZE);
        setOpaque(false);

        hoverTimer = new Timer(20, e -> {
            glowAlpha += isHovered ? 0.05f : -0.05f;
            glowAlpha = Math.max(0, Math.min(1, glowAlpha));
            if (glowAlpha == 0 || glowAlpha == 1) ((Timer)e.getSource()).stop();
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("CardSlot '" + slotName + "' clicked!");
                if (onSlotClicked != null) {
                    onSlotClicked.accept(CardSlot.this);
                } else {
                    System.out.println("WARNING: No click listener set for slot: " + slotName);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                if (!hoverTimer.isRunning()) hoverTimer.start();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int arc = 25;

        // Border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new RoundRectangle2D.Float(1, 1, width - 2, height - 2, arc, arc));

        // Glow
        if (glowAlpha > 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glowAlpha));
            g2d.setColor(borderColor.brighter());
            for (int i = 0; i < 4; i++) {
                g2d.draw(new RoundRectangle2D.Float(i, i, width - 1 - i * 2, height - 1 - i * 2, arc, arc));
            }
        }

        // Draw card stack for deck slot
        if (slotName.equals("Deck") && deployedCardName == null) {
            drawCardStack(g2d, width, height);
        }

        // Draw deployed card
        if (deployedCardName != null) {
            Icon icon = loadCardIcon(deployedCardName, width, height);
            if (icon != null) {
                icon.paintIcon(this, g2d, 0, 0);
            }
        }

        // Text (only show if no card deployed and not deck)
        if (!slotName.isEmpty() && deployedCardName == null && !slotName.equals("Deck")) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(slotName);
            g2d.drawString(slotName, (width - textWidth) / 2, height / 2 + fm.getAscent() / 2);
        }

        g2d.dispose();
    }
    
    /**
     * Draws a stack of cards to represent the deck.
     */
    private void drawCardStack(Graphics2D g2d, int width, int height) {
        int cardWidth = (int)(width * 0.7);
        int cardHeight = (int)(height * 0.85);
        int offsetX = (width - cardWidth) / 2;
        int offsetY = (height - cardHeight) / 2;
        int stackDepth = 5; // Number of cards to show in stack
        
        // Draw multiple card backs with offset to create stack effect
        for (int i = 0; i < stackDepth; i++) {
            int x = offsetX + i * 2;
            int y = offsetY - i * 2;
            
            // Shadow
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x + 2, y + 2, cardWidth, cardHeight, 8, 8);
            
            // Card back
            g2d.setColor(new Color(40, 40, 60));
            g2d.fillRoundRect(x, y, cardWidth, cardHeight, 8, 8);
            
            // Card back design
            g2d.setColor(new Color(80, 80, 120));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x + 5, y + 5, cardWidth - 10, cardHeight - 10, 5, 5);
            
            // Inner decoration
            g2d.setColor(new Color(100, 100, 150));
            int centerX = x + cardWidth / 2;
            int centerY = y + cardHeight / 2;
            int decorSize = 20;
            g2d.fillOval(centerX - decorSize/2, centerY - decorSize/2, decorSize, decorSize);
            
            // Add some card detail lines
            g2d.setColor(new Color(70, 70, 100));
            g2d.drawLine(x + 10, centerY, x + cardWidth - 10, centerY);
        }
    }

    private Icon loadCardIcon(String fileName, int w, int h) {
        // Check cache first
        if (cardIconCache.containsKey(fileName)) {
            return cardIconCache.get(fileName);
        }
        
        File file = new File("resources/cards/Fantasy Card Pack/" + fileName);
        if (!file.exists()) {
            System.err.println("Card file not found: " + file.getAbsolutePath());
            return null;
        }
        
        // Create proxy and cache it
        Icon proxy = new CardImageProxy(file.getAbsolutePath(), w, h);
        cardIconCache.put(fileName, proxy);
        return proxy;
    }
    
    public boolean hasCard() {
        return deployedCardName != null;
    }
}
