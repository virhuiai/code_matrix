package com.jgoodies.layout.factories;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.layout.layout.ConstantSize;
import com.jgoodies.layout.layout.Sizes;
import com.jgoodies.layout.util.LayoutStyle;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/Paddings.class */
public final class Paddings {
    public static final Padding EMPTY = createPadding(Sizes.ZERO, Sizes.ZERO, Sizes.ZERO, Sizes.ZERO);
    public static final Padding DLU2 = createPadding(Sizes.DLU2, Sizes.DLU2, Sizes.DLU2, Sizes.DLU2);
    public static final Padding DLU4 = createPadding(Sizes.DLU4, Sizes.DLU4, Sizes.DLU4, Sizes.DLU4);
    public static final Padding DLU7 = createPadding(Sizes.DLU7, Sizes.DLU7, Sizes.DLU7, Sizes.DLU7);
    public static final Padding DLU9 = createPadding(Sizes.DLU9, Sizes.DLU9, Sizes.DLU9, Sizes.DLU9);
    public static final Padding DLU14 = createPadding(Sizes.DLU14, Sizes.DLU14, Sizes.DLU14, Sizes.DLU14);
    public static final Padding DLU21 = createPadding(Sizes.DLU21, Sizes.DLU21, Sizes.DLU21, Sizes.DLU21);
    public static final Padding EPX4 = createPadding(Sizes.EPX4, Sizes.EPX4, Sizes.EPX4, Sizes.EPX4);
    public static final Padding EPX8 = createPadding(Sizes.EPX8, Sizes.EPX8, Sizes.EPX8, Sizes.EPX8);
    public static final Padding EPX12 = createPadding(Sizes.EPX12, Sizes.EPX12, Sizes.EPX12, Sizes.EPX12);
    public static final Padding EPX16 = createPadding(Sizes.EPX16, Sizes.EPX16, Sizes.EPX16, Sizes.EPX16);
    public static final Padding EPX24 = createPadding(Sizes.EPX24, Sizes.EPX24, Sizes.EPX24, Sizes.EPX24);
    public static final Padding EPX48 = createPadding(Sizes.EPX48, Sizes.EPX48, Sizes.EPX48, Sizes.EPX48);
    public static final Padding BUTTON_BAR_PAD = createPadding(LayoutStyle.getCurrent().getButtonBarPad(), Sizes.ZERO, Sizes.ZERO, Sizes.ZERO);
    public static final Padding DIALOG = createPadding(LayoutStyle.getCurrent().getDialogMarginY(), LayoutStyle.getCurrent().getDialogMarginX(), LayoutStyle.getCurrent().getDialogMarginY(), LayoutStyle.getCurrent().getDialogMarginX());
    public static final Padding TABBED_DIALOG = createPadding(LayoutStyle.getCurrent().getTabbedDialogMarginY(), LayoutStyle.getCurrent().getTabbedDialogMarginX(), LayoutStyle.getCurrent().getTabbedDialogMarginY(), LayoutStyle.getCurrent().getTabbedDialogMarginX());

    private Paddings() {
    }

    public static Padding createPadding(ConstantSize top, ConstantSize left, ConstantSize bottom, ConstantSize right) {
        return new Padding(top, left, bottom, right);
    }

    public static Padding createPadding(String encodedSizes, Object... args) {
        String formattedSizes = Strings.get(encodedSizes, args);
        String[] token = formattedSizes.split("\\s*,\\s*");
        int tokenCount = token.length;
        Preconditions.checkArgument(token.length == 4, "The padding requires 4 sizes, but \"%s\" has %d.", formattedSizes, Integer.valueOf(tokenCount));
        ConstantSize top = Sizes.constant(token[0]);
        ConstantSize left = Sizes.constant(token[1]);
        ConstantSize bottom = Sizes.constant(token[2]);
        ConstantSize right = Sizes.constant(token[3]);
        return createPadding(top, left, bottom, right);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/layout/factories/Paddings$Padding.class */
    public static final class Padding extends EmptyBorder {
        private final ConstantSize topMargin;
        private final ConstantSize leftMargin;
        private final ConstantSize bottomMargin;
        private final ConstantSize rightMargin;

        private Padding(ConstantSize top, ConstantSize left, ConstantSize bottom, ConstantSize right) {
            super(0, 0, 0, 0);
            this.topMargin = (ConstantSize) Preconditions.checkNotNull(top, Messages.MUST_NOT_BE_NULL, "top");
            this.leftMargin = (ConstantSize) Preconditions.checkNotNull(left, Messages.MUST_NOT_BE_NULL, "left");
            this.bottomMargin = (ConstantSize) Preconditions.checkNotNull(bottom, Messages.MUST_NOT_BE_NULL, "bottom");
            this.rightMargin = (ConstantSize) Preconditions.checkNotNull(right, Messages.MUST_NOT_BE_NULL, "right");
        }

        public Insets getBorderInsets() {
            return getBorderInsets(null);
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = this.topMargin.getPixelSize(c, false);
            insets.left = this.leftMargin.getPixelSize(c, true);
            insets.bottom = this.bottomMargin.getPixelSize(c, false);
            insets.right = this.rightMargin.getPixelSize(c, true);
            return insets;
        }

        public ConstantSize getTop() {
            return this.topMargin;
        }

        public ConstantSize getLeft() {
            return this.leftMargin;
        }

        public ConstantSize getBottom() {
            return this.bottomMargin;
        }

        public ConstantSize getRight() {
            return this.rightMargin;
        }

        public String toString() {
            return String.format("Padding[top=%s, left=%s, bottom=%s, right=%s]", getTop(), getLeft(), getBottom(), getRight());
        }
    }
}
