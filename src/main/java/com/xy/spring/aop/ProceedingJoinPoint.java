package com.xy.spring.aop;

import java.lang.reflect.Method;

/**
 * ProceedingJoinPoint - 用于环绕通知，可以控制是否执行目标方法
 * ProceedingJoinPoint - For around advice, can control whether to execute target method
 */
public class ProceedingJoinPoint extends JoinPoint {
    
    public ProceedingJoinPoint(Object target, Method method, Object[] args) {
        super(target, method, args);
    }
    
    /**
     * 执行目标方法
     * Execute target method
     */
    public Object proceed() throws Throwable {
        return getMethod().invoke(getTarget(), getArgs());
    }
    
    /**
     * 使用新参数执行目标方法
     * Execute target method with new arguments
     */
    public Object proceed(Object[] newArgs) throws Throwable {
        return getMethod().invoke(getTarget(), newArgs);
    }
}
