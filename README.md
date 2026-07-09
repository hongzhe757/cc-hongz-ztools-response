# hongz-ztools-response

**响应结构工具库**：提供多种风格的响应结构定义，支持 AJAX 风格、Java 原生风格、统一响应视图等，便于不同场景下的使用。

Java 包：`cc.hongz.ztools.response.*`

## 🚀 快速开始

### 统一入口（推荐）

所有响应结构的创建都可以通过 `ResponseFactory` 完成，无需记住各个类的具体位置：

```java
import cc.hongz.ztools.response.ResponseFactory;

// ✅ AJAX 响应
AjaxResult ajax = ResponseFactory.ajaxSuccess(data);

// ✅ 原生 Result
Result<User> result = ResponseFactory.resultSuccess(user);

// ✅ 标准协议
ProblemDetail problem = ResponseFactory.problemBadRequest("参数无效");

// ✅ HTTP 客户端包装
JdkHttpResponseWrapper<User> wrapper = ResponseFactory.jdkHttpWrapper(response, user);

// ✅ 框架集成
MyBatisPlusPageResponse<Order> page = ResponseFactory.mybatisPlusPage(iPage);

// ✅ 消息队列
RabbitMqRpcResponse<Result> rpc = ResponseFactory.rabbitmqRpc(correlationId, replyTo, data);

// ✅ 数据库/缓存
RedisResponse<String> redis = ResponseFactory.redisGet(key, value);

// ✅ 其他协议
WebSocketMessage<ChatMsg> ws = ResponseFactory.websocketText(message);

// ✅ 响应式
ReactiveAjaxResult<User> reactive = ResponseFactory.reactiveAjax(mono);
```

### 响应转换

在不同响应结构之间进行转换：

```java
import cc.hongz.ztools.response.ResponseConverter;

// 转换为 AjaxResult（归一化标准）
AjaxResult ajax = ResponseConverter.toAjaxResult(result);

// 厂商风格互转
TencentResponse<Order> txResp = ResponseConverter.aliToTencent(aliResp);

// 分页数据转换
DataListInfo pageInfo = ResponseConverter.toDataListInfo(mybatisPageResponse);

// 通用判断和提取
boolean success = ResponseConverter.isSuccess(anyResponse);
User data = ResponseConverter.extractData(anyResponse);
```

📖 **更多示例请查看**：[QUICK_REFERENCE.md](./QUICK_REFERENCE.md)

## 目录结构

```
cc.hongz.ztools.response
├── ResponseFactory.java          # 🔑 统一入口（推荐从这里开始）
├── ResponseConverter.java        # 🔄 响应转换器
│
├── ajax/              # AJAX 风格响应结构
│   └── AjaxResult     # 前后端分离 Web 应用的标准消息体（推荐用于归一化）
├── native/            # Java 原生风格响应结构
│   └── Result<T>      # 泛型通用返回结果
├── standard/          # 标准协议和行业规范响应结构
│   ├── AliResponse        # 阿里巴巴风格 (success/errorCode/errorMsg/content)
│   ├── TencentResponse    # 腾讯风格 (ret/msg/data)
│   ├── BaiduResponse      # 百度风格 (errno/errmsg/data)
│   ├── ProblemDetail      # RFC 7807 Problem Details (type/title/status/detail)
│   ├── JsonApiResponse    # JSON:API 规范 (data/errors/meta/links)
│   ├── GraphQLResponse    # GraphQL 响应 (data/errors/extensions)
│   └── GrpcResponse       # gRPC JSON Transcoding (code/message/details)
├── http/              # HTTP 客户端响应包装
│   ├── JdkHttpResponseWrapper      # JDK HttpClient 响应包装
│   ├── OkHttpResponseWrapper       # OkHttp 响应包装
│   ├── ApacheHttpResponseWrapper   # Apache HttpClient 响应包装
│   ├── SpringResponseEntityWrapper # Spring ResponseEntity 包装
│   ├── WebClientResponseWrapper    # Spring WebClient 响应包装
│   └── FeignResponseWrapper        # Feign Client 响应包装
├── framework/         # 框架特定响应
│   ├── ActuatorHealthResponse      # Spring Boot Actuator 健康检查
│   ├── OAuth2TokenResponse         # OAuth2 Token 响应
│   ├── SpringDataRestResponse      # Spring Data REST 响应
│   ├── MyBatisPlusPageResponse     # MyBatis-Plus 分页适配器
│   └── PageHelperPageResponse      # PageHelper 分页适配器
├── mq/                # 消息队列响应
│   ├── RabbitMqRpcResponse         # RabbitMQ RPC 响应
│   ├── KafkaConsumerRecordResponse # Kafka Consumer Record
│   └── RocketMqResponse            # RocketMQ 响应
├── database/          # 数据库/缓存响应
│   ├── RedisResponse               # Redis 响应
│   ├── ElasticsearchResponse       # Elasticsearch 响应
│   └── MongoDbResponse             # MongoDB 响应
├── protocol/          # 其他协议响应
│   ├── WebSocketMessage            # WebSocket 消息
│   ├── SseEvent                    # Server-Sent Events (SSE)
│   └── XmlToJsonResponse           # XML 转 JSON 响应
├── reactive/          # 响应式响应结构 ✨ 新增
│   ├── ReactiveAjaxResult          # 响应式 AJAX 结果（Mono）
│   ├── ReactiveCollectionResult    # 响应式集合结果（Flux）
│   └── ReactivePageResult          # 响应式分页结果（Flux + Mono）
├── generic/           # 通用/动态响应结构 ✨ 新增
│   ├── JsonResponse                # JSON 字符串响应包装器
│   └── MapResponse                 # Map 形式响应包装器
├── custom/            # 自定义响应结构示例 ✨ 新增
│   ├── CustomResponse              # 完整的自定义响应结构示例
│   └── CustomResponseConverter     # 自定义响应转换器
├── interceptor/       # 响应拦截与归一化（Phase 3）
│   ├── ResponseInterceptor         # ResponseBodyAdvice 自动包装
│   ├── ResponseInterceptStrategy   # 可插拔转换策略
│   ├── SkipResponseIntercept       # 跳过拦截注解
│   └── strategy/                   # Map / String / Entity 内置策略
├── config/            # Spring 配置
│   └── ResponseAutoConfiguration   # 国际化 + 异常处理 + 拦截器 Bean
├── i18n/              # 国际化响应构建
├── exception/         # BusinessException
├── handler/           # GlobalExceptionHandler
└── common/            # 通用响应数据结构 ✨ 重构
    ├── DataListInfo   # 分页数据列表信息（传统 Offset/Limit）
    ├── PageData       # 通用分页数据包装器（支持 MyBatis-Plus/PageHelper 转换）
    ├── CursorPageData # 游标分页数据包装器（无限滚动、Feed 流）
    └── KeysetPageData # 键集分页数据包装器（高性能深分页）
```

