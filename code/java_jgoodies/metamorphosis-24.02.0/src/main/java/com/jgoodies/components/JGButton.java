package com.jgoodies.components;

import com.jgoodies.components.internal.ActionConfigurationSupport;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGButton.class */
public class JGButton extends JButton {
    public JGButton() {
    }

    public JGButton(Action a) {
        super(a);
    }

    public JGButton(Icon icon) {
        super(icon);
    }

    public JGButton(String text) {
        super(text);
    }

    public JGButton(String text, Icon icon) {
        super(text, icon);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void configurePropertiesFromAction(Action action) {
        super.configurePropertiesFromAction(action);
        ActionConfigurationSupport.configureAccessiblePropertiesFromAction(this, action);
    }
}
