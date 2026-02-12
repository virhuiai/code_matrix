package com.jgoodies.binding.value;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/binding/value/BindingConverter.class */
public interface BindingConverter<S, T> {
    T targetValue(S s);

    S sourceValue(T t);
}
