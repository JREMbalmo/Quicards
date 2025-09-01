package com.quiboysstudio.quicards.components.factories;

import com.quiboysstudio.quicards.components.CustomPanel;
import com.quiboysstudio.quicards.states.State;
import com.quiboysstudio.quicards.components.utilities.FrameUtil;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;

public class CustomPanelFactory {
    
    public static JPanel createRoundedPanel(int width, int height, Color borderColor) {
        CustomPanel panel = new CustomPanel(borderColor);
        panel.setPreferredSize(FrameUtil.scale(State.frame, width, height));
        panel.setLayout(new BorderLayout());
        return panel;
    }
}