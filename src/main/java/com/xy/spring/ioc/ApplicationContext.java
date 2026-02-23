package com.xy.spring.ioc;

import com.xy.spring.annotations.*;
import com.xy.spring.aop.AopProxyFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ApplicationContext - Spring应用上下文，负责Bean的创建、管理和依赖注入
 * ApplicationContext - Spring application context, responsible for bean creation, management and dependency injection
 */
public class ApplicationContext {
    
    // Bean容器：存储所有的Bean实例
    // Bean container: stores all bean instances
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    
    // Bean类型映射：类型 -> Bean名称列表
    // Bean type mapping: type -> list of bean names
    private Map<Class<?>, List<String>> typeToBeanNames = new ConcurrentHashMap<>();
    
    // 切面Bean列表
    // Aspect bean list
    private List<Object> aspects = new ArrayList<>();
    
    // 扫描的基础包路径
    // Base package path to scan
    private String basePackage;
    
    /**
     * 构造函数：扫描指定包下的所有Component并创建Bean
     * Constructor: scan all components under specified package and create beans
     */
    public ApplicationContext(String basePackage) {
        this.basePackage = basePackage;
        try {
            // 1. 扫描所有的类
            // 1. Scan all classes
            Set<Class<?>> classes = scanClasses(basePackage);
            
            // 2. 创建Bean实例
            // 2. Create bean instances
            createBeans(classes);
            
            // 3. 依赖注入（在AOP代理之前）
            // 3. Dependency injection (before AOP proxy)
            injectDependencies();
            
            // 4. 收集切面
            // 4. Collect aspects
            collectAspects();
            
            // 5. 应用AOP代理
            // 5. Apply AOP proxies
            applyAopProxies();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ApplicationContext", e);
        }
    }
    
