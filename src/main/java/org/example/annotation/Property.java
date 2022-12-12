package org.example.annotation;

import java.lang.annotation.*;

/**
 * @author Anton Leliuk
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Property {
    String format() default "";
    String name() default "";
}

