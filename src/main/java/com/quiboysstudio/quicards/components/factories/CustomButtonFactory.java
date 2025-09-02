package com.quiboysstudio.quicards.components.factories;

import com.quiboysstudio.quicards.components.CustomButton;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class CustomButtonFactory {
        //state changer button
    public static JButton createStateChangerButton(String text, Font font, int width, State nextState) {
        return createCustomButton(text, font, width, () -> {
            State.currentState.exit(nextState);
        });
    }

    //general button
    public static JButton createCustomButton(String text, Font font, int width, Runnable action) {
        CustomButton button = new CustomButton(text);
        button.setPreferredSize(FrameUtil.scale(State.frame, width, 78));
        button.setFont(font);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setHovering(true);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setHovering(false);
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setPressed(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setPressed(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (action != null) {
                    action.run();
                }
            }
        });

        return button;
    }
}