    /**
     * 扫描包下的所有类
     * Scan all classes under the package
     */
    private Set<Class<?>> scanClasses(String packageName) throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(packagePath);
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                scanDirectory(file, packageName, classes);
            }
        }
        
        return classes;
    }
    
    /**
     * 递归扫描目录
     * Recursively scan directory
     */
    private void scanDirectory(File directory, String packageName, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    // 检查是否有Component注解或其派生注解
                    // Check if class has Component annotation or its derived annotations
                    if (isComponent(clazz)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    // 忽略无法加载的类
                    // Ignore classes that cannot be loaded
                }
            }
        }
    }
    
    /**
     * 判断类是否是Component
     * Check if class is a Component
     */
    private boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) ||
               clazz.isAnnotationPresent(Service.class) ||
               clazz.isAnnotationPresent(Repository.class) ||
               clazz.isAnnotationPresent(Aspect.class);
    }
    
    /**
     * 创建Bean实例
     * Create bean instances
     */
    private void createBeans(Set<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            // 获取Bean名称
            // Get bean name
            String beanName = getBeanName(clazz);
            
            // 创建实例
            // Create instance
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            // 存储到容器
            // Store in container
            beanMap.put(beanName, instance);
            
            // 记录类型映射
            // Record type mapping
            recordTypeMapping(clazz, beanName);
            
            System.out.println("Created bean: " + beanName + " -> " + clazz.getName());
        }
    }
    
    /**
     * 记录类型到Bean名称的映射
     * Record type to bean name mapping
     */
    private void recordTypeMapping(Class<?> clazz, String beanName) {
        // 记录自身类型
        // Record self type
        typeToBeanNames.computeIfAbsent(clazz, k -> new ArrayList<>()).add(beanName);
        
        // 记录所有接口类型
        // Record all interface types
        for (Class<?> interfaceClass : clazz.getInterfaces()) {
            typeToBeanNames.computeIfAbsent(interfaceClass, k -> new ArrayList<>()).add(beanName);
        }
        
        // 记录父类类型
        // Record parent class types
        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            typeToBeanNames.computeIfAbsent(superClass, k -> new ArrayList<>()).add(beanName);
            superClass = superClass.getSuperclass();
        }
    }
    
    /**
     * 获取Bean名称
     * Get bean name
     */
    private String getBeanName(Class<?> clazz) {
        // 优先使用注解指定的名称
        // Use annotation specified name first
        if (clazz.isAnnotationPresent(Component.class)) {
            String value = clazz.getAnnotation(Component.class).value();
            if (!value.isEmpty()) return value;
        }
        if (clazz.isAnnotationPresent(Service.class)) {
            String value = clazz.getAnnotation(Service.class).value();
            if (!value.isEmpty()) return value;
        }
        if (clazz.isAnnotationPresent(Repository.class)) {
            String value = clazz.getAnnotation(Repository.class).value();
            if (!value.isEmpty()) return value;
        }
        if (clazz.isAnnotationPresent(Aspect.class)) {
            String value = clazz.getAnnotation(Aspect.class).value();
            if (!value.isEmpty()) return value;
        }
        
        // 默认使用类名首字母小写
        // Default to class name with first letter lowercase
        String simpleName = clazz.getSimpleName();
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
    
    /**
     * 收集切面Bean
     * Collect aspect beans
     */
    private void collectAspects() {
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            Object bean = entry.getValue();
            if (bean.getClass().isAnnotationPresent(Aspect.class)) {
                aspects.add(bean);
                System.out.println("Found aspect: " + entry.getKey());
            }
        }
    }
    
    /**
     * 应用AOP代理
     * Apply AOP proxies
     */
    private void applyAopProxies() {
        if (aspects.isEmpty()) {
            return;
        }
        
        // 为每个非切面Bean创建代理
        // Create proxy for each non-aspect bean
        Map<String, Object> proxiedBeans = new HashMap<>();
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            
            // 跳过切面本身
            // Skip aspect itself
            if (bean.getClass().isAnnotationPresent(Aspect.class)) {
                continue;
            }
            
            // 创建代理
            // Create proxy
            Object proxy = AopProxyFactory.createProxy(bean, aspects);
            if (proxy != bean) {
                proxiedBeans.put(beanName, proxy);
                System.out.println("Created AOP proxy for: " + beanName);
            }
        }
        
        // 替换为代理对象
        // Replace with proxy objects
        beanMap.putAll(proxiedBeans);
    }
    
    /**
     * 依赖注入
     * Dependency injection
     */
    private void injectDependencies() throws Exception {
        for (Object bean : beanMap.values()) {
            Class<?> clazz = bean.getClass();
            
            // 遍历所有字段
            // Iterate all fields
            for (Field field : clazz.getDeclaredFields()) {
                // 检查是否有@Autowired注解
                // Check if field has @Autowired annotation
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    
                    // 根据类型查找Bean
                    // Find bean by type
                    Class<?> fieldType = field.getType();
                    Object dependency = getBeanByType(fieldType);
                    
                    if (dependency == null) {
                        Autowired autowired = field.getAnnotation(Autowired.class);
                        if (autowired.required()) {
                            throw new RuntimeException("No bean found for type: " + fieldType.getName());
                        }
                    } else {
                        field.set(bean, dependency);
                        System.out.println("Injected dependency: " + field.getName() + " into " + bean.getClass().getSimpleName());
                    }
                }
            }
        }
    }
    
    /**
     * 根据类型获取Bean
     * Get bean by type
     */
    private Object getBeanByType(Class<?> type) {
        List<String> beanNames = typeToBeanNames.get(type);
        if (beanNames == null || beanNames.isEmpty()) {
            return null;
        }
        if (beanNames.size() > 1) {
            throw new RuntimeException("Multiple beans found for type: " + type.getName() + 
                                     ". Found beans: " + beanNames);
        }
        return beanMap.get(beanNames.get(0));
    }
    
    /**
     * 根据名称获取Bean
     * Get bean by name
     */
    public Object getBean(String name) {
        return beanMap.get(name);
    }
    
    /**
     * 根据类型获取Bean（公共方法）
     * Get bean by type (public method)
     */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        return (T) getBeanByType(type);
    }
    
    /**
     * 获取所有Bean名称
     * Get all bean names
     */
    public Set<String> getBeanNames() {
        return beanMap.keySet();
    }
    
    /**
     * 获取所有Bean
     * Get all beans
     */
    public Collection<Object> getAllBeans() {
        return beanMap.values();
    }
}
