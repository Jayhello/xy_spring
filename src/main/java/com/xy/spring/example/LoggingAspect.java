package com.xy.spring.example;

import com.xy.spring.annotations.Aspect;
import com.xy.spring.annotations.Before;
import com.xy.spring.annotations.After;
import com.xy.spring.annotations.Around;
import com.xy.spring.aop.JoinPoint;
import com.xy.spring.aop.ProceedingJoinPoint;

/**
 * LoggingAspect - 日志切面，演示AOP的使用
 * LoggingAspect - Logging aspect, demonstrates AOP usage
 */
@Aspect
public class LoggingAspect {
    
    /**
     * 前置通知：在方法执行前记录日志
     * Before advice: log before method execution
     */
    @Before("execution(* com.xy.spring.example.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("=== [AOP Before] Method: " + joinPoint.getMethodName() + 
                         " in class: " + joinPoint.getTargetClass().getSimpleName() + " ===");
    }
    
    /**
     * 后置通知：在方法执行后记录日志
     * After advice: log after method execution
     */
    @After("execution(* com.xy.spring.example.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("=== [AOP After] Method: " + joinPoint.getMethodName() + 
                         " in class: " + joinPoint.getTargetClass().getSimpleName() + " completed ===");
    }
}
