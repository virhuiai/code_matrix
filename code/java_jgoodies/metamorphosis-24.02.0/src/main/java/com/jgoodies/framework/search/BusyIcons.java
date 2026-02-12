package com.jgoodies.framework.search;

import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/BusyIcons.class */
public final class BusyIcons {
    private static final ResourceMap RESOURCES = Application.getResourceMap(BusyIcons.class);
    private static final Icon[] ICONS = initIcons();

    private BusyIcons() {
    }

    public static Icon[] getIcons() {
        return ICONS;
    }

    private static Icon[] initIcons() {
        int size = RESOURCES.getInt("BusyIcons.size");
        Icon[] icons = new Icon[size];
        for (int i = 0; i < size; i++) {
            icons[i] = RESOURCES.getIcon("BusyIcons[" + i + "]");
        }
        return icons;
    }
}
