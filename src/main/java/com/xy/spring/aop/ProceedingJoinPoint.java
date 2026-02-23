package com.xy.spring.aop;

import java.lang.reflect.Method;

/**
 * 处理连接点，用于环绕通知
 */
public class ProceedingJoinPoint extends JoinPoint {

    public ProceedingJoinPoint(Object target, Method method, Object[] args) {
        super(target, method, args);
    }

    /**
     * 执行目标方法
     */
    public Object proceed() throws Throwable {
        return getMethod().invoke(getTarget(), getArgs());
    }
}
