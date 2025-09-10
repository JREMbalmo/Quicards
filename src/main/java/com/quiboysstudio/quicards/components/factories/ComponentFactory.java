package com.quiboysstudio.quicards.components.factories;

//imports
import com.quiboysstudio.quicards.components.CustomButton;
import com.quiboysstudio.quicards.components.CustomLabel;
import com.quiboysstudio.quicards.components.CustomPanel;
import com.quiboysstudio.quicards.components.CustomPasswordField;
import com.quiboysstudio.quicards.components.CustomTextField;
import com.quiboysstudio.quicards.components.CustomTextLabel;
import com.quiboysstudio.quicards.components.MenuPanel;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ComponentFactory {
    
    //BUTTON
    
    //state changer button
    public static JButton createStateChangerButton(String text, Font font, int width, State nextState) {
        return createCustomButton(text, font, width, () -> {
            State.currentState.exit(nextState);
        });
    }
    
    //toggle button
    public static JButton createToggleButton(String defaultText, String altText, Font font, int width, Runnable action) {
        CustomButton button = new CustomButton(defaultText);
        button.setPreferredSize(FrameUtil.scale(State.frame, width, 78));
        button.setFont(font);

        final boolean[] toggled = {false};

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
                toggled[0] = !toggled[0];
                button.setText(toggled[0] ? altText : defaultText);

                if (action != null) {
                    action.run();
                }
            }
        });

        return button;
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
    
    //LABEL
    
    public static JLabel createRoundedLabel(String text, int width, int height,
            Color borderColor, Font font) {
        
        CustomLabel label = new CustomLabel(text, borderColor);
        label.setFont(font);
        label.setPreferredSize(FrameUtil.scale(State.frame, width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        return label;
    }
    
    public static JLabel createRoundedLabel(String text, int width, int height,
            Color borderColor, int roundness, Font font, Color fontColor) {
        
        CustomLabel label = new CustomLabel(text, borderColor, roundness);
        label.setFont(font);
        label.setForeground(fontColor);
        label.setPreferredSize(FrameUtil.scale(State.frame, width, height));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);

        return label;
    }
    
    public static JLabel createTextLabel(String text, Font font) {
        return new CustomTextLabel(text, font);
    }
    
    //PANEL
    
    public static JPanel createRoundedPanel(int width, int height, Color borderColor) {
        CustomPanel panel = new CustomPanel(borderColor);
        panel.setPreferredSize(FrameUtil.scale(State.frame, width, height));
        return panel;
    }
    
    //TEXT FIELD
    
    public static JTextField createRoundedTextField(int width, int height,
            Color backgroundColor, Color borderColor, Font font) {

        CustomTextField field = new CustomTextField(borderColor);
        field.setBackground(backgroundColor);
        field.setFont(font);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setPreferredSize(FrameUtil.scale(State.frame, width, height));

        return field;
    }
    
    //PASSWORD FIELD
    
    public static JPasswordField createRoundedPasswordField(int width, int height,
            Color backgroundColor, Color borderColor, Font font) {

        CustomPasswordField field = new CustomPasswordField(borderColor);
        field.setBackground(backgroundColor);
        field.setFont(font);
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setPreferredSize(FrameUtil.scale(State.frame, width, height));

        return field;
    }
    
    //MENU PANEL
    
    public static MenuPanel createMenuPanel(int width, int height) {
        return new MenuPanel(width, height);
    }
}