package com.jgoodies.looks.windows;

import com.jgoodies.looks.common.ExtBasicMenuItemUI;
import com.jgoodies.looks.common.MenuItemRenderer;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsMenuItemUI.class */
public final class WindowsMenuItemUI extends ExtBasicMenuItemUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsMenuItemUI();
    }

    @Override // com.jgoodies.looks.common.ExtBasicMenuItemUI
    protected MenuItemRenderer createRenderer(JMenuItem menuItem, boolean iconBorderEnabled, Font acceleratorFont, Color selectionForeground, Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground) {
        return new WindowsMenuItemRenderer(menuItem, iconBorderEnabled(), acceleratorFont, selectionForeground, disabledForeground, acceleratorForeground, acceleratorSelectionForeground);
    }
}
