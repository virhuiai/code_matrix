package com.jgoodies.looks.windows;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsRadioButtonUI.class */
public final class WindowsRadioButtonUI extends com.sun.java.swing.plaf.windows.WindowsRadioButtonUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsRadioButtonUI();
    }

    public void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        LookAndFeel.installProperty(b, "iconTextGap", Integer.valueOf(UIManager.getInt("RadioButton.iconTextGap")));
    }
}
