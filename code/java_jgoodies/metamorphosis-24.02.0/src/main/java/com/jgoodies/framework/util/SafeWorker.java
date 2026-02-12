package com.jgoodies.framework.util;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/SafeWorker.class */
public abstract class SafeWorker<T, V> extends SwingWorker<T, V> {
    /* JADX WARN: Multi-variable type inference failed */
    protected final void done() {
        try {
            if (isCancelled()) {
                cancelled();
            } else {
                try {
                    try {
                        succeeded(get());
                    } catch (InterruptedException e) {
                        interrupted(e);
                    }
                } catch (ExecutionException e2) {
                    failed(e2.getCause());
                }
            }
        } finally {
            finished();
        }
    }

    protected void succeeded(T result) {
    }

    protected void cancelled() {
    }

    protected void failed(Throwable throwable) {
        if (throwable instanceof Error) {
            throw ((Error) throwable);
        }
        Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Background execution failed", throwable);
    }

    protected void interrupted(InterruptedException ex) {
    }

    protected void finished() {
    }
}
