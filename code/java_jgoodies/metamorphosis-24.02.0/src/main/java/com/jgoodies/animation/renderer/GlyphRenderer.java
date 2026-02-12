package com.jgoodies.animation.renderer;

import com.jgoodies.animation.AnimationFunction;
import java.awt.Color;
import java.awt.Graphics2D;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/GlyphRenderer.class */
public final class GlyphRenderer extends AbstractTextRenderer {
    private final AnimationFunction<Color> colorFunction;
    private final AnimationFunction<Float> scaleFunction;
    private final long glyphDelay;
    private long time;

    public GlyphRenderer(String text, AnimationFunction<Float> scaleFunction, AnimationFunction<Float> translateFunction, AnimationFunction<Color> colorFunction, long glyphDelay) {
        super(text);
        this.scaleFunction = scaleFunction;
        this.colorFunction = colorFunction;
        this.glyphDelay = glyphDelay;
        this.time = 0L;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long relativeTime(int glyphIndex) {
        return Math.max(0L, this.time - (this.glyphDelay * glyphIndex));
    }

    private float scaleAt(int glyphIndex) {
        return this.scaleFunction.valueAt(relativeTime(glyphIndex)).floatValue();
    }

    private Color colorAt(int glyphIndex) {
        return this.colorFunction.valueAt(relativeTime(glyphIndex));
    }

    @Override // com.jgoodies.animation.renderer.AnimationRenderer
    public void render(Graphics2D g2, int width, int height) {
        ensureValidCache(g2);
        int glyphCount = this.cachedGlyphShapes.length;
        float offsetX = (width - this.cachedTextWidth) / 2.0f;
        float offsetY = ((height + this.cachedTextHeight) / 2.0f) - getAdjustedDescent();
        g2.translate(offsetX, offsetY);
        for (int i = glyphCount - 1; i >= 0; i--) {
            float scale = scaleAt(i);
            g2.setColor(colorAt(i));
            double glyphX = this.cachedGlyphVector.getGlyphPosition(i).getX();
            double glyphY = this.cachedGlyphVector.getGlyphVisualBounds(i).getBounds2D().getHeight();
            double adjustX = (-glyphX) * (scale - 1.0f);
            double adjustY = (glyphY * (scale - 1.0f)) / 2.0d;
            g2.translate(adjustX, adjustY);
            g2.scale(scale, scale);
            g2.fill(this.cachedGlyphShapes[i]);
            g2.scale(1.0f / scale, 1.0f / scale);
            g2.translate(-adjustX, -adjustY);
        }
        g2.translate(-offsetX, -offsetY);
    }
}
