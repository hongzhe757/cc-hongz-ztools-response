响应结构 JSON 格式大全

本文档列举了 Java 生态中常见的各种响应结构 JSON 格式，包括自定义 DTO、HTTP 客户端、标准协议等。

## 目录

1. [自定义业务响应](#1-自定义业务响应)
   - 1.1 AJAX 风格 (AjaxResult)
   - 1.2 Java 原生风格 (Result\<T\>)
   - 1.3 分页数据响应 (DataListInfo)
   - 1.4 阿里巴巴风格
   - 1.5 腾讯风格
   - 1.6 百度风格
2. [HTTP 客户端响应](#2-http-客户端响应)
3. [标准协议响应](#3-标准协议响应)
4. [框架特定响应](#4-框架特定响应)
5. [消息队列响应](#5-消息队列响应)
6. [数据库/缓存响应](#6-数据库缓存响应)

---

## 1. 自定义业务响应

### 1.1 AJAX 风格 (AjaxResult)

**适用场景**：前后端分离的 Web API

```json
{
  "code": "200",
  "msg": "操作成功",
  "data": {
    "userId": 1001,
    "username": "张三"
  }
}
```

**错误响应**：

```json
{
  "code": "500",
  "msg": "数据库连接失败",
  "data": null
}
```

### 1.2 Java 原生风格 (Result\<T\>)

**适用场景**：内部服务调用、RPC

```json
{
  "success": true,
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "name": "产品A"
  }
}
```

**错误响应**：

```json
{
  "success": false,
  "code": 404,
  "message": "资源未找到",
  "data": null
}
```

### 1.3 分页数据响应 (DataListInfo)

**适用场景**：列表查询

```json
{
  "total": 100,
  "totalPage": 10,
  "list": [
    {"id": 1, "name": "Item 1"},
    {"id": 2, "name": "Item 2"}
  ]
}
```

**嵌套在 AjaxResult 中**：

```json
{
  "code": "200",
  "msg": "查询成功",
  "data": {
    "total": 100,
    "totalPage": 10,
    "list": [...]
  }
}
```

### 1.4 阿里巴巴风格

**适用场景**：阿里系项目

```json
{
  "success": true,
  "errorCode": null,
  "errorMsg": null,
  "content": {
    "data": {...}
  }
}
```

### 1.5 腾讯风格

**适用场景**：腾讯系项目

```json
{
  "ret": 0,
  "msg": "ok",
  "data": {...}
}
```

### 1.6 百度风格

**适用场景**：百度系项目

```json
{
  "errno": 0,
  "errmsg": "",
  "data": {...}
}
```

---

## 2. HTTP 客户端响应

### 2.1 JDK HttpClient (java.net.http.HttpResponse)

**原始结构**（非 JSON，需转换）：

```java
HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
// statusCode: 200
// body: "{\"key\":\"value\"}"
// headers: {...}
```

**转换为 JSON**：

```json
{
  "statusCode": 200,
  "body": {
    "key": "value"
  },
  "headers": {
    "Content-Type": ["application/json"],
    "Content-Length": ["123"]
  }
}
```

### 2.2 OkHttp Response

**原始结构**：

```java
Response response = client.newCall(request).execute();
// code: 200
// message: "OK"
// body: ResponseBody
```

**转换为 JSON**：

```json
{
  "code": 200,
  "message": "OK",
  "body": {
    "key": "value"
  },
  "headers": {
    "Content-Type": "application/json"
  },
  "isSuccessful": true
}
```

### 2.3 Apache HttpClient (CloseableHttpResponse)

**原始结构**：

```java
CloseableHttpResponse response = httpClient.execute(httpGet);
// StatusLine: HTTP/1.1 200 OK
// HttpEntity: content
```

**转换为 JSON**：

```json
{
  "protocolVersion": "HTTP/1.1",
  "statusCode": 200,
  "reasonPhrase": "OK",
  "entity": {
    "content": {"key": "value"},
    "contentType": "application/json",
    "contentLength": 123
  },
  "headers": [
    {"name": "Content-Type", "value": "application/json"}
  ]
}
```

### 2.4 Spring ResponseEntity

**原始结构**：

```java
ResponseEntity<User> response = restTemplate.getForEntity(url, User.class);
// statusCode: HttpStatus.OK
// body: User object
// headers: HttpHeaders
```

**转换为 JSON**：

```json
{
  "statusCode": "OK",
  "statusCodeValue": 200,
  "body": {
    "userId": 1001,
    "username": "张三"
  },
  "headers": {
    "Content-Type": ["application/json"]
  }
}
```

### 2.5 Spring WebClient (Reactive)

**原始结构**：

```java
Mono<ResponseEntity<User>> response = webClient.get()
    .uri(url)
    .retrieve()
    .toEntity(User.class);
```

**转换为 JSON**（同 ResponseEntity）：

```json
{
  "statusCode": "OK",
  "statusCodeValue": 200,
  "body": {...},
  "headers": {...}
}
```

### 2.6 Feign Client

**原始结构**：

```java
@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id);
}
```

**直接返回业务对象**：

```json
{
  "userId": 1001,
  "username": "张三",
  "email": "zhangsan@example.com"
}
```

**带 Feign 包装**（如果配置了 Decoder）：

```json
{
  "status": 200,
  "reason": "OK",
  "headers": {...},
  "body": {
    "userId": 1001,
    "username": "张三"
  }
}
```

---

## 3. 标准协议响应

### 3.1 RFC 7807 Problem Details (推荐标准)

**适用场景**：REST API 错误响应标准

```json
{
  "type": "https://example.com/errors/user-not-found",
  "title": "User Not Found",
  "status": 404,
  "detail": "The user with ID 12345 was not found in the database.",
  "instance": "/users/12345",
  "traceId": "abc-123-def-456"
}
```

**验证错误示例**：

```json
{
  "type": "https://example.com/errors/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Request validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Invalid email format"
    },
    {
      "field": "age",
      "message": "Must be greater than 0"
    }
  ]
}
```

### 3.2 JSON:API 规范

**适用场景**：遵循 JSON:API 标准的 REST API

**单个资源**：

```json
{
  "data": {
    "type": "articles",
    "id": "1",
    "attributes": {
      "title": "JSON:API paints my bikeshed!",
      "body": "The shortest article. Ever."
    },
    "relationships": {
      "author": {
        "data": {"type": "people", "id": "9"}
      }
    }
  },
  "links": {
    "self": "http://example.com/articles/1"
  }
}
```

**集合资源**：

```json
{
  "data": [
    {
      "type": "articles",
      "id": "1",
      "attributes": {...}
    },
    {
      "type": "articles",
      "id": "2",
      "attributes": {...}
    }
  ],
  "meta": {
    "total": 100,
    "page": 1,
    "pageSize": 10
  },
  "links": {
    "self": "http://example.com/articles?page=1",
    "next": "http://example.com/articles?page=2",
    "last": "http://example.com/articles?page=10"
  }
}
```

**错误响应**：

```json
{
  "errors": [
    {
      "status": "404",
      "code": "USER_NOT_FOUND",
      "title": "User Not Found",
      "detail": "Could not find a user with ID 12345.",
      "source": {
        "pointer": "/data/attributes/user-id"
      }
    }
  ]
}
```

### 3.3 GraphQL 响应

**成功响应**：

```json
{
  "data": {
    "user": {
      "id": "1001",
      "name": "张三",
      "email": "zhangsan@example.com"
    }
  },
  "extensions": {
    "tracing": {
      "version": 1,
      "startTime": "2024-01-01T00:00:00Z",
      "endTime": "2024-01-01T00:00:01Z"
    }
  }
}
```

**部分错误响应**：

```json
{
  "data": {
    "user": null,
    "posts": [...]
  },
  "errors": [
    {
      "message": "User not found",
      "locations": [{"line": 2, "column": 3}],
      "path": ["user"],
      "extensions": {
        "code": "USER_NOT_FOUND",
        "timestamp": "2024-01-01T00:00:00Z"
      }
    }
  ]
}
```

**请求级错误**：

```json
{
  "errors": [
    {
      "message": "Syntax Error: Expected Name, found }",
      "locations": [{"line": 1, "column": 10}]
    }
  ]
}
```

### 3.4 gRPC JSON Transcoding

**适用场景**：gRPC 转 REST/JSON

**成功响应**（直接映射 Protobuf message）：

```json
{
  "userId": "1001",
  "username": "张三",
  "email": "zhangsan@example.com",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

**错误响应**（使用 google.rpc.Status）：

```json
{
  "code": 5,
  "message": "User not found",
  "details": [
    {
      "@type": "type.googleapis.com/google.rpc.ErrorInfo",
      "reason": "USER_NOT_FOUND",
      "domain": "example.com",
      "metadata": {
        "userId": "12345"
      }
    }
  ]
}
```

**常见 gRPC 状态码**：

- `0`: OK
- `1`: CANCELLED
- `2`: UNKNOWN
- `3`: INVALID_ARGUMENT
- `5`: NOT_FOUND
- `7`: PERMISSION_DENIED
- `8`: RESOURCE_EXHAUSTED
- `13`: INTERNAL
- `14`: UNAVAILABLE

### 3.5 OpenAPI/Swagger 响应

**标准响应包装**：

```json
{
  "code": 200,
  "message": "Success",
  "data": {
    "id": 1,
    "name": "Example"
  },
  "timestamp": "2024-01-01T00:00:00Z",
  "path": "/api/example"
}
```

---

## 4. 框架特定响应

### 4.1 Spring Boot Actuator

**健康检查**：

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 100000000000,
        "threshold": 10000000
      }
    }
  }
}
```

**指标响应**：

```json
{
  "names": [
    "http.server.requests",
    "jvm.memory.used",
    "system.cpu.usage"
  ]
}
```

### 4.2 Spring Security OAuth2

**Token 响应**：

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "refresh_token": "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...",
  "scope": "read write"
}
```

**错误响应**：

```json
{
  "error": "invalid_grant",
  "error_description": "Bad credentials"
}
```

### 4.3 Spring Data REST

**集合响应**：

```json
{
  "_embedded": {
    "users": [
      {
        "id": 1,
        "name": "张三",
        "_links": {
          "self": {"href": "http://example.com/users/1"}
        }
      }
    ]
  },
  "_links": {
    "self": {"href": "http://example.com/users"},
    "profile": {"href": "http://example.com/profile/users"}
  },
  "page": {
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "number": 0
  }
}
```

### 4.4 MyBatis-Plus 分页

```json
{
  "records": [
    {"id": 1, "name": "Item 1"},
    {"id": 2, "name": "Item 2"}
  ],
  "total": 100,
  "size": 10,
  "current": 1,
  "pages": 10,
  "orders": [],
  "optimizeCountSql": true,
  "searchCount": true,
  "maxLimit": null,
  "countId": null
}
```

### 4.5 PageHelper 分页

```json
{
  "total": 100,
  "list": [
    {"id": 1, "name": "Item 1"},
    {"id": 2, "name": "Item 2"}
  ],
  "pageNum": 1,
  "pageSize": 10,
  "size": 10,
  "startRow": 1,
  "endRow": 10,
  "pages": 10,
  "prePage": 0,
  "nextPage": 2,
  "isFirstPage": true,
  "isLastPage": false,
  "hasPreviousPage": false,
  "hasNextPage": true,
  "navigatePages": 8,
  "navigatepageNums": [1, 2, 3, 4, 5, 6, 7, 8]
}
```

---

## 5. 消息队列响应

### 5.1 RabbitMQ RPC 响应

```json
{
  "correlationId": "abc-123",
  "replyTo": "rpc_queue",
  "body": {
    "result": "success",
    "data": {...}
  },
  "timestamp": 1704067200000
}
```

### 5.2 Kafka Consumer Record

```json
{
  "topic": "user-events",
  "partition": 0,
  "offset": 12345,
  "timestamp": 1704067200000,
  "key": "user-1001",
  "value": {
    "eventType": "USER_CREATED",
    "userId": 1001,
    "username": "张三"
  },
  "headers": {
    "contentType": "application/json"
  }
}
```

### 5.3 RocketMQ 响应

```json
{
  "msgId": "AC1F0001000000000000",
  "topic": "TEST_TOPIC",
  "tags": "TagA",
  "keys": "OrderID123",
  "body": {
    "orderId": "123",
    "amount": 99.99
  },
  "bornTimestamp": 1704067200000,
  "storeTimestamp": 1704067200000
}
```

---

## 6. 数据库/缓存响应

### 6.1 Redis 响应

**字符串值**：

```json
{
  "key": "user:1001",
  "value": "{\"name\":\"张三\"}",
  "ttl": 3600,
  "type": "string"
}
```

**哈希值**：

```json
{
  "key": "user:1001",
  "value": {
    "name": "张三",
    "age": "25",
    "email": "zhangsan@example.com"
  },
  "ttl": 3600,
  "type": "hash"
}
```

**列表值**：

```json
{
  "key": "user:list",
  "value": ["user1", "user2", "user3"],
  "length": 3,
  "type": "list"
}
```

### 6.2 Elasticsearch 响应

**搜索响应**：

```json
{
  "took": 30,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 100,
      "relation": "eq"
    },
    "max_score": 1.0,
    "hits": [
      {
        "_index": "users",
        "_id": "1001",
        "_score": 1.0,
        "_source": {
          "name": "张三",
          "age": 25
        }
      }
    ]
  },
  "aggregations": {
    "age_groups": {
      "buckets": [
        {"key": "20-30", "doc_count": 50},
        {"key": "30-40", "doc_count": 30}
      ]
    }
  }
}
```

### 6.3 MongoDB 响应

**查询结果**：

```json
{
  "cursor": {
    "firstBatch": [
      {
        "_id": {"$oid": "507f1f77bcf86cd799439011"},
        "name": "张三",
        "age": 25
      }
    ],
    "id": {"$numberLong": "1234567890"},
    "ns": "test.users"
  },
  "ok": 1
}
```

---

## 7. 其他常见格式

### 7.1 WebSocket 消息

```json
{
  "type": "message",
  "event": "USER_UPDATE",
  "data": {
    "userId": 1001,
    "status": "online"
  },
  "timestamp": 1704067200000,
  "messageId": "msg-abc-123"
}
```

### 7.2 Server-Sent Events (SSE)

```
data: {"event":"update","data":{"id":1,"name":"张三"}}

data: {"event":"delete","data":{"id":2}}

```

**解析为 JSON**：

```json
[
  {
    "event": "update",
    "data": {"id": 1, "name": "张三"}
  },
  {
    "event": "delete",
    "data": {"id": 2}
  }
]
```

### 7.3 XML 转 JSON

**原始 XML**：

```xml
<response>
  <code>200</code>
  <message>Success</message>
  <data>
    <user id="1001">
      <name>张三</name>
    </user>
  </data>
</response>
```

**转换为 JSON**：

```json
{
  "response": {
    "code": "200",
    "message": "Success",
    "data": {
      "user": {
        "@id": "1001",
        "name": "张三"
      }
    }
  }
}
```

---

## 8. 响应结构对比表


| 类型                  | 成功标识         | 状态码字段      | 消息字段     | 数据字段   | 适用场景    |
| --------------------- | ---------------- | --------------- | ------------ | ---------- | ----------- |
| AjaxResult            | code=="200"      | code (String)   | msg          | data       | Web API     |
| Result\<T\>           | success==true    | code (int)      | message      | data       | 内部服务    |
| RFC 7807              | -                | status          | title/detail | -          | 错误标准    |
| JSON:API              | -                | -               | errors[]     | data       | REST 标准   |
| GraphQL               | data!=null       | -               | errors[]     | data       | 图查询      |
| gRPC                  | code==0          | code            | message      | (直接)     | RPC         |
| Spring ResponseEntity | statusCode.is2xx | statusCodeValue | -            | body       | Spring      |
| OkHttp                | isSuccessful     | code            | message      | body       | HTTP 客户端 |
| PageHelper            | -                | -               | -            | list/total | 分页        |
| Redis                 | -                | -               | -            | value      | 缓存        |

---

## 9. 最佳实践建议

### 9.1 选择响应格式的考虑因素

1. **团队规范**：优先遵循团队已有规范
2. **客户端类型**：Web 前端用 AjaxResult，内部服务用 Result\<T\>
3. **标准化需求**：对外 API 考虑 RFC 7807 或 JSON:API
4. **性能要求**：gRPC 适合高性能场景
5. **灵活性需求**：GraphQL 适合复杂查询

### 9.2 统一响应结构的优点

- ✅ 便于全局异常处理
- ✅ 方便日志记录和监控
- ✅ 简化前端解析逻辑
- ✅ 支持统一的拦截器/过滤器
- ✅ 便于生成 API 文档

### 9.3 避免的陷阱

- ❌ 混用多种响应格式
- ❌ 状态码语义不一致
- ❌ 缺少必要的错误信息
- ❌ 过度嵌套导致解析困难
- ❌ 忽略国际化需求

---

## 10. 参考资料

- [RFC 7807 - Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807)
- [RFC 9457 - Problem Details for HTTP APIs (Updated)](https://www.rfc-editor.org/info/rfc9457/)
- [JSON:API Specification](https://jsonapi.org/)
- [GraphQL Specification](https://spec.graphql.org/)
- [gRPC Documentation](https://grpc.io/docs/)
- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [OkHttp Documentation](https://square.github.io/okhttp/)

---

**文档版本**：1.0
**最后更新**：2024-01-01
**维护者**：hongz-parent 团队
