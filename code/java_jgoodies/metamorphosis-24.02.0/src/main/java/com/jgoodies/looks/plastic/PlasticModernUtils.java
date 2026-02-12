package com.jgoodies.looks.plastic;

import java.awt.Color;
import java.awt.Graphics;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernUtils.class */
public final class PlasticModernUtils {
    static final Color GRAY1 = new Color(240, 240, 240);
    static final Color GRAY2 = new Color(191, 191, 191);
    static final Color GRAY3 = new Color(173, 173, 173);
    static final Color LIGHT_BLUE = new Color(0, 120, 215);
    static final Color DARK_BLUE = new Color(0, 84, 153);

    private PlasticModernUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawPlainButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawButtonBorder(g, x, y, w, h, GRAY1, PlasticLookAndFeel.getControlDarkShadow());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawRolloverButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawButtonBorder(g, x, y, w, h, GRAY1, LIGHT_BLUE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawPressedButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawButtonBorder(g, x, y, w, h, GRAY1, DARK_BLUE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawDefaultButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawRolloverButtonBorder(g, x, y, w, h);
        g.setColor(LIGHT_BLUE);
        PlasticUtils.drawRect(g, x + 2, y + 2, w - 5, h - 5);
    }

    static void drawSelectedButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawButtonBorder(g, x, y, w, h, GRAY1, DARK_BLUE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawDisabledButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawButtonBorder(g, x, y, w, h, GRAY1, GRAY2);
    }

    private static void drawButtonBorder(Graphics g, int x, int y, int w, int h, Color outerColor, Color innerColor) {
        g.setColor(outerColor);
        PlasticUtils.drawRect(g, x, y, w - 1, h - 1);
        g.setColor(innerColor);
        PlasticUtils.drawRect(g, x + 1, y + 1, w - 3, h - 3);
    }
}
