package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import javax.swing.*;
import java.awt.*;

public class CustomTextField extends JTextField {
    private int roundness = 50;
    private Color borderColor;

    public CustomTextField(Color borderColor) {
        setOpaque(false);
        setBackground(Color.WHITE); //input area always white
        setForeground(Color.BLACK); //user text stays readable
        this.borderColor = borderColor;
        setBorder(BorderFactory.createEmptyBorder(
                FrameUtil.scale(State.frame, 5),
                FrameUtil.scale(State.frame, 10),
                FrameUtil.scale(State.frame, 5),
                FrameUtil.scale(State.frame, 10)
        )); //padding
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

        g2.setColor(getBackground());
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