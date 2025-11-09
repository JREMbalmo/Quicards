package com.quiboysstudio.quicards.states.matchmaking;

import java.awt.Graphics2D;

/**
 *
 * @author Andrew
 */
interface CardComponent {
    void render(Graphics2D g2d, int x, int y, int width, int height);
    int getDepth();
    String getDisplayName();
}

