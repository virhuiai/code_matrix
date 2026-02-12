package com.jgoodies.animation.swing.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/CircleComponent.class */
public final class CircleComponent extends JComponent {
    private Point center = new Point(0, 0);
    private int radius = 30;
    private Color color = Color.BLACK;

    public void setCenter(Point p) {
        this.center = p;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        setCenter(new Point(x + (w / 2), y + (h / 2)));
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int diameter = this.radius * 2;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(this.color);
        g2.setStroke(new BasicStroke(4.0f));
        g2.drawOval(this.center.x - this.radius, this.center.y - this.radius, diameter, diameter);
    }
}
