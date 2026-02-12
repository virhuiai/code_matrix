package com.jgoodies.animation;

import java.awt.EventQueue;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationUtils.class */
final class AnimationUtils {
    AnimationUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationUtils$StopListener.class */
    public static final class StopListener extends AnimationAdapter {
        private final Runnable runnable;

        /* JADX INFO: Access modifiers changed from: package-private */
        public StopListener(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override // com.jgoodies.animation.AnimationAdapter, com.jgoodies.animation.AnimationListener
        public void animationStopped(AnimationEvent e) {
            EventQueue.invokeLater(this.runnable);
        }
    }
}
