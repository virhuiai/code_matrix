package com.jgoodies.animation.swing.components;

import com.jgoodies.animation.renderer.FanRenderer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/FanComponent.class */
public final class FanComponent extends JComponent {
    private final FanRenderer renderer;

    public FanComponent(int triangleCount, Color baseColor) {
        this.renderer = new FanRenderer(triangleCount, baseColor);
    }

    public Point2D getOrigin() {
        return this.renderer.getOrigin();
    }

    public double getRotation() {
        return this.renderer.getRotation();
    }

    public void setOrigin(Point2D origin) {
        this.renderer.setOrigin(origin);
        repaint();
    }

    public void setRotation(double rotation) {
        this.renderer.setRotation(rotation);
        repaint();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;
        int w = (getWidth() - x) - insets.right;
        int h = (getHeight() - y) - insets.bottom;
        g2.translate(x, y);
        this.renderer.render(g2, w, h);
        g2.translate(-x, -y);
    }
}
