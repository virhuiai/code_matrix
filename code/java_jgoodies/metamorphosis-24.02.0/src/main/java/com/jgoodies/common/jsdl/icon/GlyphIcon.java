package com.jgoodies.common.jsdl.icon;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/icon/GlyphIcon.class */
public final class GlyphIcon implements Icon {
    private static final int DEFAULT_SIZE = ScreenScaling.toPhysical(24);
    private final Font font;
    private final char character;
    private Color foreground;
    private final int width;
    private final int height;

    public GlyphIcon(GlyphIconValue icon) {
        this(icon, DEFAULT_SIZE);
    }

    public GlyphIcon(GlyphIconValue icon, int size) {
        this(icon, size, (Color) null);
    }

    public GlyphIcon(GlyphIconValue icon, Color foreground) {
        this(icon, DEFAULT_SIZE, foreground);
    }

    public GlyphIcon(GlyphIconValue icon, int size, Color foreground) {
        this(icon.getBaseFont().deriveFont(size), icon.getCode(), size, foreground);
    }

    private GlyphIcon(Font font, char c, int size, Color foreground) {
        this.font = (Font) Preconditions.checkNotNull(font, Messages.MUST_NOT_BE_NULL, "font");
        this.character = ((Character) Preconditions.checkNotNull(Character.valueOf(c), Messages.MUST_NOT_BE_NULL, "character")).charValue();
        this.foreground = foreground;
        Preconditions.checkArgument(size > 0, "The size must be greater than 0.");
        this.width = size;
        this.height = size;
    }

    public Color getForeground() {
        return this.foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public int getIconHeight() {
        return this.height;
    }

    public int getIconWidth() {
        return this.width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.translate(x, y);
        Font oldFont = g.getFont();
        Color oldColor = g.getColor();
        g2.setFont(this.font);
        if (this.foreground != null) {
            g2.setColor(this.foreground);
        } else {
            Color foreground = c.getForeground();
            if (foreground != null) {
                g2.setColor(foreground);
            }
        }
        g2.drawString(String.valueOf(this.character), 0, this.height);
        g2.setColor(oldColor);
        g2.setFont(oldFont);
        g2.translate(-x, -y);
    }
}
