# hongz-ztools-response 模块演进路线图

> 本文档记录响应模块的未来演进规划，包括 HTTP 状态码支持、业务状态码体系和国际化异常集成。

---

## 📋 规划概览

### 核心目标

1. **HTTP 状态码标准化** - 完整支持 HTTP 协议状态码体系
2. **业务状态码扩展** - 建立灵活的业务状态码规范
3. **国际化异常集成** - 实现多语言错误消息支持
4. **Spring Framework 对齐** - 与 Spring 生态深度整合

### 时间线

- **Phase 1 (1-2个月)**: HTTP 状态码 + 基础业务状态码
- **Phase 2 (2-3个月)**: 国际化异常集成
- **Phase 3 (3-4个月)**: 响应拦截器与归一化处理
- **Phase 4 (4-6个月)**: Spring Boot Starter + 社区贡献

---

## 🎯 Phase 1: HTTP 状态码与业务状态码体系

### 1.1 HTTP 状态码枚举

#### 设计目标
- 完整覆盖 RFC 7231/7235/7538 等标准定义的 HTTP 状态码
- 提供语义化常量和方法
- 支持与 `AjaxResult`、`ProblemDetail` 无缝集成

#### 实现方案

```java
/**
 * HTTP 状态码枚举.
 * <p>
 * 完整覆盖 IETF RFC 标准定义的 HTTP 状态码，提供语义化访问。
 * </p>
 * 
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7231">RFC 7231</a>
 */
public enum HttpStatusCode {
    
    // ==================== 1xx Informational ====================
    CONTINUE(100, "Continue"),
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    PROCESSING(102, "Processing"),
    EARLY_HINTS(103, "Early Hints"),
    
    // ==================== 2xx Success ====================
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MULTI_STATUS(207, "Multi-Status"),
    ALREADY_REPORTED(208, "Already Reported"),
    IM_USED(226, "IM Used"),
    
    // ==================== 3xx Redirection ====================
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    PERMANENT_REDIRECT(308, "Permanent Redirect"),
    
    // ==================== 4xx Client Error ====================
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    IM_A_TEAPOT(418, "I'm a teapot"),
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    LOCKED(423, "Locked"),
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    TOO_EARLY(425, "Too Early"),
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
    
    // ==================== 5xx Server Error ====================
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    LOOP_DETECTED(508, "Loop Detected"),
    NOT_EXTENDED(510, "Not Extended"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
    
    private final int code;
    private final String reasonPhrase;
    
    HttpStatusCode(int code, String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }
    
    public int getCode() { return code; }
    public String getReasonPhrase() { return reasonPhrase; }
    
    /**
     * 判断是否为信息性状态码 (1xx).
     */
    public boolean isInformational() {
        return code >= 100 && code < 200;
    }
    
    /**
     * 判断是否为成功状态码 (2xx).
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
    
    /**
     * 判断是否为重定向状态码 (3xx).
     */
    public boolean isRedirection() {
        return code >= 300 && code < 400;
    }
    
    /**
     * 判断是否为客户端错误状态码 (4xx).
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }
    
    /**
     * 判断是否为服务器错误状态码 (5xx).
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }
    
    /**
     * 判断是否为错误状态码 (4xx 或 5xx).
     */
    public boolean isError() {
        return isClientError() || isServerError();
    }
    
    /**
     * 根据状态码值获取枚举.
     *
     * @param code 状态码
     * @return 对应的枚举值，未找到返回 null
     */
    public static HttpStatusCode valueOf(int code) {
        for (HttpStatusCode status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
```

#### 与现有类集成

```java
// AjaxResult 增强
public class AjaxResult {
    // 新增：使用 HttpStatusCode
    public static AjaxResult success(HttpStatusCode status, Object data) {
        return new AjaxResult(String.valueOf(status.getCode()), status.getReasonPhrase(), data);
    }
    
    public static AjaxResult error(HttpStatusCode status, String detail) {
        return new AjaxResult(String.valueOf(status.getCode()), status.getReasonPhrase(), detail);
    }
}

// ProblemDetail 增强
public class ProblemDetail {
    public static ProblemDetail from(HttpStatusCode status, String detail) {
        return ProblemDetail.builder()
            .status(status.getCode())
            .title(status.getReasonPhrase())
            .detail(detail)
            .build();
    }
}
```

### 1.2 业务状态码体系

#### 设计目标
- 支持自定义业务状态码（如 "USER_001", "ORDER_200"）
- 与 HTTP 状态码解耦但可映射
- 支持状态码分类和层级结构
- 可扩展的错误详情

#### 实现方案

