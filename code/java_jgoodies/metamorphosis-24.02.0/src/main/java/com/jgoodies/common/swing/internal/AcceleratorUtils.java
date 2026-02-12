package com.jgoodies.common.swing.internal;

import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/internal/AcceleratorUtils.class */
public final class AcceleratorUtils {
    private AcceleratorUtils() {
    }

    public static String getAcceleratorText(Component c, KeyStroke accelerator) {
        return getAcceleratorText(c, accelerator, UIManager.getString("MenuItem.acceleratorDelimiter"));
    }

    public static String getAcceleratorText(Component c, KeyStroke accelerator, String acceleratorDelimiter) {
        String accText;
        String accText2 = "";
        if (accelerator == null) {
            return accText2;
        }
        int modifiers = accelerator.getModifiers();
        if (modifiers > 0) {
            accText2 = KeyEvent.getKeyModifiersText(modifiers) + acceleratorDelimiter;
        }
        int keyCode = accelerator.getKeyCode();
        if (keyCode != 0) {
            accText = accText2 + KeyEvent.getKeyText(keyCode);
        } else {
            accText = accText2 + accelerator.getKeyChar();
        }
        return accText;
    }
}
