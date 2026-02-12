package com.jgoodies.animation;

import com.jgoodies.common.base.Preconditions;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AbstractAnimationFunction.class */
public abstract class AbstractAnimationFunction<T> implements AnimationFunction<T> {
    private final long duration;

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAnimationFunction(long duration) {
        Preconditions.checkArgument(duration >= 0, "The duration must not be negative.");
        this.duration = duration;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void checkTimeRange(long time) {
        if (time < 0 || time >= duration()) {
            throw new IllegalArgumentException("The time must be larger than 0 and smaller than " + duration() + ".");
        }
    }

    @Override // com.jgoodies.animation.AnimationFunction
    public final long duration() {
        return this.duration;
    }
}
