package com.jgoodies.animation.renderer;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/renderer/AbstractTextRenderer.class */
public abstract class AbstractTextRenderer implements AnimationRenderer {
    private String text;
    private Font font;
    private Color color;
    private HeightMode heightMode;
    protected GlyphVector cachedGlyphVector;
    protected Shape[] cachedGlyphShapes;
    protected float cachedTextWidth;
    protected float cachedTextAscent;
    protected float cachedTextHeight;
    protected float capitalMAscent;
    private boolean cacheValid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AbstractTextRenderer(String text) {
        this(text, null);
    }

    AbstractTextRenderer(String text, Font font) {
        this.heightMode = HeightMode.CAPITAL_ASCENT;
        this.capitalMAscent = -1.0f;
        this.cacheValid = false;
        this.text = text == null ? "Karsten Lentzsch" : text;
        this.font = font == null ? createDefaultFont() : font;
        this.color = Color.BLACK;
    }

    private static Font createDefaultFont() {
        return new Font("dialog", 1, 12);
    }

    public Color getColor() {
        return this.color;
    }

    public Font getFont() {
        return this.font;
    }

    public String getText() {
        return this.text;
    }

    public HeightMode getHeightMode() {
        return this.heightMode;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setHeightMode(HeightMode heightMode) {
        this.heightMode = heightMode;
    }

    public void setFont(Font newFont) {
        Preconditions.checkNotNull(newFont, Messages.MUST_NOT_BE_NULL, "font");
        if (newFont.equals(this.font)) {
            return;
        }
        this.font = newFont;
        invalidateCache();
    }

    public void setText(String newText) {
        Preconditions.checkNotNull(newText, Messages.MUST_NOT_BE_NULL, "text");
        if (newText.equals(this.text)) {
            return;
        }
        this.text = newText;
        invalidateCache();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getAdjustedAscent() {
        if (this.heightMode == HeightMode.CAPITAL_ASCENT) {
            return this.capitalMAscent;
        }
        if (this.heightMode == HeightMode.TEXT_ASCENT) {
            return this.cachedTextAscent;
        }
        return this.cachedTextHeight;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getAdjustedDescent() {
        if (this.heightMode == HeightMode.CAPITAL_ASCENT || this.heightMode == HeightMode.TEXT_ASCENT) {
            return 0.0f;
        }
        return this.cachedTextHeight - this.cachedTextAscent;
    }

    protected boolean isCacheValid() {
        return this.cacheValid;
    }

    protected void setCacheValid(boolean b) {
        this.cacheValid = b;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void ensureValidCache(Graphics2D g2) {
        if (!isCacheValid()) {
            validateCache(g2);
        }
    }

    protected void validateCache(Graphics2D g2) {
        FontRenderContext frc = g2.getFontRenderContext();
        ensureCapitalMAscentComputed(frc);
        this.cachedGlyphVector = this.font.createGlyphVector(frc, this.text);
        Rectangle2D bounds = this.cachedGlyphVector.getVisualBounds();
        this.cachedTextWidth = (float) bounds.getWidth();
        this.cachedTextAscent = (float) (-bounds.getY());
        this.cachedTextHeight = (float) bounds.getHeight();
        int glyphCount = this.cachedGlyphVector.getNumGlyphs();
        this.cachedGlyphShapes = new Shape[glyphCount];
        for (int i = 0; i < glyphCount; i++) {
            this.cachedGlyphShapes[i] = this.cachedGlyphVector.getGlyphOutline(i);
        }
        setCacheValid(true);
    }

    private void ensureCapitalMAscentComputed(FontRenderContext frc) {
        if (this.capitalMAscent > 0.0f) {
            return;
        }
        GlyphVector mGlyphVector = this.font.createGlyphVector(frc, "M");
        this.capitalMAscent = (float) (-mGlyphVector.getVisualBounds().getY());
    }

    protected void invalidateCache() {
        setCacheValid(false);
        this.cachedGlyphVector = null;
        this.cachedGlyphShapes = null;
    }
}
