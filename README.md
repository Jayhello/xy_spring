# XY Spring - 简化版的Spring框架

一个使用Java 8和Maven实现的简化版Spring框架，帮助理解Spring的核心原理，支持IoC（控制反转）和AOP（面向切面编程）。

## 项目特性

✅ **IoC容器**: 自动扫描和管理Bean  
✅ **依赖注入**: 使用@Autowired自动注入依赖  
✅ **AOP支持**: 支持前置、后置、环绕通知  
✅ **CGLIB代理**: 使用CGLIB创建动态代理  
✅ **注解驱动**: 使用注解配置，无需XML  

## 技术栈

- Java 8
- Maven
- CGLIB 3.3.0

## 项目结构

```
xy_spring/
├── src/main/java/
│   ├── com/xy/spring/          # 框架核心代码
│   │   ├── annotation/         # 注解定义
│   │   │   ├── Component.java      # 标记Spring组件
│   │   │   ├── Autowired.java      # 标记自动注入
│   │   │   ├── Aspect.java         # 标记切面类
│   │   │   ├── Before.java         # 前置通知
│   │   │   ├── After.java          # 后置通知
│   │   │   └── Around.java         # 环绕通知
│   │   ├── context/            # 容器上下文
│   │   │   └── ApplicationContext.java  # IoC容器核心
│   │   ├── aop/                # AOP实现
│   │   │   ├── JoinPoint.java              # 连接点
│   │   │   ├── ProceedingJoinPoint.java    # 处理连接点
│   │   │   ├── AspectMethod.java           # 切面方法
│   │   │   └── AopProxyFactory.java        # AOP代理工厂
│   │   └── util/               # 工具类
│   │       └── ClassScanner.java  # 类扫描器
│   └── com/xy/example/         # 示例代码
│       ├── Application.java        # 程序入口
│       ├── service/                # 服务层
│       │   ├── UserService.java
│       │   └── OrderService.java
│       └── aspect/                 # 切面层
│           └── LogAspect.java
└── pom.xml                     # Maven配置
```

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/Jayhello/xy_spring.git
cd xy_spring
```

### 2. 编译项目

```bash
mvn clean compile
```

### 3. 运行示例

由于使用了CGLIB，需要添加JVM参数来打开Java模块系统：

```bash
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
mvn exec:java -Dexec.mainClass="com.xy.example.Application"
```

或者使用Java命令直接运行（JDK 9+）：

```bash
java --add-opens java.base/java.lang=ALL-UNNAMED \
     -cp "target/classes:~/.m2/repository/cglib/cglib/3.3.0/cglib-3.3.0.jar:~/.m2/repository/org/ow2/asm/asm/7.1/asm-7.1.jar" \
     com.xy.example.Application
```

## 核心功能详解

### 1. IoC容器 (控制反转)

使用`@Component`注解标记类，框架会自动扫描并实例化：

```java
@Component
public class UserService {
    public String getUser(String username) {
        return "用户信息: " + username;
    }
}
```

### 2. 依赖注入

使用`@Autowired`注解自动注入依赖：

```java
@Component
public class OrderService {
    @Autowired
    private UserService userService;  // 自动注入
    
    public String createOrder(String username, String product) {
        userService.getUser(username);  // 使用注入的依赖
        return "订单创建成功";
    }
}
```

### 3. AOP切面编程

#### 定义切面类

使用`@Aspect`和`@Component`注解标记切面类：

```java
@Aspect
@Component
public class LogAspect {
    
    // 前置通知 - 方法执行前
    @Before("execution(com.xy.example.service.*.*)")
    public void beforeLog(JoinPoint joinPoint) {
        System.out.println("方法开始: " + joinPoint.getMethodName());
    }
    
    // 后置通知 - 方法执行后
    @After("execution(com.xy.example.service.*.*)")
    public void afterLog(JoinPoint joinPoint) {
        System.out.println("方法结束: " + joinPoint.getMethodName());
    }
    
    // 环绕通知 - 完全控制方法执行
    @Around("execution(com.xy.example.service.UserService.createUser)")
    public Object aroundLog(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("环绕通知-前");
        Object result = joinPoint.proceed();  // 执行目标方法
        System.out.println("环绕通知-后");
        return result;
    }
}
```

#### 切点表达式

支持简单的切点表达式：

- `execution(com.xy.example.service.*.*)` - 匹配service包下所有类的所有方法
- `execution(com.xy.example.service.UserService.createUser)` - 匹配特定方法
- 支持通配符`*`

### 4. 初始化容器

```java
// 扫描指定包，自动注册Bean和配置AOP
ApplicationContext context = new ApplicationContext("com.xy.example");

