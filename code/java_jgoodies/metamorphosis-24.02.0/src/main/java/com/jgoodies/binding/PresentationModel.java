package com.jgoodies.binding;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.internal.IPresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.AbstractWrappedValueModel;
import com.jgoodies.binding.value.BufferedValueModel;
import com.jgoodies.binding.value.DefaultComponentValueModel;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.bean.BeanUtils;
import com.jgoodies.common.internal.Messages;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/PresentationModel.class */
public class PresentationModel<B> extends Model implements IPresentationModel<B> {
    public static final String PROPERTY_BEFORE_BEAN = "beforeBean";
    public static final String PROPERTY_BEAN = "bean";
    public static final String PROPERTY_AFTER_BEAN = "afterBean";
    public static final String PROPERTY_TRIGGERCHANNEL = "triggerChannel";
    public static final String PROPERTY_BUFFERING = "buffering";
    public static final String PROPERTY_CHANGED = "changed";
    private final BeanAdapter<B> beanAdapter;
    private ValueModel triggerChannel;
    private final Map<String, WrappedBuffer> wrappedBuffers;
    private final PropertyChangeListener bufferingUpdateHandler;
    private boolean buffering;
    private final PropertyChangeListener changedUpdateHandler;
    private boolean changed;
    private final Map<String, AbstractWrappedValueModel> componentModels;
    private final Map<String, AbstractWrappedValueModel> bufferedComponentModels;

    public PresentationModel() {
        this((Object) null);
    }

    public PresentationModel(B bean) {
        this((ValueModel) new ValueHolder(bean, true));
    }

    public PresentationModel(B bean, ValueModel triggerChannel) {
        this((ValueModel) new ValueHolder(bean, true), triggerChannel);
    }

    public PresentationModel(ValueModel beanChannel) {
        this(beanChannel, (ValueModel) new Trigger());
    }

    public PresentationModel(ValueModel beanChannel, ValueModel triggerChannel) {
        this.buffering = false;
        this.changed = false;
        this.beanAdapter = createBeanAdapter(beanChannel);
        this.triggerChannel = triggerChannel;
        this.wrappedBuffers = new HashMap();
        this.componentModels = new HashMap();
        this.bufferedComponentModels = new HashMap();
        this.bufferingUpdateHandler = this::onBufferingStateChanged;
        this.changed = false;
        this.changedUpdateHandler = this::onValueChanged;
        this.beanAdapter.addPropertyChangeListener(this::onBeanChanged);
        observeChanged(this.beanAdapter, "changed");
    }

    protected BeanAdapter<B> createBeanAdapter(ValueModel beanChannel) {
        return new BeanAdapter<>(beanChannel, true);
    }

    public ValueModel getBeanChannel() {
        return this.beanAdapter.getBeanChannel();
    }

    public B getBean() {
        return (B) getBeanChannel().getValue();
    }

    public void setBean(B newBean) {
        getBeanChannel().setValue(newBean);
    }

    protected void beforeBeanChange(B oldBean, B newBean) {
        firePropertyChange("beforeBean", oldBean, newBean, true);
    }

    protected void afterBeanChange(B oldBean, B newBean) {
        setChanged(false);
        firePropertyChange("afterBean", oldBean, newBean, true);
    }

    protected void onBeanChanging(B oldBean, B newBean) {
    }

    protected void onBeanChanged(B oldBean, B newBean) {
    }

    public Object getValue(String propertyName) {
        return this.beanAdapter.getValue(propertyName);
    }

    public void setValue(String propertyName, Object newValue) {
        this.beanAdapter.setValue(propertyName, newValue);
    }

    public void setVetoableValue(String propertyName, Object newValue) throws PropertyVetoException {
        this.beanAdapter.setVetoableValue(propertyName, newValue);
    }

    public Object getBufferedValue(String propertyName) {
        return getBufferedModel(propertyName).getValue();
    }

    public void setBufferedValue(String propertyName, Object newValue) {
        getBufferedModel(propertyName).setValue(newValue);
    }

    public AbstractValueModel getModel(String propertyName) {
        return this.beanAdapter.getValueModel(propertyName);
    }

    public AbstractValueModel getModel(String propertyName, String getterName, String setterName) {
        return this.beanAdapter.getValueModel(propertyName, getterName, setterName);
    }

