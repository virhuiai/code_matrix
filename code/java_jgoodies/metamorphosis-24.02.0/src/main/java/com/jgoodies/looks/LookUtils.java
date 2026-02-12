package com.jgoodies.looks;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;
import java.awt.Color;
import java.awt.Component;
import java.util.Collections;
import java.util.List;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/LookUtils.class */
public final class LookUtils {
    private static boolean loggingEnabled = true;

    private LookUtils() {
    }

    public static String getSystemProperty(String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            log("Can't read the System property " + key + ".");
            return null;
        }
    }

    public static String getSystemProperty(String key, String defaultValue) {
        try {
            return System.getProperty(key, defaultValue);
        } catch (SecurityException e) {
            log("Can't read the System property " + key + ".");
            return defaultValue;
        }
    }

    public static Boolean getBooleanSystemProperty(String key, String logMessage) {
        Boolean result;
        String value = getSystemProperty(key, "").toLowerCase();
        boolean z = -1;
        switch (value.hashCode()) {
            case 3569038:
                if (value.equals("true")) {
                    z = true;
                    break;
                }
                break;
            case 97196323:
                if (value.equals("false")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                result = Boolean.FALSE;
                break;
            case true:
                result = Boolean.TRUE;
                break;
            default:
                result = null;
                break;
        }
        if (result != null) {
            log(logMessage + " have been " + (result.booleanValue() ? "en" : "dis") + "abled in the system properties.");
        }
        return result;
    }

    public static boolean isTrueColor(Component c) {
        return c.getToolkit().getColorModel().getPixelSize() >= 24;
    }

    public static boolean getToolkitUsesNativeDropShadows() {
        return SystemUtils.IS_OS_MAC;
    }

    public static Color getSlightlyBrighter(Color color) {
        return getSlightlyBrighter(color, 1.1f);
    }

    public static Color getSlightlyBrighter(Color color, float factor) {
        float[] hsbValues = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbValues);
        float hue = hsbValues[0];
        float saturation = hsbValues[1];
        float brightness = hsbValues[2];
        float newBrightness = Math.min(brightness * factor, 1.0f);
        return Color.getHSBColor(hue, saturation, newBrightness);
    }

    public static void setLookAndTheme(LookAndFeel laf, Object theme) throws UnsupportedLookAndFeelException {
        if ((laf instanceof PlasticLookAndFeel) && (theme instanceof PlasticTheme)) {
            PlasticLookAndFeel.setPlasticTheme((PlasticTheme) theme);
        }
        UIManager.setLookAndFeel(laf);
    }

    public static PlasticTheme getDefaultTheme(LookAndFeel laf) {
        if (laf instanceof PlasticLookAndFeel) {
            return PlasticLookAndFeel.createMyDefaultTheme();
        }
        return null;
    }

    public static List<PlasticTheme> getInstalledThemes(LookAndFeel laf) {
        return laf instanceof PlasticLookAndFeel ? PlasticLookAndFeel.getInstalledThemes() : Collections.EMPTY_LIST;
    }

    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    public static void log() {
        if (loggingEnabled) {
            System.out.println();
        }
    }

    public static void log(String message) {
        if (loggingEnabled) {
            System.out.println("JGoodies Looks: " + message);
        }
    }
}
