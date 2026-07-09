# hongz-ztools-response 规划任务清单

> 本文档用于跟踪 HTTP 状态码、业务状态码和国际化异常集成的实施进度。

---

## 📋 Phase 1: HTTP 状态码与业务状态码体系 (1-2个月)

### 1.1 HTTP 状态码枚举

#### 核心实现
- [ ] 创建 `HttpStatusCode` 枚举类
  - [ ] 覆盖所有 RFC 7231/7235/7538 标准状态码（1xx, 2xx, 3xx, 4xx, 5xx）
  - [ ] 提供 `getCode()` 和 `getReasonPhrase()` 方法
  - [ ] 添加状态码分类判断方法（`isSuccess()`, `isClientError()`, `isServerError()` 等）
  - [ ] 实现 `valueOf(int code)` 静态方法

#### 集成测试
- [ ] 为 `AjaxResult` 添加 `HttpStatusCode` 支持
  - [ ] `success(HttpStatusCode status, Object data)` 方法
  - [ ] `error(HttpStatusCode status, String detail)` 方法
- [ ] 为 `ProblemDetail` 添加 `HttpStatusCode` 工厂方法
  - [ ] `from(HttpStatusCode status, String detail)` 方法
- [ ] 编写单元测试
  - [ ] 测试所有状态码枚举值
  - [ ] 测试状态码分类方法
  - [ ] 测试与 AjaxResult 的集成
  - [ ] 测试与 ProblemDetail 的集成

#### 文档
- [ ] 更新 JavaDoc 注释
- [ ] 添加使用示例到 README
- [ ] 更新 QUICK_REFERENCE.md

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 1.2 业务状态码接口

#### 核心设计
- [ ] 设计 `BusinessCode` 接口
  - [ ] `getCode()` - 获取业务状态码
  - [ ] `getDefaultMessage()` - 获取默认消息
  - [ ] `getHttpStatus()` - 获取对应的 HTTP 状态码
  - [ ] `isSuccess()` - 判断是否成功
  - [ ] `getDomain()` - 获取业务域

#### 通用状态码
- [ ] 创建 `CommonBusinessCode` 枚举
  - [ ] SUCCESS("COMMON_000", "操作成功", HttpStatusCode.OK)
  - [ ] PARAM_INVALID("COMMON_400", "参数无效", HttpStatusCode.BAD_REQUEST)
  - [ ] UNAUTHORIZED("COMMON_401", "未授权", HttpStatusCode.UNAUTHORIZED)
  - [ ] FORBIDDEN("COMMON_403", "禁止访问", HttpStatusCode.FORBIDDEN)
  - [ ] NOT_FOUND("COMMON_404", "资源不存在", HttpStatusCode.NOT_FOUND)
  - [ ] INTERNAL_ERROR("COMMON_500", "系统内部错误", HttpStatusCode.INTERNAL_SERVER_ERROR)
  - [ ] SERVICE_UNAVAILABLE("COMMON_503", "服务暂不可用", HttpStatusCode.SERVICE_UNAVAILABLE)

#### 注册表
- [ ] 实现 `BusinessCodeRegistry` 组件
  - [ ] `register(BusinessCode businessCode)` - 注册单个状态码
  - [ ] `registerAll(Class<E> enumClass)` - 批量注册枚举
  - [ ] `getByCode(String code)` - 根据代码查询
  - [ ] `@PostConstruct init()` - 自动注册通用状态码

#### 集成
- [ ] 为 `AjaxResult` 添加业务状态码支持
  - [ ] `success(BusinessCode businessCode, Object data)` 方法
  - [ ] `error(BusinessCode businessCode, String detail)` 方法
  - [ ] `error(BusinessCode businessCode, Object... args)` 方法（参数替换）

#### 示例代码
- [ ] 创建用户业务状态码示例 `UserBusinessCode`
- [ ] 创建订单业务状态码示例 `OrderBusinessCode`
- [ ] 创建支付业务状态码示例 `PaymentBusinessCode`

