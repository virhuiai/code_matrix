package com.jgoodies.framework.osx;

import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXUtils.class */
public final class OSXUtils {
    private static final String KEY_USE_SCREEN_MENU_BAR = "apple.laf.useScreenMenuBar";

    private OSXUtils() {
    }

    public static boolean getUseScreenMenuBar() {
        try {
            return "true".equalsIgnoreCase(System.getProperty(KEY_USE_SCREEN_MENU_BAR, "true"));
        } catch (SecurityException e) {
            return true;
        }
    }

    public static void setUseScreenMenuBar(boolean b) {
        try {
            System.setProperty(KEY_USE_SCREEN_MENU_BAR, b ? "true" : "false");
        } catch (SecurityException e) {
            Logger.getLogger(OSXUtils.class.getName()).warning("Can set the OS X screen menu bar placement only in trusted environments.");
        }
    }
}
