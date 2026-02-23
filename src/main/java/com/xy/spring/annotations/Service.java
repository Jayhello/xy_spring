package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 标记一个类为服务层组件
 * Marks a class as a service layer component
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
