package com.quiboysstudio.quicards.components.factories;

//imports
import com.quiboysstudio.quicards.components.CustomLabel;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import com.quiboysstudio.quicards.states.State;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class CustomLabelFactory {
    
    public static JLabel createRoundedLabel(String text, int width, int height,
            Color borderColor, Font font, Color fontColor) {
        
        CustomLabel label = new CustomLabel(text, borderColor);
        label.setFont(font);
        label.setForeground(fontColor);
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
}