package com.quiboysstudio.quicards.configs;

import com.quiboysstudio.quicards.states.State;
import javax.swing.*;
import java.awt.*;

public class LabelConfig extends JLabel {
    private int roundness = 50;
    private Color borderColor;

    public LabelConfig(String text, Color borderColor) {
        super(text, SwingConstants.CENTER);
        this.borderColor = borderColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                FrameConfig.scale(State.frame, roundness),
                FrameConfig.scale(State.frame, roundness));

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                FrameConfig.scale(State.frame, roundness),
                FrameConfig.scale(State.frame, roundness));

        g2.dispose();
    }

    public static JLabel createRoundedLabel(String text, int width, int height,
            Color backgroundColor, Color borderColor, Font font, Color fontColor) {
        
        LabelConfig label = new LabelConfig(text, borderColor);
        label.setBackground(backgroundColor);
        label.setFont(font);
        label.setForeground(fontColor);
        label.setPreferredSize(FrameConfig.scale(State.frame, width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        return label;
    }
}