package com.jgoodies.looks.windows;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsScrollBarUI.class */
public final class WindowsScrollBarUI extends com.sun.java.swing.plaf.windows.WindowsScrollBarUI {
    public static ComponentUI createUI(JComponent b) {
        return new WindowsScrollBarUI();
    }

    protected JButton createDecreaseButton(int orientation) {
        return new WindowsArrowButton(orientation);
    }

    protected JButton createIncreaseButton(int orientation) {
        return createDecreaseButton(orientation);
    }
}
