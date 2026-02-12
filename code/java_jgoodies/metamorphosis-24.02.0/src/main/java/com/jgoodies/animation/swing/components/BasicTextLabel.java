package com.jgoodies.animation.swing.components;

import com.jgoodies.animation.renderer.BasicTextRenderer;
import com.jgoodies.animation.renderer.HeightMode;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/components/BasicTextLabel.class */
public final class BasicTextLabel extends JComponent {
    public static final String PROPERTY_COLOR = "color";
    public static final String PROPERTY_HEIGHT_MODE = "heightMode";
    public static final String PROPERTY_SCALE = "scale";
    public static final String PROPERTY_SCALE_X = "scaleX";
    public static final String PROPERTY_SCALE_Y = "scaleY";
    public static final String PROPERTY_SPACE = "space";
    public static final String PROPERTY_TEXT = "text";
    public static final String PROPERTY_OFFSET_X = "offsetX";
    public static final String PROPERTY_OFFSET_Y = "offsetY";
    private final BasicTextRenderer renderer;

    public BasicTextLabel() {
        this("");
    }

    public BasicTextLabel(String text) {
        this.renderer = new BasicTextRenderer(text);
    }

    public Color getColor() {
        return this.renderer.getColor();
    }

    public HeightMode getHeightMode() {
        return this.renderer.getHeightMode();
    }

    public float getScale() {
        return Math.max(getScaleX(), getScaleX());
    }

    public float getScaleX() {
        return this.renderer.getScaleX();
    }

    public float getScaleY() {
        return this.renderer.getScaleY();
    }

    public float getSpace() {
        return this.renderer.getSpace();
    }

    public float getOffsetX() {
        return this.renderer.getOffsetX();
    }

    public float getOffsetY() {
        return this.renderer.getOffsetY();
    }

    public String getText() {
        return this.renderer.getText();
    }

    public void setColor(Color newColor) {
        Color oldColor = getColor();
        if (oldColor.equals(newColor)) {
            return;
        }
        this.renderer.setColor(newColor);
        firePropertyChange(PROPERTY_COLOR, oldColor, newColor);
        repaint();
    }

    public void setHeightMode(HeightMode heightMode) {
        HeightMode oldMode = getHeightMode();
        this.renderer.setHeightMode(heightMode);
        firePropertyChange("heightMode", oldMode, heightMode);
        repaint();
    }

    public void setScale(float newScale) {
        float oldScale = getScale();
        this.renderer.setScaleX(newScale);
        this.renderer.setScaleY(newScale);
        firePropertyChange(PROPERTY_SCALE, oldScale, newScale);
        repaint();
    }

    public void setScaleX(float newScaleX) {
        float oldScaleX = getScaleX();
        if (oldScaleX == newScaleX) {
            return;
        }
        float oldScale = getScale();
        this.renderer.setScaleX(newScaleX);
        firePropertyChange(PROPERTY_SCALE_X, oldScaleX, newScaleX);
        firePropertyChange(PROPERTY_SCALE, oldScale, getScale());
        repaint();
    }

    public void setScaleY(float newScaleY) {
        float oldScaleY = getScaleY();
        if (oldScaleY == newScaleY) {
            return;
        }
        float oldScale = getScale();
        this.renderer.setScaleY(newScaleY);
        firePropertyChange(PROPERTY_SCALE_Y, oldScaleY, newScaleY);
        firePropertyChange(PROPERTY_SCALE, oldScale, getScale());
        repaint();
    }

    public void setSpace(float newSpace) {
        float oldSpace = getSpace();
        if (oldSpace == newSpace) {
            return;
        }
        this.renderer.setSpace(newSpace);
        firePropertyChange(PROPERTY_SPACE, oldSpace, newSpace);
        repaint();
    }

    public void setOffsetX(float offsetX) {
        float oldOffsetX = getOffsetX();
        this.renderer.setOffsetX(offsetX);
        firePropertyChange(PROPERTY_OFFSET_X, oldOffsetX, offsetX);
        repaint();
    }

    public void setOffsetY(float offsetY) {
        float oldOffsetY = getOffsetY();
        this.renderer.setOffsetY(offsetY);
        firePropertyChange(PROPERTY_OFFSET_Y, oldOffsetY, offsetY);
        repaint();
    }

    public void setText(String newText) {
        String oldText = getText();
        if (oldText.equals(newText)) {
            return;
        }
        this.renderer.setText(newText);
        firePropertyChange("text", oldText, newText);
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
