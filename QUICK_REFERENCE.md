# XY Spring 快速参考指南

## 项目统计

- **总代码行数**: ~664 行 Java 代码
- **核心框架**: 16 个类文件
- **示例代码**: 4 个类文件
- **依赖**: CGLIB 3.3.0

## 核心类说明

### 注解层 (annotation)
- `@Component` - 标记Spring管理的组件
- `@Autowired` - 标记需要自动注入的字段
- `@Aspect` - 标记切面类
- `@Before` - 前置通知
- `@After` - 后置通知  
- `@Around` - 环绕通知

### 容器层 (context)
- `ApplicationContext` - IoC容器核心类，负责Bean管理和AOP配置

### AOP层 (aop)
- `AopProxyFactory` - AOP代理工厂，使用CGLIB创建代理
- `JoinPoint` - 连接点，封装目标方法信息
- `ProceedingJoinPoint` - 处理连接点，用于环绕通知
- `AspectMethod` - 切面方法包装类

### 工具层 (util)
- `ClassScanner` - 类扫描器，扫描指定包下的所有类

## 快速开始

### 1. 定义业务类

```java
@Component
public class UserService {
    public String getUser(String username) {
        return "用户信息: " + username;
    }
}
```

### 2. 定义依赖注入

```java
@Component  
public class OrderService {
    @Autowired
    private UserService userService;
    
    public void createOrder() {
        userService.getUser("张三");
    }
}
```

### 3. 定义切面

```java
@Aspect
@Component
public class LogAspect {
    @Before("execution(com.xy.example.service.*.*)")
    public void log(JoinPoint jp) {
        System.out.println("方法: " + jp.getMethodName());
    }
}
```

### 4. 初始化容器

```java
ApplicationContext ctx = new ApplicationContext("com.xy.example");
OrderService service = ctx.getBean(OrderService.class);
service.createOrder();
```

## 运行说明

### 编译
```bash
mvn clean compile
```

### 运行 (JDK 9+)
```bash
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED"
mvn exec:java -Dexec.mainClass="com.xy.example.Application"
```

### 运行 (JDK 8)
```bash
mvn exec:java -Dexec.mainClass="com.xy.example.Application"
```

## 切点表达式

支持简单的切点表达式语法：

- `execution(包名.类名.方法名)` - 精确匹配
- `execution(包名.类名.*)` - 匹配类的所有方法
- `execution(包名.*.*)` - 匹配包下所有类的所有方法
- 支持通配符 `*`

## 实现原理流程

### IoC容器初始化流程
```
1. 扫描包 (ClassScanner)
   ↓
2. 收集切面方法
   ↓
3. 确定需要代理的类
   ↓
4. 实例化Bean（不创建代理）
   ↓
5. 依赖注入 (@Autowired)
   ↓
6. 创建AOP代理 (CGLIB)
   ↓
7. 容器初始化完成
```

### AOP方法执行流程
```
调用代理方法
   ↓
CGLIB拦截器
   ↓
执行@Before通知
   ↓
执行@Around通知（如果有）或目标方法
   ↓
执行@After通知
   ↓
返回结果
```

## 注意事项

1. **JDK版本**: 编译目标是Java 8，但运行在JDK 9+时需要添加`--add-opens`参数
2. **代理限制**: 只能代理类，不支持final类和final方法
3. **依赖顺序**: 依赖注入在创建代理前完成，确保代理对象能访问注入的依赖
4. **单例模式**: 所有Bean默认是单例

## 与Spring对比

| 特性 | XY Spring | Spring Framework |
|------|-----------|------------------|
| 代码量 | ~664行 | 数十万行 |
| 学习曲线 | 简单 | 复杂 |
| 功能完整性 | 核心功能 | 企业级全功能 |
| 适用场景 | 学习原理 | 生产环境 |

## 扩展方向

1. Bean作用域 (Prototype, Request等)
2. 生命周期回调 (@PostConstruct, @PreDestroy)
3. 配置属性注入 (@Value)
4. 条件装配 (@Conditional)
5. 事务管理 (@Transactional)
6. 事件机制 (ApplicationEvent)

## 常见问题

**Q: 为什么OrderService注入失败？**  
A: 确保依赖的Bean也被@Component标记，并且在同一个扫描包下。

**Q: AOP不生效？**  
A: 检查切点表达式是否正确，确保切面类同时标记@Aspect和@Component。

**Q: CGLIB报错？**  
A: JDK 9+需要添加`--add-opens java.base/java.lang=ALL-UNNAMED`参数。

## 贡献

欢迎提交Issue和PR！

---

**学习愉快！** 🎓
