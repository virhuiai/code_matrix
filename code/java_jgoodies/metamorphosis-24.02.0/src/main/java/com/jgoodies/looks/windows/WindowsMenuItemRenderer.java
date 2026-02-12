package com.jgoodies.looks.windows;

import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.common.MenuItemRenderer;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsMenuItemRenderer.class */
final class WindowsMenuItemRenderer extends MenuItemRenderer {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowsMenuItemRenderer(JMenuItem menuItem, boolean iconBorderEnabled, Font acceleratorFont, Color selectionForeground, Color disabledForeground, Color acceleratorForeground, Color acceleratorSelectionForeground) {
        super(menuItem, iconBorderEnabled, acceleratorFont, selectionForeground, disabledForeground, acceleratorForeground, acceleratorSelectionForeground);
    }

    @Override // com.jgoodies.looks.common.MenuItemRenderer
    protected boolean isMnemonicHidden() {
        return com.sun.java.swing.plaf.windows.WindowsLookAndFeel.isMnemonicHidden();
    }

    @Override // com.jgoodies.looks.common.MenuItemRenderer
    protected boolean disabledTextHasShadow() {
        return !SystemUtils.IS_LAF_WINDOWS_XP_ENABLED || UIManager.getColor("MenuItem.disabledForeground") == null;
    }
}
