package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 前置通知，在目标方法执行前执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    /**
     * 切点表达式，格式: execution(包名.类名.方法名)
     */
    String value();
}
