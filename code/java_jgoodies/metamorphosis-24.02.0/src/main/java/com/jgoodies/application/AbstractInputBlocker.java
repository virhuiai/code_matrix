package com.jgoodies.application;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.Preconditions;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/AbstractInputBlocker.class */
public abstract class AbstractInputBlocker implements InputBlocker {
    private static final Map<Object, Integer> COUNTER_MAP = new HashMap();
    private static final Map<Object, Boolean> ENABLED_MAP = new HashMap();
    private Level logLevel = Level.FINE;

    protected abstract void block(Task<?, ?> task, Window window, int i);

    protected abstract void block(Task<?, ?> task, Application application, int i);

    protected abstract void unblock(Task<?, ?> task, Window window, int i);

    protected abstract void unblock(Task<?, ?> task, Application application, int i);

    @Override // com.jgoodies.application.InputBlocker
    public void block(Task<?, ?> task) {
        Preconditions.checkState(EventQueue.isDispatchThread(), "#block must run on the event dispatching thread (EDT).");
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$application$BlockingScope[task.getBlockingScope().ordinal()]) {
            case 1:
                throw new IllegalStateException("An InputBlocker must not be invoked with a BlockingScope.NONE.");
            case AnimatedLabel.LEFT /* 2 */:
                block(task, task.getAction());
                return;
            case 3:
                block(task, task.getComponent());
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                block(task, task.getWindow());
                return;
            case 5:
                block(task, getApplication());
                return;
            default:
                throw new IllegalStateException("Unknown blocking scope: " + task.getBlockingScope());
        }
    }

    /* renamed from: com.jgoodies.application.AbstractInputBlocker$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/AbstractInputBlocker$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$application$BlockingScope = new int[BlockingScope.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.ACTION.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.COMPONENT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.WINDOW.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.APPLICATION.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    @Override // com.jgoodies.application.InputBlocker
    public void unblock(Task<?, ?> task) {
        Preconditions.checkState(EventQueue.isDispatchThread(), "#unblock must run on the event dispatching thread (EDT).");
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$application$BlockingScope[task.getBlockingScope().ordinal()]) {
            case 1:
                throw new IllegalStateException("An InputBlocker must not be invoked with a BlockingScope.NONE.");
            case AnimatedLabel.LEFT /* 2 */:
                unblock(task, task.getAction());
                return;
            case 3:
                unblock(task, task.getComponent());
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                unblock(task, task.getWindow());
                return;
            case 5:
                unblock(task, getApplication());
                return;
            default:
                throw new IllegalStateException("Unknown blocking scope: " + task.getBlockingScope());
        }
    }

    protected void block(Task<?, ?> task, javax.swing.Action action) {
        logBlock(task, BlockingScope.ACTION, action);
        action.setEnabled(false);
    }

    protected void block(Task<?, ?> task, Component component) {
        if (component == null) {
            logScopeExpansion(task, BlockingScope.COMPONENT, null, BlockingScope.APPLICATION, getApplication());
            block(task, getApplication());
        } else {
            int oldCounter = increaseBlockingCounter(component);
            block(task, component, oldCounter);
        }
    }

    protected void block(Task<?, ?> task, Component c, int oldCounter) {
        logBlock(task, BlockingScope.COMPONENT, c);
        if (oldCounter == 0) {
            ENABLED_MAP.put(c, Boolean.valueOf(c.isEnabled()));
        }
        c.setEnabled(false);
    }

    protected void block(Task<?, ?> task, Window window) {
        if (window == null) {
            logScopeExpansion(task, BlockingScope.WINDOW, null, BlockingScope.APPLICATION, getApplication());
            block(task, getApplication());
        } else {
            int oldCounter = increaseBlockingCounter(window);
            block(task, window, oldCounter);
        }
    }

    protected void block(Task<?, ?> task, Application application) {
        int oldCounter = increaseBlockingCounter(application);
        logBlock(task, BlockingScope.APPLICATION, application);
        block(task, application, oldCounter);
    }

    protected void unblock(Task<?, ?> task, javax.swing.Action action) {
        logUnblock(task, BlockingScope.ACTION, action);
        action.setEnabled(true);
    }

    protected void unblock(Task<?, ?> task, Component component) {
        if (component == null) {
            logScopeExpansion(task, BlockingScope.COMPONENT, null, BlockingScope.APPLICATION, getApplication());
            unblock(task, getApplication());
        } else {
            int newCounter = decreaseBlockingCounter(component);
            unblock(task, component, newCounter);
        }
    }

    protected void unblock(Task<?, ?> task, Component c, int newCounter) {
        if (newCounter == 0) {
            Boolean oldEnabledState = ENABLED_MAP.get(c);
            c.setEnabled(oldEnabledState.booleanValue());
        }
    }

    protected void unblock(Task<?, ?> task, Window window) {
        if (window == null) {
            logScopeExpansion(task, BlockingScope.WINDOW, null, BlockingScope.APPLICATION, getApplication());
            unblock(task, getApplication());
        } else {
            int newCounter = decreaseBlockingCounter(window);
            unblock(task, window, newCounter);
        }
    }

    protected void unblock(Task<?, ?> task, Application application) {
        int newCounter = decreaseBlockingCounter(application);
        logUnblock(task, BlockingScope.APPLICATION, application);
        unblock(task, application, newCounter);
    }

    public final Level getLogLevel() {
        return this.logLevel;
    }

    public final void setLogLevel(Level newLevel) {
        this.logLevel = newLevel;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logBlock(Task<?, ?> task, BlockingScope scope, Object target) {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(getLogLevel(), "Block: Task title=" + task.getTitle() + "; scope=" + scope + "; target=" + name(target));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void logUnblock(Task<?, ?> task, BlockingScope scope, Object target) {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(getLogLevel(), "Unblock: Task title=" + task.getTitle() + "; scope=" + scope + "; target=" + name(target));
    }

    protected void logScopeExpansion(Task<?, ?> task, BlockingScope oldScope, Object oldTarget, BlockingScope newScope, Object newTarget) {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.log(getLogLevel(), "BlockingScope expansion: \nold scope=" + oldScope + "; old target=" + name(oldTarget) + "\nnew scope=" + newScope + "; new target=" + name(newTarget));
    }

    private static String name(Object target) {
        if (target == null) {
            return "null";
        }
        if (target instanceof javax.swing.Action) {
            return (String) ((javax.swing.Action) target).getValue("Name");
        }
        return target.toString();
    }

    protected static final Application getApplication() {
        return Application.getInstance();
    }

    public static final int getBlockingCounter(Object target) {
        Integer counter = COUNTER_MAP.get(target);
        if (counter == null) {
            counter = 0;
        }
        return counter.intValue();
    }

    public static final int increaseBlockingCounter(Object target) {
        int oldValue = getBlockingCounter(target);
        COUNTER_MAP.put(target, Integer.valueOf(oldValue + 1));
        return oldValue;
    }

    public static final int decreaseBlockingCounter(Object target) {
        Integer counter = COUNTER_MAP.get(target);
        if (counter == null) {
            Preconditions.checkState(target instanceof Window, "The unblock target %s  must have a blocking counter set.", target);
            counter = 0;
        }
        Integer counter2 = Integer.valueOf(counter.intValue() - 1);
        if (counter2.intValue() == 0) {
            COUNTER_MAP.remove(target);
        } else if (counter2.intValue() > 0) {
            COUNTER_MAP.put(target, counter2);
        }
        return counter2.intValue();
    }
}
