package com.jgoodies.looks.plastic;

import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.looks.common.ExtDefaultCaret;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticTextAreaUI.class */
public final class PlasticTextAreaUI extends BasicTextAreaUI {
    public static ComponentUI createUI(JComponent c) {
        return new PlasticTextAreaUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        updateBackground((JTextComponent) c);
    }

    protected Caret createCaret() {
        return new ExtDefaultCaret();
    }

    protected void propertyChange(PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String propertyName = evt.getPropertyName();
        if (ComponentModel.PROPERTY_EDITABLE.equals(propertyName) || ComponentModel.PROPERTY_ENABLED.equals(propertyName)) {
            updateBackground((JTextComponent) evt.getSource());
        }
    }

    private static void updateBackground(JTextComponent c) {
        Color background = c.getBackground();
        if (!(background instanceof UIResource)) {
            return;
        }
        Color newColor = null;
        if (!c.isEnabled()) {
            newColor = UIManager.getColor("TextArea.disabledBackground");
        }
        if (newColor == null && !c.isEditable()) {
            newColor = UIManager.getColor("TextArea.inactiveBackground");
        }
        if (newColor == null) {
            newColor = UIManager.getColor("TextArea.background");
        }
        if (newColor != null && newColor != background) {
            c.setBackground(newColor);
        }
    }
}
