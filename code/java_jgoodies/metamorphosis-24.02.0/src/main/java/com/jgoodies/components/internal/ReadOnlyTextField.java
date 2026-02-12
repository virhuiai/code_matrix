package com.jgoodies.components.internal;

import com.jgoodies.components.JGTextField;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ReadOnlyTextField.class */
public final class ReadOnlyTextField extends JGTextField {
    public ReadOnlyTextField(String text) {
        super(text);
        super.setEditable(false);
    }

    public void setEditable(boolean editable) {
    }

    @Override // com.jgoodies.components.JGTextField
    public void updateUI() {
        super.updateUI();
        ComponentSupport.configureTransparentBackground(this);
        setBorder(null);
    }
}
