package com.jgoodies.components.internal;

import com.jgoodies.components.util.ComponentUtils;
import javax.swing.JEditorPane;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ReadOnlyEditorPane.class */
public final class ReadOnlyEditorPane extends JEditorPane {
    public ReadOnlyEditorPane(String type, String text) {
        super(type, text);
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
