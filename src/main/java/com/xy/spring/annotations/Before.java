package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 前置通知：在目标方法执行之前执行
 * Before advice: executed before the target method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    /**
     * 切点表达式
     * Pointcut expression
     */
    String value();
}
