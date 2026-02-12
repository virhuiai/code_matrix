package com.jgoodies.looks.common;

import com.jgoodies.looks.Options;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/common/MinimumSizedIcon.class */
public class MinimumSizedIcon implements Icon {
    private final Icon icon;
    private final int width;
    private final int height;
    private final int xOffset;
    private final int yOffset;

    public MinimumSizedIcon() {
        this(null);
    }

    public MinimumSizedIcon(Icon icon) {
        Dimension minimumSize = Options.getDefaultIconSize();
        this.icon = icon;
        int iconWidth = icon == null ? 0 : icon.getIconWidth();
        int iconHeight = icon == null ? 0 : icon.getIconHeight();
        this.width = Math.max(iconWidth, Math.max(20, minimumSize.width));
        this.height = Math.max(iconHeight, Math.max(20, minimumSize.height));
        this.xOffset = Math.max(0, (this.width - iconWidth) / 2);
        this.yOffset = Math.max(0, (this.height - iconHeight) / 2);
    }

    public int getIconHeight() {
        return this.height;
    }

    public int getIconWidth() {
        return this.width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (this.icon != null) {
            this.icon.paintIcon(c, g, x + this.xOffset, y + this.yOffset);
        }
    }
}
