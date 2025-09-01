package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton{
    //custom button subclass for rounded corners and custom hover states
    static int roundness = 80;
    boolean hovering = false;
    private boolean pressed = false;

    // Default gradient colors
    private static final Color TOP_COLOR = new Color(150, 150, 150, (int)(255 * 0.8));
    private static final Color BOTTOM_COLOR = new Color(30, 30, 30, (int)(255 * 0.8));

    // Alt gradient colors (darker)
    private static final Color ALT_TOP_COLOR = FrameUtil.getAltColor(new Color(150, 150, 150, (int)(255 * 0.8)));
    private static final Color ALT_BOTTOM_COLOR = FrameUtil.getAltColor(new Color(30, 30, 30, (int)(255 * 0.8)));

    public CustomButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);

        setForeground(Color.WHITE);
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
        repaint();
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

        //Decide gradient based on pressed state
        Color top = pressed ? ALT_TOP_COLOR : TOP_COLOR;
        Color bottom = pressed ? ALT_BOTTOM_COLOR : BOTTOM_COLOR;

        GradientPaint gradient = new GradientPaint(0, 0, top, 0, getHeight(), bottom);
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
        g2.setColor(hovering ? Color.WHITE : Color.BLACK);

        g2.drawRoundRect(
            borderSize / 2,
            borderSize / 2,
            getWidth() - borderSize,
            getHeight() - borderSize,
            arc, arc
        );

        g2.dispose();
    }
    
    public void setHovering(boolean status) {
        hovering = status;
    }
}