package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import com.quiboysstudio.quicards.states.State;
import javax.swing.*;
import java.awt.*;

public class Logo extends ImageIcon implements ThemeChangeListener {
    private ImageIcon darkLogo;
    private ImageIcon lightLogo;
    private int width;
    private int height;

    public Logo(int width, int height) {
        this.width = FrameUtil.scale(State.frame, width);
        this.height = FrameUtil.scale(State.frame, height);;

        this.darkLogo = new ImageIcon(
                new ImageIcon("resources//logos//game_logo_orange_text.png")
                        .getImage()
                        .getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH)
        );
        this.lightLogo = new ImageIcon(
                new ImageIcon("resources//logos//game_logo_blue_text.png")
                        .getImage()
                        .getScaledInstance(this.width, this.height, Image.SCALE_SMOOTH)
        );

        // Set initial image
        setImage((ThemeManager.getInstance().isDarkMode() ? darkLogo : lightLogo).getImage());

        ThemeManager.getInstance().addListener(this);
    }

    @Override
    public void onThemeChanged(boolean darkMode) {
        setImage((darkMode ? darkLogo : lightLogo).getImage());
    }
}