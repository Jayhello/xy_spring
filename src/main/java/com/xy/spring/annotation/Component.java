package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 标注类为Spring组件，会被自动扫描并注册到容器中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    String value() default "";
}
