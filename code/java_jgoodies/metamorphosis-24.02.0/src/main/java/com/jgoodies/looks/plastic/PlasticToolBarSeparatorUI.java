package com.jgoodies.looks.plastic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarSeparatorUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticToolBarSeparatorUI.class */
public final class PlasticToolBarSeparatorUI extends BasicToolBarSeparatorUI {
    private static ComponentUI toolBarSeparatorUI;

    public static ComponentUI createUI(JComponent c) {
        if (toolBarSeparatorUI == null) {
            toolBarSeparatorUI = new PlasticToolBarSeparatorUI();
        }
        return toolBarSeparatorUI;
    }
}