## 模块说明

### 1. AJAX 风格 (`ajax`)

适用于前后端分离的 Web 应用，返回 JSON 格式的响应数据。

**核心类**：`AjaxResult`
- 字段：`code` (状态码)、`msg` (消息)、`data` (数据体)
- 特点：静态工厂方法、链式调用、国际化支持预留

```java
// 成功响应
AjaxResult.success();
AjaxResult.success("操作成功", userData);
AjaxResult.success("200", "自定义消息", data);

// 失败响应
AjaxResult.error();
AjaxResult.error("操作失败");
AjaxResult.error("500", "错误消息", errorData);
```

### 2. Java 原生风格 (`native`)

适用于传统的 Java 应用或非 Web 场景，使用泛型提供类型安全。

**核心类**：`Result<T>`
- 字段：`success` (布尔值)、`code` (int)、`message` (字符串)、`data` (泛型 T)
- 特点：强类型、适合内部服务调用

```java
// 成功响应
Result<User> result = Result.success(user);
Result<String> result = Result.success("消息", "data");

// 失败响应
Result<Void> result = Result.failure();
Result<Void> result = Result.failure(404, "未找到");
```

### 3. 标准协议和行业规范 (`standard`)

包含各种标准协议和行业规范的响应格式定义。

**核心类**：
- `AliResponse<T>` - 阿里巴巴风格，使用 success/errorCode/errorMsg/content 字段
- `TencentResponse<T>` - 腾讯风格，使用 ret/msg/data 字段
- `BaiduResponse<T>` - 百度风格，使用 errno/errmsg/data 字段
- `ProblemDetail` - RFC 7807 Problem Details，HTTP API 错误标准
- `JsonApiResponse<T>` - JSON:API 规范，REST API 标准格式
- `GraphQLResponse<T>` - GraphQL 查询响应标准
- `GrpcResponse<T>` - gRPC JSON Transcoding 响应格式

```java
// 阿里巴巴风格
AliResponse<User> aliResp = AliResponse.success(user);
AliResponse<Void> aliErr = AliResponse.error("INVALID_PARAM", "参数无效");

// 腾讯风格
TencentResponse<Product> txResp = TencentResponse.success(product);
TencentResponse<Void> txErr = TencentResponse.error(-1, "操作失败");

// 百度风格
BaiduResponse<Order> bdResp = BaiduResponse.success(order);
BaiduResponse<Void> bdErr = BaiduResponse.error(404, "订单不存在");

// RFC 7807 Problem Details
ProblemDetail problem = ProblemDetail.badRequest("用户名不能为空");
ProblemDetail notFound = ProblemDetail.notFound("用户 ID=123 不存在");

// JSON:API
JsonApiResponse<User> jsonApi = JsonApiResponse.ofResource(resourceObject);
JsonApiResponse<Void> jsonApiErr = JsonApiResponse.ofErrors(errorList);

// GraphQL
GraphQLResponse<Map<String, Object>> gqlResp = GraphQLResponse.success(dataMap);
GraphQLResponse<Void> gqlErr = GraphQLResponse.error("查询语法错误");

// gRPC
GrpcResponse<ServiceData> grpcResp = GrpcResponse.success(serviceData);
GrpcResponse<Void> grpcErr = GrpcResponse.error(GrpcResponse.STATUS_NOT_FOUND, "服务未找到");
```

### 4. HTTP 客户端响应包装 (`http`)

