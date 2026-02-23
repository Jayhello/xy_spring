package com.xy.spring.aop;

import java.lang.reflect.Method;

/**
 * JoinPoint - 连接点，封装了方法执行的信息
 * JoinPoint - Join point, encapsulates method execution information
 */
public class JoinPoint {
    
    private Object target;      // 目标对象 / target object
    private Method method;      // 目标方法 / target method
    private Object[] args;      // 方法参数 / method arguments
    
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
    
    public Class<?> getTargetClass() {
        return target.getClass();
    }
}
