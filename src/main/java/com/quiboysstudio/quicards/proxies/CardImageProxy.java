package com.quiboysstudio.quicards.proxies;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
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
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Optional: Create an "error" icon
                        }
                        
                        // Repaint the component (JLabel) to show the new, real icon
                        c.repaint();
                    }
                };
                worker.execute(); // Start the worker thread
            }
        }
    }
}