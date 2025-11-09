package com.quiboysstudio.quicards.states.matchmaking;
import com.quiboysstudio.quicards.proxies.CardImageProxy;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles animated card drawing from deck to hand.
 * Manages multiple simultaneous card animations with smooth easing and visual effects.
 */
public class CardAnimationSystem {
    
    private JPanel animationLayer;
    private List<AnimatedCard> activeAnimations = new ArrayList<>();
    private Timer animationTimer;
    
    /**
     * Creates a new animation system.
     * @param animationLayer The panel where animations will be painted
     */
    public CardAnimationSystem(JPanel animationLayer) {
        this.animationLayer = animationLayer;
        
        // Animation timer runs at ~60 FPS (every 16ms)
        animationTimer = new Timer(16, e -> {
            boolean hasActiveAnimations = false;
            
            // Update all active animations
            for (AnimatedCard anim : activeAnimations) {
                if (anim.update()) {
                    hasActiveAnimations = true;
                }
            }
            
            // Remove completed animations
            activeAnimations.removeIf(anim -> anim.isComplete());
            
            // Stop timer if no animations are running
            if (!hasActiveAnimations && activeAnimations.isEmpty()) {
                animationTimer.stop();
            }
            
            animationLayer.repaint();
        });
    }
    
    /**
     * Animates a card from deck position to hand position.
     * @param cardName The card's filename
     * @param startX Starting X position (deck)
     * @param startY Starting Y position (deck)
     * @param endX Ending X position (hand)
     * @param endY Ending Y position (hand)
     * @param delay Delay before starting animation (ms)
     * @param onComplete Callback when animation completes
     */
    public void animateCardDraw(String cardName, int startX, int startY, 
                                int endX, int endY, int delay, Runnable onComplete) {
        
        AnimatedCard animCard = new AnimatedCard(
            cardName, startX, startY, endX, endY, delay, onComplete
        );
        
        activeAnimations.add(animCard);
        
        if (!animationTimer.isRunning()) {
            animationTimer.start();
        }
    }
    
    /**
     * Paints all active animations.
     * Call this from your panel's paintComponent method.
     */
    public void paintAnimations(Graphics2D g2) {
        for (AnimatedCard anim : activeAnimations) {
            anim.paint(g2);
        }
    }
    
    /**
     * Inner class representing a single card being animated.
     */
    private static class AnimatedCard {
        private String cardName;
        private int startX, startY;
        private int endX, endY;
        private float currentX, currentY;
        private int delayRemaining;
        private float progress = 0f;
        private boolean complete = false;
        private Runnable onComplete;
        private Icon cardIcon;
        private float rotation = 0f;
        private float scale = 0.5f; // Start smaller
        
        private static final int CARD_WIDTH = 120;
        private static final int CARD_HEIGHT = 160;
        private static final float ANIMATION_SPEED = 0.03f; // Progress per frame
        
        public AnimatedCard(String cardName, int startX, int startY, 
                           int endX, int endY, int delay, Runnable onComplete) {
            this.cardName = cardName;
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.currentX = startX;
            this.currentY = startY;
            this.delayRemaining = delay;
            this.onComplete = onComplete;
            
            // Load card icon using proxy for lazy loading
            String path = "resources/cards/Fantasy Card Pack/" + cardName;
            this.cardIcon = new CardImageProxy(path, CARD_WIDTH, CARD_HEIGHT);
        }
        
        /**
         * Updates animation state. Returns true if still animating.
         */
        public boolean update() {
            if (complete) return false;
            
            // Handle initial delay
            if (delayRemaining > 0) {
                delayRemaining -= 16; // Subtract frame time (16ms)
                return true;
            }
            
            // Update progress
            progress += ANIMATION_SPEED;
            
            if (progress >= 1.0f) {
                progress = 1.0f;
                complete = true;
                
                // Call completion callback
                if (onComplete != null) {
                    SwingUtilities.invokeLater(onComplete);
                }
                return false;
            }
            
            // Easing function (ease-out cubic for smooth deceleration)
            float eased = 1f - (float)Math.pow(1 - progress, 3);
            
            // Update position with easing
            currentX = startX + (endX - startX) * eased;
            currentY = startY + (endY - startY) * eased;
            
            // Add rotation for visual effect (sine wave)
            rotation = (float)Math.sin(progress * Math.PI) * 15f; // Max ±15 degrees
            
            // Scale up as it moves (starts at 50%, ends at 100%)
            scale = 0.5f + (0.5f * eased);
            
            return true;
        }
        
        /**
         * Paints this animated card.
         */
        public void paint(Graphics2D g2) {
            if (complete || delayRemaining > 0) return;
            
            g2 = (Graphics2D) g2.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            // Apply transformations (translate, rotate, scale)
            AffineTransform transform = new AffineTransform();
            transform.translate(currentX, currentY);
            transform.rotate(Math.toRadians(rotation), CARD_WIDTH / 2, CARD_HEIGHT / 2);
            transform.scale(scale, scale);
            
            g2.setTransform(transform);
            
            // Draw shadow for depth
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRoundRect(4, 4, CARD_WIDTH, CARD_HEIGHT, 10, 10);
            
            // Draw card back initially, then flip to front
            if (progress < 0.3f) {
                // Draw card back (looks like deck card)
                g2.setColor(new Color(40, 40, 60));
                g2.fillRoundRect(0, 0, CARD_WIDTH, CARD_HEIGHT, 10, 10);
                
                g2.setColor(new Color(80, 80, 120));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(5, 5, CARD_WIDTH - 10, CARD_HEIGHT - 10, 8, 8);
                
                // Inner decoration
                g2.setColor(new Color(100, 100, 150));
                int centerX = CARD_WIDTH / 2;
                int centerY = CARD_HEIGHT / 2;
                int decorSize = 30;
                g2.fillOval(centerX - decorSize/2, centerY - decorSize/2, decorSize, decorSize);
            } else {
                // Draw actual card face
                if (cardIcon != null) {
                    cardIcon.paintIcon(null, g2, 0, 0);
                } else {
                    // Fallback if icon not loaded yet
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRoundRect(0, 0, CARD_WIDTH, CARD_HEIGHT, 10, 10);
                }
            }
            
            g2.dispose();
        }
        
        /**
         * Returns true if animation is complete.
         */
        public boolean isComplete() {
            return complete;
        }
    }
}