#### 测试
- [ ] 编写单元测试
  - [ ] 测试 BusinessCode 接口实现
  - [ ] 测试 BusinessCodeRegistry 注册和查询
  - [ ] 测试与 AjaxResult 的集成
  - [ ] 测试业务域提取逻辑

#### 文档
- [ ] 编写业务状态码设计文档
- [ ] 添加使用指南到 CUSTOM_RESPONSE_GUIDE.md
- [ ] 更新 README.md 示例

**预计工时**: 2周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

## 🌍 Phase 2: 国际化异常集成 (2-3个月)

### 2.1 消息资源配置

#### 资源文件
- [ ] 创建消息资源目录结构
  ```
  src/main/resources/
  └── messages/
      ├── messages_zh_CN.properties
      ├── messages_en_US.properties
      └── messages.properties (默认)
  ```

#### 中文消息
- [ ] 编写 messages_zh_CN.properties
  - [ ] common.success=操作成功
  - [ ] common.param.invalid=参数无效：{0}
  - [ ] user.not.found=用户不存在：{0}
  - [ ] user.disabled=用户已被禁用，请联系管理员
  - [ ] order.create.failed=订单创建失败：{0}，错误码：{1}
  - [ ] payment.insufficient.balance=余额不足，当前余额：{0}

#### 英文消息
- [ ] 编写 messages_en_US.properties
  - [ ] common.success=Operation successful
  - [ ] common.param.invalid=Invalid parameter: {0}
  - [ ] user.not.found=User not found: {0}
  - [ ] user.disabled=User has been disabled, please contact administrator
  - [ ] order.create.failed=Order creation failed: {0}, error code: {1}
  - [ ] payment.insufficient.balance=Insufficient balance, current balance: {0}

**预计工时**: 3天  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 2.2 国际化响应构建器

#### 核心组件
- [ ] 创建 `I18nResponseBuilder` 组件
  - [ ] 注入 `MessageSource`
  - [ ] 注入 `BusinessCodeRegistry`
  - [ ] `buildSuccess(BusinessCode, Object, Locale)` - 构建成功响应
  - [ ] `buildError(BusinessCode, Locale, Object...)` - 构建错误响应
  - [ ] `buildProblemDetail(BusinessCode, String, Locale)` - 构建 ProblemDetail
  - [ ] `resolveMessage(BusinessCode, Locale, Object...)` - 解析国际化消息

#### 消息解析逻辑
- [ ] 实现消息键转换（USER_NOT_FOUND -> user.not.found）
- [ ] 实现 MessageSource 集成
- [ ] 实现回退机制（找不到国际化消息时使用默认消息）
- [ ] 实现参数替换（MessageFormat）

#### 测试
- [ ] 编写单元测试
  - [ ] 测试中文消息解析
  - [ ] 测试英文消息解析
  - [ ] 测试参数替换
  - [ ] 测试回退机制
  - [ ] 测试未知语言区域处理

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 2.3 业务异常基类

#### 异常设计
- [ ] 创建 `BusinessException` 抽象基类
  - [ ] 继承 `RuntimeException`
  - [ ] 包含 `BusinessCode businessCode` 字段
  - [ ] 包含 `Object[] args` 字段（消息参数）
  - [ ] 构造器：`BusinessException(BusinessCode, Object...)`
  - [ ] getter 方法

#### 具体异常
- [ ] 创建 `UserNotFoundException`
- [ ] 创建 `UserDisabledException`
- [ ] 创建 `OrderCreateFailedException`
- [ ] 创建 `PaymentException`
- [ ] 创建 `ResourceNotFoundException`（通用）

#### 测试
- [ ] 编写单元测试
  - [ ] 测试异常创建
  - [ ] 测试参数传递
  - [ ] 测试业务状态码获取

**预计工时**: 3天  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐

---

### 2.4 全局异常处理器

