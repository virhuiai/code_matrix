package com.jgoodies.common.swing.internal;

import com.jgoodies.common.base.SystemUtils;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.PrintGraphics;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.print.PrinterGraphics;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/internal/RenderingUtils.class */
public final class RenderingUtils {
    private static final String PROP_DESKTOPHINTS = "awt.font.desktophints";
    private static final String SWING_UTILITIES2_NAME = "sun.swing.SwingUtilities2";
    private static final String BASIC_GRAPHIC_UTILS_NAME = "javax.swing.plaf.basic.BasicGraphicUtils";
    private static Method drawStringMethodJava8;
    private static Method drawStringMethodJava9;
    private static Method drawStringUnderlineCharAtMethodJava8;
    private static Method drawStringUnderlineCharAtMethodJava9;
    private static Method getStringWidthMethodJava8;
    private static Method getStringWidthMethodJava9;

    static {
        drawStringMethodJava8 = null;
        drawStringMethodJava9 = null;
        drawStringUnderlineCharAtMethodJava8 = null;
        drawStringUnderlineCharAtMethodJava9 = null;
        getStringWidthMethodJava8 = null;
        getStringWidthMethodJava9 = null;
        if (SystemUtils.IS_JAVA_8) {
            drawStringMethodJava8 = lookUpMethod(SWING_UTILITIES2_NAME, "drawString", JComponent.class, Graphics.class, String.class, Integer.TYPE, Integer.TYPE);
            drawStringUnderlineCharAtMethodJava8 = lookUpMethod(SWING_UTILITIES2_NAME, "drawStringUnderlineCharAt", JComponent.class, Graphics.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            getStringWidthMethodJava8 = lookUpMethod(SWING_UTILITIES2_NAME, "stringWidth", JComponent.class, FontMetrics.class, String.class);
        } else {
            drawStringMethodJava9 = lookUpMethod(BASIC_GRAPHIC_UTILS_NAME, "drawString", JComponent.class, Graphics.class, String.class, Float.TYPE, Float.TYPE);
            drawStringUnderlineCharAtMethodJava9 = lookUpMethod(BASIC_GRAPHIC_UTILS_NAME, "drawStringUnderlineCharAt", JComponent.class, Graphics.class, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            getStringWidthMethodJava9 = lookUpMethod(BASIC_GRAPHIC_UTILS_NAME, "getStringWidth", JComponent.class, FontMetrics.class, String.class);
        }
    }

    private RenderingUtils() {
    }

    public static void drawString(JComponent c, Graphics g, String text, int x, int y) {
        if (SystemUtils.IS_JAVA_8 && drawStringMethodJava8 != null) {
            drawStringMethodJava8.invoke(null, c, g, text, Integer.valueOf(x), Integer.valueOf(y));
            return;
        }
        if (drawStringMethodJava9 != null) {
            drawStringMethodJava9.invoke(null, c, g, text, Float.valueOf(x), Float.valueOf(y));
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        Map<RenderingHints.Key, Object> installDesktopHints = installDesktopHints(g2);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, -1, x, y);
        if (installDesktopHints != null) {
            g2.addRenderingHints(installDesktopHints);
        }
    }

    public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text, int underlinedIndex, int x, int y) {
        if (SystemUtils.IS_JAVA_8 && drawStringUnderlineCharAtMethodJava8 != null) {
            drawStringUnderlineCharAtMethodJava8.invoke(null, c, g, text, Integer.valueOf(underlinedIndex), Integer.valueOf(x), Integer.valueOf(y));
            return;
        }
        if (drawStringUnderlineCharAtMethodJava9 != null) {
            drawStringUnderlineCharAtMethodJava9.invoke(null, c, g, text, Integer.valueOf(underlinedIndex), Integer.valueOf(x), Integer.valueOf(y));
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        Map<RenderingHints.Key, Object> installDesktopHints = installDesktopHints(g2);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, underlinedIndex, x, y);
        if (installDesktopHints != null) {
            g2.addRenderingHints(installDesktopHints);
        }
    }

    public static int stringWidth(JComponent c, FontMetrics fm, String string) {
        if (SystemUtils.IS_JAVA_8 && getStringWidthMethodJava8 != null) {
            return ((Integer) getStringWidthMethodJava8.invoke(null, c, fm, string)).intValue();
        }
        if (getStringWidthMethodJava9 != null) {
            return ((Float) getStringWidthMethodJava9.invoke(null, c, fm, string)).intValue();
        }
        return fm.stringWidth(string);
    }

    private static Method lookUpMethod(String className, String methodName, Class<?>... argumentTypes) {
        try {
            return Class.forName(className).getMethod(methodName, argumentTypes);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            return null;
        }
    }

    private static Map<RenderingHints.Key, Object> installDesktopHints(Graphics2D g2) {
        Map<RenderingHints.Key, Object> oldRenderingHints = null;
        Map<RenderingHints.Key, Object> desktopHints = desktopHints(g2);
        if (desktopHints != null && !desktopHints.isEmpty()) {
            oldRenderingHints = new HashMap<>(desktopHints.size());
            for (RenderingHints.Key key : desktopHints.keySet()) {
                oldRenderingHints.put(key, g2.getRenderingHint(key));
            }
            g2.addRenderingHints(desktopHints);
        }
        return oldRenderingHints;
    }

    private static Map<RenderingHints.Key, Object> desktopHints(Graphics2D g2) {
        Object aaHint;
        if (isPrinting(g2)) {
            return null;
        }
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        GraphicsDevice device = g2.getDeviceConfiguration().getDevice();
        Map<RenderingHints.Key, Object> desktopHints = (Map) toolkit.getDesktopProperty("awt.font.desktophints." + device.getIDstring());
        if (desktopHints == null) {
            desktopHints = (Map) toolkit.getDesktopProperty(PROP_DESKTOPHINTS);
        }
        if (desktopHints != null && ((aaHint = desktopHints.get(RenderingHints.KEY_TEXT_ANTIALIASING)) == RenderingHints.VALUE_TEXT_ANTIALIAS_OFF || aaHint == RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) {
            desktopHints = null;
        }
        return desktopHints;
    }

    private static boolean isPrinting(Graphics g) {
        return (g instanceof PrintGraphics) || (g instanceof PrinterGraphics);
    }
}
