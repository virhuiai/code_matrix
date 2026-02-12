package com.jgoodies.layout.factories;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/ComponentFactory.class */
public interface ComponentFactory {
    JButton createButton(Action action);

    JLabel createLabel(String str);

    JLabel createReadOnlyLabel(String str);

    JLabel createTitle(String str);

    JLabel createHeaderLabel(String str);

    JComponent createSeparator(String str, int i);
}
