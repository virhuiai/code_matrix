package com.jgoodies.components;

import com.jgoodies.components.internal.ActionConfigurationSupport;
import com.jgoodies.components.internal.ToolBarComponentUtils;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToggleButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGToolBarToggleButton.class */
public class JGToolBarToggleButton extends JToggleButton {
    public JGToolBarToggleButton() {
        configureButton();
    }

    public JGToolBarToggleButton(Icon icon) {
        super(icon);
        configureButton();
    }

    public JGToolBarToggleButton(Action action) {
        super(action);
        configureButton();
    }

    private void configureButton() {
        ToolBarComponentUtils.configureButton(this);
    }

    protected void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        ActionConfigurationSupport.configureAccessiblePropertiesFromAction(this, a);
        setHideActionText(getIcon() != null);
        ToolBarComponentUtils.setFallbackToolTipText(this, a);
    }
}