提供对各种 HTTP 客户端响应的统一包装，便于转换和处理。

**核心类**：
- `JdkHttpResponseWrapper<T>` - JDK HttpClient 响应包装
- `OkHttpResponseWrapper<T>` - OkHttp 响应包装
- `ApacheHttpResponseWrapper<T>` - Apache HttpClient 响应包装
- `SpringResponseEntityWrapper<T>` - Spring ResponseEntity 包装
- `WebClientResponseWrapper<T>` - Spring WebClient 响应包装
- `FeignResponseWrapper<T>` - Feign Client 响应包装

```java
// 从 JDK HttpResponse 创建包装
JdkHttpResponseWrapper<User> wrapper = JdkHttpResponseWrapper.from(httpResponse, user);

// 从 OkHttp Response 创建包装
OkHttpResponseWrapper<Product> okWrapper = OkHttpResponseWrapper.from(okHttpResponse, product);

// 判断是否成功
if (wrapper.isSuccess()) {
    User user = wrapper.getBody();
}
```

### 5. 框架特定响应 (`framework`)

包含各种框架特定的响应格式定义。

**核心类**：
- `ActuatorHealthResponse` - Spring Boot Actuator 健康检查
- `OAuth2TokenResponse` - OAuth2 Token 响应
- `SpringDataRestResponse<T>` - Spring Data REST 响应
- `MyBatisPlusPageResponse<T>` - MyBatis-Plus 分页适配器
- `PageHelperPageResponse<T>` - PageHelper 分页适配器

```java
// 创建健康检查响应
ActuatorHealthResponse health = ActuatorHealthResponse.up();

// 创建 OAuth2 Token 响应
OAuth2TokenResponse token = OAuth2TokenResponse.success(accessToken, "Bearer", 3600L);

// 从 MyBatis-Plus IPage 转换
MyBatisPlusPageResponse<User> pageResp = MyBatisPlusPageResponse.from(page);
DataListInfo dataList = pageResp.toDataListInfo();
```

### 6. 消息队列响应 (`mq`)

包含各种消息中间件的响应格式定义。

**核心类**：
- `RabbitMqRpcResponse<T>` - RabbitMQ RPC 响应
- `KafkaConsumerRecordResponse<K, V>` - Kafka Consumer Record
- `RocketMqResponse<T>` - RocketMQ 响应

```java
// 创建 RabbitMQ RPC 响应
RabbitMqRpcResponse<Order> rpcResp = RabbitMqRpcResponse.of(correlationId, replyTo, order);

// 从 Kafka ConsumerRecord 创建
KafkaConsumerRecordResponse<String, String> kafkaResp = KafkaConsumerRecordResponse.from(record);
```

### 7. 数据库/缓存响应 (`database`)

包含各种数据库和缓存系统的响应格式定义。

**核心类**：
- `RedisResponse<T>` - Redis 响应
- `ElasticsearchResponse<T>` - Elasticsearch 响应
- `MongoDbResponse<T>` - MongoDB 响应

```java
// 创建 Redis GET 响应
RedisResponse<String> redisResp = RedisResponse.get("user:123", "张三");

// 创建 Elasticsearch 搜索响应
ElasticsearchResponse<Product> esResp = ElasticsearchResponse.search(totalHits, maxScore, hits, took);

// 创建 MongoDB 查询响应
MongoDbResponse<User> mongoResp = MongoDbResponse.find(results, totalCount);
```

### 8. 其他协议响应 (`protocol`)

包含各种通信协议的响应格式定义。

**核心类**：
- `WebSocketMessage<T>` - WebSocket 消息
- `SseEvent<T>` - Server-Sent Events (SSE)
- `XmlToJsonResponse<T>` - XML 转 JSON 响应

```java
// 创建 WebSocket 文本消息
WebSocketMessage<String> wsMsg = WebSocketMessage.text("Hello, World!");

// 创建 SSE 事件
SseEvent<Map<String, Object>> sseEvent = SseEvent.message(dataMap);
String sseString = sseEvent.toSseString();

// 创建 XML 转 JSON 响应
XmlToJsonResponse<Document> xmlResp = XmlToJsonResponse.of("document", document);
```

### 9. 响应式响应结构 (`reactive`)

提供基于 Project Reactor 的响应式响应格式定义，适用于 Spring WebFlux 等响应式框架。

**核心类**：
- `ReactiveAjaxResult<T>` - 响应式 AJAX 结果（使用 Mono）
- `ReactiveCollectionResult<T>` - 响应式集合结果（使用 Flux）
- `ReactivePageResult<T>` - 响应式分页结果（使用 Flux + Mono）

```java
// 创建响应式 AJAX 结果
Mono<User> userMono = userService.findById(123);
ReactiveAjaxResult<User> reactiveResult = ReactiveAjaxResult.success(userMono);

// 转换为 Mono<AjaxResult>
Mono<AjaxResult> ajaxMono = reactiveResult.toMono();

// 创建响应式集合结果
Flux<Product> productFlux = productService.findAll();
ReactiveCollectionResult<Product> collectionResult = ReactiveCollectionResult.success(productFlux, 100L);

// 创建响应式分页结果
Flux<Order> orderFlux = orderService.findByPage(1, 10);
Mono<Long> totalMono = orderService.count();
ReactivePageResult<Order> pageResult = ReactivePageResult.success(orderFlux, totalMono, 1, 10);

// 判断是否有下一页
Mono<Boolean> hasNext = pageResult.hasNext();
```

