package com.jgoodies.application;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.internal.AncestorSupport;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Task.class */
public abstract class Task<T, V> extends SwingWorker<T, V> {
    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_MESSAGE = "message";
    public static final String PROPERTY_CANCEL_ALLOWED = "cancelAllowed";
    public static final String PROPERTY_PROGRESS = "progress";
    public static final String PROPERTY_PROGRESS_INDETERMINATE = "progressIndeterminate";
    public static final String PROPERTY_INPUT_BLOCKER = "inputBlocker";
    public static final String PROPERTY_EXTENDED_STATE = "extendedState";
    private final BlockingScope blockingScope;
    private final String resourcePrefix;
    private final ResourceMap resourceMap;
    private String title;
    private String description;
    private String message;
    private boolean cancelAllowed;
    private boolean progressIndeterminate;
    private long startTime;
    private long doneTime;
    private long messageTime;
    private InputBlocker inputBlocker;
    private javax.swing.Action action;
    private EventObject eventObject;
    private Component component;
    private Window window;
    static PropertyChangeListener stateChangeHandler = Task::onStateChanged;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Task$ExtendedStateValue.class */
    public enum ExtendedStateValue {
        BACKGROUND_DONE
    }

    public Task(BlockingScope blockingScope) {
        this(blockingScope, "", null);
    }

    public Task(BlockingScope blockingScope, Class<?> type) {
        this(blockingScope, type.getSimpleName(), Application.getResourceMap(type));
    }

    public Task(BlockingScope blockingScope, String resourcePrefix, ResourceMap resourceMap) {
        this.progressIndeterminate = true;
        this.startTime = -1L;
        this.doneTime = -1L;
        this.messageTime = -1L;
        Preconditions.checkNotNull(blockingScope, Messages.MUST_NOT_BE_NULL, "blocking scope");
        this.blockingScope = blockingScope;
        this.resourcePrefix = resourcePrefix;
        this.resourceMap = resourceMap;
        if (resourceMap != null) {
            this.title = resourceMap.getString(getResourceName(PROPERTY_TITLE), new Object[0]);
            this.description = null;
            try {
                this.description = resourceMap.getString(getResourceName("description"), new Object[0]);
            } catch (MissingResourceException e) {
            }
            try {
                setMessage(resourceMap.getString(getResourceName(PROPERTY_MESSAGE), new Object[0]));
            } catch (MissingResourceException e2) {
            }
        }
        this.inputBlocker = Application.getInstance().getDefaultInputBlocker();
        addPropertyChangeListener(stateChangeHandler);
    }

    public final BlockingScope getBlockingScope() {
        return this.blockingScope;
    }

    public final String getResourceName(String suffix) {
        return this.resourcePrefix + '.' + suffix;
    }

    public final ResourceMap getResourceMap() {
        return this.resourceMap;
    }