#### 异常处理器
- [ ] 创建 `I18nExceptionHandler` (@RestControllerAdvice)
  - [ ] 注入 `I18nResponseBuilder`
  - [ ] `handleBusinessException(BusinessException, HttpServletRequest)` - 处理业务异常
  - [ ] `handleValidationException(MethodArgumentNotValidException, HttpServletRequest)` - 处理验证异常
  - [ ] `resolveLocale(HttpServletRequest)` - 从请求中解析语言区域

#### 语言区域解析
- [ ] 实现多来源语言解析
  - [ ] 优先级 1: Accept-Language 请求头
  - [ ] 优先级 2: Session 属性
  - [ ] 优先级 3: Cookie
  - [ ] 优先级 4: LocaleContextHolder 默认值

#### 响应构建
- [ ] 业务异常转换为 AjaxResult
- [ ] 验证异常转换为 AjaxResult（收集所有字段错误）
- [ ] 设置正确的 HTTP 状态码
- [ ] 返回 ResponseEntity

#### 测试
- [ ] 编写集成测试
  - [ ] 测试业务异常处理
  - [ ] 测试验证异常处理
  - [ ] 测试不同语言区域的响应
  - [ ] 测试语言切换功能

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 2.5 Spring 配置

#### 自动配置
- [ ] 创建 `ResponseAutoConfiguration`
  - [ ] `@ConditionalOnClass(AjaxResult.class)`
  - [ ] `@EnableConfigurationProperties(ResponseProperties.class)`
  - [ ] Bean: `BusinessCodeRegistry`
  - [ ] Bean: `I18nResponseBuilder`
  - [ ] Bean: `I18nExceptionHandler`

#### 配置属性
- [ ] 创建 `ResponseProperties`
  - [ ] `i18nEnabled` - 是否启用国际化（默认 true）
  - [ ] `defaultLocale` - 默认语言区域（默认 zh_CN）
  - [ ] `messageBasenames` - 消息资源路径（默认 classpath:messages）
  - [ ] `businessCodeRegistryEnabled` - 是否启用注册表（默认 true）

#### spring.factories
- [ ] 创建 META-INF/spring.factories
  ```
  org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
    cc.hongz.ztools.response.config.ResponseAutoConfiguration
  ```

**预计工时**: 3天  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐

---

### 2.6 文档和示例

#### 使用文档
- [ ] 编写国际化使用指南
  - [ ] 消息资源文件编写规范
  - [ ] 业务异常定义指南
  - [ ] 语言切换 API 使用说明
  - [ ] 前端集成指南

#### 示例项目
- [ ] 创建示例 Controller
- [ ] 创建示例 Service（抛出业务异常）
- [ ] 创建前端调用示例
- [ ] 创建 Postman 测试集合

#### 性能优化
- [ ] 实现消息缓存机制
- [ ] 性能测试（消息解析 < 1ms）
- [ ] 并发测试

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐

---

## 🔍 Phase 3: 响应拦截器与归一化处理 (3-4个月)

### 3.1 核心接口设计

#### 策略接口
- [ ] 设计 `ResponseInterceptStrategy` 接口
  - [ ] `supports(Class<?> responseType)` - 判断是否支持
  - [ ] `handle(Object response, HttpServletRequest request)` - 处理响应
- [ ] 编写接口文档和 JavaDoc

#### 跳过注解
- [ ] 创建 `@SkipResponseIntercept` 注解
  - [ ] 支持方法级和类级标注
  - [ ] 添加 value 属性（跳过原因说明）
- [ ] 编写使用示例

**预计工时**: 3天  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 3.2 内置转换策略

#### Entity 转 AjaxResult
- [ ] 实现 `EntityToAjaxResultStrategy`
  - [ ] 排除已是响应类型的对象
  - [ ] 排除 Spring 框架类型
  - [ ] 自动包装为 AjaxResult.success(entity)
- [ ] 编写单元测试

#### Map 转 AjaxResult
- [ ] 实现 `MapToAjaxResultStrategy`
  - [ ] 检测是否已包含 code/msg 字段
  - [ ] 智能转换为 AjaxResult
