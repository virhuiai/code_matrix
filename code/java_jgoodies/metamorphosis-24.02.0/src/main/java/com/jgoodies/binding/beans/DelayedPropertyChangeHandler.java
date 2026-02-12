package com.jgoodies.binding.beans;

import com.jgoodies.common.base.Preconditions;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/beans/DelayedPropertyChangeHandler.class */
public class DelayedPropertyChangeHandler implements PropertyChangeListener {
    public static final int DEFAULT_DELAY = 200;
    private final PropertyChangeListener delayedListener;
    private final Timer timer;
    private boolean coalesce;
    private PropertyChangeEvent pendingEvt;

    public DelayedPropertyChangeHandler() {
        this(DEFAULT_DELAY);
    }

    public DelayedPropertyChangeHandler(PropertyChangeListener delayedListener) {
        this(delayedListener, DEFAULT_DELAY, false);
    }

    public DelayedPropertyChangeHandler(int delay) {
        this(delay, false);
    }

    public DelayedPropertyChangeHandler(int delay, boolean coalesce) {
        this(null, delay, coalesce);
    }

    public DelayedPropertyChangeHandler(PropertyChangeListener delayedListener, int delay, boolean coalesce) {
        Preconditions.checkArgument(delay >= 0, "The delay must not be negative.");
        this.delayedListener = delayedListener;
        this.coalesce = coalesce;
        this.timer = new Timer(delay, this::onTimerFired);
        this.timer.setRepeats(false);
    }

    public final int getDelay() {
        return this.timer.getDelay();
    }

    public final void setDelay(int delay) {
        this.timer.setInitialDelay(delay);
        this.timer.setDelay(delay);
    }

    public final boolean isCoalesce() {
        return this.coalesce;
    }

    public final void setCoalesce(boolean b) {
        this.coalesce = b;
    }

    public final void stop() {
        this.timer.stop();
    }

    public final boolean isPending() {
        return this.timer.isRunning();
    }

    @Override // java.beans.PropertyChangeListener
    public final void propertyChange(PropertyChangeEvent evt) {
        this.pendingEvt = evt;
        if (this.coalesce) {
            this.timer.restart();
        } else {
            this.timer.start();
        }
    }

    protected void delayedPropertyChange(PropertyChangeEvent evt) {
        Preconditions.checkNotNull(this.delayedListener, "The delayed listener must be provided at construction time, or otherwise this class must be extended to override #delayedPropertyChange.");
        this.delayedListener.propertyChange(evt);
    }

    private void onTimerFired(ActionEvent evt) {
        delayedPropertyChange(this.pendingEvt);
        this.timer.stop();
    }
}
