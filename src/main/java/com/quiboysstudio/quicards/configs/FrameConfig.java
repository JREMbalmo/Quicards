package com.quiboysstudio.quicards.configs;

import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;

public class FrameConfig extends JFrame{
    //colors
    public static Color BLACK = new Color(0,0,0);
    public static Color BLUE = new Color(22,33,61);
    public static Color ORANGE = new Color(252,163,17);
    public static Color GRAY = new Color(229,229,229);
    public static Color WHITE = new Color(255,255,255);
    
    public static Color DARK_BLUE = new Color(10,17,31);
    public static Color DARK_ORANGE = new Color(242,150,2);
    public static Color DARK_GRAY = new Color(214,214,214);
    public static Color DARK_WHITE = new Color(235,235,235);
    
    //transparent colors
    public static Color TRANSPARENT_BLACK = new Color(0,0,0);
    public static Color TRANSPARENT_BLUE = new Color(22,33,61);
    public static Color TRANSPARENT_ORANGE = new Color(252,163,17);
    public static Color TRANSPARENT_GRAY = new Color(229,229,229);
    public static Color TRANSPARENT_WHITE = new Color(255,255,255);
    
    public static Color TRANSPARENT_DARK_BLUE = new Color(10,17,31);
    public static Color TRANSPARENT_DARK_ORANGE = new Color(242,150,2);
    public static Color TRANSPARENT_DARK_GRAY = new Color(214,214,214);
    public static Color TRANSPARENT_DARK_WHITE = new Color(235,235,235);
    
    //objects
    public static JFrame frame;
    
    //init frame
    public static JFrame initFrame() {
        //setup frame
        frame = new JFrame();
        frame.setSize(1920,1080); //standard 1080p
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setTitle("QuiCards");
        frame.getContentPane().setBackground(BLUE);
        
        frame.setVisible(true);
        
        return frame;
    }
    
    public static JButton createStateChangerButton(State currentState, State nextState, String text) {
        JButton button = new JButton(text);
        
        return button;
    }
}
