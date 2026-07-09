# Java 响应结构与状态码管理方案全面对比

> 本文档对比分析 Java 生态中所有主流的响应结构、状态码管理和国际化异常处理方案。

---

## 📊 方案分类总览

```
Java 响应结构方案
├── 1. Spring Framework 原生方案
│   ├── ResponseEntity<T>
│   ├── ProblemDetail (RFC 9457)
│   └── ErrorResponse 接口
├── 2. 开源响应包装库
│   ├── R / Result 模式
│   ├── ApiResult / ApiResponse
│   └── UnifiedResponse
├── 3. 业务状态码管理
│   ├── 枚举常量方式
│   ├── 接口抽象方式
│   └── 注解驱动方式
├── 4. 国际化异常处理
│   ├── Spring MessageSource
│   ├── 自定义 I18nException
│   └── 第三方 i18n 库
└── 5. hongz-ztools-response（本方案）
    ├── HttpStatusCode 枚举
    ├── BusinessCode 体系
    └── I18nResponseBuilder
```

---

## 1️⃣ Spring Framework 原生方案

### 1.1 ResponseEntity<T>

**官方文档**: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html

#### 核心特性
```java
// 基础用法
ResponseEntity.ok(data);                    // 200 OK
ResponseEntity.created(uri).body(data);     // 201 Created
ResponseEntity.noContent();                 // 204 No Content
ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400

// 完整控制
ResponseEntity<User> response = ResponseEntity
    .status(HttpStatus.OK)
    .header("X-Custom-Header", "value")
    .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS))
    .body(user);
```

#### 优点
- ✅ **Spring 官方标准**，与 Spring MVC/WebFlux 深度集成
- ✅ **完整的 HTTP 语义支持**（状态码、响应头、Cookie等）
- ✅ **类型安全**，泛型支持
- ✅ **内容协商自动处理**（JSON/XML等）
- ✅ **零额外依赖**

#### 缺点
- ❌ **缺少业务状态码**（只有 HTTP 状态码）
- ❌ **无统一业务消息字段**（msg/message）
- ❌ **前端解析不够友好**（需要判断 HTTP 状态码而非业务码）
- ❌ **不支持国际化消息**（需手动处理）
- ❌ **每个 Controller 方法都要写 ResponseEntity**（样板代码多）

#### 适用场景
- RESTful API 设计
- 需要精细控制 HTTP 响应的场景
- 微服务间通信

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐⭐⭐ | Spring 官方标准 |
| 易用性 | ⭐⭐⭐ | 需要手动构建 |
| 灵活性 | ⭐⭐⭐⭐⭐ | 完全控制 HTTP |
| 业务适配 | ⭐⭐ | 缺少业务语义 |
| 国际化 | ⭐ | 无内置支持 |
| **综合** | **⭐⭐⭐⭐** | HTTP 层最佳，业务层不足 |

---

### 1.2 ProblemDetail (RFC 9457)

**官方文档**: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html

#### 核心特性
```java
// Spring 6+ / Spring Boot 3+
ProblemDetail problem = ProblemDetail.forStatusAndDetail(
    HttpStatus.NOT_FOUND, 
    "User with ID 123 not found"
);

// 带扩展属性
problem.setProperty("userId", 123);
problem.setProperty("timestamp", Instant.now());

// 从异常创建
@ExceptionHandler(UserNotFoundException.class)
public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
        HttpStatus.NOT_FOUND, 
        ex.getMessage()
    );
    problem.setTitle("User Not Found");
    problem.setType(URI.create("https://api.example.com/errors/user-not-found"));
    return problem;
}
```

#### 优点
- ✅ **IETF RFC 9457 国际标准**
- ✅ **标准化错误字段**：type, title, status, detail, instance
- ✅ **支持扩展属性**（properties Map）
- ✅ **自动内容协商**（application/problem+json）
- ✅ **Spring 原生支持**（Spring 6+/Boot 3+）
- ✅ **与异常处理深度集成**

