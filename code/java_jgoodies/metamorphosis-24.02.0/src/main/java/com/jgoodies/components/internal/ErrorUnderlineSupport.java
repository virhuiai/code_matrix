package com.jgoodies.components.internal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ErrorUnderlineSupport.class */
public final class ErrorUnderlineSupport extends AbstractUnderlineSupport {
    public static final String PROPERTY_ERROR_UNDERLINE_PAINTED = "errorUnderlinePainted";
    private static final ErrorUnderlineSupport INSTANCE = new ErrorUnderlineSupport();

    private ErrorUnderlineSupport() {
    }

    public static boolean isErrorUnderlinePainted(JTextComponent c) {
        return INSTANCE.isEnabled(c);
    }

    public static void setErrorUnderlinePainted(JTextComponent c, boolean newValue) {
        INSTANCE.setEnabled(c, newValue);
    }

    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    protected DefaultHighlighter.DefaultHighlightPainter createHighlightPainter() {
        return new WavyUnderlinePainter();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ErrorUnderlineSupport$WavyUnderlinePainter.class */
    private static final class WavyUnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
        WavyUnderlinePainter() {
            super(Color.RED);
        }

        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            try {
                Shape shape = c.getUI().getRootView(c).modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                paintUnderline(g, outline(bounds(shape)));
            } catch (BadLocationException e) {
            }
        }

        public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
            Rectangle r = null;
            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
                r = bounds(bounds);
            } else {
                try {
                    Shape shape = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                    r = bounds(shape);
                } catch (BadLocationException e) {
                }
            }
            if (r != null) {
                Rectangle rect = outline(r);
                paintUnderline(g, rect);
                return rect;
            }
            return null;
        }

        private void paintUnderline(Graphics g, Rectangle clip) {
            int y = clip.y;
            Color oldColor = g.getColor();
            Shape oldClip = g.getClip();
            g.setColor(getColor());
            g.clipRect(clip.x, clip.y, clip.width, clip.height);
            int x = clip.x;
            int end = x + clip.width;
            while (x < end) {
                g.fillRect(x, y + 2, 1, 1);
                g.fillRect(x + 1, y + 1, 1, 1);
                g.fillRect(x + 2, y, 1, 1);
                g.fillRect(x + 3, y + 1, 1, 1);
                x += 4;
            }
            g.setClip(oldClip);
            g.setColor(oldColor);
        }

        private static Rectangle bounds(Shape shape) {
            return shape instanceof Rectangle ? (Rectangle) shape : shape.getBounds();
        }

        private static Rectangle outline(Rectangle r) {
            return new Rectangle(r.x, (r.y + r.height) - 3, r.width, 3);
        }
    }
}