    public final synchronized String getTitle() {
        return this.title;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setTitle(String newTitle) {
        String oldValue;
        synchronized (this) {
            oldValue = this.title;
            this.title = newTitle;
        }
        firePropertyChange(PROPERTY_TITLE, oldValue, newTitle);
    }

    public final synchronized String getDescription() {
        return this.description;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setDescription(String newDescription) {
        String oldValue;
        synchronized (this) {
            oldValue = this.description;
            this.description = newDescription;
        }
        firePropertyChange("description", oldValue, newDescription);
    }

    public final synchronized String getMessage() {
        return this.message;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setMessage(String newMessage) {
        String oldValue;
        synchronized (this) {
            oldValue = this.message;
            this.message = newMessage;
            this.messageTime = System.currentTimeMillis();
        }
        firePropertyChange(PROPERTY_MESSAGE, oldValue, newMessage);
    }

    protected final void message(String keySuffix, Object... args) {
        Preconditions.checkState(getResourceMap() != null, "Can't format message because this Task lacks a resource map.");
        setMessage(getResourceMap().getString(getResourceName(keySuffix), args));
    }

    public final synchronized boolean isCancelAllowed() {
        return this.cancelAllowed;
    }

    protected final void setCancelAllowed(boolean b) {
        boolean oldValue;
        boolean newValue;
        synchronized (this) {
            oldValue = this.cancelAllowed;
            this.cancelAllowed = b;
            newValue = this.cancelAllowed;
        }
        firePropertyChange(PROPERTY_CANCEL_ALLOWED, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    public final synchronized boolean isProgressIndeterminate() {
        return this.progressIndeterminate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setProgressIndeterminate(boolean b) {
        boolean oldValue;
        boolean newValue;
        synchronized (this) {
            oldValue = this.progressIndeterminate;
            this.progressIndeterminate = b;
            newValue = this.progressIndeterminate;
        }
        firePropertyChange(PROPERTY_PROGRESS_INDETERMINATE, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
    }

    public final long getExecutionDuration(TimeUnit unit) {
        long startT;
        long doneT;
        long duration;
        synchronized (this) {
            startT = this.startTime;
            doneT = this.doneTime;
        }
        if (startT == -1) {
            duration = 0;
        } else if (doneT == -1) {
            duration = System.currentTimeMillis() - startT;
        } else {
            duration = doneT - startT;
        }
        return unit.convert(Math.max(0L, duration), TimeUnit.MILLISECONDS);
    }

    public final long getMessageDuration(TimeUnit unit) {
        long messageT;
        synchronized (this) {
            messageT = this.messageTime;
        }
        long dt = messageT == -1 ? 0L : Math.max(0L, System.currentTimeMillis() - messageT);
        return unit.convert(dt, TimeUnit.MILLISECONDS);
    }

    public InputBlocker getInputBlocker() {
        return this.inputBlocker;
    }

    public void setInputBlocker(InputBlocker newInputBlocker) {
        InputBlocker oldBlocker = getInputBlocker();
        this.inputBlocker = newInputBlocker;
        firePropertyChange(PROPERTY_INPUT_BLOCKER, oldBlocker, newInputBlocker);
    }

    public final javax.swing.Action getAction() {
        return this.action;
    }

    public final void setAction(javax.swing.Action actionToBlock) {
        this.action = actionToBlock;
    }

    public final Component getComponent() {
        return this.component;
    }

    public final void setComponent(Component componentToBlock) {
        this.component = componentToBlock;
    }

    private void setComponent(EventObject evt) {
        this.component = evt == null ? null : AncestorSupport.getComponentFor(evt.getSource());
    }

    public final Window getWindow() {
        return this.window;
    }

    private void setWindow(EventObject evt) {
        this.window = evt == null ? null : AncestorSupport.getWindowFor(evt.getSource());
    }

    public final EventObject getEventObject() {
        return this.eventObject;
    }

    public final void setEventObject(EventObject eventObject) {
        Preconditions.checkState(this.eventObject == null, "The event object must be set once only.");
        this.eventObject = eventObject;
        setComponent(eventObject);
        setWindow(eventObject);
    }

    public final boolean isPending() {
        return getState() == StateValue.PENDING;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public void enqueue(ExecutorService executorService) {
        checkValidBlocking();
        if (getBlockingScope() == BlockingScope.NONE) {
            executorService.execute(this);
            return;
        }
        InputBlocker blocker = getInputBlocker();
        Preconditions.checkState(blocker != null, "An InputBlocker must be specified for a Task if its blocking scope is not NONE. An individual InputBlocker can be set by Task#setInputBlocker, a default InputBlocker can be set by Application#setDefaultInputBlocker.\nTask=" + getTitle());
        if (EventQueue.isDispatchThread()) {
            blocker.block(this);
            executorService.execute(this);
        } else {
            EventQueue.invokeLater(() -> {
                blocker.block(this);
                executorService.execute(this);
            });
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected final void done() {
        this.doneTime = System.currentTimeMillis();
        if (getBlockingScope() != BlockingScope.NONE) {
            getInputBlocker().unblock(this);
        }
        try {
            firePropertyChange(PROPERTY_EXTENDED_STATE, null, ExtendedStateValue.BACKGROUND_DONE);
            if (isCancelled()) {
                cancelled();
            } else {
                try {
                    succeeded(get());
                } catch (InterruptedException ex) {
                    interrupted(ex);
                } catch (ExecutionException ex2) {
                    failed(ex2.getCause());
                }
            }
        } finally {
            finished();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void succeeded(T result) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void cancelled() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void failed(Throwable caught) {
        Application.getInstance().handleException(caught, getTitle(), Level.SEVERE);
    }

    protected void interrupted(InterruptedException ex) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finished() {
    }

    private void checkValidBlocking() {
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$application$BlockingScope[getBlockingScope().ordinal()]) {
            case 1:
            case 5:
            default:
                return;
            case AnimatedLabel.LEFT /* 2 */:
                Preconditions.checkState(getAction() != null, withTaskInfo("The task has a blocking scope ACTION, but the Action object has not been set. It'll be set by the framework, if you #execute an Action or request it from the ApplicationContext."));
                return;
            case 3:
                if (getComponent() == null) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.warning(withTaskInfo("The task has a blocking scope COMPONENTbut the event object has not been set, or the event object is null, or the event object lacks a source component."));
                    return;
                }
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                if (getWindow() == null) {
                    Logger logger2 = Logger.getLogger(getClass().getName());
                    logger2.warning(withTaskInfo("The task has a blocking scope WINDOW but the event object has not been set, or the event object is null, or the event object lacks a source component."));
                    return;
                }
                return;
        }
    }

    private String withTaskInfo(String message) {
        return Strings.get(message + "\nTask class=%1$s\nTask title=%2$s\nTask description=%3$s\n", getClass().getName(), getTitle(), getDescription());
    }

    private static void onStateChanged(PropertyChangeEvent evt) {
        Task<?, ?> task = (Task) evt.getSource();
        String propertyName = evt.getPropertyName();
        boolean z = -1;
        switch (propertyName.hashCode()) {
            case -1001078227:
                if (propertyName.equals(PROPERTY_PROGRESS)) {
                    z = true;
                    break;
                }
                break;
            case 109757585:
                if (propertyName.equals("state")) {
                    z = false;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                StateValue state = (StateValue) evt.getNewValue();
                switch (AnonymousClass1.$SwitchMap$javax$swing$SwingWorker$StateValue[state.ordinal()]) {
                    case 1:
                        throw new IllegalStateException();
                    case AnimatedLabel.LEFT /* 2 */:
                        synchronized (task) {
                            ((Task) task).startTime = System.currentTimeMillis();
                        }
                        return;
                    case 3:
                        task.removePropertyChangeListener(stateChangeHandler);
                        return;
                    default:
                        return;
                }
            case true:
                synchronized (task) {
                    ((Task) task).progressIndeterminate = false;
                }
                return;
            default:
                return;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.jgoodies.application.Task$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Task$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$application$BlockingScope;
        static final /* synthetic */ int[] $SwitchMap$javax$swing$SwingWorker$StateValue = new int[StateValue.values().length];

        static {
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[StateValue.PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[StateValue.STARTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[StateValue.DONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$com$jgoodies$application$BlockingScope = new int[BlockingScope.values().length];
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.ACTION.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.COMPONENT.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.WINDOW.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$jgoodies$application$BlockingScope[BlockingScope.APPLICATION.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
        }
    }
}
