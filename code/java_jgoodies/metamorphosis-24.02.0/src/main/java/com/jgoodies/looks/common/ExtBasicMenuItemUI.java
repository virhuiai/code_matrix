package com.jgoodies.looks.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicMenuItemUI.class */
public class ExtBasicMenuItemUI extends BasicMenuItemUI {
    private static final int MINIMUM_WIDTH = 80;
    private MenuItemRenderer renderer;

    public static ComponentUI createUI(JComponent b) {
        return new ExtBasicMenuItemUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        this.renderer = createRenderer(this.menuItem, iconBorderEnabled(), this.acceleratorFont, this.selectionForeground, this.disabledForeground, this.acceleratorForeground, this.acceleratorSelectionForeground);
        Integer gap = (Integer) UIManager.get(getPropertyPrefix() + ".textIconGap");
        this.defaultTextIconGap = gap != null ? gap.intValue() : 2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean iconBorderEnabled() {
        return false;
    }

    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.renderer = null;
    }

    protected Dimension getPreferredMenuItemSize(JComponent c, Icon aCheckIcon, Icon anArrowIcon, int textIconGap) {
        Dimension size = this.renderer.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
        int width = Math.max(MINIMUM_WIDTH, size.width);
        int height = size.height;
        return new Dimension(width, height);
    }

    protected void paintMenuItem(Graphics g, JComponent c, Icon aCheckIcon, Icon anArrowIcon, Color background, Color foreground, int textIconGap) {
        this.renderer.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
    }

    protected MenuItemRenderer createRenderer(JMenuItem menuItem, boolean iconBorderEnabled, Font acceleratorFont, Color selectionForeground, Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground) {
        return new MenuItemRenderer(menuItem, iconBorderEnabled(), acceleratorFont, selectionForeground, disabledForeground, acceleratorForeground, acceleratorSelectionForeground);
    }
}
