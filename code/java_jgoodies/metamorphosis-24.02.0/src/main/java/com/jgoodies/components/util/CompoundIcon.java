package com.jgoodies.components.util;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/CompoundIcon.class */
public final class CompoundIcon implements Icon {
    private final Icon backgroundIcon;
    private final Icon foregroundIcon;
    private final int height;
    private final int width;
    private int xOffset;
    private int yOffset;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/CompoundIcon$Anchor.class */
    public enum Anchor {
        CENTER,
        NORTH,
        NORTHEAST,
        EAST,
        SOUTHEAST,
        SOUTH,
        SOUTHWEST,
        WEST,
        NORTHWEST
    }

    public CompoundIcon(Icon backgroundIcon, Icon foregroundIcon, Anchor anchor) {
        this.backgroundIcon = backgroundIcon;
        this.foregroundIcon = foregroundIcon;
        this.height = Math.max(backgroundIcon.getIconHeight(), foregroundIcon.getIconHeight());
        this.width = Math.max(backgroundIcon.getIconWidth(), foregroundIcon.getIconWidth());
        setAnchor(anchor);
    }

    public int getIconWidth() {
        return this.width;
    }

    public int getIconHeight() {
        return this.height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        this.backgroundIcon.paintIcon(c, g, x, y);
        this.foregroundIcon.paintIcon(c, g, x + this.xOffset, y + this.yOffset);
    }

    private void setAnchor(Anchor anchor) {
        int xDiff = this.backgroundIcon.getIconWidth() - this.foregroundIcon.getIconWidth();
        int yDiff = this.backgroundIcon.getIconHeight() - this.foregroundIcon.getIconHeight();
        this.xOffset = (anchor == Anchor.NORTHWEST || anchor == Anchor.WEST || anchor == Anchor.SOUTHWEST) ? 0 : (anchor == Anchor.NORTH || anchor == Anchor.CENTER || anchor == Anchor.SOUTH) ? xDiff / 2 : xDiff;
        this.yOffset = (anchor == Anchor.NORTHWEST || anchor == Anchor.NORTH || anchor == Anchor.NORTHEAST) ? 0 : (anchor == Anchor.WEST || anchor == Anchor.CENTER || anchor == Anchor.EAST) ? yDiff / 2 : yDiff;
    }
}
