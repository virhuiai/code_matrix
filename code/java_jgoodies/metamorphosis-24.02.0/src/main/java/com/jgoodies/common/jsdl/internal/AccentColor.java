package com.jgoodies.common.jsdl.internal;

import com.jgoodies.common.base.Preconditions;
import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/AccentColor.class */
public final class AccentColor extends Color {
    private Color light1;
    private Color light2;
    private Color light3;
    private Color dark1;
    private Color dark2;
    private Color dark3;

    public AccentColor(int red, int green, int blue) {
        super(red, green, blue);
    }

    public Color light1() {
        if (this.light1 == null) {
            this.light1 = modifyBrightness(this, 1.1f);
        }
        return this.light1;
    }

    public Color light2() {
        if (this.light2 == null) {
            this.light2 = modifyBrightness(this, 1.2f);
        }
        return this.light2;
    }

    public Color light3() {
        if (this.light3 == null) {
            this.light3 = modifyBrightness(this, 1.3f);
        }
        return this.light3;
    }

    public Color dark1() {
        if (this.dark1 == null) {
            this.dark1 = modifyBrightness(this, 0.75f);
        }
        return this.dark1;
    }

    public Color dark2() {
        if (this.dark2 == null) {
            this.dark2 = modifyBrightness(this, 0.5f);
        }
        return this.dark2;
    }

    public Color dark3() {
        if (this.dark3 == null) {
            this.dark3 = modifyBrightness(this, 0.25f);
        }
        return this.dark3;
    }

    public List<Color> darkestToAccent(int steps) {
        Preconditions.checkArgument(steps >= 0, "Steps must be >= 0");
        double darkest = 0.25d;
        double increment = (1.0d - 0.25d) / steps;
        return (List) IntStream.rangeClosed(0, steps).mapToDouble(i -> {
            return darkest + (i * increment);
        }).mapToObj(brightnessFactor -> {
            return modifyBrightness(this, (float) brightnessFactor);
        }).collect(Collectors.toList());
    }

    public List<Color> accentToDarkest(int steps) {
        Preconditions.checkArgument(steps >= 0, "Steps must be >= 0");
        double darkest = 0.25d;
        double increment = (1.0d - 0.25d) / steps;
        return (List) IntStream.rangeClosed(0, steps).mapToDouble(i -> {
            return darkest + ((steps - i) * increment);
        }).mapToObj(brightnessFactor -> {
            return modifyBrightness(this, (float) brightnessFactor);
        }).collect(Collectors.toList());
    }

    private static Color modifyBrightness(Color color, float factor) {
        float[] hsbValues = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsbValues);
        float hue = hsbValues[0];
        float saturation = hsbValues[1];
        float brightness = hsbValues[2];
        float newBrightness = Math.min(brightness * factor, 1.0f);
        return Color.getHSBColor(hue, saturation, newBrightness);
    }
}
