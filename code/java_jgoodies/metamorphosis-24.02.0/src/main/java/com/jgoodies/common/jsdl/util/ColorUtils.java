package com.jgoodies.common.jsdl.util;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.Color;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/util/ColorUtils.class */
public final class ColorUtils {
    private ColorUtils() {
    }

    public static float brightness(Color color) {
        Preconditions.checkNotNull(color, Messages.MUST_NOT_BE_NULL);
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        return hsb[2];
    }
}
