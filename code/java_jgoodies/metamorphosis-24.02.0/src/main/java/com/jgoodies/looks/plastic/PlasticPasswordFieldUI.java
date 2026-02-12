package com.jgoodies.looks.plastic;

import com.jgoodies.looks.common.FieldCaret;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.Caret;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticPasswordFieldUI.class */
public final class PlasticPasswordFieldUI extends BasicPasswordFieldUI {
    public static ComponentUI createUI(JComponent c) {
        return new PlasticPasswordFieldUI();
    }

    protected Caret createCaret() {
        return new FieldCaret();
    }
}
