package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 后置通知：在目标方法执行之后执行
 * After advice: executed after the target method
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    /**
     * 切点表达式
     * Pointcut expression
     */
    String value();
}
