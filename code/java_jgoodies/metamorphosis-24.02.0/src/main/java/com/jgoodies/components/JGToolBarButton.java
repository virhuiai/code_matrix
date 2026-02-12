package com.jgoodies.components;

import com.jgoodies.components.internal.ToolBarComponentUtils;
import javax.swing.Action;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGToolBarButton.class */
public class JGToolBarButton extends JGButton {
    public JGToolBarButton() {
        this((Icon) null);
    }

    public JGToolBarButton(Icon icon) {
        super(icon);
        configureButton();
    }

    public JGToolBarButton(Action action) {
        super(action);
        configureButton();
    }

    private void configureButton() {
        ToolBarComponentUtils.configureButton(this);
        setDefaultCapable(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.components.JGButton
    public void configurePropertiesFromAction(Action a) {
        super.configurePropertiesFromAction(a);
        setHideActionText(getIcon() != null);
        ToolBarComponentUtils.setFallbackToolTipText(this, a);
    }
}
