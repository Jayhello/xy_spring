package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 标注类为切面类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aspect {
}
