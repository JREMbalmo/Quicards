package com.quiboysstudio.quicards.components;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FrameConfig extends JFrame{
    //main frame
    public static JFrame frame;
    
    //colors
    public static final Color  BLACK = new Color(0,0,0);
    public static final Color  BLUE = new Color(22,33,61);
    public static final Color  ORANGE = new Color(252,163,17);
    public static final Color  GRAY = new Color(229,229,229);
    public static final Color  WHITE = new Color(255,255,255);
    
    //themes
    public static final Color DARK_TOP = new Color(150, 150, 150, (int)(255 * 0.8));
    public static final Color DARK_BOTTOM = new Color(30, 30, 30, (int)(255 * 0.8));
    public static final Color LIGHT_TOP = new Color(240, 240, 240, (int) (255 * 0.8));
    public static final Color LIGHT_BOTTOM = new Color(120, 120, 120, (int) (255 * 0.8));
    
    public static final Color  DARK_BLACK = new Color(51,51,51);
    public static final Color  DARK_BLUE = new Color(10,17,31);
    public static final Color  DARK_ORANGE = new Color(242,150,2);
    public static final Color  DARK_GRAY = new Color(214,214,214);
    public static final Color  DARK_WHITE = new Color(235,235,235);
    
    //transparent colors
    public static final Color  TRANSPARENT_BLACK = new Color(0,0,0,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_BLUE = new Color(22,33,61,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_ORANGE = new Color(252,163,17,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_GRAY = new Color(229,229,229,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_WHITE = new Color(255,255,255,(int)(255 * 0.8));
    
    public static final Color  TRANSPARENT_DARK_BLACK = new Color(51,51,51,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_DARK_BLUE = new Color(10,17,31,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_DARK_ORANGE = new Color(242,150,2,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_DARK_GRAY = new Color(214,214,214,(int)(255 * 0.8));
    public static final Color  TRANSPARENT_DARK_WHITE = new Color(235,235,235,(int)(255 * 0.8));
    
    //header
    public static JPanel header;
    public static JLabel logoLabel;
    public static ImageIcon gameLogo;
    
    //fonts
    public static Font SATOSHI;
    public static Font SATOSHI_BOLD;
    public static Font SATOSHI_ITALIC;
    
    //background
    public static JPanel backgroundPanel;
    public static JLabel backgroundLabel;
    
    //size
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;
    
    //graphics stuff
    protected static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    protected static final GraphicsDevice gd = ge.getDefaultScreenDevice();
}