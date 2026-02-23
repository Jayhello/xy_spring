package com.xy.spring.context;

import com.xy.spring.annotation.*;
import com.xy.spring.aop.AopProxyFactory;
import com.xy.spring.aop.AspectMethod;
import com.xy.spring.util.ClassScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用上下文，负责Bean的创建、管理和依赖注入
 */
public class ApplicationContext {
    
    // Bean容器，key为bean名称或类型
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    
    // 切面方法列表
    private List<AspectMethod> aspectMethods = new ArrayList<>();
    
    // 需要AOP代理的类
    private Set<Class<?>> proxyClasses = new HashSet<>();

    /**
     * 扫描指定包，初始化容器
     */
    public ApplicationContext(String basePackage) {
        try {
            // 1. 扫描所有类
            List<Class<?>> classes = ClassScanner.scanPackage(basePackage);
            
            // 2. 收集所有的切面方法
            collectAspectMethods(classes);
            
            // 3. 确定需要代理的类
            determineProxyClasses(classes);
            
            // 4. 实例化所有Component类（不创建代理）
            instantiateBeansWithoutProxy(classes);
            
            // 5. 依赖注入
            injectDependencies();
            
            // 6. 创建代理对象
            createProxies(classes);
            
            System.out.println("ApplicationContext initialized successfully!");
            System.out.println("Registered beans: " + beanMap.keySet());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ApplicationContext", e);
        }
    }

    /**
     * 收集切面方法
     */
    private void collectAspectMethods(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Aspect.class) && clazz.isAnnotationPresent(Component.class)) {
                Object aspectBean = clazz.newInstance();
                String beanName = getBeanName(clazz);
                beanMap.put(beanName, aspectBean);
                beanMap.put(clazz.getName(), aspectBean);
                
                // 扫描切面方法
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Before.class)) {
                        Before before = method.getAnnotation(Before.class);
                        aspectMethods.add(new AspectMethod(aspectBean, method, before.value(), 
                            AspectMethod.AdviceType.BEFORE));
                    } else if (method.isAnnotationPresent(After.class)) {
                        After after = method.getAnnotation(After.class);
                        aspectMethods.add(new AspectMethod(aspectBean, method, after.value(), 
                            AspectMethod.AdviceType.AFTER));
                    } else if (method.isAnnotationPresent(Around.class)) {
                        Around around = method.getAnnotation(Around.class);
                        aspectMethods.add(new AspectMethod(aspectBean, method, around.value(), 
                            AspectMethod.AdviceType.AROUND));
                    }
                }
            }
        }
    }

    /**
     * 确定需要代理的类
     */
    private void determineProxyClasses(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class) && !clazz.isAnnotationPresent(Aspect.class)) {
                // 检查是否有方法匹配切点
                for (AspectMethod aspectMethod : aspectMethods) {
                    String targetPattern = aspectMethod.getPointcut();
                    if (targetPattern.startsWith("execution(")) {
                        targetPattern = targetPattern.substring(10, targetPattern.length() - 1);
                    }
                    
                    // 简化的匹配逻辑
                    if (targetPattern.contains(clazz.getSimpleName()) || targetPattern.contains("*")) {
                        proxyClasses.add(clazz);
                        break;
                    }
                }
            }
        }
    }

    /**
     * 实例化Bean（不创建代理）
     */
    private void instantiateBeansWithoutProxy(List<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class) && !clazz.isAnnotationPresent(Aspect.class)) {
                Object bean = clazz.newInstance();
                
                String beanName = getBeanName(clazz);
                beanMap.put(beanName, bean);
                beanMap.put(clazz.getName(), bean);
            }
        }
    }

    /**
     * 创建代理对象
     */
    private void createProxies(List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Component.class) && !clazz.isAnnotationPresent(Aspect.class)) {
                if (needProxy(clazz)) {
                    Object originalBean = getBean(clazz);
                    Object proxyBean = AopProxyFactory.createProxy(originalBean, aspectMethods);
                    
                    String beanName = getBeanName(clazz);
                    beanMap.put(beanName, proxyBean);
                    beanMap.put(clazz.getName(), proxyBean);
                }
            }
        }
    }

    /**
     * 判断是否需要代理
     */
    private boolean needProxy(Class<?> clazz) {
        return proxyClasses.contains(clazz);
    }

    /**
     * 依赖注入
     */
    private void injectDependencies() throws Exception {
        for (Object bean : new ArrayList<>(beanMap.values())) {
            // 获取真实类（非代理类）
            Class<?> targetClass = bean.getClass();
            
            Field[] fields = targetClass.getDeclaredFields();
                
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();
                    Object dependency = getBean(fieldType);
                    if (dependency != null) {
                        field.set(bean, dependency);
                    }
                }
            }
        }
    }

    /**
     * 获取Bean名称
     */
    private String getBeanName(Class<?> clazz) {
        Component component = clazz.getAnnotation(Component.class);
        String beanName = component.value();
        if (beanName.isEmpty()) {
            beanName = clazz.getSimpleName();
            beanName = Character.toLowerCase(beanName.charAt(0)) + beanName.substring(1);
        }
        return beanName;
    }

    /**
     * 根据名称获取Bean
     */
    public Object getBean(String name) {
        return beanMap.get(name);
    }

    /**
     * 根据类型获取Bean
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        Object bean = beanMap.get(clazz.getName());
        if (bean == null) {
            // 尝试通过类型匹配
            for (Object obj : beanMap.values()) {
                if (clazz.isAssignableFrom(obj.getClass()) || 
                    (obj.getClass().getSuperclass() != null && 
                     clazz.isAssignableFrom(obj.getClass().getSuperclass()))) {
                    return (T) obj;
                }
            }
        }
        return (T) bean;
    }

    /**
     * 获取所有Bean名称
     */
    public Set<String> getBeanNames() {
        return beanMap.keySet();
    }
}
