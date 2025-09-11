package com.quiboysstudio.quicards.components.factories;

//imports
import com.quiboysstudio.quicards.components.CustomButton;
import com.quiboysstudio.quicards.components.CustomLabel;
import com.quiboysstudio.quicards.components.CustomPanel;
import com.quiboysstudio.quicards.components.CustomPasswordField;
import com.quiboysstudio.quicards.components.CustomTextField;
import com.quiboysstudio.quicards.components.CustomTextLabel;
import com.quiboysstudio.quicards.components.MenuPanel;
import com.quiboysstudio.quicards.states.State;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
        return new CustomButton(defaultText, altText, font, width, action);
    }

    //general button
    public static JButton createCustomButton(String text, Font font, int width, Runnable action) {
        return new CustomButton(text, font, width, action);
    }
    
    //LABEL
    
    public static JLabel createRoundedLabel(String text, int width, int height,
            Color borderColor, Font font) {
        return new CustomLabel(text, width, height, borderColor, font);
    }
    
    public static JLabel createRoundedLabel(String text, int width, int height,
            Color borderColor, int roundness, Font font, Color fontColor) {
        return new CustomLabel(text, width, height,
            borderColor, roundness, font, fontColor);
    }
    
    public static JLabel createTextLabel(String text, Font font) {
        return new CustomTextLabel(text, font);
    }
    
    //PANEL
    
    public static JPanel createRoundedPanel(int width, int height, Color borderColor) {
        return new CustomPanel(width, height, borderColor);
    }
    
    //MENU PANEL
    
    public static MenuPanel createMenuPanel(int width, int height) {
        return new MenuPanel(width, height);
    }
    
    //TEXT FIELD
    
    public static JTextField createRoundedTextField(int width, int height,
            Color backgroundColor, Color borderColor, Font font) {
        return new CustomTextField(width, height,
            backgroundColor, borderColor, font);
    }
    
    //PASSWORD FIELD
    
    public static JPasswordField createRoundedPasswordField(int width, int height,
            Color backgroundColor, Color borderColor, Font font) {
        return new CustomPasswordField(width, height,
            backgroundColor, borderColor, font);
    }
}