### 10. 通用/动态响应结构 (`generic`) ✨ 新增

提供非强类型的响应结构，适用于动态数据、遗留系统对接等场景。

**核心类**：
- `JsonResponse` - JSON 字符串响应包装器
- `MapResponse` - Map 形式响应包装器

```java
// 1. JSON 字符串响应
String json = "{\"id\":1,\"name\":\"张三\"}";
JsonResponse jsonResponse = JsonResponse.success(json);
JsonResponse error = JsonResponse.error("500", "服务器错误");

// 2. Map 形式响应
Map<String, Object> data = new HashMap<>();
data.put("userId", 123L);
data.put("userName", "张三");
data.put("roles", Arrays.asList("ADMIN", "USER"));
MapResponse mapResponse = MapResponse.success(data);

// 3. 链式构建 Map 响应
MapResponse response = MapResponse.builder()
    .put("status", "active")
    .put("count", 10)
    .put("items", itemList)
    .build();

// 4. 动态添加数据
mapResponse.put("newField", "newValue")
           .put("timestamp", System.currentTimeMillis());

// 5. 获取数据
Long userId = mapResponse.get("userId");
String status = mapResponse.getOrDefault("status", "inactive");
```

**适用场景**：
- 遗留系统对接（已序列化的 JSON 字符串）
- 动态字段响应（字段不固定）
- 配置信息返回
- NoSQL 数据库查询结果（MongoDB、Redis Hash）
- 第三方 API 透传

**注意事项**：
- ⚠️ 牺牲了类型安全性以换取灵活性
- ⚠️ 建议在系统边界处转换为强类型响应（如 AjaxResult）
- ⚠️ 避免在核心业务逻辑中过度使用

### 11. 通用响应数据结构 (`common`) ✨ 重构

提供可被各种响应结构复用的通用数据组件，如分页信息、错误详情等。

**核心类**：
- `DataListInfo` - 传统 Offset/Limit 分页数据列表
- `PageData<T>` - 通用分页数据包装器（支持 MyBatis-Plus/PageHelper 转换）
- `CursorPageData<T>` - 游标分页数据包装器（无限滚动、Feed 流）
- `KeysetPageData<T>` - 键集分页数据包装器（高性能深分页）

```java
// 1. DataListInfo - 传统分页
DataListInfo pageInfo = DataListInfo.of(100, 10, dataList);
DataListInfo listOnly = DataListInfo.of(dataList);
DataListInfo empty = DataListInfo.empty();

// 2. PageData - 通用分页（可与任何响应组合）
PageData<User> pageData = PageData.of(users, 100, 1, 10);
AjaxResult<PageData<User>> response = AjaxResult.success(pageData);
Result<PageData<Order>> result = Result.success(PageData.of(orders, total, pageNum, pageSize));

// 从 MyBatis-Plus IPage 转换
IPage<User> iPage = userService.page(query);
PageData<User> fromMp = PageData.from(iPage);

// 从 PageHelper PageInfo 转换
PageInfo<Order> pageInfo = new PageInfo<>(orderList);
PageData<Order> fromPh = PageData.from(pageInfo);

// 3. CursorPageData - 游标分页（无限滚动）
CursorPageData<Post> cursorPage = CursorPageData.of(posts, nextCursor, hasMore, 10);
AjaxResult<CursorPageData<Post>> feedResp = AjaxResult.success(cursorPage);

// 4. KeysetPageData - 键集分页（高性能）
KeysetPageData<Order> keysetPage = KeysetPageData.of(orders, lastOrderId, hasMore, 10);
Result<KeysetPageData<Order>> orderResp = Result.success(keysetPage);
```

**设计说明**：
- ✅ 与 `generic` 包形成互补（common=强类型通用，generic=弱类型动态）
- ✅ 作为响应体系的组成部分，而非独立的模型层
- ✅ **可与任何响应类型组合使用**：`AjaxResult<PageData<User>>`、`Result<CursorPageData<Post>>`
- ✅ 支持多种分页策略：Offset/Limit、游标、键集
- ✅ 提供框架适配器：MyBatis-Plus IPage、PageHelper PageInfo

### 12. 自定义响应结构 (`custom`) ✨ 新增

提供扩展自定义返回格式的完整示例和工具，让你能够灵活适配各种业务场景。

**核心类**：
- `CustomResponse<T>` - 完整的自定义响应结构示例（包含状态码、消息、数据、元数据等）
- `CustomResponseConverter` - 自定义响应与其他格式的互转转换器

