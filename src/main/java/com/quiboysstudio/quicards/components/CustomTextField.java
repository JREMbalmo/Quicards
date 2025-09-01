package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import javax.swing.*;
import java.awt.*;

public class CustomTextField extends JTextField {
    private int roundness = 50;
    private Color borderColor;

    public CustomTextField(Color borderColor) {
        this.borderColor = borderColor;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(FrameConfig.scale(State.frame, 5),
                FrameConfig.scale(State.frame, 10),
                FrameConfig.scale(State.frame, 5),
                FrameConfig.scale(State.frame, 10))); //padding
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1,
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
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1,
                FrameConfig.scale(State.frame, roundness),
                FrameConfig.scale(State.frame, roundness));

        g2.dispose();
    }
    
    public static JTextField createRoundedTextField(int width, int height,
            Color backgroundColor, Color borderColor, Font font) {

        CustomTextField field = new CustomTextField(borderColor);
        field.setBackground(backgroundColor);
        field.setFont(font);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setPreferredSize(FrameConfig.scale(State.frame, width, height));

        return field;
    }
}