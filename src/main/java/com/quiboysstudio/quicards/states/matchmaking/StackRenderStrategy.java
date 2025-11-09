package com.quiboysstudio.quicards.states.matchmaking;

import java.awt.Graphics2D;
import java.util.List;
//Composite Pattern
interface StackRenderStrategy {
    void render(Graphics2D g2d, List<CardComponent> cards, int x, int y, int width, int height);
}
