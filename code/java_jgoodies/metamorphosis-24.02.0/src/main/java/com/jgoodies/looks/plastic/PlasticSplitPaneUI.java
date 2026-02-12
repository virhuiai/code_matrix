package com.jgoodies.looks.plastic;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticSplitPaneUI.class */
public final class PlasticSplitPaneUI extends BasicSplitPaneUI {
    public static ComponentUI createUI(JComponent x) {
        return new PlasticSplitPaneUI();
    }

    public BasicSplitPaneDivider createDefaultDivider() {
        return new PlasticSplitPaneDivider(this);
    }
}
