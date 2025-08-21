package com.quiboysstudio.quicards.configs;

import com.quiboysstudio.quicards.states.State;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
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
    
    //frame configs
    private static final int BASE_WIDTH = 1920;
    private static final int BASE_HEIGHT = 1080;
    private static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private static final GraphicsDevice gd = ge.getDefaultScreenDevice();
    
    //init frame
    public static JFrame initFrame() {
        //setup frame
        JFrame frame = new JFrame();
        frame.setSize(1920,1080); //standard 1080p
        //frame.setSize(1280,720);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setTitle("QuiCards");
        frame.getContentPane().setBackground(BLACK);
        
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
        if (color.equals(BLACK)) return GRAY;
        if (color.equals(BLUE)) return DARK_BLUE;
        if (color.equals(ORANGE)) return DARK_ORANGE;
        if (color.equals(GRAY)) return DARK_GRAY;
        if (color.equals(WHITE)) return DARK_WHITE;
        
        System.out.println("Color not found");
        return WHITE; //whtie default if not found
    }
    
    public static JButton createStateChangerButton(String text, int width, Color color, State nextState) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(WHITE);
        button.setBackground(color);
        button.setPreferredSize(FrameConfig.scale(State.frame, scale(State.frame, width), scale(State.frame, 78)));
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(BLACK, scale(State.frame, 3)));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(WHITE, scale(State.frame, 3)));
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(BLACK, scale(State.frame, 3)));
                button.revalidate();
                button.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(getAltColor(color));

                if (button.getBackground().equals(BLACK)) {
                    button.setForeground(BLACK);
                } else {
                    button.setForeground(WHITE);
                }

                button.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(color);
                button.setForeground(WHITE);
                button.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                State.currentState.exit();
                State.currentState = nextState;
                button.repaint();
            }
        });

        return button;
    }

}