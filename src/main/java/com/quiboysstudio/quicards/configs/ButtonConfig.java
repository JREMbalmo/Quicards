package com.quiboysstudio.quicards.configs;

import com.quiboysstudio.quicards.states.State;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ButtonConfig {
    
    static int roundness = 80;
    //custom button subclass for rounded corners & custom hover states
    private static class RoundedButton extends JButton {
        boolean hovering = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), FrameConfig.scale(State.frame, roundness), FrameConfig.scale(State.frame, roundness));
            
            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(hovering ? Color.WHITE : Color.BLACK);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, FrameConfig.scale(State.frame, roundness), FrameConfig.scale(State.frame, roundness));

            g2.dispose();
        }
    }

    //state changer button
    public static JButton createStateChangerButton(String text, Font font, int width, Color color, State nextState) {
        return createCustomButton(text, font, width, color, () -> {
            State.currentState.exit();
            State.currentState = nextState;
        });
    }

    //general button
    public static JButton createCustomButton(String text, Font font, int width, Color color, Runnable action) {
        RoundedButton button = new RoundedButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setPreferredSize(FrameConfig.scale(State.frame,
                width,
                78));
        button.setFont(font);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.hovering = true;
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.hovering = false;
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.getBackground().getRGB() == Color.BLACK.getRGB()) {
                    button.setForeground(Color.BLACK);
                } else {
                    button.setForeground(FrameConfig.WHITE);
                }
                button.setBackground(FrameConfig.getAltColor(color));
                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
                button.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.run(); // ✅ Execute your custom method
                }
                button.repaint();
            }
        });

        return button;
    }
}
