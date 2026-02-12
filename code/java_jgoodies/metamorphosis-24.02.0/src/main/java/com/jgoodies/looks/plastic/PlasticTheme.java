package com.jgoodies.looks.plastic;

import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import java.awt.Color;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticTheme.class */
public abstract class PlasticTheme extends DefaultMetalTheme {
    public static final Color DARKEN_START = new Color(0, 0, 0, 0);
    public static final Color DARKEN_STOP = new Color(0, 0, 0, 64);
    public static final Color LT_DARKEN_STOP = new Color(0, 0, 0, 32);
    public static final Color BRIGHTEN_START = new Color(255, 255, 255, 0);
    public static final Color BRIGHTEN_STOP = new Color(255, 255, 255, 128);
    public static final Color LT_BRIGHTEN_STOP = new Color(255, 255, 255, 64);
    protected static final ColorUIResource WHITE = new ColorUIResource(255, 255, 255);
    protected static final ColorUIResource BLACK = new ColorUIResource(0, 0, 0);
    private FontSet fontSet;

    /* JADX INFO: Access modifiers changed from: protected */
    public ColorUIResource getBlack() {
        return BLACK;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ColorUIResource getWhite() {
        return WHITE;
    }

    public ColorUIResource getSystemTextColor() {
        return getControlInfo();
    }

    public ColorUIResource getTitleTextColor() {
        return getPrimary1();
    }

    public ColorUIResource getMenuForeground() {
        return getControlInfo();
    }

    public ColorUIResource getMenuItemBackground() {
        return getMenuBackground();
    }

    public ColorUIResource getMenuItemSelectedBackground() {
        return getMenuSelectedBackground();
    }

    public ColorUIResource getMenuItemSelectedForeground() {
        return getMenuSelectedForeground();
    }

    public ColorUIResource getSimpleInternalFrameForeground() {
        return getWhite();
    }

    public ColorUIResource getSimpleInternalFrameBackground() {
        return getPrimary1();
    }

    public ColorUIResource getToggleButtonCheckColor() {
        return getPrimary1();
    }

    public ColorUIResource getFocusColor() {
        return getBlack();
    }

    public FontUIResource getTitleTextFont() {
        return getFontSet().getTitleFont();
    }

    public FontUIResource getControlTextFont() {
        return getFontSet().getControlFont();
    }

    public FontUIResource getMenuTextFont() {
        return getFontSet().getMenuFont();
    }

    public FontUIResource getSubTextFont() {
        return getFontSet().getSmallFont();
    }

    public FontUIResource getSystemTextFont() {
        return getFontSet().getControlFont();
    }

    public FontUIResource getUserTextFont() {
        return getFontSet().getControlFont();
    }

    public FontUIResource getWindowTitleFont() {
        return getFontSet().getWindowTitleFont();
    }

    protected FontSet getFontSet() {
        if (this.fontSet == null) {
            FontPolicy policy = PlasticLookAndFeel.getFontPolicy();
            this.fontSet = policy.getFontSet("Plastic", null);
        }
        return this.fontSet;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        return getClass().equals(o.getClass());
    }

    public int hashCode() {
        return getClass().hashCode();
    }
}
