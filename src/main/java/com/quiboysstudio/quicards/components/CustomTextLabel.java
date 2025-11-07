package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import java.awt.Font;
import javax.swing.*;

public class CustomTextLabel extends JLabel implements ThemeChangeListener {

    public CustomTextLabel(String text, Font font) {
        super(text, SwingConstants.CENTER);

        setOpaque(false);
        setFont(font);
        updateColors(ThemeManager.getInstance().isDarkMode());

        // Register for theme changes
        ThemeManager.getInstance().addListener(this);
    }

    private void updateColors(boolean darkMode) {
        if (darkMode) {
            setForeground(FrameConfig.WHITE);
        } else {
            setForeground(FrameConfig.BLACK);
        }
    }

    @Override
    public void onThemeChanged(boolean darkMode) {
        updateColors(darkMode);
        repaint();
    }
}