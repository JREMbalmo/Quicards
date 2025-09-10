package com.quiboysstudio.quicards.components;

//imports
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import javax.swing.*;
import java.awt.*;

public class CustomLabel extends JLabel implements ThemeChangeListener {
    private int roundness = 50; //default
    private Color borderColor;

    //current gradient colors
    private Color topColor;
    private Color bottomColor;

    public CustomLabel(String text, Color color, int roundness) {
        super(text, SwingConstants.CENTER);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.borderColor = color;
        this.roundness = roundness;

        updateColors(ThemeManager.getInstance().isDarkMode());

        ThemeManager.getInstance().addListener(this);
    }
    
    public CustomLabel(String text, Color color) {
        super(text, SwingConstants.CENTER);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.borderColor = color;

        updateColors(ThemeManager.getInstance().isDarkMode());

        ThemeManager.getInstance().addListener(this);
    }

    private void updateColors(boolean darkMode) {
        if (darkMode) {
            topColor = FrameConfig.DARK_TOP;
            bottomColor = FrameConfig.DARK_BOTTOM;
            setForeground(FrameConfig.WHITE);
        } else {
            topColor = FrameConfig.LIGHT_TOP;
            bottomColor = FrameConfig.LIGHT_BOTTOM;
            setForeground(FrameConfig.BLACK);
        }
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

        GradientPaint gradient = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
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

    @Override
    public void onThemeChanged(boolean darkMode) {
        updateColors(darkMode);
        repaint();
    }
}