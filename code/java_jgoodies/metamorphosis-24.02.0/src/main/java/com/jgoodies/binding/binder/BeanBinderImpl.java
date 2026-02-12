package com.jgoodies.binding.binder;

import com.jgoodies.binding.beans.BeanAdapter;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/BeanBinderImpl.class */
public class BeanBinderImpl<B> extends ObjectBinderImpl implements BeanBinder {
    private final B target;
    private BeanAdapter<B> beanAdapter;

    public BeanBinderImpl(B target) {
        this.target = target;
    }

    @Override // com.jgoodies.binding.binder.BeanBinder
    public ValueModelBindingBuilder bindProperty(String propertyName) {
        return new ValueModelBindingBuilderImpl(getBeanAdapter().getValueModel(propertyName), propertyName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public B getTarget() {
        return this.target;
    }

    protected BeanAdapter<B> getBeanAdapter() {
        if (this.beanAdapter == null) {
            this.beanAdapter = new BeanAdapter<>((Object) this.target, true);
        }
        return this.beanAdapter;
    }
}
