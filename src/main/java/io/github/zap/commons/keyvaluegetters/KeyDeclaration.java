package io.github.zap.commons.keyvaluegetters;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signal and provide information for {@link KeyValueGetter}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyDeclaration {
    boolean required() default true;
    String name() default "";
    String description() default "";
}
