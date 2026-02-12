package com.jgoodies.binding.binder;

import com.jgoodies.binding.internal.IPresentationModel;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/binder/Binders.class */
public final class Binders {
    private Binders() {
    }

    public static ObjectBinder binder() {
        return new ObjectBinderImpl();
    }

    public static BeanBinder binderFor(Object bean) {
        return new BeanBinderImpl(bean);
    }

    public static PresentationModelBinder binderFor(IPresentationModel<?> model) {
        return new PresentationModelBinderImpl(model);
    }
}
