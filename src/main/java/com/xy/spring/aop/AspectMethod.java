package com.xy.spring.aop;

import java.lang.reflect.Method;

/**
 * 切面方法包装类
 */
public class AspectMethod {
    private Object aspectBean;
    private Method method;
    private String pointcut;
    private AdviceType adviceType;

    public AspectMethod(Object aspectBean, Method method, String pointcut, AdviceType adviceType) {
        this.aspectBean = aspectBean;
        this.method = method;
        this.pointcut = pointcut;
        this.adviceType = adviceType;
    }

    public Object getAspectBean() {
        return aspectBean;
    }

    public Method getMethod() {
        return method;
    }

    public String getPointcut() {
        return pointcut;
    }

    public AdviceType getAdviceType() {
        return adviceType;
    }

    public enum AdviceType {
        BEFORE, AFTER, AROUND
    }
}
