# hongz-ztools-response 规划摘要

> 快速了解模块演进方向和关键里程碑

---

## 🎯 三大核心规划

### 1️⃣ HTTP 状态码标准化 ✅ Phase 1 (1-2个月)

**目标**: 完整支持 HTTP 协议状态码体系

**关键交付物**:
- `HttpStatusCode` 枚举（覆盖 RFC 7231/7235/7538 所有标准状态码）
- `AjaxResult` 增强（支持 HttpStatusCode）
- `ProblemDetail` 增强（与 HttpStatusCode 集成）

**示例代码**:
```java
// 使用 HTTP 状态码
AjaxResult.success(HttpStatusCode.OK, data);
AjaxResult.error(HttpStatusCode.NOT_FOUND, "资源不存在");

// 判断状态码类型
if (httpStatusCode.isClientError()) { ... }
if (httpStatusCode.isSuccess()) { ... }
```

---

### 2️⃣ 业务状态码体系 ✅ Phase 1 (1-2个月)

**目标**: 建立灵活的业务状态码规范，与 HTTP 状态码解耦但可映射

**关键交付物**:
- `BusinessCode` 接口（定义业务状态码契约）
- `CommonBusinessCode` 枚举（通用业务状态码）
- `BusinessCodeRegistry` 注册表（集中管理）
- 各业务域示例（User, Order, Payment等）

**示例代码**:
```java
// 定义业务状态码
public enum UserBusinessCode implements BusinessCode {
    USER_NOT_FOUND("USER_404", "用户不存在", HttpStatusCode.NOT_FOUND),
    USER_DISABLED("USER_403", "用户已被禁用", HttpStatusCode.FORBIDDEN);
}

// 使用业务状态码
AjaxResult.success(UserBusinessCode.SUCCESS, userData);
AjaxResult.error(UserBusinessCode.USER_NOT_FOUND, userId);

// 自动映射到 HTTP 状态码
ResponseEntity.status(businessCode.getHttpStatus().getCode()).body(result);
```

**设计亮点**:
- ✅ 业务状态码格式：`{DOMAIN}_{CODE}`（如 USER_404）
- ✅ 与 HTTP 状态码解耦但可映射
- ✅ 支持动态注册和扩展
- ✅ 支持业务域分类

---

### 3️⃣ 国际化异常集成 ✅ Phase 2 (2-3个月)

**目标**: 实现多语言错误消息支持，与 Spring MessageSource 深度集成

**关键交付物**:
- 消息资源文件（messages_zh_CN.properties, messages_en_US.properties）
- `I18nResponseBuilder`（国际化响应构建器）
- `BusinessException` 基类（业务异常）
- `I18nExceptionHandler`（全局异常处理器）

**示例代码**:
```properties
# messages_zh_CN.properties
user.not.found=用户不存在：{0}
order.create.failed=订单创建失败：{0}，错误码：{1}

# messages_en_US.properties
user.not.found=User not found: {0}
order.create.failed=Order creation failed: {0}, error code: {1}
```

```java
// 抛出业务异常（自动国际化）
throw new UserNotFoundException(userId);

// 异常处理器自动转换为国际化响应
@ExceptionHandler(BusinessException.class)
public ResponseEntity<AjaxResult> handleBusinessException(
        BusinessException ex, HttpServletRequest request) {
    Locale locale = resolveLocale(request); // 从请求头/Session/Cookie解析
    AjaxResult result = responseBuilder.buildError(
        ex.getBusinessCode(), locale, ex.getArgs()
    );
    return ResponseEntity.status(ex.getBusinessCode().getHttpStatus().getCode())
                         .body(result);
}
```

**设计亮点**:
- ✅ 支持至少 2 种语言（中文、英文）
- ✅ 参数化消息模板（MessageFormat）
- ✅ 运行时语言切换（Accept-Language / Session / Cookie）
- ✅ 与 Spring MessageSource 无缝集成
- ✅ 兼容现有 AjaxResult 和 ProblemDetail

---

### 4️⃣ 响应拦截器与归一化处理 ✅ Phase 3 (3-4个月)

**目标**: 自动拦截所有响应，将非标准响应转换为 AjaxResult，或抛出异常强制规范

**关键交付物**:
- `ResponseInterceptor` (ResponseBodyAdvice 实现)
- `ResponseInterceptStrategy` 接口（策略模式）
- `@SkipResponseIntercept` 注解（跳过拦截）
- 内置转换策略（Entity、Map、String 等）
- 严格模式支持（配置化）

