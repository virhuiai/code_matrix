package com.jgoodies.binding.binder;

import com.jgoodies.binding.internal.IPresentationModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/PresentationModelBinderImpl.class */
public class PresentationModelBinderImpl<PM extends IPresentationModel<?>> extends BeanBinderImpl<PM> implements PresentationModelBinder {
    public PresentationModelBinderImpl(PM model) {
        super(model);
    }

    @Override // com.jgoodies.binding.binder.PresentationModelBinder
    public ValueModelBindingBuilder bindBeanProperty(String propertyName) {
        return new ValueModelBindingBuilderImpl(((IPresentationModel) getTarget()).getComponentModel(propertyName), propertyName);
    }
}
