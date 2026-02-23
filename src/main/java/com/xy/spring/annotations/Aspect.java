package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 标记一个类为切面
 * Marks a class as an aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Aspect {
    String value() default "";
}