```java
/**
 * 业务状态码接口.
 * <p>
 * 定义业务状态码的标准契约，支持不同业务域的状态码规范。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public interface BusinessCode {
    
    /**
     * 获取业务状态码.
     * <p>
     * 格式建议：{DOMAIN}_{CODE}，例如 USER_001, ORDER_200
     * </p>
     *
     * @return 业务状态码
     */
    String getCode();
    
    /**
     * 获取默认消息.
     *
     * @return 默认消息文本
     */
    String getDefaultMessage();
    
    /**
     * 获取对应的 HTTP 状态码.
     *
     * @return HTTP 状态码
     */
    HttpStatusCode getHttpStatus();
    
    /**
     * 判断是否为成功状态.
     *
     * @return 成功返回 true
     */
    default boolean isSuccess() {
        return getHttpStatus().isSuccess();
    }
    
    /**
     * 获取业务域.
     *
     * @return 业务域标识
     */
    default String getDomain() {
        String code = getCode();
        int underscoreIndex = code.indexOf('_');
        return underscoreIndex > 0 ? code.substring(0, underscoreIndex) : "COMMON";
    }
}

/**
 * 通用业务状态码枚举.
 */
public enum CommonBusinessCode implements BusinessCode {
    
    SUCCESS("COMMON_000", "操作成功", HttpStatusCode.OK),
    PARAM_INVALID("COMMON_400", "参数无效", HttpStatusCode.BAD_REQUEST),
    UNAUTHORIZED("COMMON_401", "未授权", HttpStatusCode.UNAUTHORIZED),
    FORBIDDEN("COMMON_403", "禁止访问", HttpStatusCode.FORBIDDEN),
    NOT_FOUND("COMMON_404", "资源不存在", HttpStatusCode.NOT_FOUND),
    INTERNAL_ERROR("COMMON_500", "系统内部错误", HttpStatusCode.INTERNAL_SERVER_ERROR),
    SERVICE_UNAVAILABLE("COMMON_503", "服务暂不可用", HttpStatusCode.SERVICE_UNAVAILABLE);
    
    private final String code;
    private final String defaultMessage;
    private final HttpStatusCode httpStatus;
    
    CommonBusinessCode(String code, String defaultMessage, HttpStatusCode httpStatus) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }
    
    @Override
    public String getCode() { return code; }
    
    @Override
    public String getDefaultMessage() { return defaultMessage; }
    
    @Override
    public HttpStatusCode getHttpStatus() { return httpStatus; }
}

/**
 * 用户业务状态码示例.
 */
public enum UserBusinessCode implements BusinessCode {
    
    USER_NOT_FOUND("USER_404", "用户不存在", HttpStatusCode.NOT_FOUND),
    USER_DISABLED("USER_403", "用户已被禁用", HttpStatusCode.FORBIDDEN),
    USER_DUPLICATE("USER_409", "用户已存在", HttpStatusCode.CONFLICT),
    PASSWORD_ERROR("USER_401", "密码错误", HttpStatusCode.UNAUTHORIZED);
    
    // ... 构造器同上
}
```

#### 业务状态码注册表

```java
/**
 * 业务状态码注册表.
 * <p>
 * 集中管理所有业务状态码，支持动态注册和查询。
 * </p>
 */
@Component
public class BusinessCodeRegistry {
    
    private final Map<String, BusinessCode> codeMap = new ConcurrentHashMap<>();
    
    /**
     * 注册业务状态码.
     *
     * @param businessCode 业务状态码
     */
    public void register(BusinessCode businessCode) {
        codeMap.put(businessCode.getCode(), businessCode);
    }
    
    /**
     * 批量注册枚举类型的业务状态码.
     *
     * @param enumClass 业务状态码枚举类
     */
    public <E extends Enum<E> & BusinessCode> void registerAll(Class<E> enumClass) {
        for (E code : enumClass.getEnumConstants()) {
            register(code);
        }
    }
    
    /**
     * 根据代码获取业务状态码.
     *
     * @param code 业务状态码字符串
     * @return 业务状态码对象，未找到返回 null
     */
    public BusinessCode getByCode(String code) {
        return codeMap.get(code);
    }
    
    /**
     * 初始化时自动注册通用状态码.
     */
    @PostConstruct
    public void init() {
        registerAll(CommonBusinessCode.class);
    }
}
```

#### 与 AjaxResult 集成

