package com.xy.spring.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * AOP代理工厂，使用CGLIB创建代理对象
 */
public class AopProxyFactory {

    /**
     * 创建代理对象
     */
    public static Object createProxy(Object target, List<AspectMethod> aspectMethods) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new AopMethodInterceptor(target, aspectMethods));
        return enhancer.create();
    }

    /**
     * 方法拦截器
     */
    static class AopMethodInterceptor implements MethodInterceptor {
        private Object target;
        private List<AspectMethod> aspectMethods;

        public AopMethodInterceptor(Object target, List<AspectMethod> aspectMethods) {
            this.target = target;
            this.aspectMethods = aspectMethods;
        }

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            String targetMethod = target.getClass().getName() + "." + method.getName();
            
            // 查找匹配的切面方法
            List<AspectMethod> beforeMethods = new ArrayList<>();
            List<AspectMethod> afterMethods = new ArrayList<>();
            List<AspectMethod> aroundMethods = new ArrayList<>();
            
            for (AspectMethod aspectMethod : aspectMethods) {
                if (matchPointcut(aspectMethod.getPointcut(), targetMethod)) {
                    switch (aspectMethod.getAdviceType()) {
                        case BEFORE:
                            beforeMethods.add(aspectMethod);
                            break;
                        case AFTER:
                            afterMethods.add(aspectMethod);
                            break;
                        case AROUND:
                            aroundMethods.add(aspectMethod);
                            break;
                    }
                }
            }

            JoinPoint joinPoint = new JoinPoint(target, method, args);
            
            try {
                // 执行前置通知
                for (AspectMethod aspectMethod : beforeMethods) {
                    aspectMethod.getMethod().invoke(aspectMethod.getAspectBean(), joinPoint);
                }

                // 执行环绕通知或目标方法
                Object result;
                if (!aroundMethods.isEmpty()) {
                    ProceedingJoinPoint pjp = new ProceedingJoinPoint(target, method, args);
                    result = aroundMethods.get(0).getMethod().invoke(
                        aroundMethods.get(0).getAspectBean(), pjp);
                } else {
                    result = method.invoke(target, args);
                }

                // 执行后置通知
                for (AspectMethod aspectMethod : afterMethods) {
                    aspectMethod.getMethod().invoke(aspectMethod.getAspectBean(), joinPoint);
                }

                return result;
            } catch (Exception e) {
                throw e.getCause() != null ? e.getCause() : e;
            }
        }

        /**
         * 简化的切点匹配，支持通配符*
         */
        private boolean matchPointcut(String pointcut, String targetMethod) {
            // 去除 execution() 包装
            if (pointcut.startsWith("execution(") && pointcut.endsWith(")")) {
                pointcut = pointcut.substring(10, pointcut.length() - 1);
            }
            
            // 简单的通配符匹配
            String regex = pointcut.replace(".", "\\.").replace("*", ".*");
            return targetMethod.matches(regex);
        }
    }
}
