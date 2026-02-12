package com.jgoodies.framework.eventbus;

import java.util.EventListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/eventbus/EventBus.class */
public interface EventBus {
    <L extends EventListener> void addListener(Class<? extends AbstractEvent<L>> cls, L l);

    <L extends EventListener> void removeListener(Class<? extends AbstractEvent<L>> cls, L l);

    <L extends EventListener> void fireEvent(AbstractEvent<L> abstractEvent);
}