    @Override // com.jgoodies.binding.internal.IPresentationModel
    public AbstractWrappedValueModel getComponentModel(String propertyName) {
        AbstractWrappedValueModel componentModel = this.componentModels.get(propertyName);
        if (componentModel == null) {
            AbstractValueModel model = getModel(propertyName);
            componentModel = new DefaultComponentValueModel(model);
            this.componentModels.put(propertyName, componentModel);
        }
        return componentModel;
    }

    public final List<AbstractWrappedValueModel> getComponentModels(String... propertyNames) {
        Preconditions.checkNotNullOrEmpty(propertyNames, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "property names");
        List<AbstractWrappedValueModel> models = new ArrayList<>();
        for (String propertyName : propertyNames) {
            models.add(getComponentModel(propertyName));
        }
        return Collections.unmodifiableList(models);
    }

    public final void setEnabled(boolean enabled, String... propertyNames) {
        for (AbstractWrappedValueModel componentModel : getComponentModels(propertyNames)) {
            componentModel.setEnabled(enabled);
        }
    }

    public final void setEditable(boolean editable, String... propertyNames) {
        for (AbstractWrappedValueModel componentModel : getComponentModels(propertyNames)) {
            componentModel.setEditable(editable);
        }
    }

    public final void setVisible(boolean visible, String... propertyNames) {
        for (AbstractWrappedValueModel componentModel : getComponentModels(propertyNames)) {
            componentModel.setVisible(visible);
        }
    }

    public BufferedValueModel getBufferedModel(String propertyName) {
        return getBufferedModel(propertyName, null, null);
    }

    public BufferedValueModel getBufferedModel(String propertyName, String getterName, String setterName) {
        WrappedBuffer wrappedBuffer = this.wrappedBuffers.get(propertyName);
        if (wrappedBuffer == null) {
            wrappedBuffer = new WrappedBuffer(buffer(getModel(propertyName, getterName, setterName)), getterName, setterName);
            this.wrappedBuffers.put(propertyName, wrappedBuffer);
        } else {
            Preconditions.checkArgument(Objects.equals(getterName, wrappedBuffer.getterName) && Objects.equals(setterName, wrappedBuffer.setterName), "You must not invoke this method twice with different getter and/or setter names.");
        }
        return wrappedBuffer.buffer;
    }

    public AbstractWrappedValueModel getBufferedComponentModel(String propertyName) {
        AbstractWrappedValueModel bufferedComponentModel = this.bufferedComponentModels.get(propertyName);
        if (bufferedComponentModel == null) {
            AbstractValueModel model = getBufferedModel(propertyName);
            bufferedComponentModel = new DefaultComponentValueModel(model);
            this.bufferedComponentModels.put(propertyName, bufferedComponentModel);
        }
        return bufferedComponentModel;
    }

    private BufferedValueModel buffer(ValueModel valueModel) {
        BufferedValueModel bufferedModel = new BufferedValueModel(valueModel, getTriggerChannel());
        bufferedModel.addPropertyChangeListener("buffering", this.bufferingUpdateHandler);
        return bufferedModel;
    }

    public ValueModel getTriggerChannel() {
        return this.triggerChannel;
    }

    public void setTriggerChannel(ValueModel newTriggerChannel) {
        Preconditions.checkNotNull(newTriggerChannel, Messages.MUST_NOT_BE_NULL, "trigger channel");
        ValueModel oldTriggerChannel = getTriggerChannel();
        this.triggerChannel = newTriggerChannel;
        for (WrappedBuffer wrappedBuffer : this.wrappedBuffers.values()) {
            wrappedBuffer.buffer.setTriggerChannel(this.triggerChannel);
        }
        firePropertyChange("triggerChannel", oldTriggerChannel, newTriggerChannel);
    }

    public void triggerCommit() {
        if (Boolean.TRUE.equals(getTriggerChannel().getValue())) {
            getTriggerChannel().setValue((Object) null);
        }
        getTriggerChannel().setValue(Boolean.TRUE);
    }

    public void triggerFlush() {
        if (Boolean.FALSE.equals(getTriggerChannel().getValue())) {
            getTriggerChannel().setValue((Object) null);
        }
        getTriggerChannel().setValue(Boolean.FALSE);
    }

    public boolean isBuffering() {
        return this.buffering;
    }

    private void setBuffering(boolean newValue) {
        boolean oldValue = isBuffering();
        this.buffering = newValue;
        firePropertyChange("buffering", oldValue, newValue);
    }

