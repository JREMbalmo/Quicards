package com.quiboysstudio.quicards.states.matchmaking.utils;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;



public class HandPanel extends JPanel {
    private final boolean isPlayerHand;
    private List<String> cardImageNames;
    private int hoveredIndex = -1;
    private float[] hoverOffsets;          // current animated offsets (pixels)
    private float hoverTarget = 40f;       // how far card pops up
    private Timer animTimer;
    private int selectedCardIndex = -1;
    private java.util.Map<String, Icon> cardIconCache = new java.util.HashMap<>();  // Cache for proxies

    public HandPanel(boolean isPlayerHand, List<String> cardImageNames) {
        this.isPlayerHand = isPlayerHand;
        this.cardImageNames = cardImageNames;
        if (cardImageNames != null) hoverOffsets = new float[cardImageNames.size()];
        setOpaque(false);
        setPreferredSize(new Dimension(1200, 220));
        setLayout(null);

        animTimer = new Timer(16, e -> {
            boolean changed = false;
            if (hoverOffsets == null) return;
            for (int i = 0; i < hoverOffsets.length; i++) {
                float target = (i == hoveredIndex && isPlayerHand) ? hoverTarget : 0f;
                float cur = hoverOffsets[i];
                float delta = (target - cur) * 0.25f; // smoothing factor
                if (Math.abs(delta) < 0.5f) {
                    if (cur != target) { hoverOffsets[i] = target; changed = true; }
                } else {
                    hoverOffsets[i] = cur + delta; changed = true;
                }
            }
            if (changed) repaint();
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isPlayerHand) return;
                int newIndex = getCardIndexAtPoint(e.getPoint());
                if (newIndex != hoveredIndex) {
                    hoveredIndex = newIndex;
                    if (!animTimer.isRunning()) animTimer.start();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isPlayerHand) return;
                hoveredIndex = -1;
                if (!animTimer.isRunning()) animTimer.start();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isPlayerHand) return;
                int index = getCardIndexAtPoint(e.getPoint());
                if (index != -1 && index < cardImageNames.size()) {
                    selectedCardIndex = index;
                    repaint();
                    System.out.println("Selected card: " + cardImageNames.get(index));
                }
            }
        });
    }

    public void setCards(List<String> names) {
        this.cardImageNames = names;
        hoverOffsets = (names != null) ? new float[names.size()] : null;
        hoveredIndex = -1;
        cardIconCache.clear();  // Clear cache when cards change
        repaint();
    }

    private int getCardIndexAtPoint(Point p) {
        if (cardImageNames == null || cardImageNames.isEmpty()) return -1;
        int cardW = 120;
        int spacing = 40;
        int totalWidth = (cardImageNames.size() - 1) * spacing + cardW;
        int startX = (getWidth() - totalWidth) / 2;
        int y = getHeight() - 160;
        for (int i = 0; i < cardImageNames.size(); i++) {
            int x = startX + i * spacing;
            Rectangle r = new Rectangle(x, y, cardW, 140);
            if (r.contains(p)) return i;
        }
        return -1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cardImageNames == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cardW = 120;
        int cardH = 160;
        int spacing = 40;
        int count = cardImageNames.size();
        if (count == 0) return;

        int totalWidth = (count - 1) * spacing + cardW;
        int startX = (getWidth() - totalWidth) / 2;
        int baseY = getHeight() - cardH - 10;
        double arcStrength = 0.012;
        int centerIndex = (count - 1) / 2;

        for (int i = 0; i < count; i++) {
            double offset = i - centerIndex;
            double angle = offset * arcStrength;
            int x = startX + i * spacing;
            int y = baseY + (int) (Math.abs(offset) * Math.abs(offset) * 2.5);

            float popOffset = (hoverOffsets != null && i < hoverOffsets.length) ? hoverOffsets[i] : 0f;
            int drawY = (int) (y - popOffset);

            g2.translate(x + cardW / 2, drawY + cardH / 2);
            g2.rotate(angle);
            g2.translate(-cardW / 2, -cardH / 2);
            
            // Highlight selected card
            if (i == selectedCardIndex && isPlayerHand) {
                g2.setColor(new Color(255, 255, 0, 128)); // yellow highlight
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(-2, -2, cardW + 4, cardH + 4, 10, 10);
            }           
            
            // === Draw card face or back ===
            if (isPlayerHand) {
                // Player sees their cards
                String fileName = cardImageNames.get(i);
                Icon icon = loadCardIcon(fileName, cardW, cardH);
                if (icon != null) {
                    icon.paintIcon(this, g2, 0, 0);
                } else {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRoundRect(0, 0, cardW, cardH, 8, 8);
                    g2.setColor(Color.WHITE);
                    g2.drawString("Missing", 10, 20);
                }
            } else {
                // Opponent sees card backs
                g2.setColor(Color.BLACK);
                g2.fillRoundRect(0, 0, cardW, cardH, 10, 10);
                g2.setColor(Color.LIGHT_GRAY);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, cardW - 1, cardH - 1, 10, 10);
            }

            g2.translate(cardW / 2, cardH / 2);
            g2.rotate(-angle);
            g2.translate(-x - cardW / 2, -drawY - cardH / 2);
        }

        g2.dispose();
    }

    private Icon loadCardIcon(String fileName, int w, int h) {
        // Check cache first
        if (cardIconCache.containsKey(fileName)) {
            return cardIconCache.get(fileName);
        }
        
        File file = new File("resources/cards/Fantasy Card Pack/" + fileName);
        if (!file.exists()) {
            System.err.println("⚠ Missing card image: " + file.getAbsolutePath());
            return null;
        }
        
        System.out.println("Creating proxy for: " + fileName);
        
        // Create proxy only once and cache it
        Icon proxy = new CardImageProxy(file.getAbsolutePath(), w, h);
        cardIconCache.put(fileName, proxy);
        return proxy;
    }
    
    public String getSelectedCard() {
        if (selectedCardIndex >= 0 && selectedCardIndex < cardImageNames.size()) {
            String card = cardImageNames.get(selectedCardIndex);
            System.out.println("getSelectedCard() returning: " + card + " at index " + selectedCardIndex);
            return card;
        }
        System.out.println("getSelectedCard() returning null (selectedCardIndex=" + selectedCardIndex + ", size=" + (cardImageNames != null ? cardImageNames.size() : "null") + ")");
        return null;
    }

    public void removeCard(String cardName) {
        if (cardImageNames.remove(cardName)) {
            hoverOffsets = new float[cardImageNames.size()];
            selectedCardIndex = -1;
            cardIconCache.remove(cardName);  // Remove from cache too
            repaint();
        }
    }

    public void clearSelection() {
        selectedCardIndex = -1;
        repaint();
    }
}
