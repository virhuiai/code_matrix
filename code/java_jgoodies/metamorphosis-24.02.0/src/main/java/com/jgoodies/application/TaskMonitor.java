package com.jgoodies.application;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.swing.internal.EDTBean;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskMonitor.class */
public final class TaskMonitor extends EDTBean {
    public static final String PROPERTY_FOREGROUND_TASK = "foregroundTask";
    public static final String PROPERTY_AUTO_UPDATE_FOREGROUND_TASK = "autoUpdateForegroundTask";
    public static final String PROPERTY_TASKS = "tasks";
    public static final String PROPERTY_PENDING = "pending";
    public static final String PROPERTY_STARTED = "started";
    public static final String PROPERTY_BACKGROUND_DONE = "backgroundDone";
    public static final String PROPERTY_DONE = "done";
    private Task<?, ?> foregroundTask = null;
    private boolean autoUpdateForegroundTask = true;
    private final PropertyChangeListener foregroundTaskStateHandler = this::onForegroundTaskChanged;
    private final LinkedList<Task<?, ?>> taskQueue = new LinkedList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskMonitor(TaskService taskService) {
        taskService.addPropertyChangeListener(this::onTaskServiceTasksChanged);
    }

    public Task<?, ?> getForegroundTask() {
        return this.foregroundTask;
    }

    public void setForegroundTask(Task<?, ?> newTask) {
        Task<?, ?> oldTask = getForegroundTask();
        if (oldTask != null) {
            oldTask.removePropertyChangeListener(this.foregroundTaskStateHandler);
        }
        this.foregroundTask = oldTask;
        if (newTask != null) {
            newTask.addPropertyChangeListener(this.foregroundTaskStateHandler);
        }
        this.foregroundTask = newTask;
        firePropertyChange(PROPERTY_FOREGROUND_TASK, oldTask, newTask);
    }

    public boolean getAutoUpdateForegroundTask() {
        return this.autoUpdateForegroundTask;
    }

    public void setAutoUpdateForegroundTask(boolean newValue) {
        boolean oldValue = getAutoUpdateForegroundTask();
        this.autoUpdateForegroundTask = newValue;
        firePropertyChange(PROPERTY_AUTO_UPDATE_FOREGROUND_TASK, oldValue, newValue);
    }

    public List<Task<?, ?>> getTasks() {
        return copyTaskQueue();
    }

    private List<Task<?, ?>> copyTaskQueue() {
        synchronized (this.taskQueue) {
            if (this.taskQueue.isEmpty()) {
                return Collections.emptyList();
            }
            return new ArrayList(this.taskQueue);
        }
    }

    private void updateTasks(List<Task<?, ?>> oldTasks, List<Task<?, ?>> newTasks) {
        boolean tasksChanged = false;
        List<Task<?, ?>> oldTaskQueue = copyTaskQueue();
        for (Task<?, ?> oldTask : oldTasks) {
            if (!newTasks.contains(oldTask) && this.taskQueue.remove(oldTask)) {
                tasksChanged = true;
            }
        }
        for (Task<?, ?> newTask : newTasks) {
            if (!this.taskQueue.contains(newTask)) {
                this.taskQueue.addLast(newTask);
                tasksChanged = true;
            }
        }
        Iterator<Task<?, ?>> tasks = this.taskQueue.iterator();
        while (tasks.hasNext()) {
            Task<?, ?> task = tasks.next();
            if (task.isDone()) {
                tasks.remove();
                tasksChanged = true;
            }
        }
        if (tasksChanged) {
            List<Task<?, ?>> newTaskQueue = copyTaskQueue();
            firePropertyChange("tasks", oldTaskQueue, newTaskQueue);
        }
        if (this.autoUpdateForegroundTask && getForegroundTask() == null) {
            setForegroundTask(this.taskQueue.isEmpty() ? null : this.taskQueue.getLast());
        }
    }

    private void onTaskServiceTasksChanged(PropertyChangeEvent e) {
        String propertyName = e.getPropertyName();
        if ("tasks".equals(propertyName)) {
            List<Task<?, ?>> oldList = (List) e.getOldValue();
            List<Task<?, ?>> newList = (List) e.getNewValue();
            updateTasks(oldList, newList);
        }
    }

    private void onForegroundTaskChanged(PropertyChangeEvent evt) {
        Task<?, ?> task = (Task) evt.getSource();
        if (task == null || task != getForegroundTask()) {
            return;
        }
        PropertyChangeEvent evtWithMonitorSource = new PropertyChangeEvent(this, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        firePropertyChange(evtWithMonitorSource);
        if (Task.PROPERTY_EXTENDED_STATE.equals(evt.getPropertyName())) {
            fireStateChange(task, PROPERTY_BACKGROUND_DONE);
            return;
        }
        if ("state".equals(evt.getPropertyName())) {
            SwingWorker.StateValue newState = (SwingWorker.StateValue) evt.getNewValue();
            switch (AnonymousClass1.$SwitchMap$javax$swing$SwingWorker$StateValue[newState.ordinal()]) {
                case 1:
                    fireStateChange(task, PROPERTY_PENDING);
                    return;
                case AnimatedLabel.LEFT /* 2 */:
                    fireStateChange(task, PROPERTY_STARTED);
                    return;
                case 3:
                    fireStateChange(task, PROPERTY_DONE);
                    setForegroundTask(null);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.jgoodies.application.TaskMonitor$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskMonitor$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$javax$swing$SwingWorker$StateValue = new int[SwingWorker.StateValue.values().length];

        static {
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[SwingWorker.StateValue.PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[SwingWorker.StateValue.STARTED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$javax$swing$SwingWorker$StateValue[SwingWorker.StateValue.DONE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void fireStateChange(Task<?, ?> task, String propertyName) {
        firePropertyChange(new PropertyChangeEvent(task, propertyName, Boolean.FALSE, Boolean.TRUE));
    }
}
