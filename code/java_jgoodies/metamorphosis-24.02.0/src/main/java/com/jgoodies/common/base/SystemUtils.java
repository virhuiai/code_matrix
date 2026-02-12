package com.jgoodies.common.base;

import java.awt.Toolkit;
import java.util.logging.Logger;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/base/SystemUtils.class */
public final class SystemUtils {
    private static final String OS_NAME = getSystemProperty("os.name");
    private static final String JAVA_VERSION = getSystemProperty("java.version");
    public static final boolean IS_OS_LINUX;
    public static final boolean IS_OS_MAC;
    public static final boolean IS_OS_SOLARIS;
    public static final boolean IS_OS_WINDOWS;
    public static final boolean IS_OS_WINDOWS_11;
    public static final boolean IS_JAVA_8;
    public static final boolean IS_LAF_WINDOWS_XP_ENABLED;

    static {
        IS_OS_LINUX = startsWith(OS_NAME, "Linux") || startsWith(OS_NAME, "LINUX");
        IS_OS_MAC = startsWith(OS_NAME, "Mac OS");
        IS_OS_SOLARIS = startsWith(OS_NAME, "Solaris");
        IS_OS_WINDOWS = startsWith(OS_NAME, "Windows");
        IS_OS_WINDOWS_11 = startsWith(OS_NAME, "Windows 11");
        IS_JAVA_8 = startsWith(JAVA_VERSION, "1.8");
        IS_LAF_WINDOWS_XP_ENABLED = isWindowsXPLafEnabled();
    }

    public static boolean isLafAqua() {
        return UIManager.getLookAndFeel().getID().equals("Aqua");
    }

    private SystemUtils() {
    }

    private static String getSystemProperty(String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            Logger.getLogger(SystemUtils.class.getName()).warning("Can't access the System property " + key + ".");
            return "";
        }
    }

    private static boolean startsWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }

    private static boolean isWindowsXPLafEnabled() {
        return IS_OS_WINDOWS && Boolean.TRUE.equals(Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive")) && getSystemProperty("swing.noxp") == null;
    }
}
