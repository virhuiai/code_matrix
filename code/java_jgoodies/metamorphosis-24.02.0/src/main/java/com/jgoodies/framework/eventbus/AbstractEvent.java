package com.jgoodies.framework.eventbus;

import java.util.EventListener;
import java.util.EventObject;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/eventbus/AbstractEvent.class */
public abstract class AbstractEvent<L extends EventListener> extends EventObject {
    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void dispatch(L l);

    protected AbstractEvent(Object source) {
        super(source);
    }
}
