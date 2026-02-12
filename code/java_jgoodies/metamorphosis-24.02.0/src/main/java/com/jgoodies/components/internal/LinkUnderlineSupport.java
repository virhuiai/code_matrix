package com.jgoodies.components.internal;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/LinkUnderlineSupport.class */
final class LinkUnderlineSupport extends AbstractUnderlineSupport {
    static final String PROPERTY_LINK_PAINTED = "linkPainted";
    private static final String KEY_STORED_FOREGROUND = "LinkUnderlineSupport.storedForegroundColor";
    private static final LinkUnderlineSupport INSTANCE = new LinkUnderlineSupport();

    static /* synthetic */ Color access$000() {
        return linkColor();
    }

    private LinkUnderlineSupport() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isLinkPainted(JTextComponent c) {
        return INSTANCE.isEnabled(c);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setLinkPainted(JTextComponent c, boolean newValue) {
        INSTANCE.setEnabled(c, newValue);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    public void install(JTextComponent c) {
        c.putClientProperty(KEY_STORED_FOREGROUND, c.getForeground());
        c.setForeground(linkColor());
        c.setCursor(Cursor.getPredefinedCursor(12));
        super.install(c);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    public void uninstall(JTextComponent c) {
        super.uninstall(c);
        Color oldForeground = (Color) c.getClientProperty(KEY_STORED_FOREGROUND);
        if (oldForeground != null) {
            c.setForeground(oldForeground);
        }
        c.setCursor(Cursor.getPredefinedCursor(2));
    }

    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    protected DefaultHighlighter.DefaultHighlightPainter createHighlightPainter() {
        return new LinkUnderlinePainter();
    }

    private static Color linkColor() {
        Color c = UIManager.getColor("Hyperlink.unvisited.foreground");
        return c != null ? c : Color.BLUE;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/LinkUnderlineSupport$LinkUnderlinePainter.class */
    private static final class LinkUnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
        LinkUnderlinePainter() {
            super(LinkUnderlineSupport.access$000());
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
            Color oldColor = g.getColor();
            Shape oldClip = g.getClip();
            g.setColor(getColor());
            g.clipRect(clip.x, clip.y, clip.width, clip.height);
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
            g.setClip(oldClip);
            g.setColor(oldColor);
        }

        private static Rectangle bounds(Shape shape) {
            return shape instanceof Rectangle ? (Rectangle) shape : shape.getBounds();
        }

        private static Rectangle outline(Rectangle r) {
            return new Rectangle(r.x, (r.y + r.height) - 1, r.width, 1);
        }
    }
}
