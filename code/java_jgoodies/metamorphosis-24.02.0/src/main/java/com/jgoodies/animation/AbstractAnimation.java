package com.jgoodies.animation;

import com.jgoodies.animation.AnimationEvent;
import java.util.LinkedList;
import java.util.List;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AbstractAnimation.class */
public abstract class AbstractAnimation implements Animation {
    private final long duration;
    private final boolean freezed;
    private final List<AnimationListener> listenerList;
    private boolean active;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void applyEffect(long j);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAnimation(long duration) {
        this(duration, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractAnimation(long duration, boolean freezed) {
        this.listenerList = new LinkedList();
        this.active = false;
        this.duration = duration;
        this.freezed = freezed;
    }

    @Override // com.jgoodies.animation.Animation
    public final long duration() {
        return this.duration;
    }

    public final boolean isFreezed() {
        return this.freezed;
    }

    @Override // com.jgoodies.animation.Animation
    public void animate(long time) {
        if (time >= this.duration) {
            if (this.active) {
                applyEffect(isFreezed() ? this.duration - 1 : 0L);
                fireAnimationStopped(time);
                this.active = false;
                return;
            }
            return;
        }
        if (!this.active) {
            this.active = true;
            fireAnimationStarted(time);
        }
        applyEffect(time);
    }

    @Override // com.jgoodies.animation.Animation
    public final void addAnimationListener(AnimationListener listener) {
        this.listenerList.add(listener);
    }

    @Override // com.jgoodies.animation.Animation
    public final void removeAnimationListener(AnimationListener listener) {
        this.listenerList.remove(listener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void fireAnimationStarted(long time) {
        AnimationEvent e = null;
        for (AnimationListener listener : this.listenerList) {
            if (e == null) {
                e = new AnimationEvent(this, AnimationEvent.Type.STARTED, time);
            }
            listener.animationStarted(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void fireAnimationStopped(long time) {
        AnimationEvent e = null;
        for (AnimationListener listener : this.listenerList) {
            if (e == null) {
                e = new AnimationEvent(this, AnimationEvent.Type.STOPPED, time);
            }
            listener.animationStopped(e);
        }
    }

    public String toString() {
        return "[" + getClass().getName() + "; duration=" + this.duration + "; active=" + this.active + ']';
    }
}
