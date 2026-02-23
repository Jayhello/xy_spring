package com.xy.spring.annotations;

import java.lang.annotation.*;

/**
 * 标记字段需要自动注入依赖
 * Marks a field for automatic dependency injection
 */
@Target({ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    /**
     * 是否必需，默认为true
     * Whether the dependency is required, defaults to true
     */
    boolean required() default true;
}
