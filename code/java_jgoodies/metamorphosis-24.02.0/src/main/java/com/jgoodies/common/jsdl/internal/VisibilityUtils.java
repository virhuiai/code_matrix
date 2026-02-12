package com.jgoodies.common.jsdl.internal;

import com.jgoodies.common.base.Strings;
import com.jgoodies.common.jsdl.icon.IconValue;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/VisibilityUtils.class */
public final class VisibilityUtils {
    private VisibilityUtils() {
    }

    public static void setIconAndVisibility(JLabel label, IconValue icon) {
        label.setVisible(icon != null);
        if (icon != null) {
            label.setIcon(icon.toIcon());
        }
    }

    public static void setTextAndVisibility(JLabel label, String str) {
        boolean notBlank = Strings.isNotBlank(str);
        label.setVisible(notBlank);
        if (notBlank) {
            label.setText(str);
        }
    }

    public static void setTextAndVisibilityAndForeground(JLabel label, String str, Color foreground) {
        boolean notBlank = Strings.isNotBlank(str);
        label.setVisible(notBlank);
        if (notBlank) {
            label.setText(str);
            if (foreground != null) {
                label.setForeground(foreground);
            }
        }
    }

    public static void setTextAndVisibility(JTextComponent c, String str) {
        boolean notBlank = Strings.isNotBlank(str);
        c.setVisible(notBlank);
        if (notBlank) {
            c.setText(str);
        }
    }

    public static void setTextAndVisibilityAndForeground(JTextComponent c, String str, Color foreground) {
        boolean notBlank = Strings.isNotBlank(str);
        c.setVisible(notBlank);
        if (notBlank) {
            c.setText(str);
            if (foreground != null) {
                c.setForeground(foreground);
            }
        }
    }
}
