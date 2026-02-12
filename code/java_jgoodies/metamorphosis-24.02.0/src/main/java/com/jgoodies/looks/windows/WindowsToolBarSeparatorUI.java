package com.jgoodies.looks.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsToolBarSeparatorUI.class */
public final class WindowsToolBarSeparatorUI extends BasicToolBarSeparatorUI {
    private static WindowsToolBarSeparatorUI toolBarSeparatorUI;

    public static ComponentUI createUI(JComponent c) {
        if (toolBarSeparatorUI == null) {
            toolBarSeparatorUI = new WindowsToolBarSeparatorUI();
        }
        return toolBarSeparatorUI;
    }

    public void paint(Graphics g, JComponent c) {
        Color temp = g.getColor();
        Color shadowColor = UIManager.getColor("ToolBar.shadow");
        Color highlightColor = UIManager.getColor("ToolBar.highlight");
        Dimension size = c.getSize();
        if (((JSeparator) c).getOrientation() == 1) {
            int x = (size.width / 2) - 1;
            g.setColor(shadowColor);
            g.drawLine(x, 0, x, size.height - 1);
            g.setColor(highlightColor);
            g.drawLine(x + 1, 0, x + 1, size.height - 1);
        } else {
            int y = (size.height / 2) - 1;
            g.setColor(shadowColor);
            g.drawLine(0, y, size.width - 1, y);
            g.setColor(highlightColor);
            g.drawLine(0, y + 1, size.width - 1, y + 1);
        }
        g.setColor(temp);
    }
}