#### 缺点
- ❌ **仅用于错误响应**（不包含成功场景）
- ❌ **缺少业务状态码**（只有 HTTP 状态码）
- ❌ **国际化需手动实现**（虽然支持 MessageSource，但需自己配置）
- ❌ **学习曲线较陡**（需要理解 RFC 规范）
- ❌ **旧版本 Spring 不支持**（需 Spring 6+）

#### 适用场景
- REST API 错误响应标准化
- 微服务间错误传递
- 对外公开 API

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐⭐⭐ | IETF 国际标准 |
| 易用性 | ⭐⭐⭐ | 需要理解 RFC |
| 灵活性 | ⭐⭐⭐⭐ | 可扩展属性 |
| 业务适配 | ⭐⭐ | 仅错误场景 |
| 国际化 | ⭐⭐⭐ | 支持但需配置 |
| **综合** | **⭐⭐⭐⭐** | 错误响应最佳实践 |

---

### 1.3 ErrorResponse 接口

**官方文档**: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/ErrorResponses.html

#### 核心特性
```java
// Spring MVC 所有异常都实现此接口
public interface ErrorResponse {
    HttpStatusCode getStatusCode();
    HttpHeaders getHeaders();
    ProblemDetail getBody();
    
    // 消息代码（用于国际化）
    String getTypeMessageCode();
    String getTitleMessageCode();
    String getDetailMessageCode();
    Object[] getDetailMessageArguments();
}

// 自定义异常实现
public class BusinessException extends RuntimeException implements ErrorResponse {
    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
    
    @Override
    public ProblemDetail getBody() {
        return ProblemDetail.forStatusAndDetail(
            getStatusCode(), 
            getMessage()
        );
    }
}
```

#### 优点
- ✅ **统一的异常响应契约**
- ✅ **内置国际化支持**（MessageSource 集成）
- ✅ **与 ResponseEntityExceptionHandler 无缝配合**
- ✅ **可自定义消息代码和参数**

#### 缺点
- ❌ **仅用于异常**（不是通用响应结构）
- ❌ **实现复杂**（需要实现多个方法）
- ❌ **文档较少**（相对较新的 API）

#### 适用场景
- 自定义异常体系
- 需要国际化的异常处理

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐⭐ | Spring 标准接口 |
| 易用性 | ⭐⭐ | 实现较复杂 |
| 灵活性 | ⭐⭐⭐⭐ | 高度可定制 |
| 业务适配 | ⭐⭐⭐ | 适合异常场景 |
| 国际化 | ⭐⭐⭐⭐⭐ | 原生支持 |
| **综合** | **⭐⭐⭐⭐** | 异常处理最佳 |

---

## 2️⃣ 开源响应包装库

### 2.1 R / Result 模式

**代表项目**: 
- https://github.com/MirwaisAkrami/result
- 各种企业内部封装的 R 类

#### 核心特性
```java
// 典型实现
public class R<T> {
    private int code;      // 业务状态码
    private String msg;    // 消息
    private T data;        // 数据
    
    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }
    
    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg, null);
    }
}

// 使用
@GetMapping("/user/{id}")
public R<User> getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return R.ok(user);
}
```

#### 优点
- ✅ **简单易用**（静态工厂方法）
- ✅ **业务语义清晰**（code/msg/data）
- ✅ **前端友好**（统一 JSON 结构）
- ✅ **广泛使用**（大量项目验证）

#### 缺点
- ❌ **无统一标准**（每个项目实现不同）
- ❌ **缺少 HTTP 状态码映射**（通常固定返回 200）
- ❌ **无国际化支持**（消息硬编码）
- ❌ **无框架集成**（纯 POJO）
- ❌ **缺少分页等高级功能**

#### 适用场景
- 简单 CRUD 应用
- 内部系统
- 快速原型开发

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐ | 无统一标准 |
| 易用性 | ⭐⭐⭐⭐⭐ | 极其简单 |
| 灵活性 | ⭐⭐⭐ | 基础功能 |
| 业务适配 | ⭐⭐⭐⭐ | 业务语义好 |
| 国际化 | ⭐ | 无支持 |
| **综合** | **⭐⭐⭐** | 简单场景够用 |