    private void updateBufferingState(boolean latestBufferingStateChange) {
        if (this.buffering == latestBufferingStateChange) {
            return;
        }
        boolean nowBuffering = false;
        for (WrappedBuffer wrappedBuffer : this.wrappedBuffers.values()) {
            BufferedValueModel model = wrappedBuffer.buffer;
            nowBuffering = nowBuffering || model.isBuffering();
            if (!this.buffering && nowBuffering) {
                setBuffering(true);
                return;
            }
        }
        setBuffering(nowBuffering);
    }

    public boolean isChanged() {
        return this.changed;
    }

    public void resetChanged() {
        setChanged(false);
        this.beanAdapter.resetChanged();
    }

    protected void setChanged(boolean newValue) {
        boolean oldValue = isChanged();
        this.changed = newValue;
        firePropertyChange("changed", oldValue, newValue);
    }

    public void observeChanged(Object bean, String propertyName) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        BeanUtils.addPropertyChangeListener(bean, propertyName, this.changedUpdateHandler);
    }

    public void observeChanged(ValueModel valueModel) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "ValueModel");
        valueModel.addValueChangeListener(this.changedUpdateHandler);
    }

    public void retractInterestFor(Object bean, String propertyName) {
        Preconditions.checkNotNull(bean, Messages.MUST_NOT_BE_NULL, "bean");
        Preconditions.checkNotNull(propertyName, Messages.MUST_NOT_BE_NULL, "property name");
        BeanUtils.removePropertyChangeListener(bean, propertyName, this.changedUpdateHandler);
    }

    public void retractInterestFor(ValueModel valueModel) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "ValueModel");
        valueModel.removeValueChangeListener(this.changedUpdateHandler);
    }

    public synchronized void addBeanPropertyChangeListener(PropertyChangeListener listener) {
        this.beanAdapter.addBeanPropertyChangeListener(listener);
    }

    public synchronized void removeBeanPropertyChangeListener(PropertyChangeListener listener) {
        this.beanAdapter.removeBeanPropertyChangeListener(listener);
    }

    public synchronized void addBeanPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.beanAdapter.addBeanPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removeBeanPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        this.beanAdapter.removeBeanPropertyChangeListener(propertyName, listener);
    }

    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners() {
        return this.beanAdapter.getBeanPropertyChangeListeners();
    }

    public synchronized PropertyChangeListener[] getBeanPropertyChangeListeners(String propertyName) {
        return this.beanAdapter.getBeanPropertyChangeListeners(propertyName);
    }

    public void release() {
        this.beanAdapter.release();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/PresentationModel$WrappedBuffer.class */
    public static final class WrappedBuffer {
        final BufferedValueModel buffer;
        final String getterName;
        final String setterName;

        WrappedBuffer(BufferedValueModel buffer, String getterName, String setterName) {
            this.buffer = buffer;
            this.getterName = getterName;
            this.setterName = setterName;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void onBeanChanged(PropertyChangeEvent evt) {
        Object oldValue = evt.getOldValue();
        Object newValue = evt.getNewValue();
        String propertyName = evt.getPropertyName();
        boolean z = -1;
        switch (propertyName.hashCode()) {
            case 3019696:
                if (propertyName.equals("bean")) {
                    z = true;
                    break;
                }
                break;
            case 517992399:
                if (propertyName.equals("beforeBean")) {
                    z = false;
                    break;
                }
                break;
            case 1018707884:
                if (propertyName.equals("afterBean")) {
                    z = 2;
                    break;
                }
                break;
        }
        switch (z) {
            case AnimatedLabel.CENTER /* 0 */:
                onBeanChanging(oldValue, newValue);
                beforeBeanChange(oldValue, newValue);
                return;
            case true:
                firePropertyChange("bean", oldValue, newValue, true);
                return;
            case AnimatedLabel.LEFT /* 2 */:
                afterBeanChange(oldValue, newValue);
                onBeanChanged(oldValue, newValue);
                return;
            default:
                return;
        }
    }

    private void onBufferingStateChanged(PropertyChangeEvent evt) {
        updateBufferingState(((Boolean) evt.getNewValue()).booleanValue());
    }

    private void onValueChanged(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (!"changed".equals(propertyName) || ((Boolean) evt.getNewValue()).booleanValue()) {
            setChanged(true);
        }
    }
}
