package com.jgoodies.animation.swing.components;

import com.jgoodies.animation.AnimationFunction;
import com.jgoodies.animation.renderer.GlyphRenderer;
import com.jgoodies.animation.renderer.HeightMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/GlyphLabel.class */
public final class GlyphLabel extends JComponent {
    public static final String PROPERTY_HEIGHT_MODE = "heightMode";
    public static final String PROPERTY_TEXT = "text";
    public static final String PROPERTY_TIME = "time";
    private final GlyphRenderer renderer;

    public GlyphLabel(String text, long duration, long glyphDelay) {
        this(text, duration, glyphDelay, Color.DARK_GRAY);
    }

    public GlyphLabel(String text, long duration, long glyphDelay, Color baseColor) {
        this.renderer = new GlyphRenderer(text, defaultScaleFunction(duration), AnimationFunction.ZERO, defaultColorFunction(duration, baseColor), glyphDelay);
    }

    public static AnimationFunction<Float> defaultScaleFunction(long duration) {
        return AnimationFunction.linear(duration, new float[]{0.0f, 0.1f, 0.12f, 1.0f}, Float.valueOf(5.0f), Float.valueOf(0.8f), Float.valueOf(1.0f), Float.valueOf(1.0f));
    }

    public static AnimationFunction<Color> defaultColorFunction(long duration, Color baseColor) {
        return AnimationFunction.alphaColor(AnimationFunction.linear(duration, new float[]{0.0f, 0.15f, 1.0f}, 0, 255, 255), baseColor);
    }

    public HeightMode getHeightMode() {
        return this.renderer.getHeightMode();
    }

    public String getText() {
        return this.renderer.getText();
    }

    public long getTime() {
        return this.renderer.getTime();
    }

    public void setHeightMode(HeightMode newHeightMode) {
        HeightMode oldHeightMode = getHeightMode();
        this.renderer.setHeightMode(newHeightMode);
        firePropertyChange("heightMode", oldHeightMode, newHeightMode);
        repaint();
    }

    public void setText(String newText) {
        String oldText = getText();
        this.renderer.setText(newText);
        firePropertyChange("text", oldText, newText);
        repaint();
    }

    public void setTime(long newTime) {
        long oldTime = getTime();
        this.renderer.setTime(newTime);
        firePropertyChange(PROPERTY_TIME, oldTime, newTime);
        repaint();
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.renderer.setFont(getFont());
        this.renderer.render(g2, getWidth(), getHeight());
    }
}
