package com.quiboysstudio.quicards.components;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel implements ThemeChangeListener {
    private final int width;
    private final int height;

    public MenuPanel(int width, int height) {
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        super.setOpaque(false); // force transparency

        // Set initial background based on current theme
        updateColors(ThemeManager.getInstance().isDarkMode());

        // Register for theme updates
        ThemeManager.getInstance().addListener(this);
    }

    private void updateColors(boolean darkMode) {
        if (darkMode) {
            setBackground(FrameConfig.TRANSPARENT_BLACK);
            setForeground(FrameConfig.WHITE);
        } else {
            setBackground(FrameConfig.TRANSPARENT_WHITE);
            setForeground(FrameConfig.BLACK);
        }
    }

    @Override
    public void onThemeChanged(boolean darkMode) {
        updateColors(darkMode);
        repaint();
    }

    //ensure button custom paint doesn't go haywire
    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
        } finally {
            g2.dispose();
        }
        super.paintComponent(g);
    }
}