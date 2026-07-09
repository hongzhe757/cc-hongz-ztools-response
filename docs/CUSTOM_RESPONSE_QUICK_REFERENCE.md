# 自定义响应结构 - 快速参考

## 🚀 5分钟快速开始

### 1. 使用预定义的 CustomResponse

```java
// 创建响应
CustomResponse<User> response = CustomResponse.success(user);
CustomResponse<Void> error = CustomResponse.error(404, "用户不存在");

// 链式构建
CustomResponse<Order> custom = CustomResponse.<Order>builder()
    .code(200)
    .message("查询成功")
    .data(order)
    .traceId("abc-123")
    .build();
```

### 2. 转换为其他格式

```java
// CustomResponse → AjaxResult
AjaxResult ajax = CustomResponseConverter.toAjaxResult(response);

// CustomResponse → Result
Result<User> result = CustomResponseConverter.toResult(response);

// AjaxResult → CustomResponse
CustomResponse<User> custom = CustomResponseConverter.fromAjaxResult(ajax);
```

### 3. 注册到工厂（一次性）

```java
// Spring Boot 应用中
@PostConstruct
public void init() {
    CustomResponseConverter.registerAll();
}

// 之后可通过工厂统一转换
CustomResponse<User> custom = ResponseConverterFactory.convert(ajax, CustomResponse.class);
```

---

## 📋 4种扩展方式速查

| 方式 | 适用场景 | 代码量 | 推荐度 |
|------|---------|--------|--------|
| **ResponseConversionStrategy** | 完整自定义响应类 | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Lambda 表达式** | 简单临时转换 | ⭐ | ⭐⭐⭐⭐ |
| **ConversionChainBuilder** | 多步骤/条件转换 | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **继承现有类** | 微调现有结构 | ⭐⭐ | ⭐⭐ |

---

## 💡 常用代码片段

### 方式1：实现策略接口

```java
public class MyStrategy implements ResponseConversionStrategy<AjaxResult, MyResponse> {
    @Override
    public MyResponse convert(AjaxResult source) {
        MyResponse resp = new MyResponse();
        resp.setStatus(Integer.parseInt(source.getCode()));
        resp.setMessage(source.getMsg());
        resp.setData(source.getData());
        return resp;
    }
}

// 注册
ResponseConverterFactory.register(AjaxResult.class, MyResponse.class, new MyStrategy());
```

### 方式2：Lambda 快速转换

```java
ResponseConverterFactory.register(
    AjaxResult.class,
    MyResponse.class,
    ajax -> {
        MyResponse resp = new MyResponse();
        resp.setStatus(Integer.parseInt(ajax.getCode()));
        resp.setMessage(ajax.getMsg());
        return resp;
    }
);
```

### 方式3：链式转换

```java
MyResponse custom = ResponseConverter.chain()
    .from(result)
    .transform(ResponseConverter::toAjaxResult)
    .transform(ajax -> convertToMyResponse(ajax))
    .execute();
```

### 方式4：继承扩展

```java
public class EnhancedAjaxResult extends AjaxResult {
    private String traceId;
    
    public static EnhancedAjaxResult success(Object data) {
        EnhancedAjaxResult r = new EnhancedAjaxResult();
        r.setCode("200");
        r.setMsg("成功");
        r.setData(data);
        r.setTraceId(UUID.randomUUID().toString());
        return r;
    }
}
```

---

## 🔧 Spring Boot 集成

### 自动配置

```java
@Configuration
public class ResponseConfig {
    @PostConstruct
    public void registerConverters() {
        CustomResponseConverter.registerAll();
        // 注册你自己的转换器
    }
}
```

### 全局响应增强

```java
@RestControllerAdvice
public class GlobalResponseEnhancer implements ResponseBodyAdvice<Object> {
    @Override
    public Object beforeBodyWrite(Object body, ...) {
        if (body instanceof CustomResponse) return body;
        if (body instanceof AjaxResult || body instanceof Result) {
            return ResponseConverterFactory.convert(body, CustomResponse.class);
        }
        return CustomResponse.success(body);
    }
}
```

---

## 🎯 选择指南

### 何时使用 CustomResponse？

✅ **推荐使用**：
- 需要额外元数据（traceId、timestamp）
- 对接遗留系统（特殊格式）
- 微服务间不同规范
- 第三方API对接

❌ **不推荐**：
- 简单的REST API（直接用AjaxResult）
- 内部服务调用（直接用Result）
- 标准协议（用ProblemDetail/JsonApiResponse）

### 转换方向选择

| 场景 | 推荐转换 |
|------|---------|
| 对外API | CustomResponse → AjaxResult |
| 内部调用 | CustomResponse → Result |
| 阿里系 | CustomResponse → AliResponse |
| 腾讯系 | CustomResponse → TencentResponse |

---

## ⚠️ 注意事项

1. **记得注册策略**
   ```java
   // ❌ 忘记注册会抛异常
   ResponseConverterFactory.convert(ajax, MyResponse.class);
   
   // ✅ 确保已注册
   ResponseConverterFactory.register(...);
   ```

2. **处理泛型擦除**
   ```java
   @SuppressWarnings("unchecked")
   public <T> MyResponse<T> convert(AjaxResult ajax) {
       resp.setData((T) ajax.getData());
   }
   ```

3. **空值检查**
   ```java
   if (source == null) {
       return MyResponse.error(500, "响应对象为空");
   }
   ```

4. **性能优化**
   - 策略会被缓存，避免重复创建
   - 使用静态方法
   - 避免复杂计算

---

## 📚 更多资源

- 📖 [完整扩展指南](CUSTOM_RESPONSE_GUIDE.md)
- 🎨 [设计模式详解](DESIGN_PATTERNS.md)
- 🚀 [快速参考](QUICK_REFERENCE.md)
- 💻 [源码示例](../src/main/java/cc/hongz/ztools/response/custom/)

---

## 🆘 常见问题

**Q: 如何支持双向转换？**  
A: 分别注册两个方向的策略。

**Q: 转换失败如何处理？**  
A: 返回错误响应或抛出明确异常。

**Q: 性能如何？**  
A: 策略缓存+静态方法，性能优秀。

**Q: 如何调试？**  
A: 添加日志或使用链式转换的调试模式。
