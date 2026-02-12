package com.jgoodies.components.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/NullIcon.class */
public final class NullIcon implements Icon {
    private final int width;
    private final int height;

    public NullIcon() {
        this(0, 0);
    }

    public NullIcon(Dimension size) {
        this(size.width, size.height);
    }

    public NullIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getIconHeight() {
        return this.height;
    }

    public int getIconWidth() {
        return this.width;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
    }
}
