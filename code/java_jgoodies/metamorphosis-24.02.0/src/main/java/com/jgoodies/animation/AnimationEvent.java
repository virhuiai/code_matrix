package com.jgoodies.animation;

import com.jgoodies.application.TaskMonitor;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationEvent.class */
public final class AnimationEvent {
    private final Animation source;
    private final Type type;
    private final long time;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AnimationEvent(Animation source, Type type, long time) {
        this.source = source;
        this.type = type;
        this.time = time;
    }

    public Animation getSource() {
        return this.source;
    }

    public Type type() {
        return this.type;
    }

    public long time() {
        return this.time;
    }

    public String toString() {
        return "[type= " + this.type + "; time= " + this.time + "; source=" + this.source + ']';
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/animation/AnimationEvent$Type.class */
    public enum Type {
        STARTED(TaskMonitor.PROPERTY_STARTED),
        STOPPED("stopped");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.name;
        }
    }
}
