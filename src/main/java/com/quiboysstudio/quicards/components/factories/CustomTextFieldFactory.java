package com.quiboysstudio.quicards.components.factories;

//imports
import com.quiboysstudio.quicards.components.CustomTextField;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;

public class CustomTextFieldFactory {
    
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
}