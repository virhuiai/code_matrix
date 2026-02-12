package com.jgoodies.common.jsdl;

import java.awt.Frame;
import javax.swing.JOptionPane;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/JSDLCommonSetup.class */
public final class JSDLCommonSetup {
    private static boolean optimizedForScreenReader;

    private JSDLCommonSetup() {
    }

    public static Frame getRootFrame() {
        return JOptionPane.getRootFrame();
    }

    public static void setRootFrame(Frame newRootFrame) {
        JOptionPane.setRootFrame(newRootFrame);
    }

    public static boolean isOptimizedForScreenReader() {
        return optimizedForScreenReader;
    }

    public static void setOptimizedForScreenReader(boolean value) {
        optimizedForScreenReader = value;
    }
}
