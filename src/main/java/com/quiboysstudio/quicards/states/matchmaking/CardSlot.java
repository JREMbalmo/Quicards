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
                JOptionPane.showMessageDialog(
                    CardSlot.this,
                    "You clicked on: " + (slotName.isEmpty() ? "Field Slot" : slotName)
                );
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

        // Text
        if (!slotName.isEmpty()) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(slotName);
            g2d.drawString(slotName, (width - textWidth) / 2, height / 2 + fm.getAscent() / 2);
        }

        g2d.dispose();
    }

}
