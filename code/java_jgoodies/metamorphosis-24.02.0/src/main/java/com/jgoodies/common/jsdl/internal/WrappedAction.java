package com.jgoodies.common.jsdl.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.internal.Messages;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/internal/WrappedAction.class */
public abstract class WrappedAction extends AbstractAction {
    private final Action delegate;

    protected WrappedAction(Action delegate) {
        this.delegate = (Action) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "wrapped action");
        delegate.addPropertyChangeListener(this::onDelegatePropertyChange);
    }

    public Object getValue(String key) {
        return this.delegate.getValue(key);
    }

    public void putValue(String key, Object value) {
        this.delegate.putValue(key, value);
    }

    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    public void setEnabled(boolean b) {
        this.delegate.setEnabled(b);
    }

    protected final void delegateActionPerformed(ActionEvent evt) {
        this.delegate.actionPerformed(evt);
    }

    protected final Action getDelegate() {
        return this.delegate;
    }

    private void onDelegatePropertyChange(PropertyChangeEvent evt) {
        firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
    }
}
