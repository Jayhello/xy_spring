package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 环绕通知：完全控制目标方法的执行
 * Around advice: full control over target method execution
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
    /**
     * 切点表达式
     * Pointcut expression
     */
    String value();
}
