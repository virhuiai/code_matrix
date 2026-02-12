package com.jgoodies.animation;

import com.jgoodies.animation.AnimationUtils;
import com.jgoodies.animation.Animations;
import java.util.Arrays;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animation.class */
public interface Animation {
    long duration();

    void animate(long j);

    void addAnimationListener(AnimationListener animationListener);

    void removeAnimationListener(AnimationListener animationListener);

    default void onStop(Runnable runnable) {
        addAnimationListener(new AnimationUtils.StopListener(runnable));
    }

    static Animation offset(Animation animation, long beginTime) {
        return new Animations.OffsetAnimation(beginTime, animation);
    }

    static Animation parallel(List<Animation> animations) {
        return new Animations.ParallelAnimation(animations);
    }

    static Animation parallel(Animation... animations) {
        return parallel((List<Animation>) Arrays.asList(animations));
    }

    static Animation pause(long duration) {
        return new Animations.PauseAnimation(duration);
    }

    static Animation repeat(Animation animation, float repeatCount) {
        return repeat(animation, ((float) animation.duration()) * repeatCount);
    }

    static Animation repeat(Animation animation, long duration) {
        return new Animations.RepeatedAnimation(duration, animation);
    }

    static Animation reverse(Animation animation) {
        return new Animations.ReversedAnimation(animation);
    }

    static Animation sequential(List<Animation> animations) {
        return new Animations.SequencedAnimation(animations);
    }

    static Animation sequential(Animation... animations) {
        return sequential((List<Animation>) Arrays.asList(animations));
    }
}