// 获取Bean
UserService userService = context.getBean(UserService.class);
```

## 实现原理

### 1. IoC容器实现

1. **类扫描**: 扫描指定包下的所有类
2. **Bean实例化**: 实例化所有标记@Component的类
3. **依赖注入**: 为标记@Autowired的字段注入依赖
4. **Bean存储**: 使用Map存储Bean实例

```java
// 核心逻辑
1. 扫描类 -> 2. 实例化Bean -> 3. 注入依赖 -> 4. 创建代理
```

### 2. AOP实现

1. **切面收集**: 扫描所有@Aspect类，收集切面方法
2. **代理创建**: 使用CGLIB为需要AOP的Bean创建代理
3. **方法拦截**: 代理对象拦截方法调用
4. **通知执行**: 在适当时机执行前置、后置、环绕通知

```java
// CGLIB代理流程
目标方法调用 -> 代理拦截 -> 执行前置通知 -> 执行目标方法 -> 执行后置通知 -> 返回结果
```

### 3. 关键设计点

- **先注入后代理**: 先完成依赖注入，再创建AOP代理，确保代理对象能访问注入的依赖
- **CGLIB代理**: 使用字节码增强技术创建子类代理，无需接口
- **切点匹配**: 使用正则表达式实现简单的切点匹配

## 示例输出

```
====================================
    XY简化版Spring框架演示
====================================

1. 初始化Spring容器...

ApplicationContext initialized successfully!
Registered beans: [orderService, userService, logAspect, ...]

====================================

2. 测试UserService（带AOP）...

=== [前置通知] 方法开始执行: createUser ===
>>> [环绕通知-前] 开始执行: createUser
正在创建用户: 张三
>>> [环绕通知-后] 执行完成，耗时: 1ms
=== [后置通知] 方法执行结束: createUser ===
返回结果: 用户 张三 创建成功！

====================================

4. 测试依赖注入（OrderService依赖UserService）...

=== [前置通知] 方法开始执行: createOrder ===
正在查询用户: 王五
用户验证成功: 用户信息: 王五
正在为用户 王五 创建订单: iPhone 15
=== [后置通知] 方法执行结束: createOrder ===
返回结果: 订单创建成功！商品: iPhone 15
```

## 与Spring框架的对比

| 特性 | XY Spring | Spring Framework |
|------|-----------|------------------|
| IoC容器 | ✅ 基础实现 | ✅ 完整实现 |
| 依赖注入 | ✅ 字段注入 | ✅ 字段/构造器/setter注入 |
| AOP | ✅ 基于CGLIB | ✅ JDK动态代理+CGLIB |
| Bean作用域 | ❌ 单例模式 | ✅ 多种作用域 |
| 生命周期回调 | ❌ | ✅ InitializingBean等 |
| 配置方式 | ✅ 注解 | ✅ 注解/XML/JavaConfig |
| 事务管理 | ❌ | ✅ 声明式事务 |

## 学习要点

通过这个简化版框架，你可以学到：

1. **IoC原理**: 如何通过反射创建和管理对象
2. **依赖注入**: 如何自动装配对象之间的依赖关系
3. **AOP原理**: 如何使用代理模式实现横切关注点
4. **注解处理**: 如何读取和处理自定义注解
5. **设计模式**: 工厂模式、代理模式、单例模式等

## 扩展建议

如果想进一步扩展这个框架，可以考虑：

1. **Bean作用域**: 支持prototype、request等作用域
2. **构造器注入**: 支持@Autowired构造器注入
3. **条件注入**: 支持@Conditional条件装配
4. **配置属性**: 支持@Value注入配置值
5. **生命周期回调**: 支持@PostConstruct和@PreDestroy
6. **事件机制**: 实现ApplicationEvent和ApplicationListener

## 常见问题

### Q: 为什么需要添加--add-opens参数？

A: 从Java 9开始，引入了模块系统。CGLIB需要访问java.lang包的内部API，因此需要显式打开该模块。

### Q: 能否支持JDK 8？

A: 可以。如果使用JDK 8运行，不需要添加--add-opens参数。项目编译目标就是Java 8。

### Q: 为什么使用CGLIB而不是JDK动态代理？

A: JDK动态代理要求目标类实现接口，而CGLIB可以直接代理类，更加灵活。Spring也是优先使用CGLIB。

## 贡献

欢迎提交Issue和Pull Request！

## 许可证

MIT License

## 作者

XY Spring Team

---

**希望这个项目能帮助你更好地理解Spring框架的核心原理！** 🚀

