package com.jgoodies.components;

import com.jgoodies.components.internal.ActionConfigurationSupport;
import javax.swing.Action;
import javax.swing.JMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGMenu.class */
public class JGMenu extends JMenu {
    public JGMenu() {
    }

    public JGMenu(String text) {
        super(text);
    }

    public JGMenu(Action action) {
        super(action);
    }

    public JGMenu(String s, boolean b) {
        super(s, b);
    }

    protected void configurePropertiesFromAction(Action action) {
        super.configurePropertiesFromAction(action);
        ActionConfigurationSupport.configureAccessiblePropertiesFromAction(this, action);
    }
}
