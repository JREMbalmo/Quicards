package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel {
    private int roundness = 50;
    private Color borderColor;

    // Default gradient colors
    private static final Color TOP_COLOR = new Color(150, 150, 150, (int)(255 * 0.8));
    private static final Color BOTTOM_COLOR = new Color(30, 30, 30, (int)(255 * 0.8));

    public CustomLabel(String text, Color color) {
        super(text, SwingConstants.CENTER);
        setOpaque(false);
        setForeground(Color.WHITE); // keep text visible
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        borderColor = color;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = FrameUtil.scale(State.frame, roundness);
        int borderSize = FrameUtil.scale(State.frame, 3);

        int x = borderSize;
        int y = borderSize;
        int w = getWidth() - (borderSize * 2 - 1);
        int h = getHeight() - (borderSize * 2 - 1);

        // Gradient background
        GradientPaint gradient = new GradientPaint(0, 0, TOP_COLOR, 0, getHeight(), BOTTOM_COLOR);
        g2.setPaint(gradient);
        g2.fillRoundRect(x, y, w, h, arc, arc);

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = FrameUtil.scale(State.frame, roundness);
        int borderSize = FrameUtil.scale(State.frame, 3);

        g2.setStroke(new BasicStroke(borderSize));
        g2.setColor(borderColor);
        g2.drawRoundRect(
            borderSize / 2,
            borderSize / 2,
            getWidth() - borderSize,
            getHeight() - borderSize,
            arc, arc
        );

        g2.dispose();
    }
}