package com.jgoodies.animation;

import com.jgoodies.common.base.Preconditions;
import java.util.Collections;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations.class */
public final class Animations {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$OffsetAnimation.class */
    public static final class OffsetAnimation extends AbstractAnimation {
        private final Animation animation;
        private final long beginTime;

        /* JADX INFO: Access modifiers changed from: package-private */
        public OffsetAnimation(long beginTime, Animation animation) {
            super(beginTime + animation.duration(), true);
            this.animation = animation;
            this.beginTime = beginTime;
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
            long relativeTime = time - this.beginTime;
            if (relativeTime >= 0) {
                this.animation.animate(relativeTime);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$OneTimeAnimation.class */
    public static abstract class OneTimeAnimation extends AbstractAnimation {
        private boolean effectApplied;

        public OneTimeAnimation() {
            super(0L, true);
            this.effectApplied = false;
        }

        @Override // com.jgoodies.animation.AbstractAnimation, com.jgoodies.animation.Animation
        public void animate(long time) {
            if (this.effectApplied) {
                return;
            }
            fireAnimationStarted(time);
            applyEffect(time);
            fireAnimationStopped(time);
            this.effectApplied = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$ParallelAnimation.class */
    public static final class ParallelAnimation extends AbstractAnimation {
        private final List<Animation> animations;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ParallelAnimation(List<Animation> animations) {
            super(maxDuration(animations), true);
            this.animations = Collections.unmodifiableList(animations);
        }

        private static long maxDuration(List<Animation> animations) {
            return animations.stream().mapToLong((v0) -> {
                return v0.duration();
            }).max().orElse(0L);
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
            for (Animation animation : this.animations) {
                animation.animate(time);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$PauseAnimation.class */
    public static final class PauseAnimation extends AbstractAnimation {
        /* JADX INFO: Access modifiers changed from: package-private */
        public PauseAnimation(long duration) {
            super(duration, true);
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$RepeatedAnimation.class */
    public static final class RepeatedAnimation extends AbstractAnimation {
        private final Animation animation;
        private final long simpleDuration;

        /* JADX INFO: Access modifiers changed from: package-private */
        public RepeatedAnimation(long duration, Animation animation) {
            super(duration, true);
            this.animation = animation;
            this.simpleDuration = animation.duration();
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
            this.animation.animate(time % this.simpleDuration);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$ReversedAnimation.class */
    static final class ReversedAnimation extends AbstractAnimation {
        private final Animation animation;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ReversedAnimation(Animation animation) {
            super(animation.duration(), true);
            this.animation = animation;
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
            long reversedTime = duration() - time;
            Preconditions.checkArgument(reversedTime >= 0, "The time must be in the valid time interval.");
            this.animation.animate(reversedTime);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animations$SequencedAnimation.class */
    public static final class SequencedAnimation extends AbstractAnimation {
        private final List<Animation> animations;

        /* JADX INFO: Access modifiers changed from: package-private */
        public SequencedAnimation(List<Animation> animations) {
            super(cumulatedDuration(animations), true);
            Preconditions.checkArgument(!animations.isEmpty(), "The list of animations must not be empty.");
            this.animations = Collections.unmodifiableList(animations);
        }

        private static long cumulatedDuration(List<Animation> animations) {
            return animations.stream().mapToLong((v0) -> {
                return v0.duration();
            }).sum();
        }

        @Override // com.jgoodies.animation.AbstractAnimation
        protected void applyEffect(long time) {
            long startTime = 0;
            for (Animation animation : this.animations) {
                long relativeTime = time - startTime;
                if (relativeTime > 0) {
                    animation.animate(relativeTime);
                }
                startTime += animation.duration();
            }
        }
    }
}
