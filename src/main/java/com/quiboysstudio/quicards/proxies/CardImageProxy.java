package com.quiboysstudio.quicards.proxies;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 * Implements the Proxy Design Pattern (a Virtual Proxy).
 * This class acts as a placeholder 'Icon' that loads the real
 * card image in a background thread to avoid freezing the UI.
 */
public class CardImageProxy implements Icon {
    private final String imagePath;
    private final int width;
    private final int height;
    
    // The "real" icon that this proxy will hold
    private ImageIcon realIcon; 
    
    // State flag to prevent multiple load attempts
    private boolean isLoading = false;
    
    // Store components that need to be repainted
    private List<Component> components = new ArrayList<>();
    
    /**
     * Creates a proxy for a card image.
     * @param imagePath The file path to the real image.
     * @param width The target display width.
     * @param height The target display height.
     */
    public CardImageProxy(String imagePath, int width, int height) {
        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.realIcon = null;
    }
    
    @Override
    public int getIconWidth() {
        return width;
    }
    
    @Override
    public int getIconHeight() {
        return height;
    }
    
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (realIcon != null) {
            // If the real icon is loaded, just paint it.
            realIcon.paintIcon(c, g, x, y);
        } else {
            // If not loaded, paint a placeholder.
            g.setColor(new Color(100, 100, 100)); // Dark gray placeholder
            g.fillRect(x, y, width, height);
            
            // Track this component for repainting
            if (!components.contains(c)) {
                components.add(c);
            }
            
            // And start loading the real image (if not already loading).
            if (!isLoading) {
                isLoading = true;
                
                // Load the image in a background thread
                SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        // This is the "expensive" operation
                        ImageIcon icon = new ImageIcon(imagePath);
                        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                        return new ImageIcon(image);
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            // Get the loaded icon and cache it
                            realIcon = get();
                            System.out.println("✓ Loaded: " + imagePath);
                        } catch (Exception e) {
                            System.err.println("✗ Failed to load: " + imagePath);
                            e.printStackTrace();
                            // Create an error placeholder
                            realIcon = createErrorIcon();
                        }
                        
                        // Repaint all components that used this icon
                        for (Component comp : components) {
                            comp.repaint();
                        }
                    }
                };
                worker.execute(); // Start the worker thread
            }
        }
    }
    //debugging
    private ImageIcon createErrorIcon() {
        java.awt.image.BufferedImage errorImg = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics g = errorImg.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.drawString("ERROR", 10, height/2);
        g.dispose();
        return new ImageIcon(errorImg);
    }
}