---

### 2.2 ApiResult / ApiResponse

**代表项目**: 
- 各大厂内部封装（阿里、腾讯、百度等）
- 开源项目如 `spring-boot-api-result`

#### 核心特性
```java
// 阿里巴巴风格
public class AliResponse<T> {
    private boolean success;
    private String errorCode;
    private String errorMsg;
    private T content;
}

// 腾讯风格
public class TencentResponse<T> {
    private int ret;       // 0=成功
    private String msg;
    private T data;
}

// 百度风格
public class BaiduResponse<T> {
    private int errno;     // 0=成功
    private String errmsg;
    private T data;
}
```

#### 优点
- ✅ **符合大厂规范**（对接大厂 API 时必需）
- ✅ **业务语义明确**
- ✅ **部分支持错误码分类**

#### 缺点
- ❌ **各家标准不统一**
- ❌ **缺少国际化**
- ❌ **缺少框架集成**
- ❌ **文档不完善**

#### 适用场景
- 对接特定厂商 API
- 遵循特定公司规范的项目

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐ | 厂商标准 |
| 易用性 | ⭐⭐⭐⭐ | 较简单 |
| 灵活性 | ⭐⭐⭐ | 基础功能 |
| 业务适配 | ⭐⭐⭐⭐ | 符合厂商规范 |
| 国际化 | ⭐ | 无支持 |
| **综合** | **⭐⭐⭐** | 特定场景使用 |

---

## 3️⃣ 业务状态码管理方案

### 3.1 枚举常量方式

#### 核心特性
```java
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统错误");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

// 使用
return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
```

#### 优点
- ✅ **简单直观**
- ✅ **编译时检查**
- ✅ **IDE 自动补全**

#### 缺点
- ❌ **与 HTTP 状态码耦合**（通常直接用 HTTP 码）
- ❌ **缺少业务域分类**
- ❌ **不易扩展**（修改枚举影响大）
- ❌ **无国际化**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 简洁性 | ⭐⭐⭐⭐⭐ | 非常简单 |
| 扩展性 | ⭐⭐ | 难以扩展 |
| 业务表达 | ⭐⭐⭐ | 基础表达 |
| 国际化 | ⭐ | 无支持 |
| **综合** | **⭐⭐⭐** | 小项目可用 |

---

### 3.2 接口抽象方式

#### 核心特性
```java
public interface IResultCode {
    int getCode();
    String getMessage();
    HttpStatus getHttpStatus();
}

public enum CommonResultCode implements IResultCode {
    SUCCESS(200, "操作成功", HttpStatus.OK),
    PARAM_ERROR(400, "参数错误", HttpStatus.BAD_REQUEST);
    
    // ...
}

public enum UserResultCode implements IResultCode {
    USER_NOT_FOUND(40401, "用户不存在", HttpStatus.NOT_FOUND),
    USER_DISABLED(40301, "用户已禁用", HttpStatus.FORBIDDEN);
    
    // ...
}
```

#### 优点
- ✅ **解耦业务码和 HTTP 码**
- ✅ **支持业务域分类**
- ✅ **易于扩展**（新增枚举即可）
- ✅ **类型安全**

#### 缺点
- ❌ **仍需手动管理消息**
- ❌ **无国际化**
- ❌ **缺少注册表机制**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 简洁性 | ⭐⭐⭐⭐ | 较简单 |
| 扩展性 | ⭐⭐⭐⭐ | 易于扩展 |
| 业务表达 | ⭐⭐⭐⭐ | 良好表达 |
| 国际化 | ⭐ | 无支持 |
| **综合** | **⭐⭐⭐⭐** | 推荐方案 |

---

### 3.3 注解驱动方式

