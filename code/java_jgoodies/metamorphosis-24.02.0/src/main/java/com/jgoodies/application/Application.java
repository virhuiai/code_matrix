package com.jgoodies.application;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.promise.Promise;
import com.jgoodies.common.promise.Promises;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.common.swing.internal.EDTBean;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Application.class */
public abstract class Application extends EDTBean {
    public static final String PROPERTY_DEFAULT_INPUT_BLOCKER = "defaultInputBlocker";
    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());
    private static Application application;
    private final DefaultApplicationContext context = createContext();
    private final List<ExitListener> exitListeners = new CopyOnWriteArrayList();
    private InputBlocker defaultInputBlocker;
    private WindowListener applicationShutdownOnWindowClosingHandler;

    protected abstract void startup(String[] strArr);

    public static ActionMap createActionMap(Object target) {
        return getInstance().getContext().createActionMap(target);
    }

    public static ResourceMap getResourceMap() {
        return getResourceMap(null);
    }

    public static ResourceMap getResourceMap(Class<?> type) {
        return getInstance().getContext().getResourceMap(type);
    }

    public static void execute(EventObject e, javax.swing.Action action) {
        long when;
        int modifiers;
        Preconditions.checkNotNull(e, Messages.MUST_NOT_BE_NULL, "event object");
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "action");
        if (!action.isEnabled()) {
            return;
        }
        if (e instanceof InputEvent) {
            InputEvent ie = (InputEvent) e;
            when = ie.getWhen();
            modifiers = ie.getModifiers();
        } else {
            when = 0;
            modifiers = 0;
        }
        ActionEvent ae = new ActionEvent(e.getSource(), 1001, "execute", when, modifiers);
        action.actionPerformed(ae);
    }

    public static void execute(EventObject e, Task<?, ?> task) {
        task.setEventObject(e);
        execute(task);
    }

    public static void execute(Task<?, ?> task) {
        getInstance().getContext().getTaskService().execute(task);
    }

    public static Application getInstance() {
        Preconditions.checkState(application != null, "The application must be launched before you can obtain the Application, ResourceMaps, and ActionMaps.");
        return application;
    }

    public static boolean hasInstance() {
        return application != null;
    }

    public final DefaultApplicationContext getContext() {
        return this.context;
    }

    protected DefaultApplicationContext createContext() {
        return new DefaultApplicationContext(this, new DefaultActionManager(), new DefaultResourceManager(getClass()), null);
    }

    public static synchronized void launch(Class<? extends Application> appClass, String... args) {
        Runnable runnable = () -> {
            application = null;
            try {
                application = (Application) appClass.newInstance();
                try {
                    application.startup(args);
                    Application application2 = application;
                    application2.getClass();
                    new ReadyOnEmptyEventQueue().execute();
                } catch (Exception e3) {
                    String message = "Failed to launch " + appClass;
                    LOGGER.log(Level.SEVERE, message, (Throwable) e3);
                    throw new Error(message, e3);
                }
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                LOGGER.log(Level.SEVERE, "Illegal Access during launch of " + appClass, (Throwable) e2);
            } catch (InstantiationException e1) {
                e1.printStackTrace();
                LOGGER.log(Level.SEVERE, "Can't instantiate " + appClass, (Throwable) e1);
            }
        };
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            EventQueue.invokeLater(runnable);
        }
    }

    public static void launchTest() {
        launchTest(new PlaceHolderApplication());
    }

    public static synchronized void launchTest(Application app) {
        application = app;
        app.startup(null);
    }

    protected void ready() {
    }

    public Promise<Boolean> exit() {
        return exit(new EventObject(JOptionPane.getRootFrame()));
    }

    public Promise<Boolean> exit(EventObject evt) {
        return applicationExiting(evt, this.exitListeners).thenApply(exitAllowed -> {
            if (exitAllowed.booleanValue()) {
                fireApplicationExited();
                try {
                    shutdown();
                } catch (Exception e) {
                    handleException(e, "Failed to shutdown the application.", Level.SEVERE);
                } finally {
                    end();
                }
            }
            return exitAllowed;
        });
    }

    protected void shutdown() {
    }

    protected void end() {
        System.exit(0);
    }

    public final void addExitListener(ExitListener listener) {
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "exit listener");
        this.exitListeners.add(listener);
    }

    public final void removeExitListener(ExitListener listener) {
        Preconditions.checkNotNull(listener, Messages.MUST_NOT_BE_NULL, "exit listener");
        this.exitListeners.remove(listener);
    }

    protected final void fireApplicationExited() {
        for (ExitListener listener : this.exitListeners) {
            try {
                listener.applicationExited();
            } catch (Exception e) {
                handleException(e, "Failed to notify a listener that the application exited.", Level.WARNING);
            }
        }
    }

    public final InputBlocker getDefaultInputBlocker() {
        return this.defaultInputBlocker;
    }

    public final void setDefaultInputBlocker(InputBlocker newDefaultBlocker) {
        InputBlocker oldValue = getDefaultInputBlocker();
        this.defaultInputBlocker = newDefaultBlocker;
        firePropertyChange(PROPERTY_DEFAULT_INPUT_BLOCKER, oldValue, newDefaultBlocker);
    }

    public final WindowListener getApplicationExitOnWindowClosingHandler() {
        if (this.applicationShutdownOnWindowClosingHandler == null) {
            this.applicationShutdownOnWindowClosingHandler = Listeners.windowClosing(this::onWindowClosing);
        }
        return this.applicationShutdownOnWindowClosingHandler;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void handleException(Throwable t, String message, Level level) {
        if (t instanceof Error) {
            throw ((Error) t);
        }
        Logger logger = Logger.getLogger(getClass().getName());
        String msg = message != null ? message : "Failure";
        logger.log(level, msg, t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getApplicationPreferencesNodeName() {
        return DefaultApplicationContext.preferencesNodeName(getContext().getApplicationClass());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void waitForEmptyEventQueue() {
        JPanel placeHolder = new JPanel();
        boolean queueEmpty = false;
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        while (!queueEmpty) {
            NotifyingEvent e = new NotifyingEvent(placeHolder);
            queue.postEvent(e);
            synchronized (e) {
                while (!e.isDispatched()) {
                    try {
                        e.wait();
                    } catch (InterruptedException e2) {
                    }
                }
                queueEmpty = e.isEventQEmpty();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Application$NotifyingEvent.class */
    public static final class NotifyingEvent extends PaintEvent implements ActiveEvent {
        private boolean dispatched;
        private boolean queueEmpty;

        NotifyingEvent(Component c) {
            super(c, 801, (Rectangle) null);
            this.dispatched = false;
            this.queueEmpty = false;
        }

        synchronized boolean isDispatched() {
            return this.dispatched;
        }

        synchronized boolean isEventQEmpty() {
            return this.queueEmpty;
        }

        public void dispatch() {
            EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
            synchronized (this) {
                this.queueEmpty = q.peekEvent() == null;
                this.dispatched = true;
                notifyAll();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Application$ReadyOnEmptyEventQueue.class */
    public final class ReadyOnEmptyEventQueue extends SwingWorker<Void, Void> {
        private ReadyOnEmptyEventQueue() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* renamed from: doInBackground, reason: merged with bridge method [inline-methods] */
        public Void m11doInBackground() {
            Application.waitForEmptyEventQueue();
            return null;
        }

        protected void done() {
            try {
                get();
            } catch (InterruptedException e) {
                Application.this.handleException(e, "Waiting for empty event queue interrupted.", Level.SEVERE);
            } catch (ExecutionException e2) {
                Application.this.handleException(e2.getCause(), "Error while waiting for an empty event queue.", Level.SEVERE);
            }
            Application.this.ready();
        }
    }

    private static Promise<Boolean> applicationExiting(EventObject evt, List<ExitListener> exitListeners) {
        return Promises.allMatch(exitListeners, listener -> {
            return listener.applicationExiting(evt);
        });
    }

    private void onWindowClosing(WindowEvent evt) {
        getInstance().exit(evt);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Application$PlaceHolderApplication.class */
    private static final class PlaceHolderApplication extends Application {
        private PlaceHolderApplication() {
        }

        @Override // com.jgoodies.application.Application
        protected void startup(String[] args) {
        }

        @Override // com.jgoodies.application.Application
        protected void end() {
        }
    }
}
