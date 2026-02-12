package com.jgoodies.looks.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/windows/WindowsArrowButton.class */
final class WindowsArrowButton extends BasicArrowButton {
    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowsArrowButton(int direction) {
        super(direction);
    }

    public Dimension getPreferredSize() {
        int width = Math.max(5, UIManager.getInt("ScrollBar.width"));
        return new Dimension(width, width);
    }

    public void paintTriangle(Graphics g, int x, int y, int size, int triangleDirection, boolean enabled) {
        Color oldColor = g.getColor();
        int j = 0;
        int size2 = Math.max(size, 2);
        int mid = (size2 - 1) / 2;
        g.translate(x, y);
        g.setColor(UIManager.getColor(enabled ? "controlText" : "controlShadow"));
        switch (triangleDirection) {
            case 1:
                int i = 0;
                while (i < size2) {
                    g.drawLine(mid - i, i, mid + i, i);
                    i++;
                }
                if (!enabled) {
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    g.drawLine((mid - i) + 2, i, mid + i, i);
                    break;
                }
                break;
            case 3:
                if (!enabled) {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    for (int i2 = size2 - 1; i2 >= 0; i2--) {
                        g.drawLine(j, mid - i2, j, mid + i2);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(UIManager.getColor("controlShadow"));
                }
                int j2 = 0;
                for (int i3 = size2 - 1; i3 >= 0; i3--) {
                    g.drawLine(j2, mid - i3, j2, mid + i3);
                    j2++;
                }
                break;
            case 5:
                if (!enabled) {
                    g.translate(1, 1);
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    for (int i4 = size2 - 1; i4 >= 0; i4--) {
                        g.drawLine(mid - i4, j, mid + i4, j);
                        j++;
                    }
                    g.translate(-1, -1);
                    g.setColor(UIManager.getColor("controlShadow"));
                }
                int j3 = 0;
                for (int i5 = size2 - 1; i5 >= 0; i5--) {
                    g.drawLine(mid - i5, j3, mid + i5, j3);
                    j3++;
                }
                break;
            case 7:
                int i6 = 0;
                while (i6 < size2) {
                    g.drawLine(i6, mid - i6, i6, mid + i6);
                    i6++;
                }
                if (!enabled) {
                    g.setColor(UIManager.getColor("controlLtHighlight"));
                    g.drawLine(i6, (mid - i6) + 2, i6, mid + i6);
                    break;
                }
                break;
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
    }
}