#### 核心特性
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ErrorCode {
    int code();
    String message();
    HttpStatus httpStatus() default HttpStatus.INTERNAL_SERVER_ERROR;
}

public class ErrorCodes {
    @ErrorCode(code = 40401, message = "用户不存在", httpStatus = HttpStatus.NOT_FOUND)
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
}

// 通过反射读取
ErrorCode annotation = field.getAnnotation(ErrorCode.class);
int code = annotation.code();
String message = annotation.message();
```

#### 优点
- ✅ **声明式定义**
- ✅ **元数据丰富**
- ✅ **可结合 AOP 使用**

#### 缺点
- ❌ **运行时反射开销**
- ❌ **编译时无法检查**
- ❌ **IDE 支持差**
- ❌ **复杂度高**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 简洁性 | ⭐⭐ | 较复杂 |
| 扩展性 | ⭐⭐⭐⭐ | 灵活扩展 |
| 业务表达 | ⭐⭐⭐⭐ | 元数据丰富 |
| 性能 | ⭐⭐ | 反射开销 |
| **综合** | **⭐⭐⭐** | 特殊场景使用 |

---

## 4️⃣ 国际化异常处理方案

### 4.1 Spring MessageSource 原生方案

#### 核心特性
```properties
# messages_zh_CN.properties
user.not.found=用户不存在：{0}
order.create.failed=订单创建失败：{0}，错误码：{1}

# messages_en_US.properties
user.not.found=User not found: {0}
order.create.failed=Order creation failed: {0}, error code: {1}
```

```java
@Service
public class UserService {
    @Autowired
    private MessageSource messageSource;
    
    public User findById(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            String message = messageSource.getMessage(
                "user.not.found", 
                new Object[]{id}, 
                LocaleContextHolder.getLocale()
            );
            throw new BusinessException(message);
        }
        return user;
    }
}
```

#### 优点
- ✅ **Spring 原生支持**
- ✅ **标准的 i18n 方案**
- ✅ **支持多种消息源**（properties、数据库、XML等）
- ✅ **参数替换**（MessageFormat）
- ✅ **回退机制**（找不到翻译时使用默认）

#### 缺点
- ❌ **手动注入 MessageSource**（每个 Service 都要注入）
- ❌ **消息键硬编码**（字符串易出错）
- ❌ **异常与消息分离**（异常类不包含消息键）
- ❌ **Controller 层需手动处理**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐⭐⭐ | Spring 标准 |
| 易用性 | ⭐⭐⭐ | 需手动处理 |
| 灵活性 | ⭐⭐⭐⭐⭐ | 高度灵活 |
| 集成度 | ⭐⭐⭐ | 需手动集成 |
| **综合** | **⭐⭐⭐⭐** | 基础方案 |

---

### 4.2 自定义 I18nException

#### 核心特性
```java
public abstract class I18nException extends RuntimeException {
    private final String messageKey;
    private final Object[] args;
    
    protected I18nException(String messageKey, Object... args) {
        super(messageKey);
        this.messageKey = messageKey;
        this.args = args;
    }
    
    public String getMessageKey() { return messageKey; }
    public Object[] getArgs() { return args; }
}

public class UserNotFoundException extends I18nException {
    public UserNotFoundException(Long userId) {
        super("user.not.found", userId);
    }
}

