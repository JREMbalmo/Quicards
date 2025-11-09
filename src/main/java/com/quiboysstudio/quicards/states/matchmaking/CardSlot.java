package com.quiboysstudio.quicards.states.matchmaking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class CardSlot extends JLabel {
    private final String slotName;
    private final Color borderColor;
    private boolean isHovered = false;
    private float glowAlpha = 0.0f;
    private final Timer hoverTimer;
    private Consumer<CardSlot> onSlotClicked;
    private String deployedCardName;

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

        // Draw deployed card
        if (deployedCardName != null) {
            System.out.println("Painting card '" + deployedCardName + "' in slot '" + slotName + "'");
            Icon icon = loadCardIcon(deployedCardName, width, height);
            if (icon != null) {
                icon.paintIcon(this, g2d, 0, 0);
            } else {
                System.out.println("WARNING: Failed to load icon for: " + deployedCardName);
            }
        }

        // Text (only show if no card deployed)
        if (!slotName.isEmpty() && deployedCardName == null) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(slotName);
            g2d.drawString(slotName, (width - textWidth) / 2, height / 2 + fm.getAscent() / 2);
        }

        g2d.dispose();
    }

    private Icon loadCardIcon(String fileName, int w, int h) {
        try {
            File file = new File("resources/cards/Fantasy Card Pack/" + fileName);
            if (!file.exists()) {
                System.err.println("Card file not found: " + file.getAbsolutePath());
                return null;
            }
            Image img = ImageIO.read(file);
            if (img == null) {
                System.err.println("Failed to read image: " + file.getAbsolutePath());
                return null;
            }
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    public boolean hasCard() {
        return deployedCardName != null;
    }
}
