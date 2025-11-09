package com.quiboysstudio.quicards.states.matchmaking;

import com.quiboysstudio.quicards.proxies.CardImageProxy;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import javax.swing.Icon;

//Composite Pattern
class SingleCard implements CardComponent {
    private final String fileName;
    private final CardType cardType;
    private Icon cardIcon;
    
    public enum CardType {
        CARD_BACK,
        CARD_FACE
    }
    
    public SingleCard(String fileName, CardType cardType) {
        this.fileName = fileName;
        this.cardType = cardType;
    }
    
    @Override
    public void render(Graphics2D g2d, int x, int y, int width, int height) {
        if (cardType == CardType.CARD_BACK) {
            renderCardBack(g2d, x, y, width, height);
        } else {
            renderCardFace(g2d, x, y, width, height);
        }
    }
    
    private void renderCardBack(Graphics2D g2d, int x, int y, int width, int height) {
        // Shadow
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRoundRect(x + 2, y + 2, width, height, 8, 8);
        
        // Card back
        g2d.setColor(new Color(40, 40, 60));
        g2d.fillRoundRect(x, y, width, height, 8, 8);
        
        // Card back design
        g2d.setColor(new Color(80, 80, 120));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 5, 5);
        
        // Inner decoration
        g2d.setColor(new Color(100, 100, 150));
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int decorSize = 20;
        g2d.fillOval(centerX - decorSize/2, centerY - decorSize/2, decorSize, decorSize);
        
        // Add some card detail lines
        g2d.setColor(new Color(70, 70, 100));
        g2d.drawLine(x + 10, centerY, x + width - 10, centerY);
    }
    
    private void renderCardFace(Graphics2D g2d, int x, int y, int width, int height) {
        if (cardIcon == null && fileName != null) {
            File file = new File("resources/cards/Fantasy Card Pack/" + fileName);
            if (file.exists()) {
                cardIcon = new CardImageProxy(file.getAbsolutePath(), width, height);
            }
        }
        
        if (cardIcon != null) {
            g2d.translate(x, y);
            cardIcon.paintIcon(null, g2d, 0, 0);
            g2d.translate(-x, -y);
        } else {
            // Fallback rendering
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRoundRect(x, y, width, height, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Missing", x + 10, y + 20);
        }
    }
    
    @Override
    public int getDepth() {
        return 1;
    }
    
    @Override
    public String getDisplayName() {
        return fileName != null ? fileName : "Card Back";
    }
    
    public String getFileName() {
        return fileName;
    }
    
}
