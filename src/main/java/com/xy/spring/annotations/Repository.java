package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 标记一个类为数据访问层组件
 * Marks a class as a data access layer component
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
    String value() default "";
}
