package com.jgoodies.common.jsdl.i18n;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/i18n/Description.class */
public @interface Description {
    String value() default "";
}