```java
public class AjaxResult {
    
    /**
     * 使用业务状态码创建成功响应.
     *
     * @param businessCode 业务状态码
     * @param data         数据
     * @return AjaxResult
     */
    public static AjaxResult success(BusinessCode businessCode, Object data) {
        return new AjaxResult(
            businessCode.getCode(),
            businessCode.getDefaultMessage(),
            data
        );
    }
    
    /**
     * 使用业务状态码创建错误响应.
     *
     * @param businessCode 业务状态码
     * @param detail       详细错误信息
     * @return AjaxResult
     */
    public static AjaxResult error(BusinessCode businessCode, String detail) {
        return new AjaxResult(
            businessCode.getCode(),
            businessCode.getDefaultMessage(),
            detail
        );
    }
    
    /**
     * 使用业务状态码创建错误响应（带参数替换）.
     *
     * @param businessCode 业务状态码
     * @param args         消息参数
     * @return AjaxResult
     */
    public static AjaxResult error(BusinessCode businessCode, Object... args) {
        String message = MessageFormat.format(businessCode.getDefaultMessage(), args);
        return new AjaxResult(
            businessCode.getCode(),
            message,
            null
        );
    }
}
```

### 1.3 任务清单

#### 短期任务（1-2周）
- [ ] 创建 `HttpStatusCode` 枚举类
- [ ] 为 `AjaxResult` 添加 `HttpStatusCode` 支持方法
- [ ] 为 `ProblemDetail` 添加 `HttpStatusCode` 工厂方法
- [ ] 编写单元测试覆盖所有状态码

#### 中期任务（2-4周）
- [ ] 设计并实现 `BusinessCode` 接口
- [ ] 创建 `CommonBusinessCode` 枚举
- [ ] 实现 `BusinessCodeRegistry` 注册表
- [ ] 为 `AjaxResult` 添加业务状态码支持
- [ ] 编写示例代码和使用指南

#### 长期任务（1-2个月）
- [ ] 提供各业务域的示例状态码枚举（User, Order, Payment等）
- [ ] 实现状态码自动生成工具（从数据库/配置文件生成）
- [ ] 添加 OpenAPI/Swagger 集成支持
- [ ] 性能优化和缓存机制

---

## 🌍 Phase 2: 国际化异常集成

### 2.1 设计目标

- 支持多语言错误消息（中文、英文等）
- 与 Spring MessageSource 深度集成
- 支持参数化消息模板
- 支持运行时语言切换
- 兼容现有 `AjaxResult` 和 `ProblemDetail`

### 2.2 实现方案

#### 消息源配置

```properties
# messages_zh_CN.properties
common.success=操作成功
common.param.invalid=参数无效：{0}
user.not.found=用户不存在：{0}
user.disabled=用户已被禁用，请联系管理员
order.create.failed=订单创建失败：{0}，错误码：{1}

# messages_en_US.properties
common.success=Operation successful
common.param.invalid=Invalid parameter: {0}
user.not.found=User not found: {0}
user.disabled=User has been disabled, please contact administrator
order.create.failed=Order creation failed: {0}, error code: {1}
```

#### 国际化响应构建器

```java
/**
 * 国际化响应构建器.
 * <p>
 * 结合业务状态码和 MessageSource，生成多语言响应。
 * </p>
 */
@Component
public class I18nResponseBuilder {
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private BusinessCodeRegistry businessCodeRegistry;
    
    /**
     * 构建国际化成功响应.
     *
     * @param businessCode 业务状态码
     * @param data         数据
     * @param locale       语言区域
     * @return AjaxResult
     */
    public AjaxResult buildSuccess(BusinessCode businessCode, Object data, Locale locale) {
        String message = resolveMessage(businessCode, locale);
        return new AjaxResult(
            businessCode.getCode(),
            message,
            data
        );
    }
    
    /**
     * 构建国际化错误响应.
     *
     * @param businessCode 业务状态码
     * @param args         消息参数
     * @param locale       语言区域
     * @return AjaxResult
     */
    public AjaxResult buildError(BusinessCode businessCode, Locale locale, Object... args) {
        String message = resolveMessage(businessCode, locale, args);
        return new AjaxResult(
            businessCode.getCode(),
            message,
            null
        );
    }
    
    /**
     * 构建国际化 ProblemDetail.
     *
     * @param businessCode 业务状态码
     * @param detail       详细描述
     * @param locale       语言区域
     * @return ProblemDetail
     */
    public ProblemDetail buildProblemDetail(BusinessCode businessCode, String detail, Locale locale) {
        String title = resolveMessage(businessCode, locale);
        return ProblemDetail.builder()
            .status(businessCode.getHttpStatus().getCode())
            .title(title)
            .detail(detail)
            .type("https://api.example.com/errors/" + businessCode.getCode())
            .build();
    }
    
    /**
     * 解析国际化消息.
     *
     * @param businessCode 业务状态码
     * @param locale       语言区域
     * @param args         消息参数
     * @return 解析后的消息
     */
    private String resolveMessage(BusinessCode businessCode, Locale locale, Object... args) {
        try {
            // 尝试从 MessageSource 获取国际化消息
            String messageKey = businessCode.getCode().toLowerCase().replace('_', '.');
            return messageSource.getMessage(messageKey, args, businessCode.getDefaultMessage(), locale);
        } catch (NoSuchMessageException e) {
            // 如果找不到国际化消息，使用默认消息
            return businessCode.getDefaultMessage();
        }
    }
}
```

