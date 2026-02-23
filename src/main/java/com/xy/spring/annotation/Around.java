package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 环绕通知，可以在目标方法执行前后都执行
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Around {
    /**
     * 切点表达式，格式: execution(包名.类名.方法名)
     */
    String value();
}
