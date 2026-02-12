package com.jgoodies.looks.plastic;

import com.jgoodies.looks.common.ExtBasicMenuUI;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticMenuUI.class */
public final class PlasticMenuUI extends ExtBasicMenuUI {
    public static ComponentUI createUI(JComponent b) {
        return new PlasticMenuUI();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.common.ExtBasicMenuUI
    public void paintMenuItem(Graphics g, JComponent c, Icon aCheckIcon, Icon anArrowIcon, Color background, Color foreground, int textIconGap) {
        JMenuItem b = (JMenuItem) c;
        if (this.menuItem.isTopLevelMenu()) {
            b.setOpaque(false);
            if (b.getModel().isSelected()) {
                int menuWidth = this.menuItem.getWidth();
                int menuHeight = this.menuItem.getHeight();
                Color oldColor = g.getColor();
                g.setColor(background);
                g.fillRect(0, 0, menuWidth, menuHeight);
                g.setColor(oldColor);
            }
        }
        super.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
    }
}
