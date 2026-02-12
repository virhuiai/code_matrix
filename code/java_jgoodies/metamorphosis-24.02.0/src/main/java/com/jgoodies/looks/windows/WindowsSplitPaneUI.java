package com.jgoodies.looks.windows;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsSplitPaneUI.class */
public final class WindowsSplitPaneUI extends com.sun.java.swing.plaf.windows.WindowsSplitPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new WindowsSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new WindowsSplitPaneDivider(this);
    }
}