// 全局异常处理器
@RestControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    private MessageSource messageSource;
    
    @ExceptionHandler(I18nException.class)
    public ResponseEntity<R<Void>> handleI18nException(I18nException ex) {
        String message = messageSource.getMessage(
            ex.getMessageKey(),
            ex.getArgs(),
            LocaleContextHolder.getLocale()
        );
        return ResponseEntity.ok(R.fail(message));
    }
}
```

#### 优点
- ✅ **异常携带消息键**（而非硬编码消息）
- ✅ **集中处理**（GlobalExceptionHandler）
- ✅ **延迟解析**（在处理器中解析消息）
- ✅ **支持参数**

#### 缺点
- ❌ **需要定义大量异常类**
- ❌ **缺少业务状态码**
- ❌ **与 HTTP 状态码未关联**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐ | 自定义方案 |
| 易用性 | ⭐⭐⭐⭐ | 较易用 |
| 灵活性 | ⭐⭐⭐⭐ | 灵活扩展 |
| 集成度 | ⭐⭐⭐⭐ | 良好集成 |
| **综合** | **⭐⭐⭐⭐** | 推荐方案 |

---

### 4.3 第三方 i18n 库

#### 代表库
- **Jilt**: https://github.com/npathai/jilt
- **i18n-processor**: https://github.com/phrase/i18n-processor
- **Lokalize**: 商业方案

#### 核心特性
```java
// Jilt 示例（编译时生成）
@I18nMessage(key = "user.not.found", defaultMessage = "User not found: {0}")
public class UserNotFoundException extends RuntimeException {
    // 自动生成构造器和 getter
}

// 使用
throw new UserNotFoundException(userId); // 自动处理 i18n
```

#### 优点
- ✅ **编译时检查**（避免消息键拼写错误）
- ✅ **代码生成**（减少样板代码）
- ✅ **IDE 支持好**（自动补全、重构安全）
- ✅ **类型安全**

#### 缺点
- ❌ **额外依赖**
- ❌ **学习成本**
- ❌ **社区较小**
- ❌ **可能与 Spring 集成不佳**

#### 评分
| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐ | 非主流 |
| 易用性 | ⭐⭐⭐⭐ | 编译时友好 |
| 灵活性 | ⭐⭐⭐ | 受限于工具 |
| 集成度 | ⭐⭐ | 需额外配置 |
| **综合** | **⭐⭐⭐** | 尝鲜可用 |

---

## 5️⃣ hongz-ztools-response（本方案）

### 5.1 整体架构

```
hongz-ztools-response
├── HttpStatusCode          # HTTP 状态码枚举（RFC 标准）
├── BusinessCode            # 业务状态码接口
│   ├── CommonBusinessCode  # 通用业务码
│   ├── UserBusinessCode    # 用户业务码
│   └── OrderBusinessCode   # 订单业务码
├── BusinessCodeRegistry    # 状态码注册表
├── I18nResponseBuilder     # 国际化响应构建器
├── BusinessException       # 业务异常基类
├── I18nExceptionHandler    # 国际化异常处理器
└── AjaxResult              # 统一响应结构（归一化标准）
```

### 5.2 核心特性

#### HTTP 状态码
```java
// 完整覆盖 RFC 标准
HttpStatusCode.OK                     // 200
HttpStatusCode.NOT_FOUND              // 404
HttpStatusCode.INTERNAL_SERVER_ERROR  // 500

// 分类判断
if (status.isClientError()) { ... }
if (status.isSuccess()) { ... }

// 与 AjaxResult 集成
AjaxResult.success(HttpStatusCode.OK, data);
AjaxResult.error(HttpStatusCode.NOT_FOUND, "资源不存在");
```

#### 业务状态码
```java
// 定义业务状态码
public enum UserBusinessCode implements BusinessCode {
    USER_NOT_FOUND("USER_404", "用户不存在", HttpStatusCode.NOT_FOUND),
    USER_DISABLED("USER_403", "用户已被禁用", HttpStatusCode.FORBIDDEN);
    
    // 自动映射到 HTTP 状态码
    @Override
    public HttpStatusCode getHttpStatus() {
        return httpStatus;
    }
}

// 使用
AjaxResult.success(UserBusinessCode.SUCCESS, userData);
AjaxResult.error(UserBusinessCode.USER_NOT_FOUND, userId);
```

#### 国际化异常
```java
// 抛出业务异常（自动国际化）
throw new UserNotFoundException(userId);

