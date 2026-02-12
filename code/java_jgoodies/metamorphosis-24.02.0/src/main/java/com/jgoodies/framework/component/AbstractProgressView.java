package com.jgoodies.framework.component;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.application.Task;
import com.jgoodies.application.TaskMonitor;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/component/AbstractProgressView.class */
public abstract class AbstractProgressView {
    private JComponent panel;

    protected abstract JComponent buildPanel();

    public abstract void setMessageForeground(Color color);

    public abstract void progressMessage(String str);

    public abstract void progressValue(int i);

    public abstract void progressVisible(boolean z);

    public abstract void progressIndeterminate(boolean z);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractProgressView(TaskMonitor taskMonitor) {
        taskMonitor.addPropertyChangeListener(this::onTaskPropertyChange);
    }

    public synchronized JComponent getPanel() {
        if (this.panel == null) {
            this.panel = buildPanel();
        }
        return this.panel;
    }

    private void onTaskPropertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        boolean z = -1;
        switch (propertyName.hashCode()) {
            case -1897185151:
                if (propertyName.equals(TaskMonitor.PROPERTY_STARTED)) {
                    z = false;
                    break;
                }
                break;
            case -1001078227:
                if (propertyName.equals(Task.PROPERTY_PROGRESS)) {
                    z = 3;
                    break;
                }
                break;
            case 5180302:
                if (propertyName.equals(Task.PROPERTY_PROGRESS_INDETERMINATE)) {
                    z = 4;
                    break;
                }
                break;
            case 954925063:
                if (propertyName.equals(Task.PROPERTY_MESSAGE)) {
                    z = 2;
                    break;
                }
                break;
            case 1427023312:
                if (propertyName.equals(TaskMonitor.PROPERTY_BACKGROUND_DONE)) {
                    z = true;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                Task<?, ?> task = (Task) evt.getSource();
                progressIndeterminate(task.isProgressIndeterminate());
                progressMessage(task.getTitle());
                progressVisible(true);
                return;
            case true:
                progressVisible(false);
                progressValue(0);
                return;
            case AnimatedLabel.LEFT /* 2 */:
                String text = (String) evt.getNewValue();
                progressMessage(text);
                return;
            case true:
                progressVisible(true);
                progressIndeterminate(false);
                progressValue(((Integer) evt.getNewValue()).intValue());
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                progressIndeterminate(((Boolean) evt.getNewValue()).booleanValue());
                return;
            default:
                return;
        }
    }
}
