package com.jgoodies.looks.common;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.AbstractBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/ShadowPopupBorder.class */
final class ShadowPopupBorder extends AbstractBorder {
    private static final int SHADOW_SIZE = 5;
    private static ShadowPopupBorder instance = new ShadowPopupBorder();
    private static Image shadow = new ImageIcon(ShadowPopupBorder.class.getResource("shadow.png")).getImage();

    ShadowPopupBorder() {
    }

    public static ShadowPopupBorder getInstance() {
        return instance;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        JComponent popup = (JComponent) c;
        Image hShadowBg = (Image) popup.getClientProperty("jgoodies.hShadowBg");
        if (hShadowBg != null) {
            g.drawImage(hShadowBg, x, (y + height) - SHADOW_SIZE, c);
        }
        Image vShadowBg = (Image) popup.getClientProperty("jgoodies.vShadowBg");
        if (vShadowBg != null) {
            g.drawImage(vShadowBg, (x + width) - SHADOW_SIZE, y, c);
        }
        g.drawImage(shadow, x + SHADOW_SIZE, (y + height) - SHADOW_SIZE, x + 10, y + height, 0, 6, SHADOW_SIZE, 11, (Color) null, c);
        g.drawImage(shadow, x + 10, (y + height) - SHADOW_SIZE, (x + width) - SHADOW_SIZE, y + height, SHADOW_SIZE, 6, 6, 11, (Color) null, c);
        g.drawImage(shadow, (x + width) - SHADOW_SIZE, y + SHADOW_SIZE, x + width, y + 10, 6, 0, 11, SHADOW_SIZE, (Color) null, c);
        g.drawImage(shadow, (x + width) - SHADOW_SIZE, y + 10, x + width, (y + height) - SHADOW_SIZE, 6, SHADOW_SIZE, 11, 6, (Color) null, c);
        g.drawImage(shadow, (x + width) - SHADOW_SIZE, (y + height) - SHADOW_SIZE, x + width, y + height, 6, 6, 11, 11, (Color) null, c);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, SHADOW_SIZE, SHADOW_SIZE);
    }

    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = 0;
        insets.left = 0;
        insets.bottom = SHADOW_SIZE;
        insets.right = SHADOW_SIZE;
        return insets;
    }
}
