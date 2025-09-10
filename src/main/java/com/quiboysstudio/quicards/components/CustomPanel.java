package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import com.quiboysstudio.quicards.states.State;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class CustomPanel extends JPanel implements ThemeChangeListener {
    private int roundness = 50;
    private final Color borderColor;

    private Color topColor;
    private Color bottomColor;

    public CustomPanel(Color borderColor) {
        this.borderColor = borderColor;
        setOpaque(false);

        updateColors(ThemeManager.getInstance().isDarkMode());

        ThemeManager.getInstance().addListener(this);
    }

    private void updateColors(boolean darkMode) {
        if (darkMode) {
            topColor = FrameConfig.DARK_TOP;
            bottomColor = FrameConfig.DARK_BOTTOM;
        } else {
            topColor = FrameConfig.LIGHT_TOP;
            bottomColor = FrameConfig.LIGHT_BOTTOM;
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

        // Gradient background
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