```java
// 1. 创建自定义响应
CustomResponse<User> response = CustomResponse.success(user);
CustomResponse<Order> error = CustomResponse.error(404, "订单不存在");

// 2. 链式构建
CustomResponse<Product> custom = CustomResponse.<Product>builder()
    .code(200)
    .message("查询成功")
    .data(product)
    .traceId("abc-123")
    .metadata("requestId", "req-456")
    .build();

// 3. 转换为其他格式
AjaxResult ajax = CustomResponseConverter.toAjaxResult(response);
Result<User> result = CustomResponseConverter.toResult(response);

// 4. 注册到工厂（应用启动时一次性调用）
CustomResponseConverter.registerAll();

// 5. 通过工厂统一转换
CustomResponse<Order> custom = ResponseConverterFactory.convert(ajaxResult, CustomResponse.class);
```

**支持的转换方向**：
- AjaxResult ↔ CustomResponse
- Result ↔ CustomResponse
- AliResponse ↔ CustomResponse
- TencentResponse ↔ CustomResponse

**适用场景**：
- 对接遗留系统（特殊响应格式）
- 微服务间不同规范转换
- 需要额外元数据（traceId、timestamp、serverInfo）
- 第三方API对接（微信、支付宝等）

📖 **详细扩展指南请查看**：[CUSTOM_RESPONSE_GUIDE.md](./docs/CUSTOM_RESPONSE_GUIDE.md)

## 依赖

```xml
<dependency>
    <groupId>cc.hongz</groupId>
    <artifactId>hongz-ztools-response</artifactId>
</dependency>
```

传递依赖 `hongz-ztools-core` 和 `lombok`。可选依赖 `spring-webmvc`（响应拦截器）、`spring-context`（国际化与异常处理）。

## Spring 集成与响应拦截器

### 自动配置

在 Spring 应用中引入配置类（Phase 4 将提供独立 Starter）：

```java
@Import(cc.hongz.ztools.response.config.ResponseAutoConfiguration.class)
@SpringBootApplication
public class Application { }
```

注册 Bean：`AjaxStatusRegistry`、`I18nResponseBuilder`、`GlobalExceptionHandler`、`ResponseInterceptor` 及内置转换策略。

**国际化键值映射**：业务状态码通过 `getMessageKey()`（如 `COMMON_404` → `common.not.found`）解析 `messages_zh_CN.properties` / `messages_en_US.properties`；自定义枚举可声明 messageKey，或启动时 `AjaxStatusMessageKeys.register(code, key)`。

### 响应拦截器（Phase 3）

Controller 可直接返回 POJO，拦截器自动包装为 `AjaxResult`：

```java
@GetMapping("/{id}")
public User getUser(@PathVariable Long id) {
    return userService.findById(id); // → AjaxResult.success(user)
}

@GetMapping("/download/{file}")
@SkipResponseIntercept("文件下载")
public ResponseEntity<Resource> download(@PathVariable String file) {
    return ResponseEntity.ok(resource);
}
```

配置项（`InterceptorProperties`，前缀 `hongz.response.interceptor`）：

| 属性 | 默认 | 说明 |
|------|------|------|
| `enabled` | `true` | 是否启用拦截器 |
| `responseStyle` | `AJAX_RESULT` | 目标格式（与 `CUSTOM` 互斥） |
| `strictMode` | `false` | 严格模式：禁止非目标格式 |
| `defaultAction` | `WRAP` | 无匹配策略时：`WRAP` / `PASS` / `THROW` |

📖 演进路线详见 [docs/ROADMAP.md](./docs/ROADMAP.md)、任务清单 [docs/TASK_CHECKLIST.md](./docs/TASK_CHECKLIST.md)

## 构建与测试

```bat
mvn -pl hongz-ztools/hongz-ztools-response -am test
```

## 设计思路

### 为什么要区分不同的响应结构？

1. **场景适配**：Web API 需要 JSON 友好的结构（AjaxResult），内部服务调用需要类型安全（Result<T>）
2. **历史兼容**：保留对既有项目（如 IVICS）响应结构的兼容
3. **扩展性**：新增响应风格只需添加新子包，不影响现有代码
4. **关注点分离**：每种风格独立演进，避免单一巨型类

### 如何选择？

| 场景 | 推荐 | 理由 |
|------|------|------|
| RESTful Web API | `AjaxResult` | JSON 友好、前端易解析、可作为归一化标准 |
| 内部服务调用 | `Result<T>` | 类型安全、语义清晰 |
| 日志/监控/网关 | `AjaxResult` | 统一使用 AjaxResult 作为归一化标准 |
| **传统分页查询** | `AjaxResult<PageData<T>>` | 标准化分页结构，支持 MyBatis-Plus/PageHelper |
| **无限滚动/Feed 流** | `AjaxResult<CursorPageData<T>>` | 游标分页，性能优异，适合移动端 |
| **大数据集深分页** | `Result<KeysetPageData<T>>` | 键集分页，避免 OFFSET 性能问题 |
| 阿里系项目 | `AliResponse` | 符合阿里巴巴内部规范 |
| 腾讯系项目 | `TencentResponse` | 符合腾讯内部规范 |
| 百度系项目 | `BaiduResponse` | 符合百度内部规范 |
| HTTP API 错误 | `ProblemDetail` | RFC 7807 标准，业界广泛采用 |
| REST 标准 API | `JsonApiResponse` | JSON:API 规范，适合复杂资源关系 |
| GraphQL API | `GraphQLResponse` | GraphQL 官方标准格式 |
| gRPC 转 REST | `GrpcResponse` | gRPC JSON Transcoding 标准 |
| 动态字段/配置 | `MapResponse` | 字段不固定、NoSQL 查询结果 |
| JSON 字符串透传 | `JsonResponse` | 遗留系统对接、第三方 API 透传 |

