package com.xy.spring.aop;

import com.xy.spring.annotations.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * AopProxyFactory - AOP代理工厂，使用JDK动态代理创建代理对象
 * AopProxyFactory - AOP proxy factory, creates proxy objects using JDK dynamic proxy
 */
public class AopProxyFactory {
    
    /**
     * 创建代理对象
     * Create proxy object
     */
    public static Object createProxy(Object target, List<Object> aspects) {
        if (aspects == null || aspects.isEmpty()) {
            return target;
        }
        
        Class<?>[] interfaces = target.getClass().getInterfaces();
        if (interfaces.length == 0) {
            // 如果没有接口，返回原对象（简化版不支持CGLIB）
            // If no interface, return original object (simplified version doesn't support CGLIB)
            System.out.println("Warning: " + target.getClass().getName() + " has no interface, cannot create proxy");
            return target;
        }
        
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new AopInvocationHandler(target, aspects)
        );
    }
    
    /**
     * AOP调用处理器
     * AOP invocation handler
     */
    private static class AopInvocationHandler implements InvocationHandler {
        
        private Object target;
        private List<Object> aspects;
        
        public AopInvocationHandler(Object target, List<Object> aspects) {
            this.target = target;
            this.aspects = aspects;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 跳过Object类的方法
            // Skip Object class methods
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(target, args);
            }
            
            String targetMethodName = method.getName();
            Class<?> targetClass = target.getClass();
            
            // 收集匹配的通知
            // Collect matching advices
            List<AdviceInfo> beforeAdvices = new ArrayList<>();
            List<AdviceInfo> afterAdvices = new ArrayList<>();
            List<AdviceInfo> aroundAdvices = new ArrayList<>();
            
            for (Object aspect : aspects) {
                collectAdvices(aspect, targetClass, targetMethodName, 
                              beforeAdvices, afterAdvices, aroundAdvices);
            }
            
            // 如果有环绕通知，使用环绕通知
            // If there are around advices, use around advice
            // 注意：如果有多个环绕通知，只执行第一个
            // Note: If there are multiple around advices, only the first one is executed
            if (!aroundAdvices.isEmpty()) {
                if (aroundAdvices.size() > 1) {
                    System.out.println("Warning: Multiple @Around advices found for method " + 
                                     method.getName() + ", only executing the first one");
                }
                return executeAroundAdvice(aroundAdvices.get(0), method, args);
            }
            
            // 执行前置通知
            // Execute before advices
            JoinPoint joinPoint = new JoinPoint(target, method, args);
            for (AdviceInfo adviceInfo : beforeAdvices) {
                adviceInfo.method.invoke(adviceInfo.aspect, joinPoint);
            }
            
            Object result = null;
            try {
                // 执行目标方法
                // Execute target method
                result = method.invoke(target, args);
            } finally {
                // 执行后置通知
                // Execute after advices
                for (AdviceInfo adviceInfo : afterAdvices) {
                    adviceInfo.method.invoke(adviceInfo.aspect, joinPoint);
                }
            }
            
            return result;
        }
        
        /**
         * 收集通知方法
         * Collect advice methods
         */
        private void collectAdvices(Object aspect, Class<?> targetClass, String targetMethodName,
                                   List<AdviceInfo> beforeAdvices, List<AdviceInfo> afterAdvices,
                                   List<AdviceInfo> aroundAdvices) {
            Method[] methods = aspect.getClass().getDeclaredMethods();
            for (Method method : methods) {
                // 检查@Before注解
                // Check @Before annotation
                if (method.isAnnotationPresent(Before.class)) {
                    Before before = method.getAnnotation(Before.class);
                    if (matchPointcut(before.value(), targetClass, targetMethodName)) {
                        beforeAdvices.add(new AdviceInfo(aspect, method));
                    }
                }
                
                // 检查@After注解
                // Check @After annotation
                if (method.isAnnotationPresent(After.class)) {
                    After after = method.getAnnotation(After.class);
                    if (matchPointcut(after.value(), targetClass, targetMethodName)) {
                        afterAdvices.add(new AdviceInfo(aspect, method));
                    }
                }
                
                // 检查@Around注解
                // Check @Around annotation
                if (method.isAnnotationPresent(Around.class)) {
                    Around around = method.getAnnotation(Around.class);
                    if (matchPointcut(around.value(), targetClass, targetMethodName)) {
                        aroundAdvices.add(new AdviceInfo(aspect, method));
                    }
                }
            }
        }
        
        /**
         * 匹配切点表达式（简化版）
         * Match pointcut expression (simplified version)
         * 支持格式：execution(* com.xy.spring.example.*.*(..))
         * Supports format: execution(* com.xy.spring.example.*.*(..))
         */
        private boolean matchPointcut(String pointcut, Class<?> targetClass, String methodName) {
            // 简化处理：只支持execution表达式
            // Simplified: only supports execution expression
            if (!pointcut.startsWith("execution(")) {
                return false;
            }
            
            // 去掉execution( 和 )
            // Remove execution( and )
            String expression = pointcut.substring(10, pointcut.length() - 1).trim();
            
            // 分割：返回值 包名.类名.方法名(参数)
            // Split: returnType package.class.method(params)
            String[] parts = expression.split("\\s+");
            if (parts.length < 2) {
                return false;
            }
            
            String methodPattern = parts[1];
            
            // 先去掉参数部分（(..)）
            // Remove parameter part first (..)
            int paramIndex = methodPattern.indexOf('(');
            if (paramIndex != -1) {
                methodPattern = methodPattern.substring(0, paramIndex);
            }
            
            // 解析方法模式
            // Parse method pattern
            int lastDotIndex = methodPattern.lastIndexOf('.');
            if (lastDotIndex == -1) {
                return false;
            }
            
            String classPattern = methodPattern.substring(0, lastDotIndex);
            String methodNamePattern = methodPattern.substring(lastDotIndex + 1);
            
            // 匹配类名
            // Match class name
            String fullClassName = targetClass.getName();
            if (!matchPattern(classPattern, fullClassName)) {
                return false;
            }
            
            // 匹配方法名
            // Match method name
            return matchPattern(methodNamePattern, methodName);
        }
        
        /**
         * 模式匹配（支持*通配符）
         * Pattern matching (supports * wildcard)
         */
        private boolean matchPattern(String pattern, String text) {
            if (pattern.equals("*")) {
                return true;
            }
            
            // 转换为正则表达式：先将.转义，再将*转换为.*
            // Convert to regex: escape . first, then convert * to .*
            String regex = pattern.replace(".", "\\.").replace("*", ".*");
            // 添加开始和结束锚点以确保完全匹配
            // Add start and end anchors to ensure full match
            regex = "^" + regex + "$";
            return text.matches(regex);
        }
        
        /**
         * 执行环绕通知
         * Execute around advice
         */
        private Object executeAroundAdvice(AdviceInfo adviceInfo, Method method, Object[] args) throws Throwable {
            ProceedingJoinPoint joinPoint = new ProceedingJoinPoint(target, method, args);
            return adviceInfo.method.invoke(adviceInfo.aspect, joinPoint);
        }
    }
    
    /**
     * 通知信息
     * Advice information
     */
    private static class AdviceInfo {
        Object aspect;
        Method method;
        
        AdviceInfo(Object aspect, Method method) {
            this.aspect = aspect;
            this.method = method;
        }
    }
}
