package com.jgoodies.looks;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/BorderStyle.class */
public enum BorderStyle {
    EMPTY("emptyBorder"),
    SEPARATOR("separatorBorder"),
    ETCHED("etchedBorder");

    private final String suffix;
    public static final String WINDOWS_KEY = "jgoodies.windows.borderStyle";
    public static final String PLASTIC_KEY = "Plastic.borderStyle";

    BorderStyle(String suffix) {
        this.suffix = suffix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public static BorderStyle from(JToolBar toolBar, String clientPropertyKey) {
        return from0(toolBar, clientPropertyKey);
    }

    public static BorderStyle from(JMenuBar menuBar, String clientPropertyKey) {
        return from0(menuBar, clientPropertyKey);
    }

    private static BorderStyle from0(JComponent c, String clientPropertyKey) {
        Object value = c.getClientProperty(clientPropertyKey);
        if (value instanceof BorderStyle) {
            return (BorderStyle) value;
        }
        if (value instanceof String) {
            String name = ((String) value).toUpperCase();
            return valueOf(name);
        }
        return null;
    }
}