#### 全局异常处理器集成

```java
/**
 * 国际化异常处理器.
 */
@RestControllerAdvice
public class I18nExceptionHandler {
    
    @Autowired
    private I18nResponseBuilder responseBuilder;
    
    /**
     * 处理业务异常.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<AjaxResult> handleBusinessException(
            BusinessException ex, 
            HttpServletRequest request) {
        
        Locale locale = resolveLocale(request);
        AjaxResult result = responseBuilder.buildError(
            ex.getBusinessCode(),
            locale,
            ex.getArgs()
        );
        
        return ResponseEntity
            .status(ex.getBusinessCode().getHttpStatus().getCode())
            .body(result);
    }
    
    /**
     * 处理参数验证异常.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AjaxResult> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        Locale locale = resolveLocale(request);
        
        // 收集所有验证错误
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();
                return field + ": " + message;
            })
            .collect(Collectors.toList());
        
        AjaxResult result = responseBuilder.buildError(
            CommonBusinessCode.PARAM_INVALID,
            locale,
            String.join("; ", errors)
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(result);
    }
    
    /**
     * 从请求中解析语言区域.
     */
    private Locale resolveLocale(HttpServletRequest request) {
        // 优先级：请求头 > Session > Cookie > 默认
        String lang = request.getHeader("Accept-Language");
        if (lang != null && !lang.isEmpty()) {
            return Locale.forLanguageTag(lang.split(",")[0]);
        }
        
        // 可以从 Session 或 Cookie 中获取用户偏好
        // ...
        
        return LocaleContextHolder.getLocale();
    }
}
```

#### 业务异常类

```java
/**
 * 业务异常基类.
 */
public abstract class BusinessException extends RuntimeException {
    
    private final BusinessCode businessCode;
    private final Object[] args;
    
    protected BusinessException(BusinessCode businessCode, Object... args) {
        super(businessCode.getDefaultMessage());
        this.businessCode = businessCode;
        this.args = args;
    }
    
    public BusinessCode getBusinessCode() {
        return businessCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}

/**
 * 用户业务异常示例.
 */
public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException(String userId) {
        super(UserBusinessCode.USER_NOT_FOUND, userId);
    }
}

public class UserDisabledException extends BusinessException {
    
    public UserDisabledException(String username) {
        super(UserBusinessCode.USER_DISABLED, username);
    }
}
```

### 2.3 任务清单

#### 短期任务（2-3周）
- [ ] 创建消息资源文件（messages_zh_CN.properties, messages_en_US.properties）
- [ ] 实现 `I18nResponseBuilder`
- [ ] 创建 `BusinessException` 基类
- [ ] 实现 `I18nExceptionHandler`
- [ ] 编写单元测试

#### 中期任务（3-4周）
- [ ] 集成到现有 Controller 示例
- [ ] 添加语言切换 API
- [ ] 实现消息缓存机制
- [ ] 编写使用文档和最佳实践

#### 长期任务（1-2个月）
- [ ] 支持更多语言（日语、韩语等）
- [ ] 实现消息热更新（无需重启）
- [ ] 添加管理后台维护消息
- [ ] 与前端 i18n 框架联动

---

## 🔍 Phase 3: 响应拦截器与归一化处理

### 3.1 设计目标

- **自动拦截所有响应**：无需修改现有 Controller 代码
- **智能归一化**：将非标准响应自动转换为 AjaxResult
- **⚠️ 互斥原则**：自定义响应格式与 AjaxResult 只能二选一，不可同时存在
- **灵活策略**：支持转换、放行、抛出异常等多种处理方式
- **可配置规则**：通过注解或配置文件定义拦截规则
- **性能优化**：最小化拦截开销，支持条件跳过

### 3.2 核心架构

```
Controller 方法返回
        ↓
ResponseInterceptor (ResponseBodyAdvice)
        ↓
    ┌───────────────┐
    │ 检查响应类型   │
    └───────┬───────┘
            ↓
    ┌───────────────────────────────────┐
    │ ⚠️ 互斥检查：                      │
    │ AjaxResult OR 自定义响应？         │
    ├─────────────┬─────────────────────┤
    │ AjaxResult  │ 自定义响应          │
    ↓             ↓                     │
  直接放行     ┌─────────────────┐      │
              │ 应用转换策略     │      │
              └────────┬────────┘      │
                       ↓               │
              ┌─────────────────┐      │
              │ 转换为AjaxResult│      │
              │ OR 抛出异常     │      │
              └────────┬────────┘      │
                       ↓               │
                   返回统一格式         │
                                       │
        ┌──────────────────────────────┘
        ↓
最终响应返回给客户端（统一为 AjaxResult）
```

