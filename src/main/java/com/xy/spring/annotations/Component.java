package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 标记一个类为Spring组件，会被容器自动扫描和管理
 * Marks a class as a Spring component to be automatically scanned and managed by the container
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {
    /**
     * Bean的名称，默认为类名首字母小写
     * The name of the bean, defaults to the class name with first letter lowercase
     */
    String value() default "";
}
