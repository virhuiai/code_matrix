package com.jgoodies.application;

import com.jgoodies.common.promise.Promise;
import java.util.EventListener;
import java.util.EventObject;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/ExitListener.class */
public interface ExitListener extends EventListener {
    default Promise<Boolean> applicationExiting(EventObject evt) {
        return Promise.of(true);
    }

    default void applicationExited() {
    }
}
