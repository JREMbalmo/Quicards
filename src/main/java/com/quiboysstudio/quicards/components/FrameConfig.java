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
    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;
    private static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final GraphicsDevice gd = ge.getDefaultScreenDevice();
    
    //init frame
    public static JFrame initFrame() {
        //setup frame
        frame = new JFrame();
        frame.setSize(1920,1080); //standard 1080p
        //frame.setSize(1280,720);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new CardLayout());
        frame.setLocationRelativeTo(null);
        frame.setTitle("QuiCards");
        frame.getContentPane().setBackground(BLACK);
        frame.setIconImage(new ImageIcon("resources//logos//game_logo_appicon.png").getImage());
        frame.setContentPane(new JPanel(new CardLayout()));
        
        
        //setup fonts
        try {
        SATOSHI = Font.createFont(Font.TRUETYPE_FONT, new File("resources//fonts//Satoshi-Regular.ttf")).
                deriveFont((float) FrameConfig.scale(frame, 20));
        SATOSHI_BOLD = Font.createFont(Font.TRUETYPE_FONT, new File("resources//fonts//Satoshi-Bold.ttf")).
                deriveFont((float) FrameConfig.scale(frame, 20));
        SATOSHI_ITALIC = Font.createFont(Font.TRUETYPE_FONT, new File("resources//fonts//Satoshi-Italic.ttf")).
                deriveFont((float) FrameConfig.scale(frame, 20));
        ge.registerFont(SATOSHI);
        ge.registerFont(SATOSHI_BOLD);
        ge.registerFont(SATOSHI_ITALIC);
        } catch (Exception e) {
            System.out.println("Error loading fonts: " + e);
        }
        
        //create background panel
        backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(false);
        backgroundLabel = new JLabel();
        backgroundLabel.setPreferredSize(scale(frame,1920,1080));
        backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        backgroundLabel.setOpaque(false);
        backgroundPanel.add(backgroundLabel);
        generateBackground();
        
        //attempt to fullscreen
//        if (gd.isFullScreenSupported()) {
//            //fullscreen if supported
//            gd.setFullScreenWindow(frame);
//        } else {
//            // fallback if fullscreen not supported
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            frame.setVisible(true);
//        }
        
        frame.setVisible(true);
        
        return frame;
    }
    
    //scales ui based on frame resolution
    public static Dimension scale(JFrame frame, int designWidth, int designHeight) {
        int currentWidth = frame.getWidth();
        int currentHeight = frame.getHeight();

        // scale factors for width & height
        double scaleX = (double) currentWidth / BASE_WIDTH;
        double scaleY = (double) currentHeight / BASE_HEIGHT;

        // pick the smaller factor to keep aspect ratio (letterbox instead of stretch)
        double scale = Math.min(scaleX, scaleY);

        int newWidth = (int) (designWidth * scale);
        int newHeight = (int) (designHeight * scale);

        return new Dimension(newWidth, newHeight);
    }
    
    public static int scale(JFrame frame, int designWidth) {
        int currentWidth = frame.getWidth();
        int currentHeight = frame.getHeight();

        double scaleX = (double) currentWidth / BASE_WIDTH;
        double scaleY = (double) currentHeight / BASE_HEIGHT;

        double scale = Math.min(scaleX, scaleY);

        return (int) (designWidth * scale);
    }
    
    public static Color getAltColor(Color color) {
        int r = Math.max(0, color.getRed() - 30);
        int g = Math.max(0, color.getGreen() - 30);
        int b = Math.max(0, color.getBlue() - 30);
        return new Color(r, g, b, color.getAlpha());
    }
    
    public static void generateBackground() {
        Random random = new Random();
        
        int num = random.nextInt(1) + 1;
        
        backgroundLabel.setIcon(new ImageIcon(new ImageIcon("resources//backgrounds//bg" + num + ".png").getImage().
                getScaledInstance(scale(frame, 1920), scale(frame, 1080), Image.SCALE_SMOOTH)));
    }
    
    public static Container getCardPanel() {
        Container cardPanel = frame.getContentPane();
        cardPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        cardPanel.setLayout(State.cardLayout);
        return cardPanel;
    }
}