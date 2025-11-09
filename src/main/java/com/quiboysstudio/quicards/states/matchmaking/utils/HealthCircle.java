package com.quiboysstudio.quicards.states.matchmaking.utils;

import com.quiboysstudio.quicards.components.UIConfig;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class HealthCircle extends JLabel {
    private int health = 50;

    public HealthCircle() {
        setPreferredSize(new Dimension(80, 80));
        setFont(new Font("Arial", Font.BOLD, 28));
        setForeground(Color.WHITE);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        setText(String.valueOf(health));
        setOpaque(false);
    }

    public void setHealth(int newHealth) {
        this.health = newHealth;
        setText(String.valueOf(this.health));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = Math.min(getWidth(), getHeight());
        GradientPaint gp = new GradientPaint(0, 0, UIConfig.ORANGE, diameter, diameter, UIConfig.BLUE);
        g2d.setPaint(gp);
        g2d.fill(new Ellipse2D.Float(1, 1, diameter - 2, diameter - 2));

        super.paintComponent(g);
        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Ellipse2D.Float(1, 1, getWidth() - 2, getHeight() - 2));
        g2d.dispose();
    }
}
