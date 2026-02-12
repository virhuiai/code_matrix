package com.jgoodies.components.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/ComponentUtils.class */
public final class ComponentUtils {
    private ComponentUtils() {
    }

    public static void clearFocusTraversalKeys(JComponent c) {
        c.setFocusTraversalKeys(1, (Set) null);
        c.setFocusTraversalKeys(0, (Set) null);
    }

    public static void registerKeyboardAction(JComponent component, Action action) {
        registerKeyboardAction(component, action, 0);
    }

    public static void registerKeyboardAction(JComponent component, Action action, int condition) {
        KeyStroke keyStroke = (KeyStroke) action.getValue("AcceleratorKey");
        registerKeyboardAction(component, action, keyStroke, condition);
    }

    public static void registerKeyboardAction(JComponent component, Action action, KeyStroke keyStroke) {
        registerKeyboardAction(component, action, keyStroke, 0);
    }

    public static void registerKeyboardAction(JComponent component, Action action, KeyStroke keyStroke, int condition) {
        if (keyStroke != null) {
            component.getInputMap(condition).put(keyStroke, action);
            component.getActionMap().put(action, action);
        }
    }

    public static void registerEnterAction(JComponent component, Action action) {
        registerKeyboardAction(component, action, KeyStroke.getKeyStroke(10, 0));
    }

    public static void registerEnterAction(Action action, JComponent... components) {
        Preconditions.checkNotNull(components, Messages.MUST_NOT_BE_NULL, "components");
        for (JComponent c : components) {
            registerEnterAction(c, action);
        }
    }

    public static void unregisterKeyboardAction(JComponent component, Action action) {
        unregisterKeyboardAction(component, action, 0);
    }

    public static void unregisterKeyboardAction(JComponent component, Action action, int condition) {
        KeyStroke keyStroke = (KeyStroke) action.getValue("AcceleratorKey");
        unregisterKeyboardAction(component, action, keyStroke, condition);
    }

    public static void unregisterKeyboardAction(JComponent component, Action action, KeyStroke keyStroke) {
        unregisterKeyboardAction(component, action, keyStroke, 0);
    }

    public static void unregisterKeyboardAction(JComponent component, Action action, KeyStroke keyStroke, int condition) {
        if (keyStroke != null) {
            component.getInputMap(condition).remove(keyStroke);
            component.getActionMap().remove(action);
        }
    }

    public static void setHorizontalAlignment(int alignment, JTextField... fields) {
        Preconditions.checkNotNull(fields, Messages.MUST_NOT_BE_NULL, "text field array");
        for (JTextField textField : fields) {
            textField.setHorizontalAlignment(alignment);
        }
    }
}
