package com.quiboysstudio.quicards.states.matchmaking;

import java.awt.Graphics2D;
import java.util.List;

class OffsetStackStrategy implements StackRenderStrategy {
    private final int offsetX;
    private final int offsetY;
    private final int maxVisible;
    
    public OffsetStackStrategy(int offsetX, int offsetY, int maxVisible) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.maxVisible = maxVisible;
    }
    
    @Override
    public void render(Graphics2D g2d, List<CardComponent> cards, int x, int y, int width, int height) {
        int visibleCards = Math.min(cards.size(), maxVisible);
        int startIndex = Math.max(0, cards.size() - visibleCards);
        
        for (int i = startIndex; i < cards.size(); i++) {
            int localIndex = i - startIndex;
            int cardX = x + localIndex * offsetX;
            int cardY = y - localIndex * offsetY;
            
            cards.get(i).render(g2d, cardX, cardY, width, height);
        }
    }
}
