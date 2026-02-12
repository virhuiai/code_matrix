package com.jgoodies.animation;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import java.awt.Color;
import java.util.List;
import java.util.Random;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions.class */
final class AnimationFunctions {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$InterpolationMode.class */
    public enum InterpolationMode {
        DISCRETE,
        LINEAR
    }

    AnimationFunctions() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$AlphaColorAnimationFunction.class */
    public static final class AlphaColorAnimationFunction implements AnimationFunction<Color> {
        private final Color baseColor;
        private final AnimationFunction<Integer> fAlpha;

        /* JADX INFO: Access modifiers changed from: package-private */
        public AlphaColorAnimationFunction(AnimationFunction<Integer> fAlpha, Color baseColor) {
            this.fAlpha = fAlpha;
            this.baseColor = baseColor;
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public long duration() {
            return this.fAlpha.duration();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.jgoodies.animation.AnimationFunction
        public Color valueAt(long time) {
            return new Color(this.baseColor.getRed(), this.baseColor.getGreen(), this.baseColor.getBlue(), this.fAlpha.valueAt(time).intValue());
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$ColorFunction.class */
    static final class ColorFunction extends AbstractAnimationFunction<Color> {
        private final AnimationFunction<Integer> redFunction;
        private final AnimationFunction<Integer> greenFunction;
        private final AnimationFunction<Integer> blueFunction;
        private final AnimationFunction<Integer> alphaFunction;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ColorFunction(long duration, Color[] colors, float[] keyTimes) {
            super(duration);
            Integer[] red = new Integer[colors.length];
            Integer[] green = new Integer[colors.length];
            Integer[] blue = new Integer[colors.length];
            Integer[] alpha = new Integer[colors.length];
            for (int i = 0; i < colors.length; i++) {
                red[i] = Integer.valueOf(colors[i].getRed());
                green[i] = Integer.valueOf(colors[i].getGreen());
                blue[i] = Integer.valueOf(colors[i].getBlue());
                alpha[i] = Integer.valueOf(colors[i].getAlpha());
            }
            this.redFunction = AnimationFunction.linear(duration, keyTimes, red);
            this.greenFunction = AnimationFunction.linear(duration, keyTimes, green);
            this.blueFunction = AnimationFunction.linear(duration, keyTimes, blue);
            this.alphaFunction = AnimationFunction.linear(duration, keyTimes, alpha);
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public Color valueAt(long time) {
            checkTimeRange(time);
            return new Color(this.redFunction.valueAt(time).intValue(), this.greenFunction.valueAt(time).intValue(), this.blueFunction.valueAt(time).intValue(), this.alphaFunction.valueAt(time).intValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$InterpolatedAnimationFunction.class */
    public static final class InterpolatedAnimationFunction<T> extends AbstractAnimationFunction<T> {
        private final float[] keyTimes;
        private final InterpolationMode mode;
        private final T[] values;

        /* JADX INFO: Access modifiers changed from: package-private */
        public InterpolatedAnimationFunction(long duration, T[] values, float[] keyTimes, InterpolationMode mode) {
            super(duration);
            this.values = values;
            this.keyTimes = keyTimes;
            this.mode = mode;
            checkValidKeyTimes(values.length, keyTimes);
        }

        private static void checkValidKeyTimes(int valuesLength, float[] theKeyTimes) {
            if (theKeyTimes == null) {
                return;
            }
            if (valuesLength < 2 || valuesLength != theKeyTimes.length) {
                throw new IllegalArgumentException("The values and key times arrays must be non-empty and must have equal length.");
            }
            for (int index = 0; index < theKeyTimes.length - 2; index++) {
                if (theKeyTimes[index] >= theKeyTimes[index + 1]) {
                    throw new IllegalArgumentException("The key times must be increasing.");
                }
            }
        }

        private T discreteValueAt(long time) {
            return this.values[indexAt(time, this.values.length)];
        }

        private int indexAt(long time, int intervalCount) {
            long duration = duration();
            if (this.keyTimes == null) {
                return (int) ((time * intervalCount) / duration);
            }
            for (int index = this.keyTimes.length - 1; index > 0; index--) {
                if (((float) time) >= ((float) duration) * this.keyTimes[index]) {
                    return index;
                }
            }
            return 0;
        }

        /* JADX WARN: Multi-variable type inference failed */
        private T interpolateLinear(T t, T t2, long j, long j2) {
            float floatValue = ((Number) t).floatValue();
            float floatValue2 = floatValue + (((((Number) t2).floatValue() - floatValue) * ((float) j)) / ((float) j2));
            if (t instanceof Float) {
                return (T) Float.valueOf(floatValue2);
            }
            return (T) Integer.valueOf((int) floatValue2);
        }

        private T linearValueAt(long time) {
            int segments = this.values.length - 1;
            int beginIndex = indexAt(time, segments);
            int endIndex = beginIndex + 1;
            long lastTime = duration() - 1;
            long beginTime = this.keyTimes == null ? (beginIndex * lastTime) / segments : this.keyTimes[beginIndex] * ((float) lastTime);
            long endTime = this.keyTimes == null ? (endIndex * lastTime) / segments : this.keyTimes[endIndex] * ((float) lastTime);
            return interpolateLinear(this.values[beginIndex], this.values[endIndex], time - beginTime, endTime - beginTime);
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public T valueAt(long time) {
            checkTimeRange(time);
            switch (AnonymousClass1.$SwitchMap$com$jgoodies$animation$AnimationFunctions$InterpolationMode[this.mode.ordinal()]) {
                case 1:
                    return discreteValueAt(time);
                case AnimatedLabel.LEFT /* 2 */:
                    return linearValueAt(time);
                default:
                    throw new UnsupportedOperationException("Unsupported interpolation mode.");
            }
        }
    }

    /* renamed from: com.jgoodies.animation.AnimationFunctions$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$animation$AnimationFunctions$InterpolationMode = new int[InterpolationMode.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$animation$AnimationFunctions$InterpolationMode[InterpolationMode.DISCRETE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$animation$AnimationFunctions$InterpolationMode[InterpolationMode.LINEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$RandomAnimationFunction.class */
    public static final class RandomAnimationFunction implements AnimationFunction<Integer> {
        private final float changeProbability;
        private final int max;
        private final int min;
        private final Random random = new Random();
        private Integer value;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RandomAnimationFunction(int min, int max, float changeProbability) {
            this.min = min;
            this.max = max;
            this.changeProbability = changeProbability;
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public long duration() {
            return 2147483647L;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.jgoodies.animation.AnimationFunction
        public Integer valueAt(long time) {
            if (this.value == null || this.random.nextFloat() < this.changeProbability) {
                this.value = Integer.valueOf(this.min + this.random.nextInt(this.max - this.min));
            }
            return this.value;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$RepeatedAnimationFunction.class */
    static final class RepeatedAnimationFunction<T> extends AbstractAnimationFunction<T> {
        private final AnimationFunction<T> f;
        private final long simpleDuration;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RepeatedAnimationFunction(AnimationFunction<T> f, long repeatTime) {
            super(repeatTime);
            this.f = f;
            this.simpleDuration = f.duration();
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public T valueAt(long time) {
            return this.f.valueAt(time % this.simpleDuration);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$ReversedAnimationFunction.class */
    static final class ReversedAnimationFunction<T> extends AbstractAnimationFunction<T> {
        private final AnimationFunction<T> f;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ReversedAnimationFunction(AnimationFunction<T> f) {
            super(f.duration());
            this.f = f;
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public T valueAt(long time) {
            return this.f.valueAt(duration() - time);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationFunctions$SequencedAnimationFunction.class */
    static final class SequencedAnimationFunction<T> implements AnimationFunction<T> {
        private final List<AnimationFunction<T>> functions;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SequencedAnimationFunction(List<AnimationFunction<T>> functions) {
            this.functions = functions;
            if (this.functions.isEmpty()) {
                throw new IllegalArgumentException("The list of functions must not be empty.");
            }
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public long duration() {
            return this.functions.stream().mapToLong((v0) -> {
                return v0.duration();
            }).sum();
        }

        @Override // com.jgoodies.animation.AnimationFunction
        public T valueAt(long time) {
            if (time < 0) {
                throw new IllegalArgumentException("The time must be positive.");
            }
            long begin = 0;
            for (AnimationFunction<T> f : this.functions) {
                long end = begin + f.duration();
                if (time < end) {
                    return f.valueAt(time - begin);
                }
                begin = end;
            }
            throw new IllegalArgumentException("The time must be smaller than the total duration.");
        }
    }
}
