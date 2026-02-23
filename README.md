# XY Spring Framework
简化版的 Java Spring 框架 - A Simplified Java Spring Framework

## 项目介绍 / Project Introduction

这是一个使用 Java JDK 8 和 Maven 实现的简化版 Spring 框架，帮助理解 Spring 的核心原理。

This is a simplified version of the Spring Framework implemented with Java JDK 8 and Maven, designed to help understand Spring's core principles.

### 核心功能 / Core Features

1. **IoC 容器 (Inversion of Control Container)**
   - 自动扫描和管理组件
   - 组件注解：`@Component`, `@Service`, `@Repository`
   - Bean 生命周期管理

2. **依赖注入 (Dependency Injection)**
   - `@Autowired` 注解支持
   - 自动类型匹配和注入

3. **AOP 支持 (Aspect-Oriented Programming)**
   - `@Aspect` 切面注解
   - `@Before` 前置通知
   - `@After` 后置通知
   - `@Around` 环绕通知
   - JDK 动态代理实现

## 项目结构 / Project Structure

```
xy-spring/
├── src/main/java/com/xy/spring/
│   ├── annotations/          # 注解定义 / Annotation definitions
│   │   ├── Component.java
│   │   ├── Service.java
│   │   ├── Repository.java
│   │   ├── Autowired.java
│   │   ├── Aspect.java
│   │   ├── Before.java
│   │   ├── After.java
│   │   └── Around.java
│   ├── ioc/                  # IoC 容器实现 / IoC container implementation
│   │   └── ApplicationContext.java
│   ├── aop/                  # AOP 实现 / AOP implementation
│   │   ├── JoinPoint.java
│   │   ├── ProceedingJoinPoint.java
│   │   └── AopProxyFactory.java
│   └── example/              # 示例代码 / Example code
│       ├── MainApplication.java
│       ├── UserService.java
│       ├── UserServiceImpl.java
│       ├── UserDao.java
│       ├── UserDaoImpl.java
│       ├── LoggingAspect.java
│       └── PerformanceAspect.java
└── pom.xml                   # Maven 配置 / Maven configuration
```

## 快速开始 / Quick Start

### 环境要求 / Requirements

- JDK 8 或更高版本 / JDK 8 or higher
- Maven 3.x

### 编译项目 / Build the Project

```bash
mvn clean compile
```

### 运行示例 / Run the Example

```bash
mvn exec:java -Dexec.mainClass="com.xy.spring.example.MainApplication"
```

## 使用说明 / Usage Guide

### 1. 创建组件 / Creating Components

使用 `@Service` 或 `@Repository` 标记你的类：

```java
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    
    public void registerUser(String username) {
        userDao.save(username);
    }
}
```

### 2. 依赖注入 / Dependency Injection

使用 `@Autowired` 注解自动注入依赖：

```java
@Autowired
private UserDao userDao;
```

### 3. 创建切面 / Creating Aspects

使用 `@Aspect` 标记切面类，使用 `@Before`, `@After`, `@Around` 定义通知：

```java
@Aspect
public class LoggingAspect {
    
    @Before("execution(* com.xy.spring.example.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before method: " + joinPoint.getMethodName());
    }
    
    @After("execution(* com.xy.spring.example.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        System.out.println("After method: " + joinPoint.getMethodName());
    }
}
```

### 4. 环绕通知 / Around Advice

```java
@Aspect
public class PerformanceAspect {
    
    @Around("execution(* com.xy.spring.example.UserServiceImpl.getUserInfo(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        System.out.println("Method took " + duration + "ms");
        return result;
    }
}
```

### 5. 初始化容器 / Initializing the Container

```java
// 创建 ApplicationContext 并扫描指定包
ApplicationContext context = new ApplicationContext("com.xy.spring.example");

// 获取 Bean
UserService userService = context.getBean(UserService.class);
userService.registerUser("John");
```

## 核心实现原理 / Core Implementation Principles

### IoC 容器 / IoC Container

1. **包扫描**：递归扫描指定包下的所有类
2. **Bean 创建**：反射创建带有 `@Component` 及其派生注解的类实例
3. **依赖注入**：扫描 `@Autowired` 字段，根据类型自动注入依赖
4. **Bean 管理**：使用 Map 存储和管理所有 Bean 实例

### AOP 实现 / AOP Implementation

1. **切面收集**：识别带有 `@Aspect` 注解的类
2. **动态代理**：使用 JDK 动态代理为实现接口的 Bean 创建代理对象
3. **通知执行**：
   - `@Before`：在目标方法执行前调用
   - `@After`：在目标方法执行后调用
   - `@Around`：完全控制目标方法的执行
4. **切点匹配**：支持简化的 execution 表达式

## 示例输出 / Example Output

```
========================================
XY Spring Framework - Simplified Version
========================================

--- Initializing ApplicationContext ---
Created bean: userDaoImpl -> com.xy.spring.example.UserDaoImpl
Created bean: userServiceImpl -> com.xy.spring.example.UserServiceImpl
Injected dependency: userDao into UserServiceImpl
Found aspect: loggingAspect
Created AOP proxy for: userServiceImpl

--- Test 1: Register User ---
=== [AOP Before] Method: registerUser in class: UserServiceImpl ===
UserService: Registering user 张三
UserDao: Saving user 张三 to database...
=== [AOP After] Method: registerUser in class: UserServiceImpl completed ===

--- Test 2: Get User Info ---
>>> [Performance] Starting method: getUserInfo
UserService: Getting user info for id 1001
<<< [Performance] Method: getUserInfo completed in 0ms
Result: User information: User_1001
```

## 学习要点 / Learning Points

1. **反射 (Reflection)**：用于扫描类、创建实例、注入依赖
2. **注解 (Annotations)**：自定义注解标记组件和配置
3. **动态代理 (Dynamic Proxy)**：JDK 动态代理实现 AOP
4. **设计模式**：工厂模式、代理模式、单例模式

## 限制和简化 / Limitations and Simplifications

相比完整的 Spring 框架，本实现进行了以下简化：

1. 只支持基于接口的 JDK 动态代理（不支持 CGLIB）
2. 简化的切点表达式解析
3. 不支持循环依赖处理
4. 不支持 Bean 作用域（只有单例）
5. 不支持属性配置文件
6. 不支持复杂的 Bean 生命周期回调

## 扩展建议 / Extension Suggestions

如果你想进一步学习，可以尝试添加：

1. CGLIB 代理支持（为没有接口的类创建代理）
2. Bean 作用域支持（Singleton, Prototype）
3. 配置文件支持（properties, YAML）
4. 更复杂的切点表达式
5. 事务管理
6. Web MVC 支持

## 贡献 / Contributing

欢迎提交 Issue 和 Pull Request！

## 许可证 / License

MIT License