// 异常处理器自动转换
@ExceptionHandler(BusinessException.class)
public ResponseEntity<AjaxResult> handleBusinessException(
        BusinessException ex, HttpServletRequest request) {
    Locale locale = resolveLocale(request);
    AjaxResult result = responseBuilder.buildError(
        ex.getBusinessCode(), locale, ex.getArgs()
    );
    return ResponseEntity
        .status(ex.getBusinessCode().getHttpStatus().getCode())
        .body(result);
}
```

### 5.3 优势总结

| 特性 | hongz-ztools-response | Spring ResponseEntity | R/Result 模式 | ProblemDetail |
|------|----------------------|----------------------|---------------|---------------|
| **HTTP 状态码** | ✅ 完整枚举 | ✅ 原生支持 | ❌ 无 | ✅ 原生支持 |
| **业务状态码** | ✅ 完整体系 | ❌ 无 | ⚠️ 简单 | ❌ 无 |
| **国际化** | ✅ 原生集成 | ❌ 需手动 | ❌ 无 | ⚠️ 需配置 |
| **异常处理** | ✅ 自动化 | ⚠️ 需手动 | ❌ 无 | ✅ 原生支持 |
| **框架集成** | ✅ Spring Boot Starter | ✅ 原生 | ❌ 无 | ✅ 原生 |
| **云厂商支持** | ✅ 阿里/腾讯/百度 | ❌ 无 | ❌ 无 | ❌ 无 |
| **分页支持** | ✅ 多种策略 | ❌ 无 | ❌ 无 | ❌ 无 |
| **响应转换** | ✅ 完善体系 | ⚠️ 需手动 | ❌ 无 | ⚠️ 需手动 |
| **学习曲线** | ⭐⭐⭐ 中等 | ⭐⭐⭐⭐ 较低 | ⭐⭐⭐⭐⭐ 极低 | ⭐⭐ 较高 |
| **社区生态** | ⭐⭐ 发展中 | ⭐⭐⭐⭐⭐ 成熟 | ⭐⭐⭐ 分散 | ⭐⭐⭐⭐ 增长中 |

### 5.4 评分

| 维度 | 评分 | 说明 |
|------|------|------|
| 标准化 | ⭐⭐⭐⭐ | 对齐 RFC + Spring |
| 易用性 | ⭐⭐⭐⭐ | Starter 开箱即用 |
| 灵活性 | ⭐⭐⭐⭐⭐ | 高度可扩展 |
| 业务适配 | ⭐⭐⭐⭐⭐ | 完美适配 |
| 国际化 | ⭐⭐⭐⭐⭐ | 原生深度集成 |
| 生态整合 | ⭐⭐⭐⭐ | Spring 生态 |
| **综合** | **⭐⭐⭐⭐⭐** | **全能型方案** |

---

## 📈 综合对比矩阵

### 功能对比表

| 功能 | Spring ResponseEntity | ProblemDetail | R/Result | 枚举状态码 | 接口状态码 | I18nException | hongz-ztools-response |
|------|----------------------|---------------|----------|-----------|-----------|---------------|----------------------|
| HTTP 状态码 | ✅ | ✅ | ❌ | ⚠️ | ✅ | ⚠️ | ✅ |
| 业务状态码 | ❌ | ❌ | ⚠️ | ✅ | ✅ | ⚠️ | ✅ |
| 国际化 | ❌ | ⚠️ | ❌ | ❌ | ❌ | ✅ | ✅ |
| 异常处理 | ⚠️ | ✅ | ❌ | ❌ | ❌ | ✅ | ✅ |
| 响应转换 | ⚠️ | ⚠️ | ❌ | ❌ | ❌ | ❌ | ✅ |
| 分页支持 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| 云厂商支持 | ❌ | ❌ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Spring 集成 | ✅ | ✅ | ❌ | ❌ | ❌ | ⚠️ | ✅ |
| 学习成本 | 低 | 中 | 极低 | 极低 | 低 | 中 | 中 |
| 开发效率 | 中 | 中 | 高 | 高 | 高 | 高 | 极高 |
| 维护成本 | 低 | 低 | 中 | 中 | 低 | 低 | 低 |

### 适用场景推荐

| 场景 | 推荐方案 | 理由 |
|------|---------|------|
| **RESTful API 设计** | ResponseEntity + ProblemDetail | HTTP 语义完整，符合标准 |
| **简单 CRUD 应用** | R/Result 模式 | 简单易用，快速开发 |
| **企业级应用** | hongz-ztools-response | 功能全面，易于维护 |
| **微服务间通信** | ResponseEntity | 轻量，HTTP 标准 |
| **对外公开 API** | ProblemDetail | RFC 标准，业界认可 |
| **对接大厂 API** | 对应厂商响应格式 | 符合厂商规范 |
| **国际化应用** | hongz-ztools-response | 原生 i18n 支持 |
| **快速原型** | R/Result + 枚举状态码 | 开发速度最快 |
| **遗留系统改造** | hongz-ztools-response | 兼容性好，渐进式迁移 |

---

## 🎯 选型建议

### 如果你的项目...

#### ✅ 选择 Spring ResponseEntity + ProblemDetail
- 是纯 RESTful API 项目
- 不需要业务状态码
- 团队熟悉 Spring 生态
- 不需要国际化
- 追求最小依赖

#### ✅ 选择 R/Result 模式
- 是简单的 CRUD 应用
- 团队技术水平一般
- 项目周期紧张
- 不需要复杂功能
- 内部系统，不对外开放

#### ✅ 选择 hongz-ztools-response
- 是企业级应用
- 需要完整的状态码体系
- 需要国际化支持
- 需要对接多个厂商
- 需要长期维护和扩展
- 希望与 Spring 深度集成
- 需要完善的文档和示例

#### ✅ 混合使用
- **Controller 层**: ResponseEntity（HTTP 控制）
- **Service 层**: BusinessException + BusinessCode（业务逻辑）
- **响应结构**: AjaxResult（统一格式）
- **错误响应**: ProblemDetail（RFC 标准）
- **国际化**: I18nResponseBuilder（多语言）

---

## 🔮 未来趋势

### 1. 标准化趋势
- **RFC 9457 (ProblemDetail)** 将成为事实标准
- **OpenAPI 3.1** 对错误响应的规范更严格
- **GraphQL** 有自己的错误规范

### 2. 框架集成趋势
- **Spring Boot 3.x** 原生支持 ProblemDetail
- **Quarkus/Micronaut** 也在跟进 RFC 标准
- **响应式框架**（WebFlux）需要专门的响应结构

### 3. 国际化趋势
- **编译时 i18n** 将更流行（类型安全）
- **动态消息源**（数据库、配置中心）需求增加
- **AI 辅助翻译** 降低多语言维护成本

### 4. 业务状态码趋势
- **业务码与 HTTP 码解耦** 成为共识
- **状态码注册中心** 便于统一管理
- **状态码文档自动生成** 提升开发体验

---

## 📚 参考资料

### 官方文档
- [Spring Framework Error Responses](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)
- [RFC 9457 - Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457)
- [RFC 7231 - HTTP/1.1 Semantics and Content](https://www.rfc-editor.org/rfc/rfc7231)

### 开源项目
- [spring-boot-api-result](https://github.com/benweizhu/spring-boot-api-result)
- [result-pattern-java](https://github.com/MirwaisAkrami/result)
- [jilt-i18n](https://github.com/npathai/jilt)

### 技术文章
- [5 Approaches to Response Handling with Java Spring Boot](https://probocodinglab.medium.com/5-approaches-to-response-handling-with-java-spring-boot-4ea430345415)
- [HTTP Status Codes as Business Language](https://www.reddit.com/r/softwarearchitecture/comments/1gfo58o/http_status_codes_as_business_language/)
- [Exception Handling and I18N on Spring Boots APIs](https://auth0.com/blog/exception-handling-and-i18n-on-spring-boots-apis/)

---

**最后更新**: 2026-06-22  
**版本**: 1.0.0-SNAPSHOT  
**维护者**: hongz-parent 团队
