package com.jgoodies.looks.plastic;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticTabbedPaneUI.class */
public final class PlasticTabbedPaneUI extends BasicTabbedPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new PlasticTabbedPaneUI();
    }

    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        g.setColor(this.lightHighlight);
        if (tabPlacement != 1 || selectedIndex < 0 || selRect.y + selRect.height + 1 < y || selRect.x < x || selRect.x > x + w) {
            g.drawLine(x, y, (x + w) - 2, y);
            return;
        }
        g.drawLine(x, y, selRect.x, y);
        if (selRect.x + selRect.width < (x + w) - 2) {
            g.drawLine((selRect.x + selRect.width) - 1, y, (x + w) - 2, y);
        } else {
            g.setColor(this.shadow);
            g.drawLine((x + w) - 2, y, (x + w) - 2, y);
        }
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        g.setColor(this.shadow);
        if (tabPlacement != 3 || selectedIndex < 0 || selRect.y - 1 > h || selRect.x < x || selRect.x > x + w) {
            g.drawLine(x + 1, (y + h) - 2, (x + w) - 2, (y + h) - 2);
            g.setColor(this.darkShadow);
            g.drawLine(x, (y + h) - 1, (x + w) - 1, (y + h) - 1);
        } else {
            g.drawLine(x, (y + h) - 1, selRect.x, (y + h) - 1);
            if (selRect.x + selRect.width < (x + w) - 2) {
                g.setColor(this.shadow);
                g.drawLine((selRect.x + selRect.width) - 1, (y + h) - 1, (x + w) - 1, (y + h) - 1);
            }
        }
    }

    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        g.setColor(this.lightHighlight);
        switch (tabPlacement) {
            case 1:
            default:
                g.drawLine(x, y, x, (y + h) - 1);
                g.drawLine(x + 1, y, (x + w) - 2, y);
                g.drawLine((x + w) - 1, y, (x + w) - 1, y + h + 2);
                return;
            case AnimatedLabel.LEFT /* 2 */:
                g.drawLine(x + 1, (y + h) - 2, x + 1, (y + h) - 2);
                g.drawLine(x, y + 2, x, (y + h) - 3);
                g.drawLine(x + 1, y + 1, x + 1, y + 1);
                g.drawLine(x + 2, y, (x + w) - 1, y);
                g.setColor(this.shadow);
                g.drawLine(x + 2, (y + h) - 2, (x + w) - 1, (y + h) - 2);
                g.setColor(this.darkShadow);
                g.drawLine(x + 2, (y + h) - 1, (x + w) - 1, (y + h) - 1);
                return;
            case 3:
                g.drawLine(x, y - 1, x, (y + h) - 1);
                g.drawLine(x + 1, (y + h) - 1, (x + w) - 1, (y + h) - 1);
                g.drawLine((x + w) - 1, y, (x + w) - 1, (y + h) - 1);
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                g.drawLine(x, y, (x + w) - 3, y);
                g.setColor(this.shadow);
                g.drawLine(x, (y + h) - 2, (x + w) - 3, (y + h) - 2);
                g.drawLine((x + w) - 2, y + 2, (x + w) - 2, (y + h) - 3);
                g.setColor(this.darkShadow);
                g.drawLine((x + w) - 2, y + 1, (x + w) - 2, y + 1);
                g.drawLine((x + w) - 2, (y + h) - 2, (x + w) - 2, (y + h) - 2);
                g.drawLine((x + w) - 1, y + 2, (x + w) - 1, (y + h) - 3);
                g.drawLine(x, (y + h) - 1, (x + w) - 3, (y + h) - 1);
                return;
        }
    }
}
