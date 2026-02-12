package com.jgoodies.components.internal;

import java.text.Format;
import javax.swing.JFormattedTextField;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ReadOnlyFormattedTextField.class */
public final class ReadOnlyFormattedTextField extends JFormattedTextField {
    public ReadOnlyFormattedTextField(Format format) {
        super(format);
        super.setEditable(false);
    }

    public ReadOnlyFormattedTextField(AbstractFormatter formatter) {
        super(formatter);
        super.setEditable(false);
    }

    public void setEditable(boolean editable) {
    }

    public void updateUI() {
        super.updateUI();
        ComponentSupport.configureTransparentBackground(this);
        setBorder(null);
    }
}
