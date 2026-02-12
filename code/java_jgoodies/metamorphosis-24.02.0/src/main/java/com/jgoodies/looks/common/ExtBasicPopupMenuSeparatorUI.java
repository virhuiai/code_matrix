package com.jgoodies.looks.common;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicPopupMenuSeparatorUI.class */
public final class ExtBasicPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
    private static final int SEPARATOR_HEIGHT = 2;
    private Insets insets;
    private static ComponentUI popupMenuSeparatorUI;

    public static ComponentUI createUI(JComponent b) {
        if (popupMenuSeparatorUI == null) {
            popupMenuSeparatorUI = new ExtBasicPopupMenuSeparatorUI();
        }
        return popupMenuSeparatorUI;
    }

    protected void installDefaults(JSeparator s) {
        super.installDefaults(s);
        this.insets = UIManager.getInsets("PopupMenuSeparator.margin");
    }

    public void paint(Graphics g, JComponent c) {
        Dimension s = c.getSize();
        int topInset = this.insets.top;
        int leftInset = this.insets.left;
        int rightInset = this.insets.right;
        g.setColor(UIManager.getColor("MenuItem.background"));
        g.fillRect(0, 0, s.width, s.height);
        g.translate(0, topInset);
        g.setColor(c.getForeground());
        g.drawLine(leftInset, 0, s.width - rightInset, 0);
        g.setColor(c.getBackground());
        g.drawLine(leftInset, 1, s.width - rightInset, 1);
        g.translate(0, -topInset);
    }

    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(0, this.insets.top + 2 + this.insets.bottom);
    }
}
