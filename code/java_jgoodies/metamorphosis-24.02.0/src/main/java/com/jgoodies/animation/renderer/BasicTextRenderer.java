package com.jgoodies.animation.renderer;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.layout.layout.FormSpec;
import java.awt.Graphics2D;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/BasicTextRenderer.class */
public final class BasicTextRenderer extends AbstractTextRenderer {
    private float offsetX;
    private float offsetY;
    private float scaleX;
    private float scaleY;
    private float space;

    public BasicTextRenderer(String text) {
        super(text);
        this.offsetX = 0.0f;
        this.offsetY = 0.0f;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
        this.space = 0.0f;
    }

    public float getOffsetX() {
        return this.offsetX;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public float getSpace() {
        return this.space;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setSpace(float space) {
        this.space = space;
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleX(float scaleX) {
        Preconditions.checkArgument(scaleX > 0.0f, "scaleX must be positive.");
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        Preconditions.checkArgument(scaleY > 0.0f, "scaleY must be positive.");
        this.scaleY = scaleY;
    }

    @Override // com.jgoodies.animation.renderer.AnimationRenderer
    public void render(Graphics2D g2, int width, int height) {
        ensureValidCache(g2);
        int glyphCount = this.cachedGlyphShapes.length;
        float totalSpace = (glyphCount - 1) * this.space;
        float totalWidth = this.cachedTextWidth + totalSpace;
        float textHeight = getAdjustedAscent();
        float xOffset = getOffsetX() + ((width - (totalWidth * this.scaleX)) / 2.0f);
        float yOffset = (getOffsetY() + ((height + (textHeight * this.scaleY)) / 2.0f)) - getAdjustedDescent();
        g2.setColor(getColor());
        g2.translate(xOffset, yOffset);
        g2.scale(this.scaleX, this.scaleY);
        for (int i = 0; i < glyphCount; i++) {
            g2.fill(this.cachedGlyphShapes[i]);
            g2.translate(this.space, FormSpec.NO_GROW);
        }
        g2.scale(1.0f / this.scaleX, 1.0f / this.scaleY);
        g2.translate((-totalSpace) - xOffset, -yOffset);
    }
}
