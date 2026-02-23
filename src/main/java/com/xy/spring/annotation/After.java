package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 后置通知，在目标方法执行后执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    /**
     * 切点表达式，格式: execution(包名.类名.方法名)
     */
    String value();
}
