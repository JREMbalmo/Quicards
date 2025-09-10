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
        setOpaque(true);

        // Set initial background based on current theme
        updateColors(ThemeManager.getInstance().isDarkMode());

        // Register for theme updates
        ThemeManager.getInstance().addListener(this);
    }

    private void updateColors(boolean darkMode) {
        if (darkMode) {
            setBackground(FrameConfig.BLACK);
            setForeground(FrameConfig.WHITE);
        } else {
            setBackground(FrameConfig.WHITE);
            setForeground(FrameConfig.BLACK);
        }
    }

    @Override
    public void onThemeChanged(boolean darkMode) {
        updateColors(darkMode);
        repaint();
    }
}