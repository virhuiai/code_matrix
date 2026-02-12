package com.jgoodies.animation;

import com.jgoodies.animation.AnimationFunctions;
import java.awt.Color;
import java.util.Arrays;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunction.class */
public interface AnimationFunction<T> {
    public static final AnimationFunction<Float> ONE = constant(2147483647L, Float.valueOf(1.0f));
    public static final AnimationFunction<Float> ZERO = constant(2147483647L, Float.valueOf(0.0f));

    long duration();

    T valueAt(long j);

    static AnimationFunction<Color> alphaColor(AnimationFunction<Integer> f, Color baseColor) {
        return new AnimationFunctions.AlphaColorAnimationFunction(f, baseColor);
    }

    static <T> AnimationFunction<T> concat(AnimationFunction<T>... functions) {
        return new AnimationFunctions.SequencedAnimationFunction(Arrays.asList(functions));
    }

    static <T> AnimationFunction<T> constant(long duration, T value) {
        return discrete(duration, value);
    }

    static <T> AnimationFunction<T> discrete(long duration, T... values) {
        return discrete(duration, (float[]) null, values);
    }

    static <T> AnimationFunction<T> discrete(long duration, float[] keyTimes, T... values) {
        return new AnimationFunctions.InterpolatedAnimationFunction(duration, values, keyTimes, AnimationFunctions.InterpolationMode.DISCRETE);
    }

    static AnimationFunction<Float> fromBy(long duration, float from, float by) {
        return fromTo(duration, from, from + by);
    }

    static AnimationFunction<Float> fromTo(long duration, float from, float to) {
        return linear(duration, Float.valueOf(from), Float.valueOf(to));
    }

    /* JADX WARN: Incorrect types in method signature: <T:Ljava/lang/Number;>(J[TT;)Lcom/jgoodies/animation/AnimationFunction<TT;>; */
    static AnimationFunction linear(long duration, Number... numberArr) {
        return linear(duration, null, numberArr);
    }

    /* JADX WARN: Incorrect types in method signature: <T:Ljava/lang/Number;>(J[F[TT;)Lcom/jgoodies/animation/AnimationFunction<TT;>; */
    static AnimationFunction linear(long duration, float[] keyTimes, Number... numberArr) {
        return new AnimationFunctions.InterpolatedAnimationFunction(duration, numberArr, keyTimes, AnimationFunctions.InterpolationMode.LINEAR);
    }

    static AnimationFunction<Color> linearColors(long duration, float[] keyTimes, Color... colors) {
        return new AnimationFunctions.ColorFunction(duration, colors, keyTimes);
    }

    static AnimationFunction<Integer> random(int min, int max, float changeProbability) {
        return new AnimationFunctions.RandomAnimationFunction(min, max, changeProbability);
    }

    static <T> AnimationFunction<T> repeat(AnimationFunction<T> f, long repeatTime) {
        return new AnimationFunctions.RepeatedAnimationFunction(f, repeatTime);
    }

    static <T> AnimationFunction<T> reverse(AnimationFunction<T> f) {
        return new AnimationFunctions.ReversedAnimationFunction(f);
    }
}