## 迁移指南

### 从 IVICS AjaxResult 迁移

原 IVICS 项目中的 `com.gientech.ivics.common.response.AjaxResult` 已迁移至本模块：

```java
// 旧代码
import com.gientech.ivics.common.response.AjaxResult;

// 新代码
import cc.hongz.ztools.response.ajax.AjaxResult;
```

**注意**：新版本移除了对 `HttpStatusEnum` 和 `I18nUtil` 的硬依赖，改为更通用的实现。如需国际化支持，可在业务层自行处理。

## 未来规划

### 🚀 演进路线图（重点规划）

详细的 HTTP 状态码、业务状态码和国际化异常集成规划请参考：

👉 **[ROADMAP.md](./docs/ROADMAP.md)** - 完整演进路线图（含详细设计和代码示例）  
👉 **[ROADMAP_SUMMARY.md](./docs/ROADMAP_SUMMARY.md)** - 规划摘要（快速了解）

**三大核心规划**：
1. ✅ **Phase 1 (1-2个月)**: HTTP 状态码标准化 + 业务状态码体系
2. ✅ **Phase 2 (2-3个月)**: 国际化异常集成
3. ✅ **Phase 3 (3-6个月)**: Spring Boot Starter + 社区贡献

### ✅ 已完成
- [x] 添加更多响应风格（如 GraphQL、gRPC 等）✅ 已完成
- [x] 提供 HTTP 客户端响应包装类 ✅ 已完成
- [x] 添加框架特定响应（Spring Boot Actuator、OAuth2、Data REST、MyBatis-Plus/PageHelper 分页）✅ 已完成
- [x] 添加消息队列响应（RabbitMQ、Kafka、RocketMQ）✅ 已完成
- [x] 添加数据库/缓存响应（Redis、Elasticsearch、MongoDB）✅ 已完成
- [x] 添加其他协议响应（WebSocket、SSE、XML 转 JSON）✅ 已完成
- [x] 添加通用/动态响应结构（JSON 字符串、Map 形式）✅ 已完成 - `generic` 包
- [x] **提供自定义响应结构扩展支持** ✅ 已完成 - `custom` 包 + 详细扩展指南
- [x] 提供响应结构转换器（Adapter）✅ 已完成 - `ResponseConverter`
- [x] 提供统一入口工厂类 ✅ 已完成 - `ResponseFactory`
- [x] 重构 model 包为 common 包 ✅ 已完成

### 📋 待实现（按优先级排序）

#### ⭐⭐⭐⭐⭐ 高优先级（非常常用）
- [ ] **ValidationResponse** - 表单验证结果响应（字段级错误列表）
- [ ] **BatchResponse<T>** - 批量操作结果响应（成功数、失败数、失败详情）
- [ ] **FileResponse** - 文件下载响应（文件名、大小、MIME 类型、流）

#### ⭐⭐⭐⭐ 中优先级（特定场景常用）
- [ ] **AsyncTaskResponse** - 异步任务提交响应（taskId、状态、轮询地址）
- [ ] **SearchResultResponse<T>** - 搜索结果响应（高亮、建议词、分面统计）
- [ ] **AggregateResponse** - 聚合查询结果响应（分组统计、指标）
- [ ] **StreamResponse** - 流式数据响应（大文件、视频流）
- [ ] **ChunkedResponse** - 分块传输响应

#### 📄 分页方式扩展规划

**当前已支持：**
- ✅ MyBatis-Plus IPage → `MyBatisPlusPageResponse`
- ✅ PageHelper PageInfo → `PageHelperPageResponse`
- ✅ 通用分页模型 → `DataListInfo`（common 包）

**待实现（按使用频率排序）：**

##### ⭐⭐⭐⭐⭐ 高优先级
- [ ] **SpringDataPageResponse<T>** - Spring Data Page<T> 适配器（JPA/MongoDB）
- [ ] **SpringDataSliceResponse<T>** - Spring Data Slice<T> 适配器（无总数统计，性能更好）
- [ ] **CursorPageResponse<T>** - 游标分页响应（基于时间戳/ID，适用于无限滚动）
- [ ] **KeysetPageResponse<T>** - 键集分页响应（基于唯一键，避免深分页问题）

##### ⭐⭐⭐⭐ 中优先级
- [ ] **ElasticsearchPageResponse<T>** - ES SearchHits 分页适配器（含 from/size/search_after）
- [ ] **MongoDbPageResponse<T>** - MongoDB FindIterable 分页适配器
- [ ] **RedisZSetPageResponse<T>** - Redis ZSET 分页响应（ZRANGE）
- [ ] **JsonApiPageResponse<T>** - JSON:API 标准分页（page[number]/page[size]）
- [ ] **ODataPageResponse<T>** - OData 标准分页（$skip/$top）
- [ ] **GraphQLRelayConnectionResponse<T>** - GraphQL Relay 连接规范（first/after, last/before）

