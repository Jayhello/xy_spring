package com.xy.example.aspect;

import com.xy.spring.annotation.After;
import com.xy.spring.annotation.Around;
import com.xy.spring.annotation.Aspect;
import com.xy.spring.annotation.Before;
import com.xy.spring.annotation.Component;
import com.xy.spring.aop.JoinPoint;
import com.xy.spring.aop.ProceedingJoinPoint;

/**
 * 日志切面，演示AOP功能
 */
@Aspect
@Component
public class LogAspect {

    @Before("execution(com.xy.example.service.*.*)")
    public void beforeLog(JoinPoint joinPoint) {
        System.out.println("=== [前置通知] 方法开始执行: " + joinPoint.getMethodName() + " ===");
    }

    @After("execution(com.xy.example.service.*.*)")
    public void afterLog(JoinPoint joinPoint) {
        System.out.println("=== [后置通知] 方法执行结束: " + joinPoint.getMethodName() + " ===");
    }

    @Around("execution(com.xy.example.service.UserService.createUser)")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println(">>> [环绕通知-前] 开始执行: " + joinPoint.getMethodName());
        long startTime = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();
        
        long endTime = System.currentTimeMillis();
        System.out.println(">>> [环绕通知-后] 执行完成，耗时: " + (endTime - startTime) + "ms");
        return result;
    }
}
