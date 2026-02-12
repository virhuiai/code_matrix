package com.jgoodies.application;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/ResourceConverter.class */
public interface ResourceConverter {
    boolean supportsType(Class<?> cls);

    Object convert(ResourceMap resourceMap, String str, String str2, Class<?> cls);
}
