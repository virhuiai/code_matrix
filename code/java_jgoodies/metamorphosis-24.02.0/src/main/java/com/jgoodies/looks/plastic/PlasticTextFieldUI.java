package com.jgoodies.looks.plastic;

import com.jgoodies.looks.common.FieldCaret;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTextFieldUI;
import javax.swing.text.Caret;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticTextFieldUI.class */
public final class PlasticTextFieldUI extends MetalTextFieldUI {
    public static ComponentUI createUI(JComponent c) {
        return new PlasticTextFieldUI();
    }

    protected Caret createCaret() {
        return new FieldCaret();
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = super.getPreferredSize(c);
        return new Dimension(dim.width + 1, dim.height);
    }
}
