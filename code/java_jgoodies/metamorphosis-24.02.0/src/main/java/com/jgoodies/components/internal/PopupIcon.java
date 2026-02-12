package com.jgoodies.components.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/PopupIcon.class */
public final class PopupIcon implements Icon {
    private static final int ICON_HEIGHT = 5;
    private static final int ICON_WIDTH = 11;

    public int getIconWidth() {
        return ICON_WIDTH;
    }

    public int getIconHeight() {
        return ICON_HEIGHT;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = getIconWidth() - 2;
        g.translate(x, y);
        Color oldColor = g.getColor();
        g.setColor(UIManager.getColor(c.isEnabled() ? "controlText" : "textInactiveText"));
        for (int i = 1; i < ICON_HEIGHT; i++) {
            g.fillRect(i + 1, i, (w - i) - i, 1);
        }
        g.setColor(oldColor);
        g.translate(-x, -y);
    }
}
