package com.jgoodies.looks.windows;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsTreeUI.class */
public final class WindowsTreeUI extends com.sun.java.swing.plaf.windows.WindowsTreeUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsTreeUI();
    }

    protected void drawCentered(Component c, Graphics graphics, Icon icon, int x, int y) {
        icon.paintIcon(c, graphics, (x - (icon.getIconWidth() / 2)) - 1, y - (icon.getIconHeight() / 2));
    }
}
