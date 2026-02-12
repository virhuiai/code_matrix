package com.jgoodies.framework.util;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.prefs.Preferences;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/ScreenUtils.class */
public final class ScreenUtils {

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/ScreenUtils$ScreenLocation.class */
    public enum ScreenLocation {
        WEST,
        NORTH_WEST,
        NORTH,
        NORTH_EAST,
        EAST,
        SOUTH_EAST,
        SOUTH,
        SOUTH_WEST,
        CENTER,
        OPTICAL_CENTER
    }

    private ScreenUtils() {
    }

    public static void locateAt(Component component, ScreenLocation location) {
        int x;
        int y;
        Dimension paneSize = component.getSize();
        Rectangle screenBounds = screenBounds(component);
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[location.ordinal()]) {
            case 1:
            case AnimatedLabel.LEFT /* 2 */:
            case 3:
                x = screenBounds.x;
                break;
            case AnimatedLabel.RIGHT /* 4 */:
            case 5:
            case 6:
            case 7:
            default:
                x = screenBounds.x + ((screenBounds.width - paneSize.width) / 2);
                break;
            case AnimatedLabel.DEFAULT_FONT_EXTRA_SIZE /* 8 */:
            case 9:
            case 10:
                x = (screenBounds.x + screenBounds.width) - paneSize.width;
                break;
        }
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[location.ordinal()]) {
            case 1:
            case AnimatedLabel.RIGHT /* 4 */:
            case AnimatedLabel.DEFAULT_FONT_EXTRA_SIZE /* 8 */:
                y = screenBounds.y;
                break;
            case AnimatedLabel.LEFT /* 2 */:
            case 5:
            case 9:
                y = screenBounds.y + ((screenBounds.height - paneSize.height) / 2);
                break;
            case 3:
            case 7:
            case 10:
                y = (screenBounds.y + screenBounds.height) - paneSize.height;
                break;
            case 6:
            default:
                y = screenBounds.y + ((int) ((screenBounds.height - paneSize.height) * 0.45d));
                break;
        }
        component.setLocation(x, y);
    }

    /* renamed from: com.jgoodies.framework.util.ScreenUtils$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/ScreenUtils$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation = new int[ScreenLocation.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.NORTH_WEST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.WEST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.SOUTH_WEST.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.NORTH.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.CENTER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.OPTICAL_CENTER.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.SOUTH.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.NORTH_EAST.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.EAST.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$util$ScreenUtils$ScreenLocation[ScreenLocation.SOUTH_EAST.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
        }
    }

    public static Dimension getEffectiveScreenSize() {
        return ScreenScaling.toEffective(Toolkit.getDefaultToolkit().getScreenSize());
    }

    public static String encodedScreenConfiguration() {
        StringBuilder builder = new StringBuilder();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            builder.append(encodeRectangle(gc.getBounds()));
        }
        return builder.toString();
    }

    public static Preferences screenConfigurationNode(Preferences prefs) {
        Preferences node = prefs;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : ge.getScreenDevices()) {
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            node = node.node(encodeRectangle(gc.getBounds()));
        }
        return node;
    }

    private static String encodeRectangle(Rectangle r) {
        return String.format("[%1$s,%2$s,%3$s,%4$s]", Integer.valueOf(r.x), Integer.valueOf(r.y), Integer.valueOf(r.width), Integer.valueOf(r.height));
    }

    private static Rectangle screenBounds(Component c) {
        Dimension screenSize = c.getToolkit().getScreenSize();
        Insets screenInsets = c.getToolkit().getScreenInsets(c.getGraphicsConfiguration());
        return new Rectangle(screenInsets.left, screenInsets.top, screenSize.width - (screenInsets.left + screenInsets.right), screenSize.height - (screenInsets.top + screenInsets.bottom));
    }
}
