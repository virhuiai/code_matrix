package com.jgoodies.components.internal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/MnemonicUnderlineSupport.class */
public final class MnemonicUnderlineSupport extends AbstractUnderlineSupport {
    static final String PROPERTY_MNEMONIC_INDEX = "mnemomicIndex";
    private static final int NO_MNEMONIC_INDEX = -1;
    private static final MnemonicUnderlineSupport INSTANCE = new MnemonicUnderlineSupport();

    private MnemonicUnderlineSupport() {
    }

    public static int getMnemonicIndex(JTextComponent c) {
        Object index = c.getClientProperty(PROPERTY_MNEMONIC_INDEX);
        return index instanceof Integer ? ((Integer) index).intValue() : NO_MNEMONIC_INDEX;
    }

    public static void setMnemonicIndex(JTextComponent c, int newValue) {
        int oldValue = getMnemonicIndex(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(PROPERTY_MNEMONIC_INDEX, Integer.valueOf(newValue));
        INSTANCE.uninstall(c);
        if (newValue != NO_MNEMONIC_INDEX) {
            INSTANCE.install(c);
        }
        c.repaint();
    }

    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    protected void updateUnderline(JTextComponent c) {
        Highlighter highlighter = c.getHighlighter();
        int index = getMnemonicIndex(c);
        int length = c.getDocument().getLength();
        if (index >= length) {
            return;
        }
        Object tag = c.getClientProperty(this.keyUnderlinePainterTag);
        try {
            if (tag == null) {
                c.putClientProperty(this.keyUnderlinePainterTag, highlighter.addHighlight(index, index + 1, createHighlightPainter()));
            } else {
                highlighter.changeHighlight(tag, index, index + 1);
            }
        } catch (BadLocationException e) {
        }
    }

    @Override // com.jgoodies.components.internal.AbstractUnderlineSupport
    protected DefaultHighlighter.DefaultHighlightPainter createHighlightPainter() {
        return new MnemonicUnderlinePainter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/MnemonicUnderlineSupport$MnemonicUnderlinePainter.class */
    public static final class MnemonicUnderlinePainter extends DefaultHighlighter.DefaultHighlightPainter {
        MnemonicUnderlinePainter() {
            super((Color) null);
        }

        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            try {
                Shape shape = c.getUI().getRootView(c).modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                paintUnderline(g, c, outline(bounds(shape)));
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
                paintUnderline(g, c, rect);
                return rect;
            }
            return null;
        }

        private static void paintUnderline(Graphics g, JTextComponent c, Rectangle clip) {
            Color oldColor = g.getColor();
            Shape oldClip = g.getClip();
            g.setColor(c.getForeground());
            g.clipRect(clip.x, clip.y, clip.width, clip.height);
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
            g.setClip(oldClip);
            g.setColor(oldColor);
        }

        private static Rectangle bounds(Shape shape) {
            return shape instanceof Rectangle ? (Rectangle) shape : shape.getBounds();
        }

        private static Rectangle outline(Rectangle r) {
            return new Rectangle(r.x, (r.y + r.height) - 1, r.width - 2, 1);
        }
    }
}
