package com.xy.example;

import com.xy.example.service.OrderService;
import com.xy.example.service.UserService;
import com.xy.spring.context.ApplicationContext;

/**
 * 示例程序入口
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("    XY简化版Spring框架演示");
        System.out.println("====================================\n");

        // 1. 创建应用上下文，扫描com.xy.example包
        System.out.println("1. 初始化Spring容器...\n");
        ApplicationContext context = new ApplicationContext("com.xy.example");

        System.out.println("\n====================================\n");

        // 2. 获取UserService bean
        System.out.println("2. 测试UserService（带AOP）...\n");
        UserService userService = context.getBean(UserService.class);
        String result1 = userService.createUser("张三");
        System.out.println("返回结果: " + result1);

        System.out.println("\n====================================\n");

        // 3. 测试查询用户
        System.out.println("3. 测试查询用户...\n");
        String result2 = userService.getUser("李四");
        System.out.println("返回结果: " + result2);

        System.out.println("\n====================================\n");

        // 4. 测试依赖注入 - OrderService依赖UserService
        System.out.println("4. 测试依赖注入（OrderService依赖UserService）...\n");
        OrderService orderService = context.getBean(OrderService.class);
        String result3 = orderService.createOrder("王五", "iPhone 15");
        System.out.println("返回结果: " + result3);

        System.out.println("\n====================================");
        System.out.println("    演示完成！");
        System.out.println("====================================");
        
        System.out.println("\n总结:");
        System.out.println("✓ IoC容器: 自动扫描@Component注解的类并实例化");
        System.out.println("✓ 依赖注入: 自动注入@Autowired标记的字段");
        System.out.println("✓ AOP支持: 支持@Before、@After、@Around通知");
        System.out.println("✓ 代理模式: 使用CGLIB创建AOP代理");
    }
}