##### ⭐⭐⭐ 低优先级
- [ ] **ReactivePageResponse<T>** - 响应式分页响应（Flux + 分页元数据）
- [ ] **MemoryPageResponse<T>** - 内存分页响应（List.subList）
- [ ] **OffsetLimitPageResponse<T>** - 传统 Offset/Limit 分页响应
- [ ] **CassandraPageResponse<T>** - Cassandra PagingState 分页
- [ ] **SolrCursorPageResponse<T>** - Solr 游标分页
- [ ] **CustomPageResponse<T>** - 自定义分页响应（支持多种分页策略）

**分页方式选择指南：**

| 场景 | 推荐方案 | 理由 |
|------|---------|------|
| 后台管理系统 | Offset/Limit 或 Spring Data Page | 数据量小，需要跳页 |
| 移动端 Feed 流 | 游标分页或键集分页 | 无限滚动，性能要求高 |
| 电商订单列表 | 键集分页 | 数据量大，需要稳定性能 |
| 搜索结果 | ES search_after | 深度分页，结果集大 |
| 排行榜 | Redis ZRANGE | 实时排序，性能极高 |
| 日志查看 | 游标分页（时间戳） | 实时数据，只向前翻 |
| 数据导出 | 键集分页 | 遍历全表，性能稳定 |
| API 对外服务 | JSON:API 或 GraphQL Relay | 符合标准，易于集成 |

⚠️ **重要提示**：
- OFFSET 深分页性能极差（OFFSET 1000000 需扫描 100 万行）
- 大数据集优先使用键集分页或游标分页
- 不需要总数时使用 Slice 而非 Page，性能更好
- 分页必须配合索引，否则全表扫描

#### ⭐⭐⭐ 低优先级（可选扩展）
- [ ] **DetailedHealthResponse** - 详细健康检查响应（各组件状态、依赖服务）
- [ ] **ApiVersionResponse** - API 版本信息响应
- [ ] **MetadataResponse** - 资源元数据响应（创建时间、更新时间、版本号）
- [ ] **I18nResponse** - 带多语言消息的响应
- [ ] **CaptchaResponse** - 验证码响应
- [ ] **TwoFactorAuthResponse** - 双因素认证响应
- [ ] **PermissionCheckResponse** - 权限校验响应
- [ ] **FacetResponse** - 分面搜索响应
- [ ] **SuggestionResponse** - 搜索建议/自动补全响应
- [ ] **DashboardResponse** - 仪表盘数据响应（多指标聚合）
- [ ] **StatisticsResponse** - 统计数据响应
- [ ] **FormSubmissionResponse** - 表单提交响应
- [ ] **JobResponse** - 后台作业响应
- [ ] **ReadinessResponse / LivenessResponse** - K8s 探针响应

### 🔧 功能增强
- [ ] 集成 Bean Validation 注解
- [ ] 提供 Spring Boot Starter 自动配置
- [ ] 添加响应序列化/反序列化工具类
- [ ] 提供响应校验器（Validator）
- [ ] 支持响应压缩（GZIP）
- [ ] 添加响应缓存机制

## 🎨 设计模式

本模块的响应转换功能采用了多种经典设计模式，构建了一个灵活、可扩展的转换架构。

### 核心设计模式概览

| 模式 | 用途 | 代表类/接口 | 使用场景 |
|------|------|------------|----------|
| **适配器模式** | 将不同格式转换为统一格式 | `ResponseConverter.toAjaxResult()` | 异构系统间数据交换 |
| **策略模式** | 定义可互换的转换算法族 | `ResponseConversionStrategy<S,T>` | 运行时切换转换逻辑 |
| **工厂方法模式** | 根据类型动态创建策略 | `ResponseConverterFactory.getStrategy()` | 解耦对象创建和使用 |
| **建造者模式** | 分步构建复杂转换链 | `ConversionChainBuilder` | 多步骤转换流程 |
| **责任链模式** | 依次执行多个转换步骤 | `ConversionChainBuilder.execute()` | 链式数据处理 |
| **门面模式** | 提供统一的转换入口 | `ResponseConverter` | 简化客户端调用 |
| **注册表模式** | 集中管理所有转换策略 | `ResponseConverterFactory.STRATEGIES` | 动态策略管理 |

### 快速示例

#### 1. 适配器模式 - 基础转换

```java
// 将 Result 适配为 AjaxResult
Result<User> result = Result.success(user);
AjaxResult ajax = ResponseConverter.toAjaxResult(result);
```

#### 2. 策略模式 - 自定义转换算法

```java
// 定义自定义策略
public class CustomStrategy implements ResponseConversionStrategy<Result<User>, AjaxResult> {
    @Override
    public AjaxResult convert(Result<User> source) {
        return AjaxResult.success("CUSTOM_" + source.getCode(), source.getMessage(), source.getData());
    }
}

// 使用策略
AjaxResult ajax = ResponseConverter.convert(result, new CustomStrategy());

// 策略组合
ResponseConversionStrategy<Result<User>, String> combined = 
    new CustomStrategy().andThen(ajax -> JSON.toJSONString(ajax));
String json = ResponseConverter.convert(result, combined);
```

#### 3. 建造者 + 责任链 - 链式转换

