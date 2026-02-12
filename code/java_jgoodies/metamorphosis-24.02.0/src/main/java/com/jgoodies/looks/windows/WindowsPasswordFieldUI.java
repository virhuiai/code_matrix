package com.jgoodies.looks.windows;

import com.jgoodies.looks.common.FieldCaret;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.Caret;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsPasswordFieldUI.class */
public final class WindowsPasswordFieldUI extends com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI {
    public static ComponentUI createUI(JComponent c) {
        return new WindowsPasswordFieldUI();
    }

    protected Caret createCaret() {
        return new FieldCaret();
    }
}
