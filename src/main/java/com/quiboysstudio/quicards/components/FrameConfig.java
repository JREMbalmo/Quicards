package com.quiboysstudio.quicards.components;

import com.quiboysstudio.quicards.states.State;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.io.File;
import java.awt.Font;
import java.awt.Image;
import java.util.Random;
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
    
    public static final Color  DARK_BLACK = new Color(51,51,51);
    public static final Color  DARK_BLUE = new Color(10,17,31);
    public static final Color  DARK_ORANGE = new Color(242,150,2);
    public static final Color  DARK_GRAY = new Color(214,214,214);
    public static final Color  DARK_WHITE = new Color(235,235,235);
    
    //transparent colors
    public static final Color  TRANSPARENT_BLACK = new Color(0,0,0,50);
    public static final Color  TRANSPARENT_BLUE = new Color(22,33,61,50);
    public static final Color  TRANSPARENT_ORANGE = new Color(252,163,17,50);
    public static final Color  TRANSPARENT_GRAY = new Color(229,229,229,50);
    public static final Color  TRANSPARENT_WHITE = new Color(255,255,255,50);
    
    public static final Color  TRANSPARENT_DARK_BLACK = new Color(51,51,51,50);
    public static final Color  TRANSPARENT_DARK_BLUE = new Color(10,17,31,50);
    public static final Color  TRANSPARENT_DARK_ORANGE = new Color(242,150,2,50);
    public static final Color  TRANSPARENT_DARK_GRAY = new Color(214,214,214,50);
    public static final Color  TRANSPARENT_DARK_WHITE = new Color(235,235,235,50);
    
    //fonts
    public static Font SATOSHI;
    public static Font SATOSHI_BOLD;
    public static Font SATOSHI_ITALIC;
    
    //background
    public static JPanel backgroundPanel;
    public static JLabel backgroundLabel;
    
    //frame configs
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;
    protected static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    protected static final GraphicsDevice gd = ge.getDefaultScreenDevice();
}