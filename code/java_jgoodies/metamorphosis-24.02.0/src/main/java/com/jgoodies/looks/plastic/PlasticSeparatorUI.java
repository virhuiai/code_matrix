package com.jgoodies.looks.plastic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSeparatorUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticSeparatorUI.class */
public final class PlasticSeparatorUI extends MetalSeparatorUI {
    private static ComponentUI separatorUI;

    public static ComponentUI createUI(JComponent c) {
        if (separatorUI == null) {
            separatorUI = new PlasticSeparatorUI();
        }
        return separatorUI;
    }
}
