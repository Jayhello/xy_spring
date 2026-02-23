package com.xy.spring.annotation;

import java.lang.annotation.*;

/**
 * 标注字段需要自动注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
}
