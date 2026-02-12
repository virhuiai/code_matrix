package com.jgoodies.looks.windows;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.SystemUtils;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsTabbedPaneUI.class */
public final class WindowsTabbedPaneUI extends com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI {
    private static final Insets CONTENT_BORDER_NORTH_INSETS = new Insets(0, 2, 4, 4);
    private static final Insets CONTENT_BORDER_WEST_INSETS = new Insets(2, 0, 4, 4);
    private static final Insets CONTENT_BORDER_SOUTH_INSETS = new Insets(4, 2, 0, 4);
    private static final Insets CONTENT_BORDER_EAST_INSETS = new Insets(2, 4, 4, 0);

    public static ComponentUI createUI(JComponent x) {
        return new WindowsTabbedPaneUI();
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        if (!SystemUtils.IS_LAF_WINDOWS_XP_ENABLED) {
            return this.contentBorderInsets;
        }
        switch (tabPlacement) {
            case 1:
                return CONTENT_BORDER_NORTH_INSETS;
            case AnimatedLabel.LEFT /* 2 */:
                return CONTENT_BORDER_WEST_INSETS;
            case 3:
            default:
                return CONTENT_BORDER_SOUTH_INSETS;
            case AnimatedLabel.RIGHT /* 4 */:
                return CONTENT_BORDER_EAST_INSETS;
        }
    }

    protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected) {
        switch (tabPlacement) {
            case 1:
            case 3:
            default:
                return 0;
            case AnimatedLabel.LEFT /* 2 */:
                return isSelected ? -2 : 0;
            case AnimatedLabel.RIGHT /* 4 */:
                return isSelected ? 2 : 0;
        }
    }

    protected Insets getSelectedTabPadInsets(int tabPlacement) {
        Insets superInsets = super.getSelectedTabPadInsets(tabPlacement);
        int equalized = superInsets.left + (superInsets.right / 2);
        superInsets.right = equalized;
        superInsets.left = equalized;
        return superInsets;
    }

    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        if (tabPlacement != 1) {
            return;
        }
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        if (tabPlacement != 1 || selectedIndex < 0 || selRect.y + selRect.height + 1 < y || selRect.x < x || selRect.x > x + w) {
            super.paintContentBorderTopEdge(g, tabPlacement, selectedIndex, x, y, w, h);
            return;
        }
        g.setColor(this.lightHighlight);
        g.fillRect(x, y, (selRect.x + 1) - x, 1);
        g.fillRect(selRect.x + selRect.width, y, (((x + w) - 2) - selRect.x) - selRect.width, 1);
    }

    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        if (tabPlacement != 3 || selectedIndex < 0 || selRect.y - 1 > h + y || selRect.x < x || selRect.x > x + w) {
            super.paintContentBorderBottomEdge(g, tabPlacement, selectedIndex, x, y, w, h);
        }
    }

    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        if (tabPlacement != 2 || selectedIndex < 0 || selRect.x + selRect.width + 1 < x || selRect.y < y || selRect.y > y + h) {
            super.paintContentBorderLeftEdge(g, tabPlacement, selectedIndex, x, y, w, h);
            return;
        }
        g.setColor(this.lightHighlight);
        g.fillRect(x, y, 1, (selRect.y + 1) - y);
        g.fillRect(x, selRect.y + selRect.height, 1, (((y + h) - 1) - selRect.y) - selRect.height);
    }

    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {
        Rectangle selRect = selectedIndex < 0 ? null : getTabBounds(selectedIndex, this.calcRect);
        if (tabPlacement != 4 || selectedIndex < 0 || selRect.x - 1 > x + w || selRect.y < y || selRect.y > y + h) {
            super.paintContentBorderRightEdge(g, tabPlacement, selectedIndex, x, y, w, h);
            return;
        }
        g.setColor(this.lightHighlight);
        g.fillRect((x + w) - 1, y, 1, 1);
        g.setColor(this.shadow);
        g.fillRect((x + w) - 2, y + 1, 1, (selRect.y - 1) - y);
        g.fillRect((x + w) - 2, selRect.y + selRect.height, 1, (((y + h) - 1) - selRect.y) - selRect.height);
        g.setColor(this.darkShadow);
        g.fillRect((x + w) - 1, y, 1, selRect.y - y);
        g.fillRect((x + w) - 1, (selRect.y + selRect.height) - 1, 1, ((y + h) - selRect.y) - selRect.height);
    }

    protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title, Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        iconRect.y = 0;
        iconRect.x = 0;
        textRect.y = 0;
        textRect.x = 0;
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
            this.tabPane.putClientProperty("html", v);
        }
        int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
        int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
        if ((tabPlacement == 4 || tabPlacement == 2) && icon != null && title != null && !title.equals("")) {
            SwingUtilities.layoutCompoundLabel(this.tabPane, metrics, title, icon, 0, 2, 0, 11, tabRect, iconRect, textRect, this.textIconGap);
            xNudge += 4;
        } else {
            SwingUtilities.layoutCompoundLabel(this.tabPane, metrics, title, icon, 0, 0, 0, 11, tabRect, iconRect, textRect, this.textIconGap);
        }
        this.tabPane.putClientProperty("html", (Object) null);
        iconRect.x += xNudge;
        iconRect.y += yNudge;
        textRect.x += xNudge;
        textRect.y += yNudge;
    }
}