#### ⚠️ 互斥原则说明

**核心规则**: 项目中只能选择一种响应风格，要么全部使用 `AjaxResult`，要么全部使用自定义响应格式（如 `CustomResponse`），**绝对不能混用**。

**为什么需要互斥？**
1. **前端解析一致性**：前端只需要处理一种响应格式
2. **避免混淆**：不同接口返回不同结构会导致维护困难
3. **简化拦截逻辑**：拦截器只需关注一种转换方向
4. **团队协作**：统一的规范减少沟通成本

**如何选择？**
- ✅ **新项目**: 推荐使用 `AjaxResult`（本模块标准）
- ✅ **遗留系统**: 可保留原有 `CustomResponse`，通过拦截器统一转换
- ❌ **禁止**: 同一项目中部分接口用 AjaxResult，部分用 CustomResponse

### 3.3 实现方案

#### 响应拦截器接口

```java
/**
 * 响应拦截策略接口.
 * <p>
 * 定义如何处理非标准响应的转换规则。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@FunctionalInterface
public interface ResponseInterceptStrategy {
    
    /**
     * 判断是否支持处理该响应类型.
     *
     * @param responseType 响应类型
     * @return 支持返回 true
     */
    boolean supports(Class<?> responseType);
    
    /**
     * 处理响应.
     *
     * @param response 原始响应对象
     * @param request  HTTP 请求
     * @return 处理后的响应（可以是 AjaxResult、原对象或抛出异常）
     */
    Object handle(Object response, HttpServletRequest request);
}
```

#### 内置策略实现

```java
/**
 * 实体类转 AjaxResult 策略.
 * <p>
 * 将普通的实体类自动包装为 AjaxResult.success(entity)
 * </p>
 */
@Component
public class EntityToAjaxResultStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        // 排除已经是响应类型的对象
        return !AjaxResult.class.isAssignableFrom(responseType)
            && !Result.class.isAssignableFrom(responseType)
            && !ResponseEntity.class.isAssignableFrom(responseType)
            && !ProblemDetail.class.isAssignableFrom(responseType)
            && !isFrameworkType(responseType); // 排除 Spring 框架类型
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        if (response == null) {
            return AjaxResult.success();
        }
        return AjaxResult.success(response);
    }
    
    private boolean isFrameworkType(Class<?> type) {
        String packageName = type.getPackage().getName();
        return packageName.startsWith("org.springframework")
            || packageName.startsWith("javax.")
            || packageName.startsWith("jakarta.");
    }
}

/**
 * Map 转 AjaxResult 策略.
 */
@Component
public class MapToAjaxResultStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        return Map.class.isAssignableFrom(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        Map<?, ?> map = (Map<?, ?>) response;
        // 检查是否已包含 code/msg 字段
        if (map.containsKey("code") && map.containsKey("msg")) {
            return AjaxResult.success(
                String.valueOf(map.get("code")),
                String.valueOf(map.get("msg")),
                map.get("data")
            );
        }
        return AjaxResult.success(map);
    }
}

/**
 * 字符串转 AjaxResult 策略.
 */
@Component
public class StringToAjaxResultStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        return String.class.equals(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        String str = (String) response;
        // 尝试解析为 JSON
        try {
            Object json = JSON.parse(str);
            return AjaxResult.success(json);
        } catch (Exception e) {
            // 不是 JSON，作为普通消息处理
            return AjaxResult.success(str);
        }
    }
}

/**
 * 严格模式策略 - 抛出异常.
 * <p>
 * 对于不符合规范的响应直接抛出异常，强制开发者修正。
 * </p>
 */
@Component
@ConditionalOnProperty(name = "hongz.response.interceptor.strict-mode", havingValue = "true")
public class StrictModeStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        // 只处理非标准响应
        return !AjaxResult.class.isAssignableFrom(responseType)
            && !Result.class.isAssignableFrom(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        throw new IllegalStateException(
            "Response must be AjaxResult or Result, but got: " + 
            response.getClass().getName() +
            ". Please wrap your response or disable strict mode."
        );
    }
}
```

#### 响应拦截器核心实现

