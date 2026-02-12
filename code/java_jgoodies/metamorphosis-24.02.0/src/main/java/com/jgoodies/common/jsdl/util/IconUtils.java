package com.jgoodies.common.jsdl.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.jsdl.icon.IconValue;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/util/IconUtils.class */
public final class IconUtils {
    private IconUtils() {
    }

    public static Icon crop(Icon icon, Insets insets) {
        if (icon == null) {
            return null;
        }
        return new PaddedIcon(icon, new Insets(-insets.top, -insets.left, -insets.bottom, -insets.right));
    }

    public static Icon crop(IconValue icon, Insets insets) {
        if (icon == null) {
            return null;
        }
        return crop(icon.toIcon(), insets);
    }

    public static Icon pad(Icon icon, Insets insets) {
        if (icon == null) {
            return null;
        }
        return new PaddedIcon(icon, insets);
    }

    public static Icon pad(IconValue icon, Insets insets) {
        if (icon == null) {
            return null;
        }
        return pad(icon.toIcon(), insets);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/util/IconUtils$PaddedIcon.class */
    public static final class PaddedIcon implements Icon {
        private final Icon wrappedIcon;
        private final Insets insets;

        PaddedIcon(Icon wrappedIcon, Insets insets) {
            this.wrappedIcon = (Icon) Preconditions.checkNotNull(wrappedIcon, Messages.MUST_NOT_BE_NULL, "wrapped icon");
            this.insets = (Insets) Preconditions.checkNotNull(insets, Messages.MUST_NOT_BE_NULL, "icon insets");
        }

        public int getIconHeight() {
            return this.wrappedIcon.getIconHeight() + this.insets.top + this.insets.bottom;
        }

        public int getIconWidth() {
            return this.wrappedIcon.getIconWidth() + this.insets.left + this.insets.right;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            this.wrappedIcon.paintIcon(c, g, x + this.insets.left, y + this.insets.top);
        }
    }
}