```java
// 多步骤转换
AjaxResult result = ResponseConverter.chain()
    .from(baiduResponse)
    .transform(ResponseConverter::baiduToTencent)  // 第1步：百度→腾讯
    .transform(ResponseConverter::toAjaxResult)    // 第2步：腾讯→AJAX
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

#### 4. 工厂方法 + 注册表 - 动态策略管理

```java
// 注册自定义策略（应用启动时）
ResponseConverterFactory.register(
    CustomRequest.class,
    CustomResponse.class,
    new CustomConversionStrategy()
);

// 获取并使用策略
ResponseConversionStrategy<CustomRequest, CustomResponse> strategy = 
    ResponseConverterFactory.getStrategy(CustomRequest.class, CustomResponse.class);
CustomResponse response = strategy.convert(request);

// 直接通过工厂转换
AjaxResult ajax = ResponseConverterFactory.convert(anyResponse, AjaxResult.class);
```

### 架构图

```
┌─────────────────────────────────────────────────────────┐
│              开发者友好的 API 层                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ResponseFactory          ResponseConverter             │
│  (创建响应)                (转换响应 - 门面模式)          │
│                                                         │
└─────────────────────────────────────────────────────────┘
                        ↓
        ┌───────────────┴───────────────┐
        ↓                               ↓
┌──────────────────┐         ┌──────────────────┐
│ Strategy Pattern │         │ Builder Pattern  │
│                  │         │                  │
│ ResponseConver-  │         │ ConversionChain- │
│ sionStrategy<S,T>│         │ Builder          │
└──────────────────┘         └──────────────────┘
        ↑                             ↓
        │                    ┌──────────────────┐
        │                    │ Chain of Resp.   │
        │                    │ [Func1→Func2→...]│
        │                    └──────────────────┘
        │
┌──────────────────────────────────────────────┐
│      Factory + Registry Pattern              │
│                                              │
│  ResponseConverterFactory                    │
│  • STRATEGIES Map                            │
│  • register() / getStrategy()                │
│  • convert()                                 │
└──────────────────────────────────────────────┘
```

📖 **详细设计模式说明请查看**：[DESIGN_PATTERNS.md](./DESIGN_PATTERNS.md)

## 相关文档

- 🚀 **[快速参考指南](docs/QUICK_REFERENCE.md)** - 响应结构速查表、常用场景示例、转换功能说明
- 🔧 **[自定义响应扩展指南](docs/CUSTOM_RESPONSE_GUIDE.md)** - 详细的自定义返回结构扩展教程，包含4种扩展方式、完整示例和Spring Boot集成
- 📖 **[响应结构 JSON 格式大全](docs/RESPONSE_FORMATS.md)** - 详细列举了 Java 生态中所有常见的响应结构 JSON 格式，包括：
  - 自定义业务响应（AjaxResult、Result 等）
  - HTTP 客户端响应（JDK HttpClient、OkHttp、Apache HttpClient、Spring ResponseEntity 等）
  - 标准协议响应（RFC 7807、JSON:API、GraphQL、gRPC 等）
  - 框架特定响应（Spring Boot Actuator、OAuth2、MyBatis-Plus 分页等）
  - 消息队列响应（RabbitMQ、Kafka、RocketMQ 等）
  - 数据库/缓存响应（Redis、Elasticsearch、MongoDB 等）
- 📋 **[实现状态清单](docs/IMPLEMENTATION_STATUS.md)** - 文档与代码的对比，查看所有已实现的响应格式
- 🎨 **[设计模式详解](docs/DESIGN_PATTERNS.md)** - 7 种设计模式的完整说明、代码示例和最佳实践
- 🔍 **[方案全面对比](docs/COMPARISON.md)** - Java 生态中所有主流响应结构和状态码管理方案的深度对比分析，包括：
  - Spring Framework 原生方案（ResponseEntity、ProblemDetail、ErrorResponse）
  - 开源响应包装库（R/Result、ApiResult、ApiResponse）
  - 业务状态码管理（枚举、接口、注解驱动）
  - 国际化异常处理（MessageSource、I18nException、第三方库）
  - hongz-ztools-response 优势分析和选型建议

## 归一化说明

本模块推荐使用 **`AjaxResult`** 作为统一的归一化响应结构。

### 为什么选择 AjaxResult 作为归一化标准？

1. **广泛使用**：已在多个项目中验证，前端解析友好
2. **结构简单**：`code` / `msg` / `data` 三字段清晰明了
3. **易于扩展**：可通过继承或包装适配不同场景
4. **兼容性好**：支持字符串状态码，便于国际化

### 如何使用 AjaxResult 进行归一化

```java
// 将其他响应转换为 AjaxResult
public AjaxResult normalize(Object response) {
    if (response instanceof Result<?> result) {
        return result.isSuccess() 
            ? AjaxResult.success(String.valueOf(result.getCode()), result.getMessage(), result.getData())
            : AjaxResult.error(String.valueOf(result.getCode()), result.getMessage(), result.getData());
    }
    // 其他类型转换...
    return AjaxResult.success(response);
}
```

## 变更记录

详见 [CHANGELOG.md](./CHANGELOG.md)。
