package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.managers.ThemeManager;
import com.quiboysstudio.quicards.managers.listeners.ThemeChangeListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton implements ThemeChangeListener {

    //vairables
    static int roundness = 80;
    private boolean hovering = false;
    private boolean pressed = false;

    //colors
    private Color topColor;
    private Color bottomColor;
    private Color altTopColor;
    private Color altBottomColor;

    public CustomButton(String text, Font font, int width, Runnable action) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

        //apply current theme
        applyTheme(ThemeManager.getInstance().isDarkMode());

        ThemeManager.getInstance().addListener(this);
        
        setPreferredSize(FrameUtil.scale(State.frame, width, 78));
        setFont(font);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setHovering(true);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovering(false);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setPressed(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setPressed(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.run();
                }
            }
        });
    }
    
    public CustomButton(String defaultText, String altText, Font font, int width, Runnable action) {
        super(defaultText);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

        //apply current theme
        applyTheme(ThemeManager.getInstance().isDarkMode());

        ThemeManager.getInstance().addListener(this);
        
        setPreferredSize(FrameUtil.scale(State.frame, width, 78));
        setFont(font);

        final boolean[] toggled = {false};

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setHovering(true);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovering(false);
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setPressed(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setPressed(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                toggled[0] = !toggled[0];
                setText(toggled[0] ? altText : defaultText);

                if (action != null) {
                    action.run();
                }
            }
        });
    }

    @Override
    public void onThemeChanged(boolean darkMode) {
        applyTheme(darkMode);
    }

    private void applyTheme(boolean darkMode) {
        //pull base colors from FrameConfig
        if (darkMode) {
            topColor = FrameConfig.DARK_TOP;
            bottomColor = FrameConfig.DARK_BOTTOM;
        } else {
            topColor = FrameConfig.LIGHT_TOP;
            bottomColor = FrameConfig.LIGHT_BOTTOM;
        }

        //compute darker versions for pressed state
        altTopColor = FrameUtil.getAltColor(topColor);
        altBottomColor = FrameUtil.getAltColor(bottomColor);

        //update text color
        setForeground(darkMode ? FrameConfig.WHITE : FrameConfig.BLACK);

        repaint();
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        repaint();
    }

    public void setHovering(boolean status) {
        hovering = status;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = FrameUtil.scale(State.frame, roundness);
            int borderSize = FrameUtil.scale(State.frame, 3);

            int x = borderSize;
            int y = borderSize;
            int w = Math.max(0, getWidth() - (borderSize * 2 - 1));
            int h = Math.max(0, getHeight() - (borderSize * 2 - 1));

            //use alt colors if pressed, else normal theme colors
            Color top = pressed ? altTopColor : topColor;
            Color bottom = pressed ? altBottomColor : bottomColor;

            GradientPaint gradient = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
            g2.setPaint(gradient);
            g2.fillRoundRect(x, y, w, h, arc, arc);

            super.paintComponent(g2);
        } finally {
            g2.dispose();
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = FrameUtil.scale(State.frame, roundness);
            int borderSize = FrameUtil.scale(State.frame, 3);

            g2.setStroke(new BasicStroke(borderSize));
            g2.setColor(hovering ? Color.WHITE : Color.BLACK);

            g2.drawRoundRect(
                borderSize / 2,
                borderSize / 2,
                getWidth() - borderSize,
                getHeight() - borderSize,
                arc, arc
            );
        } finally {
            g2.dispose();
        }
    }
}