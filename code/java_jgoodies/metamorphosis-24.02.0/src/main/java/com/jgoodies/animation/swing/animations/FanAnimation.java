package com.jgoodies.animation.swing.animations;

import com.jgoodies.animation.AbstractAnimation;
import com.jgoodies.animation.AnimationFunction;
import com.jgoodies.animation.swing.components.FanComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/swing/animations/FanAnimation.class */
public final class FanAnimation extends AbstractAnimation {
    public static final double DEFAULT_CLOCKWISE_ROTATION = 0.01d;
    public static final double DEFAULT_ANTICLOCKWISE_ROTATION = -0.02d;
    private final FanComponent fan;
    private final AnimationFunction<Float> rotationFunction;

    public FanAnimation(FanComponent fan, long duration, AnimationFunction<Float> rotationFunction) {
        super(duration);
        this.fan = fan;
        this.rotationFunction = rotationFunction != null ? rotationFunction : defaultRotationFunction(duration);
    }

    public static FanAnimation defaultFan(FanComponent fan, long duration) {
        return new FanAnimation(fan, duration, null);
    }

    public static AnimationFunction<Float> defaultRotationFunction(long duration) {
        return AnimationFunction.fromTo(duration, 0.0f, 6.2831855f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.jgoodies.animation.AbstractAnimation
    public void applyEffect(long time) {
        this.fan.setRotation(this.rotationFunction.valueAt(time).floatValue());
    }
}
