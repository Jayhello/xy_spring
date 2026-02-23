package com.xy.spring.aop;

import java.lang.reflect.Method;

/**
 * 连接点，封装了目标方法的信息
 */
public class JoinPoint {
    private Object target;
    private Method method;
    private Object[] args;

    public JoinPoint(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public String getMethodName() {
        return method.getName();
    }
}