**示例代码**:
```java
// Controller 直接返回实体类
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.findById(id); // 拦截器自动包装为 AjaxResult.success(user)
}

// 跳过拦截（文件下载、流式响应等）
@GetMapping("/download/{filename}")
@SkipResponseIntercept("文件下载不需要包装")
public ResponseEntity<Resource> download(@PathVariable String filename) {
    Resource resource = fileService.load(filename);
    return ResponseEntity.ok().body(resource);
}

// 自定义转换策略
@Component
public class LegacyResponseStrategy implements ResponseInterceptStrategy {
    @Override
    public boolean supports(Class<?> responseType) {
        return LegacyResponse.class.isAssignableFrom(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        LegacyResponse legacy = (LegacyResponse) response;
        return AjaxResult.success(legacy.getStatus(), legacy.getMessage(), legacy.getData());
    }
}
```

```yaml
# application.yml
hongz:
  response:
    interceptor:
      enabled: true
      strict-mode: false  # 严格模式：非标准响应抛出异常
      default-action: WRAP  # WRAP/PASS/THROW
```

**设计亮点**:
- ✅ **零侵入**：无需修改现有 Controller 代码
- ✅ **智能识别**：自动排除文件下载、SSE、Flux 等特殊响应
- ✅ **策略模式**：可插拔的转换策略，易于扩展
- ✅ **灵活配置**：支持注解跳过、配置文件控制
- ✅ **严格模式**：强制团队遵循响应规范
- ✅ **性能优化**：条件检查最小化开销
- ⚠️ **互斥原则**：AjaxResult 与自定义响应只能二选一，不可混用

---

## 📅 时间线

```
Phase 1 (1-2个月)          Phase 2 (2-3个月)         Phase 3 (3-4个月)         Phase 4 (4-6个月)
├─ HTTP 状态码枚举         ├─ 消息资源文件           ├─ ResponseInterceptor    ├─ Spring Boot Starter
├─ 业务状态码接口         ├─ I18nResponseBuilder    ├─ InterceptStrategy      ├─ 自动配置
├─ CommonBusinessCode     ├─ BusinessException      ├─ @SkipResponseIntercept ├─ 社区贡献
├─ BusinessCodeRegistry   ├─ I18nExceptionHandler   ├─ 内置转换策略           ├─ Maven Central 发布
└─ AjaxResult 增强        └─ 单元测试               └─ 严格模式支持           └─ 文档站点
```

---

## ✅ 验收标准

### Phase 1
- [ ] `HttpStatusCode` 枚举覆盖所有标准状态码
- [ ] `BusinessCode` 接口设计合理，易于扩展
- [ ] `AjaxResult` 支持 HTTP 和业务状态码
- [ ] 单元测试覆盖率 > 90%
- [ ] 文档完整，包含使用示例

### Phase 2
- [ ] 支持至少 2 种语言（中文、英文）
- [ ] 消息参数替换正常工作
- [ ] 异常处理器正确捕获并转换异常
- [ ] 语言切换功能正常
- [ ] 性能测试通过（消息解析 < 1ms）

### Phase 3
- [ ] ResponseInterceptor 正确拦截非标准响应
- [ ] 内置策略（Entity/Map/String）工作正常
- [ ] @SkipResponseIntercept 注解生效
- [ ] 严格模式可配置且工作正常
- [ ] 自动排除文件下载、SSE、Flux 等特殊响应
- [ ] 性能测试通过（拦截开销 < 0.5ms）

### Phase 4
- [ ] Spring Boot Starter 自动配置正常
- [ ] 配置属性生效
- [ ] PR 被 Spring Cloud Alibaba 接受
- [ ] 独立项目发布到 Maven Central
- [ ] 至少有 3 个外部项目使用

---

## 🔗 详细文档

- 📘 **[ROADMAP.md](./ROADMAP.md)** - 完整演进路线图（含详细设计和代码示例）
- 📊 **[IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md)** - 当前实现状态
- 📖 **[RESPONSE_FORMATS.md](./RESPONSE_FORMATS.md)** - 响应格式大全
- 🛠️ **[CUSTOM_RESPONSE_GUIDE.md](./CUSTOM_RESPONSE_GUIDE.md)** - 自定义响应指南

---

## 💡 核心价值

1. **标准化**: 对齐 Spring Framework 和 RFC 标准
2. **灵活性**: 业务状态码与 HTTP 状态码解耦
3. **国际化**: 原生支持多语言错误消息
4. **可扩展**: 插件化设计，易于扩展新业务域
5. **生态整合**: 与 Spring Boot/Spring Cloud 深度集成

---

**最后更新**: 2026-06-22  
**版本**: 1.0.0-SNAPSHOT
