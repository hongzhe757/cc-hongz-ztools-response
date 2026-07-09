# 响应结构快速参考指南

本文档提供 `hongz-ztools-response` 模块的快速参考，帮助您快速找到和使用各种响应结构。

## 📚 目录

- [1. 统一入口](#1-统一入口)
- [2. 响应类型速查表](#2-响应类型速查表)
- [3. 常用场景示例](#3-常用场景示例)
- [4. 转换功能](#4-转换功能)
- [5. 包结构导航](#5-包结构导航)
- [6. 涉及的设计模式](#6-涉及的设计模式)

---

## 1. 统一入口

### ResponseFactory - 工厂类（推荐）

所有响应结构的创建都可以通过 `ResponseFactory` 完成，无需记住各个类的具体位置：

```java
import cc.hongz.ztools.response.ResponseFactory;

// ✅ AJAX 响应
AjaxResult ajax = ResponseFactory.ajaxSuccess(data);
AjaxResult error = ResponseFactory.ajaxError("操作失败");

// ✅ 原生 Result
Result<User> result = ResponseFactory.resultSuccess(user);
Result<Void> fail = ResponseFactory.resultFailure(500, "错误");

// ✅ 标准协议
ProblemDetail problem = ResponseFactory.problemBadRequest("参数无效");
GraphQLResponse<Data> graphql = ResponseFactory.graphqlSuccess(data);

// ✅ HTTP 客户端包装
JdkHttpResponseWrapper<User> wrapper = ResponseFactory.jdkHttpWrapper(response, user);

// ✅ 框架集成
MyBatisPlusPageResponse<Order> page = ResponseFactory.mybatisPlusPage(iPage);
OAuth2TokenResponse token = ResponseFactory.oauth2Token(accessToken, "Bearer", 3600L);

// ✅ 消息队列
RabbitMqRpcResponse<Result> rpc = ResponseFactory.rabbitmqRpc(correlationId, replyTo, data);

// ✅ 数据库/缓存
RedisResponse<String> redis = ResponseFactory.redisGet(key, value);
ElasticsearchResponse<Doc> es = ResponseFactory.elasticsearchSearch(totalHits, maxScore, hits, took);

// ✅ 其他协议
WebSocketMessage<ChatMsg> ws = ResponseFactory.websocketText(message);
SseEvent<Notification> sse = ResponseFactory.sseMessage(notification);

// ✅ 响应式
ReactiveAjaxResult<User> reactive = ResponseFactory.reactiveAjax(mono);
ReactiveCollectionResult<Item> collection = ResponseFactory.reactiveCollection(flux);
ReactivePageResult<Record> page = ResponseFactory.reactivePage(records, total, current, size);
```

### ResponseConverter - 转换器

在不同响应结构之间进行转换：

```java
import cc.hongz.ztools.response.ResponseConverter;

// ✅ 转换为 AjaxResult（归一化标准）
AjaxResult ajax = ResponseConverter.toAjaxResult(result);
AjaxResult fromAli = ResponseConverter.toAjaxResult(aliResponse);

// ✅ 厂商风格互转
TencentResponse<Order> txResp = ResponseConverter.aliToTencent(aliResp);
AliResponse<Order> aliResp = ResponseConverter.tencentToAli(txResp);

// ✅ 分页数据转换
DataListInfo pageInfo = ResponseConverter.toDataListInfo(mybatisPageResponse);

// ✅ 通用判断和提取
boolean success = ResponseConverter.isSuccess(anyResponse);
User data = ResponseConverter.extractData(anyResponse);
```

---

## 2. 响应类型速查表

| 分类 | 类名 | 用途 | 创建方式 |
|------|------|------|----------|
| **AJAX** | `AjaxResult` | 前后端分离项目（推荐） | `ResponseFactory.ajaxSuccess()` |
| **Native** | `Result<T>` | Java 服务间调用 | `ResponseFactory.resultSuccess()` |
| **Standard** | `AliResponse` | 阿里巴巴风格 API | `ResponseFactory.aliSuccess()` |
| | `TencentResponse` | 腾讯风格 API | `ResponseFactory.tencentSuccess()` |
| | `BaiduResponse` | 百度风格 API | `ResponseFactory.baiduSuccess()` |
| | `ProblemDetail` | RFC 7807 错误详情 | `ResponseFactory.problemBadRequest()` |
| | `JsonApiResponse` | JSON:API 规范 | `new JsonApiResponse()` |
| | `GraphQLResponse` | GraphQL 响应 | `ResponseFactory.graphqlSuccess()` |
| | `GrpcResponse` | gRPC JSON Transcoding | `ResponseFactory.grpcSuccess()` |
| **HTTP** | `JdkHttpResponseWrapper` | JDK HttpClient | `ResponseFactory.jdkHttpWrapper()` |
| | `OkHttpResponseWrapper` | OkHttp | `ResponseFactory.okHttpWrapper()` |
| | `ApacheHttpResponseWrapper` | Apache HttpClient | `new ApacheHttpResponseWrapper()` |
| | `SpringResponseEntityWrapper` | Spring ResponseEntity | `ResponseFactory.springResponseWrapper()` |
| | `WebClientResponseWrapper` | Spring WebClient | `new WebClientResponseWrapper()` |
| | `FeignResponseWrapper` | Feign Client | `new FeignResponseWrapper()` |
| **Framework** | `ActuatorHealthResponse` | Spring Boot Actuator | `ResponseFactory.actuatorUp()` |
| | `OAuth2TokenResponse` | OAuth2 Token | `ResponseFactory.oauth2Token()` |
| | `SpringDataRestResponse` | Spring Data REST | `new SpringDataRestResponse()` |
| | `MyBatisPlusPageResponse` | MyBatis-Plus 分页 | `ResponseFactory.mybatisPlusPage()` |
| | `PageHelperPageResponse` | PageHelper 分页 | `ResponseFactory.pageHelperPage()` |
| **MQ** | `RabbitMqRpcResponse` | RabbitMQ RPC | `ResponseFactory.rabbitmqRpc()` |
| | `KafkaConsumerRecordResponse` | Kafka Consumer | `ResponseFactory.kafkaRecord()` |
| | `RocketMqResponse` | RocketMQ | `new RocketMqResponse()` |
| **Database** | `RedisResponse` | Redis 操作 | `ResponseFactory.redisGet()` |
| | `ElasticsearchResponse` | ES 搜索 | `ResponseFactory.elasticsearchSearch()` |
| | `MongoDbResponse` | MongoDB 查询 | `ResponseFactory.mongodbFind()` |
| **Protocol** | `WebSocketMessage` | WebSocket 消息 | `ResponseFactory.websocketText()` |
| | `SseEvent` | Server-Sent Events | `ResponseFactory.sseMessage()` |
| | `XmlToJsonResponse` | XML 转 JSON | `ResponseFactory.xmlToJson()` |
| **Reactive** | `ReactiveAjaxResult` | 响应式 AJAX (Mono) | `ResponseFactory.reactiveAjax()` |
| | `ReactiveCollectionResult` | 响应式集合 (Flux) | `ResponseFactory.reactiveCollection()` |
| | `ReactivePageResult` | 响应式分页 | `ResponseFactory.reactivePage()` |
| **Model** | `DataListInfo` | 分页数据模型 | `ResponseFactory.dataListInfo()` |

---

## 3. 常用场景示例

### 场景 1：REST API 返回用户信息

```java
@GetMapping("/users/{id}")
public AjaxResult getUser(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseFactory.ajaxSuccess(user);
}
```

### 场景 2：微服务间调用返回结果

```java
@FeignClient(name = "order-service")
public interface OrderClient {
    @GetMapping("/orders/{id}")
    Result<Order> getOrder(@PathVariable Long id);
}

// 使用时
Result<Order> result = orderClient.getOrder(orderId);
if (result.isSuccess()) {
    Order order = result.getData();
    // 处理订单
}
```

### 场景 3：对接第三方阿里风格 API

```java
// 接收阿里风格响应
AliResponse<Product> response = thirdPartyApi.getProduct(productId);

// 转换为内部使用的 AjaxResult
AjaxResult ajax = ResponseConverter.toAjaxResult(response);
return ajax;
```

### 场景 4：MyBatis-Plus 分页查询

```java
@GetMapping("/products")
public AjaxResult listProducts(PageParam param) {
    IPage<Product> page = productService.page(param);
    
    // 转换为统一的分页格式
    MyBatisPlusPageResponse<Product> response = ResponseFactory.mybatisPlusPage(page);
    DataListInfo pageInfo = ResponseConverter.toDataListInfo(response);
    
    return ResponseFactory.ajaxSuccess(pageInfo);
}
```

### 场景 5：Spring WebFlux 响应式编程

```java
@GetMapping("/users/reactive")
public Mono<AjaxResult> getUsersReactive() {
    Flux<User> users = userService.findAllReactive();
    Mono<Long> total = userService.countReactive();
    
    ReactivePageResult<User> result = ResponseFactory.reactivePage(users, total, 1, 10);
    
    // 转换为 Mono<AjaxResult>
    return result.toMono();
}
```

### 场景 6：WebSocket 实时消息推送

```java
@ServerEndpoint("/chat/{userId}")
public class ChatEndpoint {
    @OnMessage
    public void onMessage(String message, Session session) {
        ChatMsg msg = parseMessage(message);
        
        // 创建 WebSocket 消息
        WebSocketMessage<ChatMsg> wsMsg = ResponseFactory.websocketText(msg);
        
        // 发送给客户端
        session.getAsyncRemote().sendText(JSON.toJSONString(wsMsg));
    }
}
```

### 场景 7：OAuth2 认证返回 Token

```java
@PostMapping("/oauth/token")
public OAuth2TokenResponse login(@RequestBody LoginRequest request) {
    // 验证用户名密码
    UserDetails user = authenticate(request);
    
    // 生成 JWT Token
    String accessToken = jwtGenerator.generate(user);
    
    // 返回 OAuth2 标准格式
    return ResponseFactory.oauth2Token(accessToken, "Bearer", 3600L);
}
```

### 场景 8：Elasticsearch 搜索结果

```java
@GetMapping("/search")
public ElasticsearchResponse<Article> search(@RequestParam String keyword) {
    SearchResponse<Article> response = elasticsearchService.search(keyword);
    
    List<ElasticsearchResponse.Hit<Article>> hits = convertHits(response);
    
    return ResponseFactory.elasticsearchSearch(
        response.getTotalHits(),
        response.getMaxScore(),
        hits,
        response.getTook()
    );
}
```

---

## 4. 转换功能

### 支持的转换路径

```
┌─────────────────┐
│   AliResponse   │◄────► TencentResponse
└────────┬────────┘         ▲
         │                  │
         ▼                  │
┌─────────────────┐         │
│   AjaxResult    │◄────────┘
│  (归一化标准)    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Result<T>    │
└─────────────────┘
```

### 基础转换示例

```java
// 1. 任意响应 → AjaxResult
AjaxResult ajax = ResponseConverter.toAjaxResult(anyResponse);

// 2. 阿里 ↔ 腾讯
TencentResponse<Order> tx = ResponseConverter.aliToTencent(aliResp);
AliResponse<Order> ali = ResponseConverter.tencentToAli(txResp);

// 3. 分页响应 → DataListInfo
DataListInfo info = ResponseConverter.toDataListInfo(mybatisPage);

// 4. 通用判断
if (ResponseConverter.isSuccess(response)) {
    User data = ResponseConverter.extractData(response);
}
```

### 高级转换功能

#### 策略模式 - 自定义转换算法

```java
// 定义自定义策略
public class CustomStrategy implements ResponseConversionStrategy<Result<User>, AjaxResult> {
    @Override
    public AjaxResult convert(Result<User> source) {
        return AjaxResult.success("CUSTOM_" + source.getCode(), source.getMessage(), source.getData());
    }
}

// 使用策略
Result<User> result = Result.success(user);
AjaxResult ajax = ResponseConverter.convert(result, new CustomStrategy());

// 策略组合
ResponseConversionStrategy<Result<User>, String> combined = 
    new CustomStrategy().andThen(ajax -> JSON.toJSONString(ajax));
String json = ResponseConverter.convert(result, combined);
```

#### 建造者模式 - 链式转换

```java
// 多步骤转换
AjaxResult result = ResponseConverter.chain()
    .from(baiduResponse)
    .transform(ResponseConverter::baiduToTencent)  // 第1步
    .transform(ResponseConverter::toAjaxResult)    // 第2步
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

#### 工厂方法 + 注册表 - 动态策略管理

```java
// 注册自定义策略
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

---

## 5. 包结构导航

```
cc.hongz.ztools.response
├── ResponseFactory.java          ← 🔑 统一入口（推荐从这里开始）
├── ResponseConverter.java        ← 🔄 转换器
│
├── ajax/                         # AJAX 风格
│   └── AjaxResult                ← ⭐ 推荐用于归一化
│
├── native/                       # Java 原生
│   └── Result<T>                 ← 泛型通用返回
│
├── standard/                     # 标准协议
│   ├── AliResponse               # 阿里巴巴
│   ├── TencentResponse           # 腾讯
│   ├── BaiduResponse             # 百度
│   ├── ProblemDetail             # RFC 7807
│   ├── JsonApiResponse           # JSON:API
│   ├── GraphQLResponse           # GraphQL
│   └── GrpcResponse              # gRPC
│
├── http/                         # HTTP 客户端
│   ├── JdkHttpResponseWrapper    # JDK HttpClient
│   ├── OkHttpResponseWrapper     # OkHttp
│   ├── ApacheHttpResponseWrapper # Apache HttpClient
│   ├── SpringResponseEntityWrapper # Spring ResponseEntity
│   ├── WebClientResponseWrapper  # Spring WebClient
│   └── FeignResponseWrapper      # Feign
│
├── framework/                    # 框架集成
│   ├── ActuatorHealthResponse    # Spring Boot Actuator
│   ├── OAuth2TokenResponse       # OAuth2
│   ├── SpringDataRestResponse    # Spring Data REST
│   ├── MyBatisPlusPageResponse   # MyBatis-Plus 分页
│   └── PageHelperPageResponse    # PageHelper 分页
│
├── mq/                           # 消息队列
│   ├── RabbitMqRpcResponse       # RabbitMQ RPC
│   ├── KafkaConsumerRecordResponse # Kafka
│   └── RocketMqResponse          # RocketMQ
│
├── database/                     # 数据库/缓存
│   ├── RedisResponse             # Redis
│   ├── ElasticsearchResponse     # Elasticsearch
│   └── MongoDbResponse           # MongoDB
│
├── protocol/                     # 其他协议
│   ├── WebSocketMessage          # WebSocket
│   ├── SseEvent                  # SSE
│   └── XmlToJsonResponse         # XML→JSON
│
├── reactive/                     # ✨ 响应式
│   ├── ReactiveAjaxResult        # Mono 包装
│   ├── ReactiveCollectionResult  # Flux 包装
│   └── ReactivePageResult        # 响应式分页
│
└── model/                        # 数据模型
    └── DataListInfo              # 分页数据
```

---

## 💡 最佳实践

1. **优先使用 `ResponseFactory`**：不要直接 new 对象，使用工厂方法更简洁
2. **归一化到 `AjaxResult`**：在系统边界处将各种响应转换为统一的 AjaxResult
3. **善用转换器**：异构系统间通信时，使用 `ResponseConverter` 进行格式转换
4. **选择合适的响应类型**：根据使用场景选择最匹配的响应结构
5. **响应式场景用 reactive 包**：Spring WebFlux 项目使用响应式响应结构
6. **复杂转换用链式构建器**：多步骤转换使用 `ResponseConverter.chain()`
7. **自定义算法用策略模式**：需要运行时切换转换逻辑时使用 `ResponseConversionStrategy`
8. **动态注册用工厂**：需要动态管理转换策略时使用 `ResponseConverterFactory`

---

## 6. 涉及的设计模式

本模块的响应转换功能采用了多种经典设计模式，以下是简要说明：

### 📐 适配器模式（Adapter Pattern）
- **用途**：将不同格式的响应转换为统一格式
- **代表类**：`ResponseConverter.toAjaxResult()` 系列方法
- **示例**：`Result<User>` → `AjaxResult`

### 🎯 策略模式（Strategy Pattern）
- **用途**：定义可互换的转换算法族
- **代表接口**：`ResponseConversionStrategy<S, T>`
- **特点**：支持函数式编程、策略组合

### 🏭 工厂方法模式（Factory Method Pattern）
- **用途**：根据类型动态创建转换策略
- **代表类**：`ResponseConverterFactory.getStrategy()`
- **优势**：解耦对象创建和使用

### 🔨 建造者模式（Builder Pattern）
- **用途**：分步构建复杂的转换链
- **代表类**：`ConversionChainBuilder`
- **特点**：链式调用、可读性强

### 🔗 责任链模式（Chain of Responsibility）
- **用途**：依次执行多个转换步骤
- **实现位置**：`ConversionChainBuilder.execute()` 内部
- **优势**：每个步骤只关注自己的逻辑

### 🚪 门面模式（Facade Pattern）
- **用途**：提供统一的转换入口
- **代表类**：`ResponseConverter`
- **效果**：简化客户端调用

### 📋 注册表模式（Registry Pattern）
- **用途**：集中管理所有转换策略
- **实现位置**：`ResponseConverterFactory.STRATEGIES`
- **特点**：支持动态注册和查找

📖 **详细设计模式说明请查看**：[DESIGN_PATTERNS.md](./DESIGN_PATTERNS.md)

---

## 📖 相关文档

- [README.md](./README.md) - 完整使用说明
- [RESPONSE_FORMATS.md](./RESPONSE_FORMATS.md) - 响应格式详细定义
- [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) - 实现状态清单
- [DESIGN_PATTERNS.md](./DESIGN_PATTERNS.md) - 设计模式详解

## 🔮 未来规划

### 高优先级（待实现）
- ValidationResponse - 表单验证结果
- BatchResponse<T> - 批量操作结果
- FileResponse - 文件下载响应

### 中优先级（待实现）
- AsyncTaskResponse - 异步任务响应
- SearchResultResponse<T> - 搜索结果响应
- AggregateResponse - 聚合查询响应
- StreamResponse / ChunkedResponse - 流式响应

### 📄 分页方式扩展

**已支持：** MyBatis-Plus、PageHelper、DataListInfo

**新增通用分页包装器（可与任何响应组合）：**
- `PageData<T>` - 通用分页数据（支持 MyBatis-Plus/PageHelper 转换）
- `CursorPageData<T>` - 游标分页数据（无限滚动、Feed 流）
- `KeysetPageData<T>` - 键集分页数据（高性能深分页）

**使用示例：**
```java
// 传统分页 + AjaxResult
AjaxResult<PageData<User>> resp = AjaxResult.success(PageData.of(users, total, pageNum, pageSize));

// 游标分页 + Result
Result<CursorPageData<Post>> feedResp = Result.success(CursorPageData.of(posts, nextCursor, hasMore));

// 键集分页 + TencentResponse
TencentResponse<KeysetPageData<Order>> txResp = TencentResponse.success(KeysetPageData.of(orders, lastId, hasMore));
```

📖 **完整规划请查看**：[README.md - 未来规划章节](./README.md#未来规划)
