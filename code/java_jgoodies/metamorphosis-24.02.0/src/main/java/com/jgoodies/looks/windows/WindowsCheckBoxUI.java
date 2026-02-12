package com.jgoodies.looks.windows;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsCheckBoxUI.class */
public final class WindowsCheckBoxUI extends com.sun.java.swing.plaf.windows.WindowsCheckBoxUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsCheckBoxUI();
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "iconTextGap", Integer.valueOf(UIManager.getInt("CheckBox.iconTextGap")));
    }
}
