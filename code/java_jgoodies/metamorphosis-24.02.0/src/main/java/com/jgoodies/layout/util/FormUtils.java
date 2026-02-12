package com.jgoodies.layout.util;

import com.jgoodies.common.base.SystemUtils;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/util/FormUtils.class */
public final class FormUtils {
    private static LookAndFeel cachedLookAndFeel;
    private static Boolean cachedIsLafAqua;

    private FormUtils() {
    }

    public static boolean isLafAqua() {
        ensureValidCache();
        if (cachedIsLafAqua == null) {
            cachedIsLafAqua = Boolean.valueOf(SystemUtils.isLafAqua());
        }
        return cachedIsLafAqua.booleanValue();
    }

    public static void clearLookAndFeelBasedCaches() {
        cachedIsLafAqua = null;
        DefaultUnitConverter.getInstance().clearCache();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void ensureValidCache() {
        LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
        if (currentLookAndFeel != cachedLookAndFeel) {
            clearLookAndFeelBasedCaches();
            cachedLookAndFeel = currentLookAndFeel;
        }
    }
}
