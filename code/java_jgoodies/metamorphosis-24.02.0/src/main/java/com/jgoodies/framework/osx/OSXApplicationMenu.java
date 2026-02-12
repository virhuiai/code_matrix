package com.jgoodies.framework.osx;

import com.jgoodies.common.base.SystemUtils;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/osx/OSXApplicationMenu.class */
public final class OSXApplicationMenu {
    private static final String KEY_MAC_APPLICATION_MENU_ABOUT_NAME = "apple.awt.application.name";
    static ActionListener aboutListener;
    static ActionListener prefsListener;
    private static boolean registered = false;

    private OSXApplicationMenu() {
    }

    public static void setAboutName(String aboutName) {
        try {
            System.setProperty(KEY_MAC_APPLICATION_MENU_ABOUT_NAME, aboutName);
        } catch (SecurityException e) {
            Logger.getLogger(OSXApplicationMenu.class.getName()).warning("Can set the OS X application menu name only in trusted environments.");
        }
    }

    public static synchronized boolean register(ActionListener about, ActionListener preferences) {
        if (about == null && preferences == null) {
            return false;
        }
        aboutListener = about;
        prefsListener = preferences;
        registered = register();
        return registered;
    }

    public static boolean isRegisteredAbout() {
        return registered && aboutListener != null;
    }

    public static boolean isRegisteredPreferences() {
        return registered && prefsListener != null;
    }

    public static boolean isRegistered() {
        return registered;
    }

    private static boolean register() {
        if (!SystemUtils.IS_OS_MAC) {
            return false;
        }
        if (SystemUtils.IS_JAVA_8) {
            OSXApplicationHandler8.register();
            return true;
        }
        OSXApplicationHandler9.register();
        return true;
    }
}
