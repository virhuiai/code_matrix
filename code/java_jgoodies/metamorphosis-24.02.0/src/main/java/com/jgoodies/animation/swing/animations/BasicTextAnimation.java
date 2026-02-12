package com.jgoodies.animation.swing.animations;

import com.jgoodies.animation.AbstractAnimation;
import com.jgoodies.animation.AnimationFunction;
import com.jgoodies.animation.swing.components.BasicTextLabel;
import java.awt.Color;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/animations/BasicTextAnimation.class */
public final class BasicTextAnimation extends AbstractAnimation {
    private final BasicTextLabel label;
    private final String text;
    private final AnimationFunction<Color> colorFunction;
    private final AnimationFunction<Integer> offsetFunction;
    private final AnimationFunction<Float> scaleXFunction;
    private final AnimationFunction<Float> scaleYFunction;
    private final AnimationFunction<Float> spaceFunction;
    private boolean offsetEnabled;

    public BasicTextAnimation(BasicTextLabel label, long duration, String text, AnimationFunction<Color> colorFunction, AnimationFunction<Float> scaleXFunction, AnimationFunction<Float> scaleYFunction, AnimationFunction<Float> spaceFunction) {
        super(duration);
        this.offsetEnabled = false;
        this.label = label;
        this.text = text;
        this.colorFunction = colorFunction != null ? colorFunction : defaultFadeColorFunction(duration, Color.DARK_GRAY);
        this.scaleXFunction = scaleXFunction != null ? scaleXFunction : AnimationFunction.ONE;
        this.scaleYFunction = scaleYFunction != null ? scaleYFunction : AnimationFunction.ONE;
        this.spaceFunction = spaceFunction != null ? spaceFunction : AnimationFunction.ZERO;
        this.offsetFunction = defaultOffsetFunction();
    }

    public static BasicTextAnimation defaultFade(BasicTextLabel label, long duration, String text, Color baseColor) {
        return new BasicTextAnimation(label, duration, text, cinemaFadeColorFunction(duration, baseColor), null, null, null);
    }

    public static BasicTextAnimation defaultScale(BasicTextLabel label, long duration, String text, Color baseColor) {
        return new BasicTextAnimation(label, duration, text, defaultScaleColorFunction(duration, baseColor), defaultScaleFunction(duration), defaultScaleFunction(duration), null);
    }

    public static BasicTextAnimation defaultSpace(BasicTextLabel label, long duration, String text, Color baseColor) {
        return new BasicTextAnimation(label, duration, text, defaultSpaceColorFunction(duration, baseColor), null, null, defaultSpaceFunction(duration));
    }

    public static AnimationFunction<Color> defaultFadeColorFunction(long duration, Color baseColor) {
        return AnimationFunction.alphaColor(AnimationFunction.linear(duration, new float[]{0.0f, 0.3f, 0.7f, 1.0f}, 0, 255, 255, 0), baseColor);
    }

    public static AnimationFunction<Color> cinemaFadeColorFunction(long duration, Color baseColor) {
        return AnimationFunction.alphaColor(AnimationFunction.linear(duration, new float[]{0.0f, 0.3f, 0.85f, 1.0f}, 0, 255, 255, 0), baseColor);
    }

    public static AnimationFunction<Color> defaultScaleColorFunction(long duration, Color baseColor) {
        return AnimationFunction.alphaColor(AnimationFunction.linear(duration, new float[]{0.0f, 0.2f, 0.85f, 1.0f}, 0, 255, 255, 0), baseColor);
    }

    public static AnimationFunction<Color> defaultSpaceColorFunction(long duration, Color baseColor) {
        return AnimationFunction.alphaColor(AnimationFunction.linear(duration, new float[]{0.0f, 0.2f, 0.8f, 1.0f}, 0, 255, 255, 0), baseColor);
    }

    public static AnimationFunction<Integer> defaultOffsetFunction() {
        return AnimationFunction.random(-2, 2, 0.5f);
    }

    public static AnimationFunction<Float> defaultScaleFunction(long duration) {
        return AnimationFunction.linear(duration, new float[]{0.0f, 0.85f, 1.0f}, Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(1.8f));
    }

    public static AnimationFunction<Float> defaultSpaceFunction(long duration) {
        return AnimationFunction.fromTo(duration, 0.0f, 10.0f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.animation.AbstractAnimation
    public void applyEffect(long time) {
        this.label.setText(time == 0 ? " " : this.text);
        this.label.setColor(this.colorFunction.valueAt(time));
        this.label.setScaleX(this.scaleXFunction.valueAt(time).floatValue());
        this.label.setScaleY(this.scaleYFunction.valueAt(time).floatValue());
        this.label.setSpace(this.spaceFunction.valueAt(time).floatValue());
        if (isOffsetEnabled()) {
            this.label.setOffsetX(this.offsetFunction.valueAt(time).intValue());
            this.label.setOffsetY(this.offsetFunction.valueAt(time).intValue());
        }
    }

    public boolean isOffsetEnabled() {
        return this.offsetEnabled;
    }

    public void setOffsetEnabled(boolean b) {
        this.offsetEnabled = b;
    }
}
