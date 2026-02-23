package com.xy.spring.example;

import com.xy.spring.ioc.ApplicationContext;

/**
 * MainApplication - 主应用程序，演示XY Spring框架的使用
 * MainApplication - Main application, demonstrates the usage of XY Spring framework
 */
public class MainApplication {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("XY Spring Framework - Simplified Version");
        System.out.println("========================================\n");
        
        // 1. 创建Spring应用上下文，扫描指定包下的所有组件
        // 1. Create Spring application context, scan all components under specified package
        System.out.println("--- Initializing ApplicationContext ---");
        ApplicationContext context = new ApplicationContext("com.xy.spring.example");
        System.out.println("\n--- ApplicationContext initialized ---\n");
        
        // 2. 从容器中获取UserService Bean
        // 2. Get UserService bean from container
        UserService userService = context.getBean(UserService.class);
        
        System.out.println("========================================");
        System.out.println("Testing IoC and AOP Features");
        System.out.println("========================================\n");
        
        // 3. 测试用户注册功能（会触发AOP切面）
        // 3. Test user registration (will trigger AOP aspects)
        System.out.println("--- Test 1: Register User ---");
        userService.registerUser("张三");
        System.out.println();
        
        // 4. 测试获取用户信息功能（会触发AOP切面）
        // 4. Test get user info (will trigger AOP aspects)
        System.out.println("--- Test 2: Get User Info ---");
        String userInfo = userService.getUserInfo(1001);
        System.out.println("Result: " + userInfo);
        System.out.println();
        
        // 5. 再测试一次以演示AOP效果
        // 5. Test again to demonstrate AOP effect
        System.out.println("--- Test 3: Register Another User ---");
        userService.registerUser("李四");
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("All tests completed successfully!");
        System.out.println("========================================\n");
        
        // 6. 显示容器中的所有Bean
        // 6. Show all beans in container
        System.out.println("--- Beans in ApplicationContext ---");
        for (String beanName : context.getBeanNames()) {
            System.out.println("  - " + beanName);
        }
        System.out.println();
        
        System.out.println("========================================");
        System.out.println("Framework features demonstrated:");
        System.out.println("1. IoC Container - Component scanning and bean management");
        System.out.println("2. Dependency Injection - @Autowired annotation");
        System.out.println("3. AOP - @Before, @After, @Around advices");
        System.out.println("4. JDK Dynamic Proxy - Creating proxies for interfaces");
        System.out.println("========================================");
    }
}
