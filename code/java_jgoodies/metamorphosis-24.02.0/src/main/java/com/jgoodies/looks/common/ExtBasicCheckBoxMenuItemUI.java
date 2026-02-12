package com.jgoodies.looks.common;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ExtBasicCheckBoxMenuItemUI.class */
public final class ExtBasicCheckBoxMenuItemUI extends ExtBasicRadioButtonMenuItemUI {
    @Override // com.jgoodies.looks.common.ExtBasicRadioButtonMenuItemUI
    protected String getPropertyPrefix() {
        return "CheckBoxMenuItem";
    }

    public static ComponentUI createUI(JComponent b) {
        return new ExtBasicCheckBoxMenuItemUI();
    }
}
