package com.jgoodies.animation;

import java.util.EventListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationListener.class */
public interface AnimationListener extends EventListener {
    void animationStarted(AnimationEvent animationEvent);

    void animationStopped(AnimationEvent animationEvent);
}
