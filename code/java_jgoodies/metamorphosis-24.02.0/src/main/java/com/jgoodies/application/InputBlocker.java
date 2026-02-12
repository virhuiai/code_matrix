package com.jgoodies.application;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/InputBlocker.class */
public interface InputBlocker {
    void block(Task<?, ?> task);

    void unblock(Task<?, ?> task);
}
