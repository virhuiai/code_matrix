package com.jgoodies.components.util;

import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.SystemUtils;
import java.io.Serializable;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/Modes.class */
public final class Modes implements Mode, Serializable {
    private final transient String name;
    private final int ordinal;
    private static LookAndFeel cachedLaf;
    private static boolean cachedIsLafAqua;
    private static boolean cachedIsLafWindows;
    public static final Modes ALWAYS = new Modes("Always");
    public static final Modes NEVER = new Modes("Never");
    public static final Modes LAF_AQUA = new Modes("Aqua l&f only");
    public static final Modes LAF_WINDOWS = new Modes("Windows l&f only");
    public static final Modes LAF_NON_AQUA = new Modes("No Aqua l&f");
    public static final Modes LAF_NON_WINDOWS = new Modes("No Windows l&f");
    public static final Modes OS_MAC = new Modes("Mac OS only");
    public static final Modes OS_WINDOWS = new Modes("Windows OS Only");
    public static final Modes OS_NON_MAC = new Modes("No Mac OS");
    public static final Modes OS_NON_WINDOWS = new Modes("No Windows OS");
    public static final Modes OS_LINUX = new Modes("Linux only");
    public static final Modes OS_NON_LINUX = new Modes("No Linux");
    private static int nextOrdinal = 0;
    private static final Modes[] VALUES = {ALWAYS, NEVER, LAF_AQUA, LAF_WINDOWS, LAF_NON_AQUA, LAF_NON_WINDOWS, OS_MAC, OS_WINDOWS, OS_NON_MAC, OS_NON_WINDOWS, OS_LINUX, OS_NON_LINUX};

    private Modes(String name) {
        int i = nextOrdinal;
        nextOrdinal = i + 1;
        this.ordinal = i;
        this.name = name;
    }

    @Override // com.jgoodies.components.util.Mode
    public boolean enabled() {
        if (this == ALWAYS) {
            return true;
        }
        if (this == NEVER) {
            return false;
        }
        if (this == OS_LINUX) {
            return SystemUtils.IS_OS_LINUX;
        }
        if (this == OS_MAC) {
            return SystemUtils.IS_OS_MAC;
        }
        if (this == OS_WINDOWS) {
            return SystemUtils.IS_OS_WINDOWS;
        }
        if (this == OS_NON_LINUX) {
            return !SystemUtils.IS_OS_LINUX;
        }
        if (this == OS_NON_MAC) {
            return !SystemUtils.IS_OS_MAC;
        }
        if (this == OS_NON_WINDOWS) {
            return !SystemUtils.IS_OS_WINDOWS;
        }
        if (this == LAF_AQUA) {
            return isLafAqua();
        }
        if (this == LAF_WINDOWS) {
            return isLafWindows();
        }
        if (this == LAF_NON_AQUA) {
            return !isLafAqua();
        }
        if (this == LAF_NON_WINDOWS) {
            return !isLafWindows();
        }
        throw new IllegalStateException("Unknown mode");
    }

    public String toString() {
        return "Mode: " + this.name + " currently " + (enabled() ? ComponentModel.PROPERTY_ENABLED : "disabled");
    }

    private Object readResolve() {
        return VALUES[this.ordinal];
    }

    public static boolean isLafAqua() {
        ensureValidCache();
        return cachedIsLafAqua;
    }

    public static boolean isLafWindows() {
        ensureValidCache();
        return cachedIsLafWindows;
    }

    private static void ensureValidCache() {
        LookAndFeel currentLaf = UIManager.getLookAndFeel();
        if (cachedLaf != currentLaf) {
            cachedLaf = currentLaf;
            cachedIsLafAqua = currentLaf.getID().equals("Aqua");
            cachedIsLafWindows = currentLaf.getID().equals("Windows");
        }
    }
}
