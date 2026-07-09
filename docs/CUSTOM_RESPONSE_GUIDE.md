# 自定义响应结构扩展指南

本指南详细说明如何扩展和使用自定义返回结构，让你的项目能够灵活适配各种业务场景。

## 📋 目录

- [为什么需要自定义响应结构](#为什么需要自定义响应结构)
- [扩展机制概览](#扩展机制概览)
- [方式1：实现 ResponseConversionStrategy](#方式1实现-responseconversionstrategy)
- [方式2：使用 Lambda 表达式](#方式2使用-lambda-表达式)
- [方式3：链式组合转换](#方式3链式组合转换)
- [方式4：继承现有响应结构](#方式4继承现有响应结构)
- [完整示例：CustomResponse](#完整示例customresponse)
- [Spring Boot 集成](#spring-boot-集成)
- [最佳实践](#最佳实践)
- [常见问题](#常见问题)

---

## 为什么需要自定义响应结构

### 典型场景

1. **对接遗留系统**
   - 老项目使用特殊的响应格式（如 `retCode/retMsg/retData`）
   - 需要与现有系统集成，不能修改原有接口

2. **微服务间不同规范**
   - 订单服务使用腾讯风格
   - 用户服务使用阿里风格
   - 网关层需要统一转换

3. **特殊业务需求**
   - 需要额外的元数据（traceId、timestamp、serverInfo）
   - 特定的错误码体系
   - 多语言消息支持

4. **第三方API对接**
   - 微信、支付宝等平台的响应格式
   - 云服务商（AWS、阿里云）的API响应

---

## 扩展机制概览

你的模块提供了 **4种扩展方式**，从简单到复杂：

```
┌─────────────────────────────────────────────────────────┐
│              自定义响应结构扩展方式                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  方式1: ResponseConversionStrategy    ⭐⭐⭐⭐⭐ (推荐)   │
│  • 完整控制转换逻辑                                       │
│  • 可复用、可测试                                         │
│  • 支持注册到工厂                                         │
│                                                         │
│  方式2: Lambda 表达式                 ⭐⭐⭐⭐            │
│  • 快速、简洁                                             │
│  • 适合临时转换                                           │
│  • 无需创建新类                                           │
│                                                         │
│  方式3: ConversionChainBuilder        ⭐⭐⭐             │
│  • 多步骤转换                                             │
│  • 条件转换                                               │
│  • 复杂业务流程                                           │
│                                                         │
│  方式4: 继承扩展现有类                  ⭐⭐              │
│  • 微调现有结构                                           │
│  • 添加额外字段                                           │
│  • 保持兼容性                                             │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 方式1：实现 ResponseConversionStrategy

这是**最推荐**的方式，适用于完整的自定义响应类。

### 步骤1：定义自定义响应类

```java
package com.example.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class MyCustomResponse<T> implements Serializable {
    private int status;      // 自定义状态码
    private String message;  // 自定义消息
    private T payload;       // 自定义数据字段名
    private long timestamp;  // 时间戳
    private String traceId;  // 链路追踪ID
    
    // 静态工厂方法
    public static <T> MyCustomResponse<T> success(T data) {
        MyCustomResponse<T> response = new MyCustomResponse<>();
        response.setStatus(200);
        response.setMessage("操作成功");
        response.setPayload(data);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
    
    public static <T> MyCustomResponse<T> error(int code, String msg) {
        MyCustomResponse<T> response = new MyCustomResponse<>();
        response.setStatus(code);
        response.setMessage(msg);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
```

### 步骤2：创建转换策略

```java
package com.example.converter;

import cc.hongz.ztools.response.ResponseConversionStrategy;
import cc.hongz.ztools.response.ajax.AjaxResult;
import com.example.response.MyCustomResponse;

/**
 * AjaxResult → MyCustomResponse 转换策略
 */
public class AjaxToMyCustomStrategy 
    implements ResponseConversionStrategy<AjaxResult, MyCustomResponse<Object>> {
    
    @Override
    public MyCustomResponse<Object> convert(AjaxResult source) {
        if (source == null) {
            return MyCustomResponse.error(500, "响应对象为空");
        }
        
        MyCustomResponse<Object> response = new MyCustomResponse<>();
        
        // 转换状态码
        try {
            response.setStatus(Integer.parseInt(source.getCode()));
        } catch (NumberFormatException e) {
            response.setStatus(source.isSuccess() ? 200 : 500);
        }
        
        // 转换消息和数据
        response.setMessage(source.getMsg());
        response.setPayload(source.getData());
        response.setTimestamp(System.currentTimeMillis());
        response.setTraceId(getTraceIdFromContext());
        
        return response;
    }
    
    private String getTraceIdFromContext() {
        // 从 MDC 或请求上下文中获取 traceId
        return org.slf4j.MDC.get("traceId");
    }
}
```

### 步骤3：注册策略

```java
// 应用启动时注册（如 Spring Boot 的 @PostConstruct）
@PostConstruct
public void initResponseConverters() {
    ResponseConverterFactory.register(
        AjaxResult.class,
        MyCustomResponse.class,
        new AjaxToMyCustomStrategy()
    );
    
    // 可以注册多个方向的转换
    ResponseConverterFactory.register(
        MyCustomResponse.class,
        AjaxResult.class,
        new MyCustomToAjaxStrategy()
    );
}
```

### 步骤4：使用转换

```java
// 通过工厂转换
AjaxResult ajax = AjaxResult.success(user);
MyCustomResponse<User> custom = ResponseConverterFactory.convert(ajax, MyCustomResponse.class);

// 或直接使用策略
ResponseConversionStrategy<AjaxResult, MyCustomResponse<Object>> strategy = 
    new AjaxToMyCustomStrategy();
MyCustomResponse<Object> result = strategy.convert(ajax);
```

---

## 方式2：使用 Lambda 表达式

适合**简单、临时**的转换场景，无需创建新类。

### 基础用法

```java
// 直接使用 Lambda
ResponseConversionStrategy<Result<User>, MyCustomResponse<User>> strategy = 
    result -> {
        MyCustomResponse<User> response = new MyCustomResponse<>();
        response.setStatus(result.getCode());
        response.setMessage(result.getMessage());
        response.setPayload(result.getData());
        response.setTimestamp(System.currentTimeMillis());
        return response;
    };

// 执行转换
MyCustomResponse<User> custom = ResponseConverter.convert(result, strategy);
```

### 结合工厂注册

```java
// 注册 Lambda 策略
ResponseConverterFactory.register(
    AjaxResult.class,
    MyCustomResponse.class,
    ajax -> {
        MyCustomResponse<Object> resp = new MyCustomResponse<>();
        resp.setStatus(Integer.parseInt(ajax.getCode()));
        resp.setMessage(ajax.getMsg());
        resp.setPayload(ajax.getData());
        return resp;
    }
);
```

---

## 方式3：链式组合转换

当需要经过**多个中间态**或**条件转换**时使用。

### 多步骤转换

```java
// Result → AjaxResult → MyCustomResponse
MyCustomResponse<User> custom = ResponseConverter.chain()
    .from(result)
    .transform(ResponseConverter::toAjaxResult)           // 第1步
    .transform(ajax -> {                                   // 第2步
        MyCustomResponse<User> resp = new MyCustomResponse<>();
        resp.setStatus(Integer.parseInt(ajax.getCode()));
        resp.setMessage(ajax.getMsg());
        resp.setPayload((User) ajax.getData());
        return resp;
    })
    .execute();
```

### 条件转换

```java
MyCustomResponse<User> custom = ResponseConverter.chain()
    .from(ajaxResult)
    .transformIf(
        ajax -> ajax.getCode().equals("200"),  // 仅成功时
        ajax -> MyCustomResponse.success(ajax.getData())
    )
    .transformIf(
        ajax -> !ajax.getCode().equals("200"), // 失败时
        ajax -> MyCustomResponse.error(
            Integer.parseInt(ajax.getCode()), 
            ajax.getMsg()
        )
    )
    .execute();
```

### 策略组合复用

```java
// 定义可复用的基础策略
ResponseConversionStrategy<AjaxResult, MyCustomResponse<Object>> baseStrategy = 
    ajax -> {
        MyCustomResponse<Object> resp = new MyCustomResponse<>();
        resp.setStatus(Integer.parseInt(ajax.getCode()));
        resp.setMessage(ajax.getMsg());
        resp.setPayload(ajax.getData());
        return resp;
    };

// 组合额外处理
ResponseConversionStrategy<AjaxResult, MyCustomResponse<Object>> withMetadata = 
    baseStrategy.andThen(resp -> {
        resp.setTimestamp(System.currentTimeMillis());
        resp.setTraceId(MDC.get("traceId"));
        return resp;
    });

// 注册组合策略
ResponseConverterFactory.register(AjaxResult.class, MyCustomResponse.class, withMetadata);
```

---

## 方式4：继承扩展现有类

如果你的自定义结构与现有结构**相似**，可以直接继承。

### 扩展示例

```java
/**
 * 增强版 AjaxResult，添加额外字段
 */
public class EnhancedAjaxResult extends AjaxResult {
    private String traceId;
    private Long serverTime;
    private Map<String, Object> metadata;
    
    public static EnhancedAjaxResult success(Object data) {
        EnhancedAjaxResult result = new EnhancedAjaxResult();
        result.setCode("200");
        result.setMsg("操作成功");
        result.setData(data);
        result.setTraceId(UUID.randomUUID().toString());
        result.setServerTime(System.currentTimeMillis());
        return result;
    }
}

// 注册转换
ResponseConverterFactory.register(
    AjaxResult.class,
    EnhancedAjaxResult.class,
    ajax -> {
        EnhancedAjaxResult enhanced = new EnhancedAjaxResult();
        enhanced.setCode(ajax.getCode());
        enhanced.setMsg(ajax.getMsg());
        enhanced.setData(ajax.getData());
        enhanced.setTraceId(MDC.get("traceId"));
        enhanced.setServerTime(System.currentTimeMillis());
        return enhanced;
    }
);
```

---

## 完整示例：CustomResponse

本模块已提供完整的自定义响应示例，位于 `cc.hongz.ztools.response.custom` 包：

### 文件结构

```
cc.hongz.ztools.response.custom
├── CustomResponse.java              # 自定义响应结构
├── CustomResponseConverter.java     # 转换器
└── package-info.java                # 包说明
```

### 快速使用

```java
// 1. 创建响应
CustomResponse<User> response = CustomResponse.success(user);

// 2. 转换为其他格式
AjaxResult ajax = CustomResponseConverter.toAjaxResult(response);
Result<User> result = CustomResponseConverter.toResult(response);

// 3. 注册到工厂（一次性）
CustomResponseConverter.registerAll();

// 4. 通过工厂统一转换
CustomResponse<Order> custom = ResponseConverterFactory.convert(ajaxResult, CustomResponse.class);
```

### 支持的转换

| 源类型 | 目标类型 | 方法 |
|--------|---------|------|
| AjaxResult | CustomResponse | `fromAjaxResult()` |
| Result | CustomResponse | `fromResult()` |
| AliResponse | CustomResponse | `fromAliResponse()` |
| TencentResponse | CustomResponse | `fromTencentResponse()` |
| CustomResponse | AjaxResult | `toAjaxResult()` |
| CustomResponse | Result | `toResult()` |
| CustomResponse | AliResponse | `toAliResponse()` |
| CustomResponse | TencentResponse | `toTencentResponse()` |

---

## Spring Boot 集成

### 自动配置类

```java
@Configuration
public class ResponseAutoConfiguration {
    
    /**
     * 注册自定义响应转换器
     */
    @PostConstruct
    public void registerCustomConverters() {
        // 注册 CustomResponse 转换器
        CustomResponseConverter.registerAll();
        
        // 注册你自己的转换器
        ResponseConverterFactory.register(
            YourCustomResponse.class,
            AjaxResult.class,
            new YourCustomToAjaxStrategy()
        );
    }
    
    /**
     * 全局响应增强（可选）
     */
    @Bean
    public ResponseBodyAdvice<Object> globalResponseEnhancer() {
        return new GlobalResponseEnhancer();
    }
}
```

### 全局响应增强

```java
@RestControllerAdvice
public class GlobalResponseEnhancer implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }
    
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 如果已经是目标格式，直接返回
        if (body instanceof CustomResponse) {
            return body;
        }
        
        // 转换为自定义格式
        if (body instanceof AjaxResult || body instanceof Result) {
            return ResponseConverterFactory.convert(body, CustomResponse.class);
        }
        
        // 普通对象包装
        return CustomResponse.success(body);
    }
}
```

### Controller 使用

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public CustomResponse<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return CustomResponse.success(user);
    }
    
    @PostMapping
    public CustomResponse<User> createUser(@RequestBody User user) {
        User saved = userService.save(user);
        return CustomResponse.success("创建成功", saved);
    }
}
```

---

## 最佳实践

### ✅ 推荐做法

1. **统一归一化标准**
   ```java
   // 所有响应最终转换为 AjaxResult 作为内部标准
   AjaxResult normalized = ResponseConverterFactory.convert(anyResponse, AjaxResult.class);
   ```

2. **在边界处转换**
   ```java
   // Controller 层：外部格式 → 内部格式
   // Service 层：使用内部格式
   // Gateway 层：内部格式 → 外部格式
   ```

3. **注册策略集中管理**
   ```java
   @Configuration
   public class ResponseConfig {
       @PostConstruct
       public void init() {
           // 所有转换策略在这里注册
           CustomResponseConverter.registerAll();
           LegacyResponseConverter.registerAll();
       }
   }
   ```

4. **添加单元测试**
   ```java
   @Test
   void testAjaxToCustomConversion() {
       AjaxResult ajax = AjaxResult.success("test", userData);
       CustomResponse<User> custom = CustomResponseConverter.fromAjaxResult(ajax);
       
       assertEquals(200, custom.getStatus());
       assertEquals("test", custom.getMessage());
       assertEquals(userData, custom.getPayload());
   }
   ```

### ❌ 避免的做法

1. **不要在业务逻辑中硬编码转换**
   ```java
   // ❌ 错误
   if (response instanceof AjaxResult) {
       // 手动转换...
   }
   
   // ✅ 正确
   CustomResponse<User> custom = ResponseConverterFactory.convert(response, CustomResponse.class);
   ```

2. **不要创建过多的响应类型**
   - 优先复用现有结构
   - 只在必要时创建新类型

3. **不要忘记注册策略**
   ```java
   // ❌ 忘记注册会导致运行时异常
   ResponseConverterFactory.convert(ajax, MyCustomResponse.class);
   
   // ✅ 确保已注册
   ResponseConverterFactory.register(AjaxResult.class, MyCustomResponse.class, strategy);
   ```

---

## 常见问题

### Q1: 如何处理泛型擦除问题？

**A:** 使用 `@SuppressWarnings("unchecked")` 并在文档中说明：

```java
@SuppressWarnings("unchecked")
public static <T> CustomResponse<T> fromAjaxResult(AjaxResult ajax) {
    // 转换逻辑
    response.setData((T) ajax.getData());
    return response;
}
```

### Q2: 转换失败如何处理？

**A:** 抛出明确的异常或返回错误响应：

```java
public static <T> CustomResponse<T> fromAjaxResult(AjaxResult ajax) {
    if (ajax == null) {
        return CustomResponse.error(500, "响应对象为空");
    }
    // ...
}
```

### Q3: 如何支持双向转换？

**A:** 分别注册两个方向的策略：

```java
// A → B
ResponseConverterFactory.register(A.class, B.class, aToBStrategy);

// B → A
ResponseConverterFactory.register(B.class, A.class, bToAStrategy);
```

### Q4: 性能如何优化？

**A:** 
- 策略会被缓存，避免重复创建
- 使用静态方法而非实例方法
- 避免在转换中进行复杂计算

### Q5: 如何调试转换过程？

**A:** 添加日志或使用链式转换的调试模式：

```java
MyCustomResponse<User> custom = ResponseConverter.chain()
    .from(ajax)
    .transform(ajax -> {
        log.debug("Converting AjaxResult: {}", ajax);
        return CustomResponseConverter.fromAjaxResult(ajax);
    })
    .execute();
```

---

## 总结

你的模块提供了**完善的自定义响应结构支持**：

| 特性 | 说明 |
|------|------|
| ✅ 零侵入 | 无需修改现有代码即可扩展 |
| ✅ 类型安全 | 泛型保证编译期检查 |
| ✅ 热插拔 | 运行时动态注册/注销策略 |
| ✅ 可测试 | 策略独立，易于单元测试 |
| ✅ 高性能 | 策略缓存，避免重复创建 |
| ✅ 灵活组合 | 支持链式、条件、组合转换 |

**下一步**：
1. 参考 `CustomResponse` 示例创建你的自定义响应
2. 实现转换策略并注册到工厂
3. 在 Spring Boot 中配置自动注册
4. 编写单元测试验证转换逻辑

有任何问题欢迎查阅源码或提出 Issue！
