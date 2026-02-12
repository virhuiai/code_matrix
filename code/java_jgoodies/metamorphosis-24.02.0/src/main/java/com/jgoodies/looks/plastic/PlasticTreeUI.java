package com.jgoodies.looks.plastic;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticTreeUI.class */
public final class PlasticTreeUI extends BasicTreeUI {
    public static ComponentUI createUI(JComponent b) {
        return new PlasticTreeUI();
    }

    protected void drawCentered(Component c, Graphics graphics, Icon icon, int x, int y) {
        icon.paintIcon(c, graphics, (x - (icon.getIconWidth() / 2)) - 1, y - (icon.getIconHeight() / 2));
    }
}