```java
/**
 * 响应体拦截器.
 * <p>
 * 实现 ResponseBodyAdvice，在响应写出前进行拦截和处理。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {
    
    private final List<ResponseInterceptStrategy> strategies;
    private final ResponseProperties properties;
    
    @Autowired
    public ResponseInterceptor(
            List<ResponseInterceptStrategy> strategies,
            ResponseProperties properties) {
        this.strategies = strategies;
        this.properties = properties;
    }
    
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 检查是否启用拦截器
        if (!properties.isInterceptorEnabled()) {
            return false;
        }
        
        // 检查是否有跳过注解
        if (hasSkipAnnotation(returnType)) {
            return false;
        }
        
        // 检查返回类型是否需要处理
        Class<?> parameterType = returnType.getParameterType();
        return shouldIntercept(parameterType);
    }
    
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        
        if (body == null) {
            return AjaxResult.success();
        }
        
        // 如果已经是标准响应类型，直接放行
        if (isStandardResponse(body.getClass())) {
            return body;
        }
        
        // 查找匹配的策略
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        for (ResponseInterceptStrategy strategy : strategies) {
            if (strategy.supports(body.getClass())) {
                return strategy.handle(body, servletRequest);
            }
        }
        
        // 没有匹配的策略，使用默认处理
        return defaultHandle(body, servletRequest);
    }
    
    /**
     * 判断是否应该拦截.
     */
    private boolean shouldIntercept(Class<?> type) {
        // 排除不需要拦截的类型
        if (isStandardResponse(type)) {
            return false;
        }
        
        // 排除文件下载、流式响应等
        if (isStreamingResponse(type)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 判断是否为标准响应类型.
     */
    private boolean isStandardResponse(Class<?> type) {
        return AjaxResult.class.isAssignableFrom(type)
            || Result.class.isAssignableFrom(type)
            || ResponseEntity.class.isAssignableFrom(type)
            || ProblemDetail.class.isAssignableFrom(type)
            || ErrorResponse.class.isAssignableFrom(type);
    }
    
    /**
     * 判断是否为流式响应.
     */
    private boolean isStreamingResponse(Class<?> type) {
        return StreamingResponseBody.class.isAssignableFrom(type)
            || SseEmitter.class.isAssignableFrom(type)
            || Flux.class.isAssignableFrom(type)
            || InputStream.class.isAssignableFrom(type)
            || Resource.class.isAssignableFrom(type);
    }
    
    /**
     * 检查是否有跳过注解.
     */
    private boolean hasSkipAnnotation(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(SkipResponseIntercept.class)
            || returnType.getContainingClass().isAnnotationPresent(SkipResponseIntercept.class);
    }
    
    /**
     * 默认处理逻辑.
     */
    private Object defaultHandle(Object body, HttpServletRequest request) {
        // 根据配置决定默认行为
        switch (properties.getDefaultInterceptAction()) {
            case WRAP:
                return AjaxResult.success(body);
            case PASS:
                return body;
            case THROW:
                throw new IllegalStateException(
                    "No strategy found for response type: " + body.getClass().getName()
                );
            default:
                return AjaxResult.success(body);
        }
    }
}
```

#### 跳过拦截注解

```java
/**
 * 跳过响应拦截注解.
 * <p>
 * 标注在 Controller 方法或类上，表示该响应不需要被拦截器处理。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipResponseIntercept {
    
    /**
     * 跳过原因说明（可选）.
     */
    String value() default "";
}
```

#### 配置属性

```java
@Data
@ConfigurationProperties(prefix = "hongz.response.interceptor")
public class InterceptorProperties {
    
    /**
     * 是否启用响应拦截器.
     */
    private boolean enabled = true;
    
    /**
     * ⚠️ 响应风格选择（互斥）.
     * AJAX_RESULT: 统一使用 AjaxResult（推荐）
     * CUSTOM: 统一使用自定义响应格式
     * 
     * 注意：项目中只能选择一种风格，不可混用！
     */
    private ResponseStyle responseStyle = ResponseStyle.AJAX_RESULT;
    
    /**
     * 默认拦截动作.
     * WRAP: 包装为目标响应格式
     * PASS: 放行原始响应
     * THROW: 抛出异常
     */
    private DefaultAction defaultAction = DefaultAction.WRAP;
    
    /**
     * 是否启用严格模式.
     * 严格模式下，非目标响应格式会抛出异常。
     */
    private boolean strictMode = false;
    
    /**
     * 需要排除的包路径列表.
     */
    private List<String> excludePackages = Arrays.asList(
        "org.springframework",
        "javax.",
        "jakarta."
    );
    
    /**
     * 需要排除的类名列表.
     */
    private List<String> excludeClasses = new ArrayList<>();
    
    /**
     * 响应风格枚举（互斥）.
     */
    public enum ResponseStyle {
        /**
         * 统一使用 AjaxResult（推荐）.
         */
        AJAX_RESULT,
        
        /**
         * 统一使用自定义响应格式.
         * 需配合自定义策略使用。
         */
        CUSTOM
    }
    
    /**
     * 默认拦截动作枚举.
     */
    public enum DefaultAction {
        WRAP,   // 包装为目标格式
        PASS,   // 放行
        THROW   // 抛出异常
    }
}
```

