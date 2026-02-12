package com.jgoodies.common.swing;

import com.jgoodies.binding.beans.DelayedPropertyChangeHandler;
import com.jgoodies.common.base.SystemUtils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/ScreenScaling.class */
public final class ScreenScaling {
    private static ScaleFactor SCALE_FACTOR = computeScaleFactor();
    private static int SCALE_FACTOR_VALUE = SCALE_FACTOR.intValue();

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/ScreenScaling$ScaleFactor.class */
    public enum ScaleFactor {
        F100(100, 108),
        F125(125, 132),
        F150(150, 168),
        F200(DelayedPropertyChangeHandler.DEFAULT_DELAY, 216),
        F250(250, 264),
        F300(300, 336),
        F400(400, Integer.MAX_VALUE);

        private final int value;
        private final int breakpoint;

        ScaleFactor(int value, int breakpoint) {
            this.value = value;
            this.breakpoint = breakpoint;
        }

        public int intValue() {
            return this.value;
        }

        int breakpoint() {
            return this.breakpoint;
        }
    }

    private ScreenScaling() {
    }

    public static ScaleFactor getScaleFactor() {
        return SCALE_FACTOR;
    }

    public static boolean isScale100() {
        return getScaleFactor() == ScaleFactor.F100;
    }

    public static Dimension physicalDimension(int effectiveWidth, int effectiveHeight) {
        return new Dimension(toPhysical(effectiveWidth), toPhysical(effectiveHeight));
    }

    public static DimensionUIResource physicalDimensionUIResource(int effectiveWidth, int effectiveHeight) {
        return new DimensionUIResource(toPhysical(effectiveWidth), toPhysical(effectiveHeight));
    }

    public static EmptyBorder physicalEmptyBorder(int effectiveTop, int effectiveLeft, int effectiveBottom, int effectiveRight) {
        return new EmptyBorder(toPhysical(effectiveTop), toPhysical(effectiveLeft), toPhysical(effectiveBottom), toPhysical(effectiveRight));
    }

    public static Font physicalFont(String name, int style, int effectiveSize) {
        return new Font(name, style, toPhysical(effectiveSize));
    }

    public static Font physicalFont(JLabel label, int effectiveSize) {
        return label.getFont().deriveFont(toPhysical(effectiveSize));
    }

    public static Insets physicalInsets(int effectiveTop, int effectiveLeft, int effectiveBottom, int effectiveRight) {
        return new Insets(toPhysical(effectiveTop), toPhysical(effectiveLeft), toPhysical(effectiveBottom), toPhysical(effectiveRight));
    }

    public static InsetsUIResource physicalInsetsUIResource(int effectiveTop, int effectiveLeft, int effectiveBottom, int effectiveRight) {
        return new InsetsUIResource(toPhysical(effectiveTop), toPhysical(effectiveLeft), toPhysical(effectiveBottom), toPhysical(effectiveRight));
    }

    public static Integer physicalInteger(int effectiveIntValue) {
        return Integer.valueOf(toPhysical(effectiveIntValue));
    }

    public static double toPhysical(double effectiveValue) {
        return (effectiveValue * SCALE_FACTOR_VALUE) / 100.0d;
    }

    public static float toPhysical(float effectiveValue) {
        return (effectiveValue * SCALE_FACTOR_VALUE) / 100.0f;
    }

    public static int toPhysical(int effectiveValue) {
        return (effectiveValue * SCALE_FACTOR_VALUE) / 100;
    }

    public static Dimension toPhysical(Dimension effectiveDimension) {
        return physicalDimension(effectiveDimension.width, effectiveDimension.height);
    }

    public static EmptyBorder toPhysical(EmptyBorder effectiveBorder) {
        return new EmptyBorder(toPhysical(effectiveBorder.getBorderInsets()));
    }

    public static Font toPhysical(Font effectiveFont) {
        return effectiveFont.deriveFont((effectiveFont.getSize() * SCALE_FACTOR_VALUE) / 100.0f);
    }

    public static Insets toPhysical(Insets effectiveInsets) {
        return physicalInsets(effectiveInsets.top, effectiveInsets.left, effectiveInsets.bottom, effectiveInsets.right);
    }

    public static int toEffective(int physicalValue) {
        return (physicalValue * 100) / SCALE_FACTOR_VALUE;
    }

    public static Dimension toEffective(Dimension physicalDimension) {
        return new Dimension(toEffective(physicalDimension.width), toEffective(physicalDimension.height));
    }

    private static ScaleFactor computeScaleFactor() {
        int resolution = lookupScreenResolution();
        for (ScaleFactor factor : ScaleFactor.values()) {
            if (resolution <= factor.breakpoint) {
                return factor;
            }
        }
        throw new IllegalStateException("No ScaleFactor matches the resolution: " + resolution);
    }

    private static int lookupScreenResolution() {
        if (!SystemUtils.IS_JAVA_8 || GraphicsEnvironment.isHeadless()) {
            return 96;
        }
        return Toolkit.getDefaultToolkit().getScreenResolution();
    }
}
