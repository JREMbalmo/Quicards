package com.quiboysstudio.quicards.states.matchmaking;

import java.awt.Graphics2D;
import java.util.List;

//Composite Pattern
class SingleCardStrategy implements StackRenderStrategy {
    @Override
    public void render(Graphics2D g2d, List<CardComponent> cards, int x, int y, int width, int height) {
        if (!cards.isEmpty()) {
            CardComponent topCard = cards.get(cards.size() - 1);
            topCard.render(g2d, x, y, width, height);
        }
    }
