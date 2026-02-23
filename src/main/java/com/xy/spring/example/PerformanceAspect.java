package com.xy.spring.example;

import com.xy.spring.annotations.Aspect;
import com.xy.spring.annotations.Around;
import com.xy.spring.aop.ProceedingJoinPoint;

/**
 * PerformanceAspect - 性能监控切面，演示环绕通知
 * PerformanceAspect - Performance monitoring aspect, demonstrates around advice
 */
@Aspect
public class PerformanceAspect {
    
    /**
     * 环绕通知：监控方法执行时间
     * Around advice: monitor method execution time
     */
    @Around("execution(* com.xy.spring.example.UserServiceImpl.getUserInfo(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        System.out.println(">>> [Performance] Starting method: " + joinPoint.getMethodName());
        
        // 执行目标方法
        // Execute target method
        Object result = joinPoint.proceed();
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("<<< [Performance] Method: " + joinPoint.getMethodName() + 
                         " completed in " + duration + "ms");
        
        return result;
    }
}
