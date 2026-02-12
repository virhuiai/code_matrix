package com.jgoodies.application;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Action.class */
public @interface Action {
    String id() default "";

    boolean enabled() default true;

    String text() default "";

    String icon() default "";

    String accelerator() default "";

    String shortDescription() default "";

    String longDescription() default "";

    String accessibleName() default "";

    String accessibleDescription() default "";

    String command() default "";
}