### 3.4 使用示例

#### ⚠️ 重要：响应风格选择（互斥）

在开始使用前，必须确定项目的响应风格，**二选一，不可混用**：

```yaml
# application.yml
hongz:
  response:
    interceptor:
      enabled: true
      # ⚠️ 关键配置：选择响应风格（互斥）
      response-style: AJAX_RESULT  # 选项：AJAX_RESULT 或 CUSTOM
      strict-mode: true  # 严格模式，防止混用
```

**风格对比**:

| 维度 | AJAX_RESULT（推荐） | CUSTOM |
|------|-------------------|--------|
| **适用场景** | 新项目、标准化项目 | 遗留系统、特殊规范 |
| **前端解析** | 统一 code/msg/data | 需适配自定义格式 |
| **拦截器行为** | 将所有非 AjaxResult 转换为 AjaxResult | 将所有非 CustomResponse 转换为 CustomResponse |
| **严格模式** | 禁止返回 CustomResponse | 禁止返回 AjaxResult |
| **文档示例** | 本模块标准示例 | 需自定义策略 |

---

#### 示例 1: 使用 AjaxResult 风格（推荐）

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // 直接返回实体类，拦截器会自动包装为 AjaxResult.success(user)
        return userService.findById(id);
    }
    
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        // 创建成功后返回实体，自动包装
        return userService.create(request);
    }
}

// 实际返回的 JSON:
// {
//   "code": "200",
//   "msg": "操作成功",
//   "data": {
//     "id": 123,
//     "name": "张三",
//     "email": "zhangsan@example.com"
//   }
// }
```

#### 示例 2: 跳过拦截

```java
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @GetMapping("/download/{filename}")
    @SkipResponseIntercept("文件下载不需要包装")
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource resource = fileService.load(filename);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }
    
    @GetMapping("/stream")
    @SkipResponseIntercept
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter();
        // SSE 流式响应，不需要包装
        return emitter;
    }
}
```

#### 示例 3: 自定义策略

```java
/**
 * 自定义策略：处理特定的遗留响应格式.
 */
@Component
public class LegacyResponseStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        return LegacyResponse.class.isAssignableFrom(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        LegacyResponse legacy = (LegacyResponse) response;
        // 将遗留格式转换为 AjaxResult
        return AjaxResult.success(
            String.valueOf(legacy.getStatus()),
            legacy.getMessage(),
            legacy.getData()
        );
    }
}
```

#### 示例 4: 配置严格模式

```yaml
# application.yml
hongz:
  response:
    interceptor:
      enabled: true
      strict-mode: true  # 开启严格模式
      default-action: THROW  # 默认抛出异常
      exclude-packages:
        - org.springframework
        - com.example.openapi  # 排除 OpenAPI 生成的接口
```

---

#### 示例 5: 使用自定义响应风格（遗留系统）

如果你的项目已有自定义响应格式（如 `CustomResponse`），可以选择 CUSTOM 风格：

```yaml
# application.yml
hongz:
  response:
    interceptor:
      enabled: true
      response-style: CUSTOM  # ⚠️ 选择自定义风格
      strict-mode: true  # 禁止返回 AjaxResult
```

```java
// 自定义响应类
public class CustomResponse<T> {
    private int status;      // 业务状态码
    private String message;  // 消息
    private T data;          // 数据
    private long timestamp;  // 时间戳
    
    public static <T> CustomResponse<T> success(T data) {
        return new CustomResponse<>(200, "success", data, System.currentTimeMillis());
    }
}

// Controller 直接返回实体，拦截器自动转换为 CustomResponse
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.findById(id); // 自动包装为 CustomResponse.success(user)
}

// 自定义转换策略
@Component
public class EntityToCustomResponseStrategy implements ResponseInterceptStrategy {
    
    @Override
    public boolean supports(Class<?> responseType) {
        // 排除已是 CustomResponse 的对象
        return !CustomResponse.class.isAssignableFrom(responseType)
            && !isFrameworkType(responseType);
    }
    
