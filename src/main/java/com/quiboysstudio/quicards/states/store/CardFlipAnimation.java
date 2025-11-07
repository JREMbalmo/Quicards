/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quiboysstudio.quicards.states.store;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public class CardFlipAnimation {
    
    private static final int ANIMATION_DURATION = 1000; // Total duration in ms
    private static final int FRAME_DELAY = 16; // ~60 FPS
    
    public static void animateFlip(JLabel label, Icon backIcon, Icon frontIcon, Runnable onComplete) {
        // First do the shake animation, then the flip
        animateShake(label, () -> {
            // After shake completes, do the flip
            animateFlipOnly(label, backIcon, frontIcon, onComplete);
        });
    }
    

    private static void animateShake(JLabel label, Runnable onComplete) {
        final int SHAKE_DURATION = 400; // Duration in ms
        final int SHAKE_INTENSITY = 8; // Pixels to shake
        final int SHAKE_SPEED = 30; // ms per shake movement
        
        // Store original position
        final Point originalLocation = label.getLocation();
        
        Timer shakeTimer = new Timer(SHAKE_SPEED, null);
        final long startTime = System.currentTimeMillis();
        final java.util.Random random = new java.util.Random();
        
        shakeTimer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            
            if (elapsed >= SHAKE_DURATION) {
                // Shake complete - restore original position
                shakeTimer.stop();
                label.setLocation(originalLocation);
                label.repaint();
                
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }
            
            // Calculate shake intensity that decreases over time
            float progress = (float) elapsed / SHAKE_DURATION;
            int currentIntensity = (int) (SHAKE_INTENSITY * (1.0f - progress));
            
            // Random offset in both X and Y directions
            int offsetX = random.nextInt(currentIntensity * 2 + 1) - currentIntensity;
            int offsetY = random.nextInt(currentIntensity * 2 + 1) - currentIntensity;
            
            // Apply shake offset
            label.setLocation(
                originalLocation.x + offsetX,
                originalLocation.y + offsetY
            );
            label.repaint();
        });
        
        shakeTimer.start();
    }
    
    private static void animateFlipOnly(JLabel label, Icon backIcon, Icon frontIcon, Runnable onComplete) {
        // Create a timer for the animation
        Timer timer = new Timer(FRAME_DELAY, null);
        final long startTime = System.currentTimeMillis();
        
        timer.addActionListener(e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = Math.min(1.0f, (float) elapsed / ANIMATION_DURATION);
            
            if (progress >= 1.0f) {
                // Animation complete
                timer.stop();
                label.setIcon(frontIcon);
                label.repaint();
                if (onComplete != null) {
                    onComplete.run();
                }
                return;
            }
            
            // Calculate flip progress (0 to 1 for first half, 1 to 0 for second half)
            float flipProgress;
            Icon currentIcon;
            
            if (progress < 0.5f) {
                // First half: shrink the back image
                flipProgress = 1.0f - (progress * 2.0f);
                currentIcon = backIcon;
            } else {
                // Second half: grow the front image
                flipProgress = (progress - 0.5f) * 2.0f;
                currentIcon = frontIcon;
            }
            
            // Create scaled image
            int width = label.getWidth();
            int height = label.getHeight();
            int scaledWidth = Math.max(1, (int) (width * flipProgress));
            
            BufferedImage flippedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = flippedImage.createGraphics();
            
            // Enable anti-aliasing for smoother animation
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw the icon scaled
            int xOffset = (width - scaledWidth) / 2;
            currentIcon.paintIcon(label, g2d, xOffset, 0);
            
            // Apply horizontal scaling transform
            AffineTransform transform = new AffineTransform();
            transform.translate(xOffset, 0);
            transform.scale(flipProgress, 1.0);
            
            g2d.dispose();
            
            // Update label with flipped image
            label.setIcon(new ImageIcon(flippedImage));
            label.repaint();
        });
        
        timer.start();
    }
    

    public static void animateFlipSequence(JLabel[] labels, Icon backIcon, Icon[] frontIcons, 
                                          int staggerDelay, Runnable onAllComplete) {
        if (labels.length == 0) {
            if (onAllComplete != null) onAllComplete.run();
            return;
        }
        
        final int[] completedCount = {0};
        
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            
            // Schedule each card's flip with a stagger delay
            Timer delayTimer = new Timer(staggerDelay * i, e -> {
                animateFlip(labels[index], backIcon, frontIcons[index], () -> {
                    completedCount[0]++;
                    if (completedCount[0] == labels.length && onAllComplete != null) {
                        onAllComplete.run();
                    }
                });
                ((Timer) e.getSource()).stop();
            });
            delayTimer.setRepeats(false);
            delayTimer.start();
        }
    }
    

    public static ImageIcon createCardBackIcon(int width, int height) {
        try {
            // Load your card back image from resources
            String cardBackPath = "resources/cards/card_back.png"; // CHANGE THIS PATH
            java.io.File imgFile = new java.io.File(cardBackPath);
            
            if (imgFile.exists()) {
                BufferedImage originalImage = javax.imageio.ImageIO.read(imgFile);
                
                // Scale the image to fit the dimensions
                Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } else {
                System.err.println("Card back image not found at: " + cardBackPath);
                // Fall back to generated design
                return createDefaultCardBack(width, height);
            }
        } catch (Exception e) {
            System.err.println("Error loading card back image: " + e.getMessage());
            return createDefaultCardBack(width, height);
        }
    }
    
    /**
     * Creates an elaborate fantasy-themed card back design.
     */
    private static ImageIcon createDefaultCardBack(int width, int height) {
        BufferedImage backImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = backImage.createGraphics();
        
        // Enable anti-aliasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Background - Deep gradient from dark purple to navy
        GradientPaint bgGradient = new GradientPaint(
            0, 0, new Color(30, 10, 60),
            width, height, new Color(10, 20, 50)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRoundRect(0, 0, width, height, 20, 20);
        
        // Outer border - Triple layered
        g2d.setStroke(new BasicStroke(4));
        g2d.setColor(new Color(255, 215, 0)); // Gold outer
        g2d.drawRoundRect(6, 6, width - 12, height - 12, 16, 16);
        
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(new Color(139, 69, 19)); // Brown middle
        g2d.drawRoundRect(12, 12, width - 24, height - 24, 12, 12);
        
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(255, 223, 150)); // Light gold inner
        g2d.drawRoundRect(16, 16, width - 32, height - 32, 10, 10);
        
        // Central ornate design area
        int centerX = width / 2;
        int centerY = height / 2;
        
        // Radial gradient background for center
        RadialGradientPaint radialGradient = new RadialGradientPaint(
            centerX, centerY, Math.min(width, height) / 3f,
            new float[]{0.0f, 0.7f, 1.0f},
            new Color[]{
                new Color(80, 40, 120, 200),
                new Color(50, 20, 80, 150),
                new Color(30, 10, 60, 100)
            }
        );
        g2d.setPaint(radialGradient);
        g2d.fillOval(centerX - width/3, centerY - height/3, width*2/3, height*2/3);
        
        // Mystical circle pattern
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < 3; i++) {
            int radius = 30 + (i * 15);
            g2d.setColor(new Color(255, 215, 0, 150 - i * 30));
            g2d.drawOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        }
        
        // Ornate corner designs
        drawCornerOrnament(g2d, 30, 30, 1, width, height); // Top-left
        drawCornerOrnament(g2d, width - 30, 30, 2, width, height); // Top-right
        drawCornerOrnament(g2d, 30, height - 30, 3, width, height); // Bottom-left
        drawCornerOrnament(g2d, width - 30, height - 30, 4, width, height); // Bottom-right
        
        // Central star/compass design
        drawCentralStar(g2d, centerX, centerY, 40, 8);
        
        // Magical runes around the circle
        drawMagicalRunes(g2d, centerX, centerY, width, height);
        
        // Sparkle effects
        drawSparkles(g2d, width, height);
        
        // Inner decorative lines
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(255, 215, 0, 80));
        
        // Diagonal pattern
        for (int i = 0; i < 8; i++) {
            int offset = i * 30;
            g2d.drawLine(20, 20 + offset, 20 + offset, 20);
            g2d.drawLine(width - 20 - offset, height - 20, width - 20, height - 20 - offset);
        }
        
        g2d.dispose();
        return new ImageIcon(backImage);
    }
    
    /**
     * Draws ornate corner decorations.
     */
    private static void drawCornerOrnament(Graphics2D g2d, int x, int y, int corner, int width, int height) {
        g2d.setColor(new Color(255, 215, 0, 200));
        g2d.setStroke(new BasicStroke(2));
        
        // Calculate rotation based on corner
        double angle = Math.toRadians((corner - 1) * 90);
        AffineTransform old = g2d.getTransform();
        g2d.rotate(angle, x, y);
        
        // Draw ornate flourish
        int size = 20;
        g2d.drawArc(x - size, y - size, size * 2, size * 2, 0, 90);
        g2d.drawLine(x, y, x + size/2, y - size/2);
        g2d.drawLine(x, y, x - size/2, y + size/2);
        
        // Small decorative circles
        g2d.fillOval(x - 3, y - size - 3, 6, 6);
        g2d.fillOval(x + size - 3, y - 3, 6, 6);
        
        g2d.setTransform(old);
    }
    
    /**
     * Draws a central star/compass design.
     */
    private static void drawCentralStar(Graphics2D g2d, int centerX, int centerY, int radius, int points) {
        g2d.setColor(new Color(255, 215, 0, 220));
        g2d.setStroke(new BasicStroke(2.5f));
        
        for (int i = 0; i < points; i++) {
            double angle = Math.toRadians(i * 360.0 / points - 90);
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            g2d.drawLine(centerX, centerY, x, y);
        }
        
        // Inner circle
        g2d.setColor(new Color(139, 69, 19));
        g2d.fillOval(centerX - 8, centerY - 8, 16, 16);
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawOval(centerX - 8, centerY - 8, 16, 16);
        
        // Outer ring
        g2d.drawOval(centerX - radius - 5, centerY - radius - 5, (radius + 5) * 2, (radius + 5) * 2);
    }

    private static void drawMagicalRunes(Graphics2D g2d, int centerX, int centerY, int width, int height) {
        int runeRadius = Math.min(width, height) / 3 + 20;
        int runeSize = 12;
        
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(i * 60 - 90);
            int x = centerX + (int) (runeRadius * Math.cos(angle));
            int y = centerY + (int) (runeRadius * Math.sin(angle));
            
            // Draw glow effect first (larger, more transparent)
            g2d.setColor(new Color(100, 200, 255, 80));
            g2d.setStroke(new BasicStroke(3));
            drawRuneShape(g2d, x, y, runeSize + 2, i);
            
            // Draw main rune (sharper, more visible)
            g2d.setColor(new Color(150, 220, 255, 220));
            g2d.setStroke(new BasicStroke(2));
            drawRuneShape(g2d, x, y, runeSize, i);
            
            // Draw bright core
            g2d.setColor(new Color(200, 240, 255, 255));
            g2d.setStroke(new BasicStroke(1));
            drawRuneShape(g2d, x, y, runeSize - 1, i);
        }
    }
    
    private static void drawRuneShape(Graphics2D g2d, int x, int y, int size, int type) {
        switch (type % 6) {
            case 0: // Vertical line with branches (Ansuz-like)
                g2d.drawLine(x, y - size, x, y + size);
                g2d.drawLine(x, y - size/2, x + size/2, y);
                g2d.drawLine(x, y, x + size/2, y + size/2);
                break;
                
            case 1: // Diamond with cross (Gebo-like)
                g2d.drawLine(x - size, y, x, y - size);
                g2d.drawLine(x, y - size, x + size, y);
                g2d.drawLine(x + size, y, x, y + size);
                g2d.drawLine(x, y + size, x - size, y);
                g2d.drawLine(x, y - size, x, y + size);
                g2d.drawLine(x - size, y, x + size, y);
                break;
                
            case 2: // P-shape (Wunjo-like)
                g2d.drawLine(x, y - size, x, y + size);
                g2d.drawArc(x - size/2, y - size, size, size, 270, 180);
                break;
                
            case 3: // Triangle with line (Teiwaz-like)
                g2d.drawLine(x, y - size, x, y + size);
                g2d.drawLine(x, y - size, x - size/2, y);
                g2d.drawLine(x, y - size, x + size/2, y);
                break;
                
            case 4: // Lightning bolt (Sowilo-like)
                g2d.drawLine(x - size/2, y - size, x + size/2, y - size/3);
                g2d.drawLine(x + size/2, y - size/3, x - size/2, y + size/3);
                g2d.drawLine(x - size/2, y + size/3, x + size/2, y + size);
                break;
                
            case 5: // Hourglass (Dagaz-like)
                g2d.drawLine(x - size, y - size, x + size, y - size);
                g2d.drawLine(x - size, y - size, x, y);
                g2d.drawLine(x + size, y - size, x, y);
                g2d.drawLine(x, y, x - size, y + size);
                g2d.drawLine(x, y, x + size, y + size);
                g2d.drawLine(x - size, y + size, x + size, y + size);
                break;
        }
    }

    private static void drawSparkles(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.setStroke(new BasicStroke(1.5f));
        
        // Random-like sparkle positions (deterministic for consistency)
        int[][] sparkles = {
            {width/4, height/5}, {width*3/4, height/6},
            {width/5, height*3/4}, {width*4/5, height*2/3},
            {width/2, height/8}, {width/3, height*5/6}
        };
        
        for (int[] sparkle : sparkles) {
            int x = sparkle[0];
            int y = sparkle[1];
            int size = 4;
            
            // Draw 4-pointed star
            g2d.drawLine(x - size, y, x + size, y);
            g2d.drawLine(x, y - size, x, y + size);
            g2d.drawLine(x - size/2, y - size/2, x + size/2, y + size/2);
            g2d.drawLine(x - size/2, y + size/2, x + size/2, y - size/2);
        }
    }
}