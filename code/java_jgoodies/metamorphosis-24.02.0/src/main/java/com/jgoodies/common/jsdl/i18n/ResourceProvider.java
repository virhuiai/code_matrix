package com.jgoodies.common.jsdl.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/ResourceProvider.class */
public @interface ResourceProvider {
    String keyPrefix() default "";

    Class<?> resourceClass() default Object.class;
}
