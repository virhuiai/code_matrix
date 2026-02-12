package com.jgoodies.looks.plastic;

import com.jgoodies.looks.common.FieldCaret;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import javax.swing.text.Caret;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticFormattedTextFieldUI.class */
public final class PlasticFormattedTextFieldUI extends BasicFormattedTextFieldUI {
    public static ComponentUI createUI(JComponent c) {
        return new PlasticFormattedTextFieldUI();
    }

    protected Caret createCaret() {
        return new FieldCaret();
    }

    public Dimension getPreferredSize(JComponent c) {
        Dimension dim = super.getPreferredSize(c);
        return new Dimension(dim.width + 1, dim.height);
    }
}
