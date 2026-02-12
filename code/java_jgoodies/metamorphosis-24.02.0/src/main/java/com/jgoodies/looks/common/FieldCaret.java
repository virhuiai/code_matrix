package com.jgoodies.looks.common;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoundedRangeModel;
import javax.swing.JTextField;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.Position;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/FieldCaret.class */
public final class FieldCaret extends ExtDefaultCaret {
    private static final LayeredHighlighter.LayerPainter WINDOWS_PAINTER = new ExtDefaultHighlightPainter(null);

    protected void adjustVisibility(Rectangle r) {
        if (!(getComponent() instanceof JTextField)) {
            return;
        }
        EventQueue.invokeLater(new SafeScroller(r));
    }

    protected Highlighter.HighlightPainter getSelectionPainter() {
        return WINDOWS_PAINTER;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/FieldCaret$SafeScroller.class */
    private final class SafeScroller implements Runnable {
        private final Rectangle r;

        SafeScroller(Rectangle r) {
            this.r = r;
        }

        @Override // java.lang.Runnable
        public void run() {
            JTextField field = FieldCaret.this.getComponent();
            if (field == null) {
                return;
            }
            TextUI ui = field.getUI();
            int dot = FieldCaret.this.getDot();
            Position.Bias bias = Position.Bias.Forward;
            Rectangle startRect = null;
            try {
                startRect = ui.modelToView(field, dot, bias);
            } catch (BadLocationException e) {
            }
            Insets i = field.getInsets();
            BoundedRangeModel vis = field.getHorizontalVisibility();
            int x = (this.r.x + vis.getValue()) - i.left;
            int quarterSpan = vis.getExtent() / 4;
            if (this.r.x < i.left) {
                vis.setValue(x - quarterSpan);
            } else if (this.r.x + this.r.width > i.left + vis.getExtent() + 1) {
                vis.setValue(x - (3 * quarterSpan));
            }
            if (startRect == null) {
                return;
            }
            try {
                Rectangle endRect = ui.modelToView(field, dot, bias);
                if (endRect != null && !endRect.equals(startRect)) {
                    FieldCaret.this.damage(endRect);
                }
            } catch (BadLocationException e2) {
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/FieldCaret$ExtDefaultHighlightPainter.class */
    private static final class ExtDefaultHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        ExtDefaultHighlightPainter(Color c) {
            super(c);
        }

        public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
            Rectangle alloc = bounds.getBounds();
            try {
                TextUI mapper = c.getUI();
                Rectangle p0 = mapper.modelToView(c, offs0);
                Rectangle p1 = mapper.modelToView(c, offs1);
                Color color = getColor();
                if (color == null) {
                    g.setColor(c.getSelectionColor());
                } else {
                    g.setColor(color);
                }
                boolean firstIsDot = false;
                boolean secondIsDot = false;
                if (c.isEditable()) {
                    int dot = c.getCaretPosition();
                    firstIsDot = offs0 == dot;
                    secondIsDot = offs1 == dot;
                }
                if (p0.y == p1.y) {
                    Rectangle r = p0.union(p1);
                    if (r.width > 0) {
                        if (firstIsDot) {
                            r.x++;
                            r.width--;
                        } else if (secondIsDot) {
                            r.width--;
                        }
                    }
                    g.fillRect(r.x, r.y, r.width, r.height);
                } else {
                    int p0ToMarginWidth = (alloc.x + alloc.width) - p0.x;
                    if (firstIsDot && p0ToMarginWidth > 0) {
                        p0.x++;
                        p0ToMarginWidth--;
                    }
                    g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
                    if (p0.y + p0.height != p1.y) {
                        g.fillRect(alloc.x, p0.y + p0.height, alloc.width, p1.y - (p0.y + p0.height));
                    }
                    if (secondIsDot && p1.x > alloc.x) {
                        p1.x--;
                    }
                    g.fillRect(alloc.x, p1.y, p1.x - alloc.x, p1.height);
                }
            } catch (BadLocationException e) {
            }
        }

        public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
            Rectangle alloc;
            Color color = getColor();
            if (color == null) {
                g.setColor(c.getSelectionColor());
            } else {
                g.setColor(color);
            }
            boolean firstIsDot = false;
            boolean secondIsDot = false;
            if (c.isEditable()) {
                int dot = c.getCaretPosition();
                firstIsDot = offs0 == dot;
                secondIsDot = offs1 == dot;
            }
            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
                if (bounds instanceof Rectangle) {
                    alloc = (Rectangle) bounds;
                } else {
                    alloc = bounds.getBounds();
                }
                if (firstIsDot && alloc.width > 0) {
                    g.fillRect(alloc.x + 1, alloc.y, alloc.width - 1, alloc.height);
                } else if (secondIsDot && alloc.width > 0) {
                    g.fillRect(alloc.x, alloc.y, alloc.width - 1, alloc.height);
                } else {
                    g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
                }
                return alloc;
            }
            try {
                Rectangle modelToView = view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);
                Rectangle r = modelToView instanceof Rectangle ? modelToView : modelToView.getBounds();
                if (firstIsDot && r.width > 0) {
                    g.fillRect(r.x + 1, r.y, r.width - 1, r.height);
                } else if (secondIsDot && r.width > 0) {
                    g.fillRect(r.x, r.y, r.width - 1, r.height);
                } else {
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
                return r;
            } catch (BadLocationException e) {
                return null;
            }
        }
    }
}
