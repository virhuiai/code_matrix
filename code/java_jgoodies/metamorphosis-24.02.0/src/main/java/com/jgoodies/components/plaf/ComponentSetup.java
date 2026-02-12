package com.jgoodies.components.plaf;

import com.jgoodies.components.plaf.basic.BasicComponentSetup;
import com.jgoodies.components.plaf.mac.MacComponentSetup;
import com.jgoodies.components.plaf.windows.WindowsMetroComponentSetup;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/ComponentSetup.class */
public final class ComponentSetup {
    private static BasicComponentSetup setup;
    private static LookAndFeel cachedLookAndFeel;

    private ComponentSetup() {
    }

    public static void ensureSetup() {
        getSetup();
    }

    public static BasicComponentSetup getSetup() {
        LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
        if (cachedLookAndFeel != currentLookAndFeel) {
            BasicComponentSetup newSetup = lookUpSetup(currentLookAndFeel);
            updateSetup(setup, newSetup);
            cachedLookAndFeel = currentLookAndFeel;
        }
        return setup;
    }

    public static void setSetup(BasicComponentSetup newSetup) {
        updateSetup(getSetup(), newSetup);
    }

    private static void updateSetup(BasicComponentSetup oldSetup, BasicComponentSetup newSetup) {
        if (oldSetup == newSetup) {
            return;
        }
        if (newSetup != null) {
            newSetup.install(UIManager.getDefaults());
        }
        setup = newSetup;
    }

    private static BasicComponentSetup lookUpSetup(LookAndFeel laf) {
        if ("Windows".equals(laf.getID())) {
            return new WindowsMetroComponentSetup();
        }
        if ("Plastic".equals(laf.getID())) {
            return new WindowsMetroComponentSetup();
        }
        if ("Aqua".equals(laf.getID())) {
            return new MacComponentSetup();
        }
        return new BasicComponentSetup();
    }
}
