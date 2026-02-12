package com.jgoodies.looks;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/HeaderStyle.class */
public enum HeaderStyle {
    SINGLE,
    BOTH;

    public static final String KEY = "jgoodies.headerStyle";

    public static HeaderStyle from(JMenuBar menuBar) {
        return from0(menuBar);
    }

    public static HeaderStyle from(JToolBar toolBar) {
        return from0(toolBar);
    }

    private static HeaderStyle from0(JComponent c) {
        Object value = c.getClientProperty(KEY);
        if (value instanceof HeaderStyle) {
            return (HeaderStyle) value;
        }
        if (value instanceof String) {
            String name = ((String) value).toUpperCase();
            return valueOf(name);
        }
        return null;
    }
}
