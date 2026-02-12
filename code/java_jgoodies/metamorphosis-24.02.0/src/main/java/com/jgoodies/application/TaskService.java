package com.jgoodies.application;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.internal.EDTBean;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingWorker;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskService.class */
public final class TaskService extends EDTBean {
    public static final String PROPERTY_TASKS = "tasks";
    private final String name;
    private final ExecutorService executorService;
    private final List<Task<?, ?>> tasks;
    private final PropertyChangeListener taskListUpdater;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TaskService(String name) {
        this(name, new ThreadPoolExecutor(3, 10, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue()));
    }

    public TaskService(String name, ExecutorService executorService) {
        this.name = (String) Preconditions.checkNotNull(name, Messages.MUST_NOT_BE_NULL, "name");
        this.executorService = (ExecutorService) Preconditions.checkNotNull(executorService, Messages.MUST_NOT_BE_NULL, "executorService");
        this.tasks = new ArrayList();
        this.taskListUpdater = this::onStateChanged;
    }

    public String getName() {
        return this.name;
    }

    public void execute(Task<?, ?> task) {
        List<Task<?, ?>> oldTaskList;
        List<Task<?, ?>> newTaskList;
        Preconditions.checkNotNull(task, Messages.MUST_NOT_BE_NULL, "task");
        Preconditions.checkState(task.isPending(), "The task has already been executed.");
        synchronized (this.tasks) {
            oldTaskList = copyTasksList();
            this.tasks.add(task);
            newTaskList = copyTasksList();
            task.addPropertyChangeListener(this.taskListUpdater);
        }
        firePropertyChange("tasks", oldTaskList, newTaskList);
        task.enqueue(this.executorService);
    }

    public List<Task<?, ?>> getTasks() {
        return copyTasksList();
    }

    public void shutdown() {
        this.executorService.shutdown();
    }

    public List<Runnable> shutdownNow() {
        return this.executorService.shutdownNow();
    }

    public boolean isShutdown() {
        return this.executorService.isShutdown();
    }

    public boolean isTerminated() {
        return this.executorService.isTerminated();
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.executorService.awaitTermination(timeout, unit);
    }

    private List<Task<?, ?>> copyTasksList() {
        synchronized (this.tasks) {
            if (this.tasks.isEmpty()) {
                return Collections.emptyList();
            }
            return new ArrayList(this.tasks);
        }
    }

    private void onStateChanged(PropertyChangeEvent evt) {
        List<Task<?, ?>> oldTaskList;
        List<Task<?, ?>> newTaskList;
        if (!"state".equals(evt.getPropertyName()) || SwingWorker.StateValue.DONE != evt.getNewValue()) {
            return;
        }
        Task<?, ?> task = (Task) evt.getSource();
        if (task.isDone() || task.isCancelled()) {
            synchronized (this.tasks) {
                oldTaskList = copyTasksList();
                this.tasks.remove(task);
                task.removePropertyChangeListener(this.taskListUpdater);
                newTaskList = copyTasksList();
            }
            firePropertyChange("tasks", oldTaskList, newTaskList);
        }
    }
}
