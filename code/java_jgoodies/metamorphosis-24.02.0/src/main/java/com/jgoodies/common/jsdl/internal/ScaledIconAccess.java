package com.jgoodies.common.jsdl.internal;

import com.jgoodies.application.ResourceMap;
import com.jgoodies.common.jsdl.util.IconUtils;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Insets;
import java.net.URL;
import java.util.MissingResourceException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.IconUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/ScaledIconAccess.class */
public final class ScaledIconAccess {
    private ScaledIconAccess() {
    }

    public static Icon getIcon(ResourceMap map, String baseKey) {
        return readIcon(map, baseKey, ScreenScaling.getScaleFactor());
    }

    public static Icon getIcon(Class<?> clazz, String filename, String description, Insets insets) {
        return new IconUIResource(readIcon(clazz, filename, description, insets));
    }

    private static Icon readIcon(Class<?> clazz, String filename, String description, Insets insets) {
        Icon icon = readIcon(clazz, filename, description, ScreenScaling.getScaleFactor());
        return (icon == null || insets == null) ? icon : IconUtils.crop(icon, insets);
    }

    private static Icon readIcon(Class<?> clazz, String filename, String description, ScreenScaling.ScaleFactor factor) {
        String suffix;
        String strippedName;
        URL fallbackURL1;
        int suffixIndex = filename.lastIndexOf(46);
        if (suffixIndex == -1) {
            suffix = "";
            strippedName = filename;
        } else {
            suffix = filename.substring(suffixIndex);
            strippedName = filename.substring(0, suffixIndex);
        }
        URL scaledURL = clazz.getResource(strippedName + ".scale-" + factor.intValue() + suffix);
        if (scaledURL != null) {
            return new ImageIcon(scaledURL, description);
        }
        if (factor.intValue() >= 200 && (fallbackURL1 = clazz.getResource(strippedName + ".scale-200" + suffix)) != null) {
            return new ImageIcon(fallbackURL1, description);
        }
        URL fallbackURL12 = clazz.getResource(strippedName + ".scale-100" + suffix);
        if (fallbackURL12 != null) {
            return new ImageIcon(fallbackURL12, description);
        }
        URL fallbackURL = clazz.getResource(filename);
        if (fallbackURL != null) {
            return new ImageIcon(fallbackURL, description);
        }
        return null;
    }

    private static Icon readIcon(ResourceMap map, String baseKey, ScreenScaling.ScaleFactor factor) {
        String suffix = factor.intValue() >= 200 ? "200" : "100";
        try {
            return map.getIcon(baseKey + "." + suffix);
        } catch (MissingResourceException e) {
            return map.getIcon(baseKey);
        }
    }
}
