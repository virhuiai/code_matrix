package com.jgoodies.binding.value;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Function;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/ConverterValueModel.class */
public final class ConverterValueModel<S, T> extends AbstractWrappedValueModel {
    private final ValueModel source;
    private final Function<S, T> sourceToTarget;
    private final Function<T, S> targetToSource;

    public ConverterValueModel(ValueModel source, BindingConverter<S, T> converter) {
        this(source, src -> {
            return converter.targetValue(src);
        }, trgt -> {
            return converter.sourceValue(trgt);
        });
    }

    public ConverterValueModel(ValueModel source, Function<S, T> sourceToTarget, Function<T, S> targetToSource) {
        super(source);
        this.source = source;
        this.sourceToTarget = sourceToTarget;
        this.targetToSource = targetToSource;
    }

    public Object convertFromSubject(Object sourceValue) {
        return this.sourceToTarget.apply(sourceValue);
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public void setValue(Object targetValue) {
        this.source.setValue(this.targetToSource.apply(targetValue));
    }

    @Override // com.jgoodies.binding.value.ValueModel
    public Object getValue() {
        return convertFromSubject(this.source.getValue());
    }

    @Override // com.jgoodies.binding.value.AbstractWrappedValueModel
    protected PropertyChangeListener createValueChangeHandler() {
        return this::onValueChanged;
    }

    private void onValueChanged(PropertyChangeEvent evt) {
        Object convertedOldValue = evt.getOldValue() == null ? null : convertFromSubject(evt.getOldValue());
        Object convertedNewValue = evt.getNewValue() == null ? null : convertFromSubject(evt.getNewValue());
        fireValueChange(convertedOldValue, convertedNewValue);
    }
}
