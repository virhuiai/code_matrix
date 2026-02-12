package com.jgoodies.framework.eventbus;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/eventbus/DefaultEventBus.class */
public class DefaultEventBus implements EventBus {
    private final List<EventEntry<?>> listeners = new CopyOnWriteArrayList();

    @Override // com.jgoodies.framework.eventbus.EventBus
    public <L extends EventListener> void addListener(Class<? extends AbstractEvent<L>> type, L listener) {
        this.listeners.add(new EventEntry<>(type, listener));
    }

    @Override // com.jgoodies.framework.eventbus.EventBus
    public <L extends EventListener> void removeListener(Class<? extends AbstractEvent<L>> type, L listener) {
        for (EventEntry<?> entry : this.listeners) {
            if (listener == entry.getListener()) {
                this.listeners.remove(entry);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.jgoodies.framework.eventbus.EventBus
    public <L extends EventListener> void fireEvent(AbstractEvent<L> abstractEvent) {
        for (EventEntry<?> entry : this.listeners) {
            Object listener = entry.getListener();
            if (listener == null) {
                this.listeners.remove(entry);
            } else if (entry.matches(abstractEvent)) {
                abstractEvent.dispatch(listener);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/eventbus/DefaultEventBus$EventEntry.class */
    static final class EventEntry<L extends EventListener> {
        private final Class<? extends AbstractEvent<L>> eventClass;
        private final WeakReference<L> listenerReference;

        EventEntry(Class<? extends AbstractEvent<L>> eventClass, L listener) {
            this.eventClass = eventClass;
            this.listenerReference = new WeakReference<>(listener);
        }

        boolean matches(AbstractEvent<?> event) {
            return this.eventClass.isInstance(event);
        }

        L getListener() {
            return this.listenerReference.get();
        }
    }
}
