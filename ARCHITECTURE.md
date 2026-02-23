# XY Spring Framework - 架构设计文档
Architecture Design Document

## 系统架构图 / System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌────────────────────┐  ┌──────────────────────┐          │
│  │  MainApplication   │  │  Example Services    │          │
│  │                    │  │  - UserService       │          │
│  │  - 启动入口        │  │  - UserDao           │          │
│  │  - 获取Bean        │  │  - LoggingAspect     │          │
│  └─────────┬──────────┘  └──────────────────────┘          │
└────────────┼─────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────┐
│                    XY Spring Framework                       │
│                                                               │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              ApplicationContext (IoC 容器)            │  │
│  │                                                         │  │
│  │  1. 包扫描 (Package Scanning)                         │  │
│  │     - 递归扫描指定包                                   │  │
│  │     - 识别 @Component, @Service, @Repository         │  │
│  │                                                         │  │
│  │  2. Bean 创建 (Bean Creation)                         │  │
│  │     - 反射创建实例                                     │  │
│  │     - 管理 Bean 生命周期                              │  │
│  │                                                         │  │
│  │  3. 依赖注入 (Dependency Injection)                   │  │
│  │     - 扫描 @Autowired 字段                           │  │
│  │     - 自动类型匹配和注入                              │  │
│  │                                                         │  │
│  │  4. AOP 处理 (AOP Processing)                         │  │
│  │     - 收集切面 (@Aspect)                             │  │
│  │     - 创建动态代理                                     │  │
│  │                                                         │  │
│  └────────────────────┬──────────────────────────────────┘  │
│                       │                                      │
│                       ▼                                      │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              AopProxyFactory (AOP 实现)               │  │
│  │                                                         │  │
│  │  1. 动态代理创建 (Dynamic Proxy Creation)             │  │
│  │     - JDK Dynamic Proxy for interfaces               │  │
│  │                                                         │  │
│  │  2. 切点匹配 (Pointcut Matching)                      │  │
│  │     - execution() 表达式解析                          │  │
│  │     - 类名和方法名通配符匹配                          │  │
│  │                                                         │  │
│  │  3. 通知执行 (Advice Execution)                       │  │
│  │     - @Before: 前置通知                              │  │
│  │     - @After: 后置通知                               │  │
│  │     - @Around: 环绕通知                              │  │
│  │                                                         │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 核心流程 / Core Workflow

### 1. 容器初始化流程 / Container Initialization Flow

```
开始 (Start)
  │
  ▼
包扫描 (Package Scan)
  │ - 递归扫描指定包路径
  │ - 查找带有 @Component 等注解的类
  ▼
创建 Bean 实例 (Create Bean Instances)
  │ - 使用反射创建对象
  │ - 存储到 beanMap 中
  ▼
依赖注入 (Dependency Injection)
  │ - 查找 @Autowired 字段
  │ - 根据类型自动注入依赖
  ▼
收集切面 (Collect Aspects)
  │ - 识别 @Aspect 注解的类
  ▼
应用 AOP 代理 (Apply AOP Proxies)
  │ - 为 Bean 创建动态代理
  │ - 替换容器中的原始对象
  ▼
容器就绪 (Container Ready)
```

### 2. AOP 代理执行流程 / AOP Proxy Execution Flow

```
方法调用 (Method Call)
  │
  ▼
代理拦截 (Proxy Intercepts)
  │
  ▼
收集匹配的通知 (Collect Matching Advices)
  │ - @Before advices
  │ - @After advices
  │ - @Around advices
  ▼
执行通知链 (Execute Advice Chain)
  │
  ├─ 有 @Around? ──Yes──→ 执行环绕通知
  │                       │ - 完全控制执行
  │                       │ - 调用 proceed()
  │                       └─→ 返回结果
  │
  └─ No ───→ 执行前置通知 (@Before)
             │
             ▼
             执行目标方法 (Execute Target Method)
             │
             ▼
             执行后置通知 (@After)
             │
             ▼
             返回结果 (Return Result)
```

## 关键技术实现 / Key Technical Implementation

### 1. 反射机制 / Reflection

```java
// 类扫描
Class<?> clazz = Class.forName(className);

// 创建实例
Object instance = clazz.getDeclaredConstructor().newInstance();

// 字段注入
field.setAccessible(true);
field.set(bean, dependency);

// 方法调用
method.invoke(target, args);
```

### 2. JDK 动态代理 / JDK Dynamic Proxy

```java
Proxy.newProxyInstance(
    classLoader,
    interfaces,
    invocationHandler
);

// InvocationHandler 实现
public Object invoke(Object proxy, Method method, Object[] args) {
    // 前置处理
    // 目标方法执行
    // 后置处理
}
```

### 3. 注解处理 / Annotation Processing

```java
// 检查注解
if (clazz.isAnnotationPresent(Component.class)) {
    // 处理逻辑
}

// 获取注解值
Component component = clazz.getAnnotation(Component.class);
String value = component.value();
```

## 数据结构设计 / Data Structure Design

### ApplicationContext

```
beanMap: ConcurrentHashMap<String, Object>
  - Key: Bean 名称
  - Value: Bean 实例（可能是代理对象）

typeToBeanNames: ConcurrentHashMap<Class<?>, List<String>>
  - Key: Bean 类型（类、接口、父类）
  - Value: 该类型对应的 Bean 名称列表

aspects: List<Object>
  - 存储所有切面 Bean
```

### AopProxyFactory

```
AopInvocationHandler:
  - target: 目标对象
  - aspects: 切面列表
  
AdviceInfo:
  - aspect: 切面实例
  - method: 通知方法
```

## 设计模式应用 / Design Pattern Application

1. **工厂模式 (Factory Pattern)**
   - ApplicationContext 作为 Bean 工厂
   - AopProxyFactory 创建代理对象

2. **单例模式 (Singleton Pattern)**
   - 所有 Bean 默认为单例

3. **代理模式 (Proxy Pattern)**
   - JDK 动态代理实现 AOP

4. **模板方法模式 (Template Method Pattern)**
   - AOP 通知执行流程

5. **策略模式 (Strategy Pattern)**
   - 不同类型的通知 (@Before, @After, @Around)

## 扩展点 / Extension Points

如需扩展功能，可以考虑以下方面：

1. **BeanPostProcessor**: Bean 后置处理器
2. **BeanFactoryPostProcessor**: BeanFactory 后置处理器
3. **ApplicationContextAware**: 获取 ApplicationContext 引用
4. **InitializingBean**: Bean 初始化回调
5. **DisposableBean**: Bean 销毁回调
6. **Scope**: Bean 作用域支持
7. **Configuration**: 配置类支持
