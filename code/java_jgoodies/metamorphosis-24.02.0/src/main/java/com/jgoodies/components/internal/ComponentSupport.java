package com.jgoodies.components.internal;

import com.jgoodies.common.base.SystemUtils;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/ComponentSupport.class */
public final class ComponentSupport {
    private static final Color TRANSPARENT = new Color(255, 255, 255, 0);

    private ComponentSupport() {
    }

    public static void configureTransparentBackground(JComponent component) {
        if (SystemUtils.isLafAqua() || !(UIManager.getLookAndFeel() instanceof NimbusLookAndFeel)) {
            component.setOpaque(false);
        } else {
            component.setBackground(TRANSPARENT);
        }
    }

    public static boolean isMnemonicHidden() {
        return !isWindowsLookAndFeel() || WindowsLookAndFeel.isMnemonicHidden();
    }

    private static boolean isWindowsLookAndFeel() {
        return UIManager.getLookAndFeel().getID().equals("Windows");
    }
}
