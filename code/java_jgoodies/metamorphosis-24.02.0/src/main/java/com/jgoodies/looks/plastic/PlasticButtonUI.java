package com.jgoodies.looks.plastic;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalButtonUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticButtonUI.class */
public class PlasticButtonUI extends MetalButtonUI {
    private static final PlasticButtonUI INSTANCE = new PlasticButtonUI();
    private boolean borderPaintsFocus;
    private Insets focusInsets;

    public static ComponentUI createUI(JComponent b) {
        return INSTANCE;
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        this.borderPaintsFocus = Boolean.TRUE.equals(UIManager.get(getPropertyPrefix() + "borderPaintsFocus"));
        this.focusInsets = UIManager.getInsets(getPropertyPrefix() + "focusInsets");
    }

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new ActiveBasicButtonListener(b);
    }

    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            AbstractButton b = (AbstractButton) c;
            if (isToolBarButton(b)) {
                c.setOpaque(false);
            } else if (b.isContentAreaFilled()) {
                g.setColor(c.getBackground());
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
                if (is3D(b)) {
                    Rectangle r = new Rectangle(1, 1, c.getWidth() - 2, c.getHeight() - 1);
                    PlasticUtils.add3DEffekt(g, r);
                }
            }
        }
        paint(g, c);
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
        if (this.borderPaintsFocus) {
            return;
        }
        int x = this.focusInsets.left;
        int y = this.focusInsets.top;
        int w = (b.getWidth() - x) - this.focusInsets.right;
        int h = (b.getHeight() - y) - this.focusInsets.bottom;
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
    }

    protected boolean isToolBarButton(AbstractButton b) {
        Container parent = b.getParent();
        return parent != null && ((parent instanceof JToolBar) || (parent.getParent() instanceof JToolBar));
    }

    protected boolean is3D(AbstractButton b) {
        ButtonModel model = b.getModel();
        return PlasticUtils.is3D("Button.") && b.isBorderPainted() && model.isEnabled() && !(model.isPressed() && model.isArmed());
    }
}
