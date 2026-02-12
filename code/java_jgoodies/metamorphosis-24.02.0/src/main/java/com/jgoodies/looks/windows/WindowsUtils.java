package com.jgoodies.looks.windows;

import java.awt.Graphics;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsUtils.class */
final class WindowsUtils {
    private WindowsUtils() {
    }

    public static void drawRoundedDashedRect(Graphics g, int x, int y, int width, int height) {
        for (int vx = x + 1; vx < x + width; vx += 2) {
            g.fillRect(vx, y, 1, 1);
            g.fillRect(vx, (y + height) - 1, 1, 1);
        }
        int offset = (width + 1) % 2;
        for (int vy = y + 1; vy < (y + height) - offset; vy += 2) {
            g.fillRect(x, vy, 1, 1);
            g.fillRect((x + width) - 1, vy + offset, 1, 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(UIManager.getColor("controlLtHighlight"));
        g.drawLine(0, 0, w - 2, 0);
        g.drawLine(0, 0, 0, h - 2);
        g.setColor(UIManager.getColor("controlShadow"));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.translate(-x, -y);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate(x, y);
        g.setColor(UIManager.getColor("controlShadow"));
        g.drawLine(0, 0, w - 2, 0);
        g.drawLine(0, 0, 0, h - 2);
        g.setColor(UIManager.getColor("controlLtHighlight"));
        g.drawLine(w - 1, 0, w - 1, h - 1);
        g.drawLine(0, h - 1, w - 1, h - 1);
        g.translate(-x, -y);
    }
}
