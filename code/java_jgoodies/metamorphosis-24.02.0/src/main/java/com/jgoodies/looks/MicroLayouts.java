package com.jgoodies.looks;

import java.awt.Insets;
import javax.swing.plaf.InsetsUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/MicroLayouts.class */
public final class MicroLayouts {
    private static final InsetsUIResource PLASTIC_MENU_ITEM_MARGIN = new InsetsUIResource(3, 0, 3, 0);
    private static final InsetsUIResource PLASTIC_MENU_MARGIN = new InsetsUIResource(2, 4, 2, 4);
    private static final InsetsUIResource PLASTIC_CHECK_BOX_MARGIN = new InsetsUIResource(2, 0, 2, 1);

    private MicroLayouts() {
    }

    public static MicroLayout createPlasticMicroLayout100() {
        return new MicroLayout(new InsetsUIResource(1, 1, 2, 1), new InsetsUIResource(2, 2, 2, 1), new InsetsUIResource(1, 1, 2, 1), -1, 1, new Insets(2, 3, 3, 3), new InsetsUIResource(1, 4, 1, 4), 6, PLASTIC_CHECK_BOX_MARGIN, PLASTIC_MENU_ITEM_MARGIN, PLASTIC_MENU_MARGIN, null);
    }

    public static MicroLayout createPlasticMicroLayout125() {
        return new MicroLayout(new InsetsUIResource(1, 1, 2, 1), new InsetsUIResource(2, 2, 2, 1), new InsetsUIResource(1, 1, 2, 1), -1, 1, new Insets(1, 3, 1, 3), new InsetsUIResource(2, 5, 3, 5), 7, PLASTIC_CHECK_BOX_MARGIN, PLASTIC_MENU_ITEM_MARGIN, PLASTIC_MENU_MARGIN, null);
    }

    public static MicroLayout createPlasticModernMicroLayout100() {
        return new MicroLayout(new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), -1, 1, new Insets(2, 2, 2, 2), new InsetsUIResource(1, 4, 2, 4), 6, PLASTIC_CHECK_BOX_MARGIN, PLASTIC_MENU_ITEM_MARGIN, PLASTIC_MENU_MARGIN, null);
    }

    public static MicroLayout createPlasticModernMicroLayout125() {
        return new MicroLayout(new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), -1, 1, new Insets(2, 2, 2, 2), new InsetsUIResource(1, 5, 2, 5), 7, PLASTIC_CHECK_BOX_MARGIN, PLASTIC_MENU_ITEM_MARGIN, PLASTIC_MENU_MARGIN, null);
    }

    public static MicroLayout createPlasticModernMicroLayoutHigh(int scaleFactorPercent) {
        int h1 = (1 * scaleFactorPercent) / 100;
        int h2 = (2 * scaleFactorPercent) / 100;
        int h4 = (4 * scaleFactorPercent) / 100;
        int h6 = (6 * scaleFactorPercent) / 100;
        int v1 = (2 * scaleFactorPercent) / 100;
        int v2 = (2 * scaleFactorPercent) / 100;
        int v3 = (3 * scaleFactorPercent) / 100;
        InsetsUIResource textInsets = new InsetsUIResource(v2, h2, v2, h2);
        return new MicroLayout(textInsets, textInsets, textInsets, -1, 1, new Insets(2, 2, 2, 2), new InsetsUIResource(v1, h4, v2, h4), h6, new InsetsUIResource(v2, 0, v2, h1), new InsetsUIResource(v3, h1, v3, h1), new InsetsUIResource(v2, h4, v2, h4), null);
    }

    public static MicroLayout createWindowsClassicMicroLayout100() {
        return new MicroLayout(new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), 2, 1, new Insets(3, 2, 4, 2), new InsetsUIResource(1, 4, 1, 4), 6, new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(3, 0, 3, 0), new InsetsUIResource(2, 3, 2, 3), new InsetsUIResource(2, 0, 3, 0));
    }

    public static MicroLayout createWindowsClassicMicroLayout125() {
        return new MicroLayout(new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), new InsetsUIResource(1, 2, 2, 2), 2, 1, new Insets(3, 2, 4, 2), new InsetsUIResource(1, 5, 1, 5), 7, new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(2, 4, 2, 4), new InsetsUIResource(3, 0, 4, 0));
    }

    public static MicroLayout createWindowsModernMicroLayout100() {
        InsetsUIResource textInsets = new InsetsUIResource(1, 2, 2, 2);
        return new MicroLayout(textInsets, textInsets, textInsets, 1, 1, new Insets(3, 2, 4, 2), new InsetsUIResource(1, 4, 2, 4), 6, new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(3, 0, 3, 0), new InsetsUIResource(2, 3, 2, 4), new InsetsUIResource(2, 3, 3, 3));
    }

    public static MicroLayout createWindowsModernMicroLayout125() {
        InsetsUIResource textInsets = new InsetsUIResource(1, 2, 2, 2);
        return new MicroLayout(textInsets, textInsets, textInsets, 1, 1, new Insets(3, 2, 4, 2), new InsetsUIResource(1, 5, 2, 5), 7, new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(2, 0, 2, 0), new InsetsUIResource(2, 5, 2, 6), new InsetsUIResource(3, 3, 4, 3));
    }

    public static MicroLayout createWindowsModernMicroLayoutHigh(int scaleFactorPercent) {
        int h2 = (2 * scaleFactorPercent) / 100;
        int h3 = (3 * scaleFactorPercent) / 100;
        int h4 = (4 * scaleFactorPercent) / 100;
        int h6 = (6 * scaleFactorPercent) / 100;
        int v1 = (1 * scaleFactorPercent) / 100;
        int v2 = (2 * scaleFactorPercent) / 100;
        int v4 = (4 * scaleFactorPercent) / 100;
        InsetsUIResource textInsets = new InsetsUIResource(v2, h2, v2, h2);
        return new MicroLayout(textInsets, textInsets, textInsets, 1, 1, new Insets(3, 2, 4, 2), new InsetsUIResource(v1, h4, v2, h4), h6, new InsetsUIResource(v2, 0, v2, 0), new InsetsUIResource(v2, h4, v2, h4), new InsetsUIResource(v2, h4, v2, h4), new InsetsUIResource(v4, h3, v4, h3));
    }
}
