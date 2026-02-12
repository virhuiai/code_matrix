package com.jgoodies.animation;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.event.ActionEvent;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/Animator.class */
public final class Animator {
    private final Animation animation;
    private final Timer timer;
    private final int framesPerSecond;
    private long startTime;
    private long elapsedTime = 0;

    public Animator(Animation animation, int framesPerSecond) {
        Preconditions.checkArgument(framesPerSecond > 0, "The frame rate must be positive.");
        this.animation = (Animation) Preconditions.checkNotNull(animation, Messages.MUST_NOT_BE_NULL, "animation");
        this.framesPerSecond = framesPerSecond;
        this.timer = createTimer(framesPerSecond);
    }

    public Animation animation() {
        return this.animation;
    }

    public int framesPerSecond() {
        return this.framesPerSecond;
    }

    public long elapsedTime() {
        if (!this.timer.isRunning()) {
            return this.elapsedTime;
        }
        long now = System.currentTimeMillis();
        if (this.startTime == -1) {
            this.startTime = now;
        }
        return (now - this.startTime) + this.elapsedTime;
    }

    public void start() {
        if (!this.timer.isRunning()) {
            registerStopListener();
            this.startTime = -1L;
            this.timer.start();
        }
    }

    public void stop() {
        if (this.timer.isRunning()) {
            this.elapsedTime = elapsedTime();
            this.timer.stop();
        }
    }

    private void onTimerFired(ActionEvent e) {
        this.animation.animate(elapsedTime());
    }

    public String toString() {
        return "elapsedTime=" + elapsedTime() + "; fps=" + this.framesPerSecond;
    }

    private Timer createTimer(int fps) {
        int delay = 1000 / fps;
        Timer aTimer = new Timer(delay, this::onTimerFired);
        aTimer.setInitialDelay(0);
        aTimer.setCoalesce(true);
        return aTimer;
    }

    private void registerStopListener() {
        this.animation.addAnimationListener(new AnimationAdapter() { // from class: com.jgoodies.animation.Animator.1
            @Override // com.jgoodies.animation.AnimationAdapter, com.jgoodies.animation.AnimationListener
            public void animationStopped(AnimationEvent e) {
                Animator.this.stop();
            }
        });
    }
}