    @Override
    public Object handle(Object response, HttpServletRequest request) {
        if (response == null) {
            return CustomResponse.success(null);
        }
        return CustomResponse.success(response);
    }
}
```

**⚠️ 注意**: 选择 CUSTOM 风格后，项目中所有接口都必须返回 `CustomResponse`，禁止返回 `AjaxResult`！

### 3.5 任务清单

#### 短期任务（2-3周）
- [ ] 设计 `ResponseInterceptStrategy` 接口
- [ ] 实现 `ResponseInterceptor` (ResponseBodyAdvice)
- [ ] 创建 `@SkipResponseIntercept` 注解
- [ ] 实现基础策略（Entity、Map、String 转换）
- [ ] 编写单元测试

#### 中期任务（3-4周）
- [ ] 实现严格模式策略
- [ ] 添加配置属性支持
- [ ] 集成到 Spring Boot Starter
- [ ] 编写使用文档和示例
- [ ] 性能测试和优化

#### 长期任务（1-2个月）
- [ ] 支持异步响应拦截（DeferredResult、CompletableFuture）
- [ ] 支持响应式响应拦截（Flux/Mono）
- [ ] 添加拦截器链（多个策略按顺序执行）
- [ ] 提供管理端点查看拦截统计
- [ ] 支持动态加载策略（无需重启）

---

## 🔌 Phase 4: Spring Boot Starter 与社区贡献

### 4.1 Spring Boot Starter

#### 自动配置类

```java
@Configuration
@ConditionalOnClass(AjaxResult.class)
@EnableConfigurationProperties(ResponseProperties.class)
public class ResponseAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    public BusinessCodeRegistry businessCodeRegistry() {
        return new BusinessCodeRegistry();
    }
    
    @Bean
    @ConditionalOnMissingBean
    public I18nResponseBuilder i18nResponseBuilder(MessageSource messageSource,
                                                    BusinessCodeRegistry registry) {
        return new I18nResponseBuilder(messageSource, registry);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public I18nExceptionHandler i18nExceptionHandler(I18nResponseBuilder builder) {
        return new I18nExceptionHandler(builder);
    }
}
```

#### 配置属性

```java
@Data
@ConfigurationProperties(prefix = "hongz.response")
public class ResponseProperties {
    
    /**
     * 是否启用国际化支持.
     */
    private boolean i18nEnabled = true;
    
    /**
     * 默认语言区域.
     */
    private String defaultLocale = "zh_CN";
    
    /**
     * 消息资源文件路径.
     */
    private String[] messageBasenames = {"classpath:messages"};
    
    /**
     * 是否启用业务状态码注册表.
     */
    private boolean businessCodeRegistryEnabled = true;
}
```

### 4.2 社区贡献计划

#### 向 Spring Cloud Alibaba 贡献
- 提交 PR 添加中国云厂商响应适配器
- 提供阿里云/腾讯云/华为云响应转换工具

#### 向 Spring Framework 提议
- 提议增加 `BusinessResponse` 抽象
- 增强 `ProblemDetail` 本地化支持

#### 开源独立项目
- 创建 `hongz-response-spring-boot-starter`
- 发布到 Maven Central
- 编写完整文档和示例

### 4.3 任务清单

#### 短期任务（1个月）
- [ ] 创建 Spring Boot Starter 模块
- [ ] 实现自动配置
- [ ] 编写配置文档

#### 中期任务（2-3个月）
- [ ] 准备 Spring Cloud Alibaba PR
- [ ] 编写提案文档
- [ ] 创建示例项目

#### 长期任务（6个月+）
- [ ] 向 Spring Framework 提交提案
- [ ] 开源独立项目
- [ ] 建立社区和文档站点

---

## 📊 里程碑与验收标准

### Phase 1 验收标准
- ✅ `HttpStatusCode` 枚举覆盖所有标准状态码
- ✅ `BusinessCode` 接口设计合理，易于扩展
- ✅ `AjaxResult` 支持 HTTP 和业务状态码
- ✅ 单元测试覆盖率 > 90%
- ✅ 文档完整，包含使用示例

### Phase 2 验收标准
- ✅ 支持至少 2 种语言（中文、英文）
- ✅ 消息参数替换正常工作
- ✅ 异常处理器正确捕获并转换异常
- ✅ 语言切换功能正常
- ✅ 性能测试通过（消息解析 < 1ms）

### Phase 3 验收标准
- ✅ Spring Boot Starter 自动配置正常
- ✅ 配置属性生效
- ✅ PR 被 Spring Cloud Alibaba 接受
- ✅ 独立项目发布到 Maven Central
- ✅ 至少有 3 个外部项目使用

---

## 🔗 相关资源

### 参考标准
- [RFC 7231 - HTTP/1.1 Semantics and Content](https://www.rfc-editor.org/rfc/rfc7231)
- [RFC 7807 - Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc7807)
- [RFC 9457 - Problem Details for HTTP APIs (Updated)](https://www.rfc-editor.org/rfc/rfc9457)
- [Spring Framework Error Responses](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)

### 相关文档
- [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) - 当前实现状态
- [RESPONSE_FORMATS.md](./RESPONSE_FORMATS.md) - 响应格式大全
- [CUSTOM_RESPONSE_GUIDE.md](./CUSTOM_RESPONSE_GUIDE.md) - 自定义响应指南

---

**最后更新**: 2026-06-22  
**版本**: 1.0.0-SNAPSHOT  
**维护者**: hongz-parent 团队
