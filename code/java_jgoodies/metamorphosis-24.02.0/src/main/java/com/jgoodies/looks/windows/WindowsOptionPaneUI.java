package com.jgoodies.looks.windows;

import com.jgoodies.looks.common.ExtButtonAreaLayout;
import java.awt.Container;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicOptionPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsOptionPaneUI.class */
public final class WindowsOptionPaneUI extends BasicOptionPaneUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsOptionPaneUI();
    }

    protected boolean getSizeButtonsToSameWidth() {
        return false;
    }

    protected Container createButtonArea() {
        JPanel bottom = new JPanel(new ExtButtonAreaLayout());
        bottom.setName("OptionPane.buttonArea");
        bottom.setBorder(UIManager.getBorder("OptionPane.buttonAreaBorder"));
        addButtonComponents(bottom, getButtons(), getInitialValueIndex());
        return bottom;
    }
}
