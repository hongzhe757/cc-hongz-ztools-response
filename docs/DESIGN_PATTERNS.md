# 响应结构转换 - 设计模式详解

本文档详细说明 `hongz-ztools-response` 模块中响应结构转换功能所涉及的设计模式。

## 📚 目录

- [1. 概述](#1-概述)
- [2. 适配器模式（Adapter Pattern）](#2-适配器模式adapter-pattern)
- [3. 策略模式（Strategy Pattern）](#3-策略模式strategy-pattern)
- [4. 工厂方法模式（Factory Method Pattern）](#4-工厂方法模式factory-method-pattern)
- [5. 建造者模式（Builder Pattern）](#5-建造者模式builder-pattern)
- [6. 责任链模式（Chain of Responsibility）](#6-责任链模式chain-of-responsibility)
- [7. 门面模式（Facade Pattern）](#7-门面模式facade-pattern)
- [8. 注册表模式（Registry Pattern）](#8-注册表模式registry-pattern)
- [9. 综合应用示例](#9-综合应用示例)
- [10. 最佳实践](#10-最佳实践)

---

## 1. 概述

响应结构转换系统采用了多种经典设计模式，以实现灵活、可扩展、易维护的转换架构。这些模式相互配合，共同构建了一个强大的响应格式转换框架。

### 核心类图

```
┌─────────────────────────────────────────────────────────────┐
│                    ResponseConverter                        │
│                  (门面模式 - Facade)                         │
├─────────────────────────────────────────────────────────────┤
│ + toAjaxResult(Result): AjaxResult                          │
│ + toAjaxResult(AliResponse): AjaxResult                     │
│ + aliToTencent(AliResponse): TencentResponse                │
│ + convert(S, Strategy): T                                   │
│ + chain(): ConversionChainBuilder                           │
└─────────────────────────────────────────────────────────────┘
                            ↓ uses
        ┌───────────────────┴───────────────────┐
        ↓                                       ↓
┌──────────────────────┐            ┌──────────────────────┐
│ ResponseConversion   │            │ ConversionChain      │
│ Strategy<S,T>        │            │ Builder              │
│ (策略模式)            │            │ (建造者+责任链)       │
└──────────────────────┘            └──────────────────────┘
        ↑ implements                        ↓ builds
        │                                   ↓
┌──────────────────────┐            ┌──────────────────────┐
│ CustomStrategy       │            │ Transformation Chain │
│                      │            │ [Func1, Func2, ...]  │
└──────────────────────┘            └──────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              ResponseConverterFactory                       │
│          (工厂方法 + 注册表 + 单例)                          │
├─────────────────────────────────────────────────────────────┤
│ - STRATEGIES: Map<String, Strategy>                         │
│ + register(source, target, strategy)                        │
│ + getStrategy(source, target): Strategy                     │
│ + convert(source, targetType): T                            │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 适配器模式（Adapter Pattern）

### 模式定义

将一个类的接口转换成客户希望的另外一个接口。适配器模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。

### 在本项目中的应用

**核心类**：[`ResponseConverter`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ResponseConverter.java)

**应用场景**：将各种不同格式的响应结构转换为统一的 `AjaxResult` 格式。

### 代码示例

```java
// 源接口：Result<T>
public class Result<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;
}

// 目标接口：AjaxResult
public class AjaxResult {
    private String code;
    private String msg;
    private Object data;
}

// 适配器：ResponseConverter
public class ResponseConverter {
    public static <T> AjaxResult toAjaxResult(final Result<T> result) {
        if (result.isSuccess()) {
            return AjaxResult.success(
                String.valueOf(result.getCode()), 
                result.getMessage(), 
                result.getData()
            );
        } else {
            return AjaxResult.error(
                String.valueOf(result.getCode()), 
                result.getMessage(), 
                result.getData()
            );
        }
    }
}

// 使用
Result<User> result = Result.success(user);
AjaxResult ajax = ResponseConverter.toAjaxResult(result); // 适配成功
```

### 优势

- ✅ 让不兼容的接口能够协同工作
- ✅ 提高现有类的复用性
- ✅ 符合开闭原则（对扩展开放，对修改关闭）

---

## 3. 策略模式（Strategy Pattern）

### 模式定义

定义一系列算法，把它们一个个封装起来，并且使它们可互换。策略模式让算法独立于使用它的客户端而变化。

### 在本项目中的应用

**核心接口**：[`ResponseConversionStrategy<S, T>`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ResponseConversionStrategy.java)

**应用场景**：允许在运行时选择不同的转换算法。

### 代码示例

```java
// 1. 定义策略接口
@FunctionalInterface
public interface ResponseConversionStrategy<S, T> {
    T convert(S source);
    
    // 支持策略组合
    default <V> ResponseConversionStrategy<S, V> andThen(
        final ResponseConversionStrategy<? super T, ? extends V> after) {
        return (S s) -> {
            T intermediate = this.convert(s);
            return after.convert(intermediate);
        };
    }
}

// 2. 实现具体策略
public class CustomResultToAjaxStrategy 
    implements ResponseConversionStrategy<Result<User>, AjaxResult> {
    
    @Override
    public AjaxResult convert(Result<User> source) {
        if (source.isSuccess()) {
            return AjaxResult.success(
                "CUSTOM_" + source.getCode(), 
                source.getMessage(), 
                source.getData()
            );
        } else {
            return AjaxResult.error(
                "ERROR_" + source.getCode(), 
                source.getMessage()
            );
        }
    }
}

// 3. 使用策略
Result<User> result = Result.success(user);
ResponseConversionStrategy<Result<User>, AjaxResult> strategy = 
    new CustomResultToAjaxStrategy();
AjaxResult ajax = ResponseConverter.convert(result, strategy);

// 4. 策略组合
ResponseConversionStrategy<Result<User>, String> combined = 
    new CustomResultToAjaxStrategy()
        .andThen(ajax -> JSON.toJSONString(ajax));
String json = ResponseConverter.convert(result, combined);
```

### 优势

- ✅ 算法可以自由切换
- ✅ 避免使用多重条件语句
- ✅ 易于扩展新的算法
- ✅ 符合开闭原则

---

## 4. 工厂方法模式（Factory Method Pattern）

### 模式定义

定义一个用于创建对象的接口，让子类决定实例化哪一个类。工厂方法使一个类的实例化延迟到其子类。

### 在本项目中的应用

**核心类**：[`ResponseConverterFactory`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ResponseConverterFactory.java)

**应用场景**：根据源类型和目标类型动态获取合适的转换策略。

### 代码示例

```java
// 1. 工厂类提供静态工厂方法
public final class ResponseConverterFactory {
    private static final Map<String, ResponseConversionStrategy<?, ?>> STRATEGIES = new HashMap<>();
    
    // 工厂方法：根据类型获取策略
    @SuppressWarnings("unchecked")
    public static <S, T> ResponseConversionStrategy<S, T> getStrategy(
        final Class<S> sourceType, 
        final Class<T> targetType) {
        
        String key = generateKey(sourceType, targetType);
        return (ResponseConversionStrategy<S, T>) STRATEGIES.get(key);
    }
    
    // 执行转换的工厂方法
    public static <S, T> T convert(final S source, final Class<T> targetType) {
        ResponseConversionStrategy<S, T> strategy = getStrategy(source.getClass(), targetType);
        if (strategy == null) {
            throw new UnsupportedOperationException("未找到转换策略");
        }
        return strategy.convert(source);
    }
}

// 2. 使用工厂
Result<User> result = Result.success(user);

// 方式一：获取策略后手动转换
ResponseConversionStrategy<Result, AjaxResult> strategy = 
    ResponseConverterFactory.getStrategy(Result.class, AjaxResult.class);
AjaxResult ajax = strategy.convert(result);

// 方式二：直接通过工厂转换
AjaxResult ajax2 = ResponseConverterFactory.convert(result, AjaxResult.class);
```

### 优势

- ✅ 将对象的创建和使用分离
- ✅ 降低耦合度
- ✅ 易于添加新的产品类型
- ✅ 符合依赖倒置原则

---

## 5. 建造者模式（Builder Pattern）

### 模式定义

将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示。

### 在本项目中的应用

**核心类**：[`ConversionChainBuilder`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ConversionChainBuilder.java)

**应用场景**：构建复杂的转换链，支持多步骤转换。

### 代码示例

```java
// 1. 建造者类
public class ConversionChainBuilder {
    private Object source;
    private final List<Function<Object, Object>> transformations = new ArrayList<>();
    
    public ConversionChainBuilder from(final Object source) {
        this.source = source;
        return this; // 返回 this 支持链式调用
    }
    
    public <T> ConversionChainBuilder to(final Class<T> targetType) {
        transformations.add(current -> /* 转换逻辑 */);
        return this;
    }
    
    public <T, R> ConversionChainBuilder transform(final Function<T, R> transformer) {
        transformations.add(current -> transformer.apply((T) current));
        return this;
    }
    
    public <T> T execute() {
        Object current = source;
        for (Function<Object, Object> transformation : transformations) {
            current = transformation.apply(current);
        }
        return (T) current;
    }
}

// 2. 使用建造者
BaiduResponse<Order> baiduResp = BaiduResponse.success(order);

// 链式构建转换流程
AjaxResult result = ResponseConverter.chain()
    .from(baiduResp)
    .transform(ResponseConverter::toTencentResponse)  // 第1步：百度→腾讯
    .transform(ResponseConverter::toAjaxResult)       // 第2步：腾讯→AJAX
    .execute();

// 带条件转换
Result<User> finalResult = ResponseConverter.chain()
    .from(ajaxResult)
    .transform(ResponseConverter::toResult)
    .transformIf(
        r -> r.getCode() == 200, 
        r -> Result.success(r.getCode(), "Modified: " + r.getMessage(), r.getData())
    )
    .execute();
```

### 优势

- ✅ 分步构建复杂对象
- ✅ 可以精确控制构建过程
- ✅ 链式调用提高可读性
- ✅ 易于扩展新的构建步骤

---

## 6. 责任链模式（Chain of Responsibility）

### 模式定义

使多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系。将这些对象连成一条链，并沿着这条链传递该请求，直到有一个对象处理它为止。

### 在本项目中的应用

**核心类**：[`ConversionChainBuilder`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ConversionChainBuilder.java)

**应用场景**：转换链中的每个转换函数依次处理数据。

### 代码示例

```java
// 转换链内部实现
public <T> T execute() {
    Object current = source;
    // 责任链：依次执行每个转换
    for (Function<Object, Object> transformation : transformations) {
        current = transformation.apply(current);
    }
    return (T) current;
}

// 使用示例
Result<User> result = ResponseConverter.chain()
    .from(baiduResponse)
    .transform(bd -> ResponseConverter.baiduToTencent(bd))  // 处理器1
    .transform(tx -> ResponseConverter.tencentToAli(tx))    // 处理器2
    .transform(ali -> ResponseConverter.toAjaxResult(ali))  // 处理器3
    .execute();
```

### 优势

- ✅ 降低耦合度
- ✅ 增强给对象指派职责的灵活性
- ✅ 便于增加新的处理类
- ✅ 每个处理类只需关注自己的逻辑

---

## 7. 门面模式（Facade Pattern）

### 模式定义

为子系统中的一组接口提供一个一致的界面，门面模式定义了一个高层接口，这个接口使得这一子系统更加容易使用。

### 在本项目中的应用

**核心类**：[`ResponseConverter`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ResponseConverter.java)

**应用场景**：提供统一的转换入口，隐藏底层复杂的转换逻辑。

### 代码示例

```java
// 门面类提供简单接口
public final class ResponseConverter {
    // 简单的静态方法，隐藏内部复杂性
    public static <T> AjaxResult toAjaxResult(final Result<T> result) { ... }
    public static <T> AjaxResult toAjaxResult(final AliResponse<T> aliResponse) { ... }
    public static <T> AjaxResult toAjaxResult(final TencentResponse<T> tencentResponse) { ... }
    
    // 高级功能也通过简单接口暴露
    public static <S, T> T convert(final S source, final ResponseConversionStrategy<S, T> strategy) { ... }
    public static ConversionChainBuilder chain() { ... }
}

// 客户端使用非常简单
AjaxResult ajax = ResponseConverter.toAjaxResult(anyResponse);
```

### 优势

- ✅ 简化客户端调用
- ✅ 松散耦合
- ✅ 更好的划分访问层次
- ✅ 提高了安全性

---

## 8. 注册表模式（Registry Pattern）

### 模式定义

提供一个中心化的位置来存储和检索对象实例，通常是一个全局可访问的映射表。

### 在本项目中的应用

**核心类**：[`ResponseConverterFactory`](file:///D:/workspace/code_workspace/hongz-parent/hongz-ztools/hongz-ztools-response/src/main/java/cc/hongz/ztools/response/ResponseConverterFactory.java)

**应用场景**：集中管理所有的转换策略。

### 代码示例

```java
public final class ResponseConverterFactory {
    // 注册表：存储所有策略
    private static final Map<String, ResponseConversionStrategy<?, ?>> STRATEGIES = new HashMap<>();
    
    // 注册策略
    public static <S, T> void register(
        final Class<S> sourceType, 
        final Class<T> targetType, 
        final ResponseConversionStrategy<S, T> strategy) {
        
        String key = generateKey(sourceType, targetType);
        STRATEGIES.put(key, strategy);
    }
    
    // 检索策略
    public static <S, T> ResponseConversionStrategy<S, T> getStrategy(
        final Class<S> sourceType, 
        final Class<T> targetType) {
        
        String key = generateKey(sourceType, targetType);
        return (ResponseConversionStrategy<S, T>) STRATEGIES.get(key);
    }
}

// 使用
ResponseConverterFactory.register(CustomRequest.class, CustomResponse.class, new CustomStrategy());
ResponseConversionStrategy<CustomRequest, CustomResponse> strategy = 
    ResponseConverterFactory.getStrategy(CustomRequest.class, CustomResponse.class);
```

### 优势

- ✅ 集中管理对象
- ✅ 便于查找和复用
- ✅ 支持动态注册
- ✅ 解耦组件间的依赖

---

## 9. 综合应用示例

### 场景：微服务间的数据交换

假设我们有三个微服务，分别使用不同的响应格式：

- **用户服务**：使用 `Result<User>`
- **订单服务**：使用 `AliResponse<Order>`
- **网关服务**：统一转换为 `AjaxResult`

```java
// 1. 在网关服务中配置转换策略（启动时执行一次）
@Configuration
public class ResponseConversionConfig {
    
    @PostConstruct
    public void init() {
        // 注册用户服务的转换策略
        ResponseConverterFactory.register(
            Result.class, 
            AjaxResult.class,
            ResponseConverter::toAjaxResult
        );
        
        // 注册订单服务的转换策略
        ResponseConverterFactory.register(
            AliResponse.class, 
            AjaxResult.class,
            ResponseConverter::toAjaxResult
        );
        
        // 注册自定义的日志记录策略
        ResponseConverterFactory.register(
            Object.class, 
            Object.class,
            source -> {
                log.info("Converting response: {}", source);
                return source;
            }
        );
    }
}

// 2. 在控制器中使用
@RestController
@RequestMapping("/api")
public class GatewayController {
    
    @Autowired
    private UserServiceClient userService;
    
    @Autowired
    private OrderServiceClient orderService;
    
    @GetMapping("/users/{id}")
    public AjaxResult getUser(@PathVariable Long id) {
        // 调用用户服务（返回 Result<User>）
        Result<User> userResult = userService.getUser(id);
        
        // 自动转换为 AjaxResult
        return ResponseConverterFactory.convert(userResult, AjaxResult.class);
    }
    
    @GetMapping("/orders/{id}")
    public AjaxResult getOrder(@PathVariable Long id) {
        // 调用订单服务（返回 AliResponse<Order>）
        AliResponse<Order> orderResponse = orderService.getOrder(id);
        
        // 使用链式转换（先转腾讯风格，再转 AJAX）
        return ResponseConverter.chain()
            .from(orderResponse)
            .transform(ResponseConverter::aliToTencent)
            .transform(ResponseConverter::toAjaxResult)
            .execute();
    }
    
    @GetMapping("/complex")
    public AjaxResult complexConversion() {
        // 使用自定义策略
        ResponseConversionStrategy<BaiduResponse, AjaxResult> customStrategy = 
            bd -> {
                // 自定义转换逻辑
                if (bd.isSuccess()) {
                    return AjaxResult.success("CUSTOM", bd.getErrmsg(), bd.getData());
                }
                return AjaxResult.error("CUSTOM_ERROR", bd.getErrmsg());
            };
        
        BaiduResponse<Product> bdResp = productService.getProduct(1L);
        return ResponseConverter.convert(bdResp, customStrategy);
    }
}
```

---

## 10. 最佳实践

### 1. 选择合适的模式

| 场景 | 推荐模式 | 说明 |
|------|---------|------|
| 简单的格式转换 | 适配器模式 | 直接使用 `ResponseConverter.toXxx()` |
| 需要运行时切换算法 | 策略模式 | 使用 `ResponseConversionStrategy` |
| 需要动态注册/查找策略 | 工厂方法 + 注册表 | 使用 `ResponseConverterFactory` |
| 多步骤复杂转换 | 建造者 + 责任链 | 使用 `ResponseConverter.chain()` |
| 简化客户端调用 | 门面模式 | 直接使用 `ResponseConverter` |

### 2. 性能考虑

- **缓存策略**：`ResponseConverterFactory` 已经缓存了所有策略，无需重复创建
- **避免过度设计**：简单转换直接用适配器，不要强行使用策略模式
- **链式转换注意**：每增加一步都会增加开销，尽量精简转换链

### 3. 扩展新格式

```java
// 1. 创建新的响应类
public class NewFormatResponse<T> {
    private int status;
    private String message;
    private T payload;
}

// 2. 在 ResponseConverter 中添加转换方法
public static <T> AjaxResult toAjaxResult(final NewFormatResponse<T> response) {
    if (response.getStatus() == 200) {
        return AjaxResult.success(response.getMessage(), response.getPayload());
    }
    return AjaxResult.error(String.valueOf(response.getStatus()), response.getMessage());
}

// 3. 在 ResponseConverterFactory 中注册
static {
    register(NewFormatResponse.class, AjaxResult.class, ResponseConverter::toAjaxResult);
}
```

### 4. 测试建议

```java
@Test
void testConversionStrategy() {
    // 测试策略
    ResponseConversionStrategy<Result<User>, AjaxResult> strategy = 
        new CustomResultToAjaxStrategy();
    
    Result<User> input = Result.success(new User("test"));
    AjaxResult output = strategy.convert(input);
    
    assertNotNull(output);
    assertTrue(output.isSuccess());
}

@Test
void testConversionChain() {
    // 测试转换链
    BaiduResponse<Order> input = BaiduResponse.success(new Order());
    
    AjaxResult output = ResponseConverter.chain()
        .from(input)
        .transform(ResponseConverter::baiduToTencent)
        .transform(ResponseConverter::toAjaxResult)
        .executeAs(AjaxResult.class);
    
    assertNotNull(output);
    assertTrue(output.isSuccess());
}
```

---

## 📖 参考资料

- 《Head First 设计模式》
- 《Effective Java》
- [Refactoring Guru - Design Patterns](https://refactoring.guru/design-patterns)
- [Java Design Patterns](https://java-design-patterns.com/)

---

**最后更新**：2026-06-22  
**作者**：hongz  
**版本**：1.0.0-SNAPSHOT
