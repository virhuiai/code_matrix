package com.jgoodies.looks.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicMenuUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicMenuUI.class */
public class ExtBasicMenuUI extends BasicMenuUI {
    private static final String MENU_PROPERTY_PREFIX = "Menu";
    private static final String SUBMENU_PROPERTY_PREFIX = "MenuItem";
    private String propertyPrefix = MENU_PROPERTY_PREFIX;
    private MenuItemRenderer renderer;
    private MouseListener mouseListener;

    public static ComponentUI createUI(JComponent b) {
        return new ExtBasicMenuUI();
    }

    protected void installDefaults() {
        super.installDefaults();
        if (this.arrowIcon == null || (this.arrowIcon instanceof UIResource)) {
            this.arrowIcon = UIManager.getIcon("Menu.arrowIcon");
        }
        this.renderer = new MenuItemRenderer(this.menuItem, false, this.acceleratorFont, this.selectionForeground, this.disabledForeground, this.acceleratorForeground, this.acceleratorSelectionForeground);
        Integer gap = (Integer) UIManager.get(getPropertyPrefix() + ".textIconGap");
        this.defaultTextIconGap = gap != null ? gap.intValue() : 2;
        LookAndFeel.installBorder(this.menuItem, getPropertyPrefix() + ".border");
    }

    protected void uninstallDefaults() {
        super.uninstallDefaults();
        this.renderer = null;
    }

    protected String getPropertyPrefix() {
        return this.propertyPrefix;
    }

    protected Dimension getPreferredMenuItemSize(JComponent c, Icon aCheckIcon, Icon anArrowIcon, int textIconGap) {
        if (isSubMenu(this.menuItem)) {
            ensureSubMenuInstalled();
            return this.renderer.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
        }
        return super.getPreferredMenuItemSize(c, aCheckIcon, anArrowIcon, textIconGap);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void paintMenuItem(Graphics g, JComponent c, Icon aCheckIcon, Icon anArrowIcon, Color background, Color foreground, int textIconGap) {
        if (isSubMenu(this.menuItem)) {
            this.renderer.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
        } else {
            super.paintMenuItem(g, c, aCheckIcon, anArrowIcon, background, foreground, textIconGap);
        }
    }

    private void ensureSubMenuInstalled() {
        if (this.propertyPrefix.equals(SUBMENU_PROPERTY_PREFIX)) {
            return;
        }
        ButtonModel model = this.menuItem.getModel();
        boolean oldArmed = model.isArmed();
        boolean oldSelected = model.isSelected();
        uninstallRolloverListener();
        uninstallDefaults();
        this.propertyPrefix = SUBMENU_PROPERTY_PREFIX;
        installDefaults();
        model.setArmed(oldArmed);
        model.setSelected(oldSelected);
    }

    protected void installListeners() {
        super.installListeners();
        this.mouseListener = new RolloverHandler();
        this.menuItem.addMouseListener(this.mouseListener);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        uninstallRolloverListener();
    }

    private void uninstallRolloverListener() {
        if (this.mouseListener != null) {
            this.menuItem.removeMouseListener(this.mouseListener);
            this.mouseListener = null;
        }
    }

    private static boolean isSubMenu(JMenuItem aMenuItem) {
        return !((JMenu) aMenuItem).isTopLevelMenu();
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicMenuUI$RolloverHandler.class */
    private static final class RolloverHandler extends MouseAdapter {
        private RolloverHandler() {
        }

        public void mouseEntered(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            b.getModel().setRollover(true);
        }

        public void mouseExited(MouseEvent e) {
            AbstractButton b = (AbstractButton) e.getSource();
            b.getModel().setRollover(false);
        }
    }
}
