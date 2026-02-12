package com.jgoodies.binding.value;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/AbstractWrappedValueModel.class */
public abstract class AbstractWrappedValueModel extends AbstractValueModel implements ComponentValueModel {
    private final ValueModel wrappee;
    private final ComponentModel componentPropertyProvider;
    private final PropertyChangeListener valueChangeHandler;
    private final PropertyChangeListener componentPropertyChangeHandler;

    protected abstract PropertyChangeListener createValueChangeHandler();

    public AbstractWrappedValueModel(ValueModel wrappee) {
        this.wrappee = (ValueModel) Preconditions.checkNotNull(wrappee, Messages.MUST_NOT_BE_NULL, "wrapped model");
        this.componentPropertyProvider = wrappee instanceof ComponentModel ? (ComponentModel) wrappee : new SimpleComponentModel();
        this.valueChangeHandler = createValueChangeHandler();
        this.componentPropertyChangeHandler = this::onComponentPropertyChanged;
        wrappee.addValueChangeListener(this.valueChangeHandler);
        this.componentPropertyProvider.addPropertyChangeListener(this.componentPropertyChangeHandler);
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public boolean isEnabled() {
        return this.componentPropertyProvider.isEnabled();
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public void setEnabled(boolean b) {
        this.componentPropertyProvider.setEnabled(b);
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public boolean isVisible() {
        return this.componentPropertyProvider.isVisible();
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public void setVisible(boolean b) {
        this.componentPropertyProvider.setVisible(b);
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public boolean isEditable() {
        return this.componentPropertyProvider.isEditable();
    }

    @Override // com.jgoodies.binding.value.ComponentModel
    public void setEditable(boolean b) {
        this.componentPropertyProvider.setEditable(b);
    }

    public void release() {
        this.wrappee.removeValueChangeListener(this.valueChangeHandler);
        this.componentPropertyProvider.removePropertyChangeListener(this.componentPropertyChangeHandler);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ValueModel getWrappee() {
        return this.wrappee;
    }

    private void onComponentPropertyChanged(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName == null || propertyName.equals(ComponentModel.PROPERTY_ENABLED) || propertyName.equals(ComponentModel.PROPERTY_VISIBLE) || propertyName.equals(ComponentModel.PROPERTY_EDITABLE)) {
            firePropertyChange(propertyName, evt.getOldValue(), evt.getNewValue());
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/AbstractWrappedValueModel$SimpleComponentModel.class */
    private static final class SimpleComponentModel extends Bean implements ComponentModel {
        private boolean enabled = true;
        private boolean visible = true;
        private boolean editable = true;

        SimpleComponentModel() {
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public boolean isEnabled() {
            return this.enabled;
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public void setEnabled(boolean b) {
            boolean oldEnabled = isEnabled();
            this.enabled = b;
            firePropertyChange(ComponentModel.PROPERTY_ENABLED, oldEnabled, b);
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public boolean isVisible() {
            return this.visible;
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public void setVisible(boolean b) {
            boolean oldVisible = isVisible();
            this.visible = b;
            firePropertyChange(ComponentModel.PROPERTY_VISIBLE, oldVisible, b);
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public boolean isEditable() {
            return this.editable;
        }

        @Override // com.jgoodies.binding.value.ComponentModel
        public void setEditable(boolean b) {
            boolean oldEditable = isEditable();
            this.editable = b;
            firePropertyChange(ComponentModel.PROPERTY_EDITABLE, oldEditable, b);
        }
    }
}
