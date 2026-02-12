package com.jgoodies.looks.common;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicRadioButtonMenuItemUI.class */
public class ExtBasicRadioButtonMenuItemUI extends ExtBasicMenuItemUI {
    protected String getPropertyPrefix() {
        return "RadioButtonMenuItem";
    }

    public static ComponentUI createUI(JComponent b) {
        return new ExtBasicRadioButtonMenuItemUI();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.looks.common.ExtBasicMenuItemUI
    public boolean iconBorderEnabled() {
        return true;
    }

    public void processMouseEvent(JMenuItem item, MouseEvent e, MenuElement[] path, MenuSelectionManager manager) {
        Point p = e.getPoint();
        if (p.x >= 0 && p.x < item.getWidth() && p.y >= 0 && p.y < item.getHeight()) {
            if (e.getID() == 502) {
                manager.clearSelectedPath();
                item.doClick(0);
                item.setArmed(false);
                return;
            }
            manager.setSelectedPath(path);
            return;
        }
        if (item.getModel().isArmed()) {
            MenuElement[] newPath = new MenuElement[path.length - 1];
            int c = path.length - 1;
            for (int i = 0; i < c; i++) {
                newPath[i] = path[i];
            }
            manager.setSelectedPath(newPath);
        }
    }
}
