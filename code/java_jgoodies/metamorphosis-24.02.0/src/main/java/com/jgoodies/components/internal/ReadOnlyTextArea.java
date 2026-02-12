package com.jgoodies.components.internal;

import com.jgoodies.components.util.ComponentUtils;
import javax.swing.JTextArea;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ReadOnlyTextArea.class */
public final class ReadOnlyTextArea extends JTextArea {
    public ReadOnlyTextArea(String text) {
        super(text);
        super.setEditable(false);
        ComponentUtils.clearFocusTraversalKeys(this);
    }

    public void setEditable(boolean editable) {
    }

    public void updateUI() {
        super.updateUI();
        ComponentSupport.configureTransparentBackground(this);
        setBorder(null);
    }
}