- [ ] 编写单元测试

#### String 转 AjaxResult
- [ ] 实现 `StringToAjaxResultStrategy`
  - [ ] 尝试解析为 JSON
  - [ ] 非 JSON 作为普通消息处理
- [ ] 编写单元测试

#### 严格模式策略
- [ ] 实现 `StrictModeStrategy`
  - [ ] 条件装配（@ConditionalOnProperty）
  - [ ] 抛出明确的异常信息
- [ ] 编写单元测试

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 3.3 响应拦截器核心实现

#### ResponseInterceptor
- [ ] 实现 `ResponseBodyAdvice<Object>`
- [ ] 实现 `supports()` 方法
  - [ ] 检查拦截器开关
  - [ ] 检查 @SkipResponseIntercept 注解
  - [ ] 检查返回类型是否需要拦截
- [ ] 实现 `beforeBodyWrite()` 方法
  - [ ] null 值处理
  - [ ] 标准响应类型放行
  - [ ] 查找匹配的策略
  - [ ] 默认处理逻辑
- [ ] 添加辅助方法
  - [ ] `shouldIntercept()` - 判断是否应该拦截
  - [ ] `isStandardResponse()` - 判断标准响应类型
  - [ ] `isStreamingResponse()` - 判断流式响应
  - [ ] `hasSkipAnnotation()` - 检查跳过注解
  - [ ] `defaultHandle()` - 默认处理逻辑

#### 配置属性
- [ ] 创建 `InterceptorProperties`
  - [ ] `enabled` - 是否启用拦截器
  - [ ] ⚠️ `responseStyle` - 响应风格选择（AJAX_RESULT/CUSTOM，互斥）
  - [ ] `defaultAction` - 默认动作（WRAP/PASS/THROW）
  - [ ] `strictMode` - 严格模式开关
  - [ ] `excludePackages` - 排除的包路径
  - [ ] `excludeClasses` - 排除的类名
- [ ] 编写配置文档
- [ ] **重点说明互斥原则**（文档中醒目提示）

**预计工时**: 1.5周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐⭐

---

### 3.4 集成测试

#### 功能测试
- [ ] 测试实体类自动包装
- [ ] 测试 Map 转换
- [ ] 测试 String 转换
- [ ] 测试 @SkipResponseIntercept 注解
- [ ] 测试严格模式
- [ ] 测试文件下载跳过
- [ ] 测试 SSE 跳过
- [ ] 测试 Flux/Mono 跳过

#### 性能测试
- [ ] 基准测试（无拦截器 vs 有拦截器）
- [ ] 确保拦截开销 < 0.5ms
- [ ] 并发测试
- [ ] 内存占用测试

#### 边界测试
- [ ] null 响应处理
- [ ] 泛型响应处理
- [ ] 继承关系处理
- [ ] 代理类处理

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐

---

### 3.5 文档和示例

#### 使用文档
- [ ] 编写响应拦截器使用指南
  - [ ] 快速开始
  - [ ] 配置说明
  - [ ] 自定义策略开发
  - [ ] 最佳实践
  - [ ] 常见问题

#### 示例项目
- [ ] 创建示例 Controller（各种场景）
- [ ] 创建自定义策略示例
- [ ] 创建配置文件示例
- [ ] 创建 Postman 测试集合

**预计工时**: 1周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐

---

## 🔌 Phase 4: Spring Boot Starter 与社区贡献 (4-6个月)

### 4.1 Spring Boot Starter

#### 模块创建
- [ ] 创建 `hongz-ztools-response-spring-boot-starter` 模块
- [ ] 配置 pom.xml
- [ ] 整合自动配置类
- [ ] 编写 starter 文档

#### 发布准备
- [ ] 配置 Maven Central 发布
- [ ] 编写 CHANGELOG
- [ ] 准备发布说明

**预计工时**: 2周  
**负责人**: TBD  
**优先级**: ⭐⭐⭐⭐

---

### 4.2 社区贡献

#### Spring Cloud Alibaba
- [ ] 准备 PR 材料
  - [ ] 中国云厂商响应适配器
  - [ ] 阿里云响应转换工具
  - [ ] 腾讯云响应转换工具
  - [ ] 华为云响应转换工具
- [ ] 提交 PR
- [ ] 跟进评审反馈

#### Spring Framework
- [ ] 撰写提案文档
  - [ ] BusinessResponse 抽象提议
  - [ ] ProblemDetail 本地化增强提议
- [ ] 提交到 Spring Framework Issues
- [ ] 参与社区讨论

**预计工时**: 2个月  
**负责人**: TBD  
**优先级**: ⭐⭐⭐

---

### 4.3 开源独立项目

#### 项目准备
- [ ] 创建独立 GitHub 仓库
- [ ] 迁移核心代码
- [ ] 完善文档站点
- [ ] 准备示例项目

#### 推广
- [ ] 撰写技术博客
- [ ] 在技术社区分享
- [ ] 收集用户反馈
- [ ] 持续迭代优化

**预计工时**: 持续进行  
**负责人**: TBD  
**优先级**: ⭐⭐

---

## 📊 进度跟踪

### 总体进度

| Phase | 开始时间 | 结束时间 | 进度 | 状态 |
|-------|---------|---------|------|------|
| Phase 1 | TBD | TBD | 0% | ⏳ 待开始 |
| Phase 2 | TBD | TBD | 0% | ⏳ 待开始 |
| Phase 3 | TBD | TBD | 0% | ⏳ 待开始 |
| Phase 4 | TBD | TBD | 0% | ⏳ 待开始 |

### 里程碑

- [ ] **M1**: HttpStatusCode 枚举完成（Phase 1.1）
- [ ] **M2**: BusinessCode 体系完成（Phase 1.2）
- [ ] **M3**: 国际化基础框架完成（Phase 2.1-2.2）
- [ ] **M4**: 异常处理完成（Phase 2.3-2.4）
- [ ] **M5**: 响应拦截器完成（Phase 3）
- [ ] **M6**: Spring Boot Starter 发布（Phase 4.1）
- [ ] **M7**: 社区贡献被接受（Phase 4.2）

---

## 🎯 验收标准

### Phase 1 验收
- ✅ HttpStatusCode 枚举覆盖所有标准状态码
- ✅ BusinessCode 接口设计合理，易于扩展
- ✅ AjaxResult 支持 HTTP 和业务状态码
- ✅ 单元测试覆盖率 > 90%
- ✅ 文档完整，包含使用示例

### Phase 2 验收
- ✅ 支持至少 2 种语言（中文、英文）
- ✅ 消息参数替换正常工作
- ✅ 异常处理器正确捕获并转换异常
- ✅ 语言切换功能正常
- ✅ 性能测试通过（消息解析 < 1ms）

### Phase 3 验收
- ✅ ResponseInterceptor 正确拦截非标准响应
- ✅ 内置策略（Entity/Map/String）工作正常
- ✅ @SkipResponseIntercept 注解生效
- ✅ 严格模式可配置且工作正常
- ✅ 自动排除文件下载、SSE、Flux 等特殊响应
- ✅ **互斥原则生效**：AJAX_RESULT 模式下禁止返回 CustomResponse，CUSTOM 模式下禁止返回 AjaxResult
- ✅ 性能测试通过（拦截开销 < 0.5ms）

### Phase 4 验收
- ✅ Spring Boot Starter 自动配置正常
- ✅ 配置属性生效
- ✅ PR 被 Spring Cloud Alibaba 接受
- ✅ 独立项目发布到 Maven Central
- ✅ 至少有 3 个外部项目使用

---

## 📝 备注

- 所有任务完成后需要更新 IMPLEMENTATION_STATUS.md
- 每个阶段结束后进行回顾和总结
- 根据实际情况调整优先级和时间线
- 鼓励团队成员认领任务并提交 PR

---

**最后更新**: 2026-06-22  
**维护者**: hongz-parent 团队
