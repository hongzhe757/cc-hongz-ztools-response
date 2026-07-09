# 变更日志

## 2026-06-23

- **Phase 3 响应拦截器**：`ResponseInterceptor`（`ResponseBodyAdvice`）、`ResponseInterceptStrategy`、`@SkipResponseIntercept`
- 内置策略：`EntityToAjaxResultStrategy`、`MapToAjaxResultStrategy`、`StringToAjaxResultStrategy`
- **Phase 2.5 Spring 配置**：`ResponseAutoConfiguration`、`ResponseProperties`、`InterceptorProperties`
- 可选依赖 `spring-webmvc`；单元测试 **588** 用例
- **国际化键值映射**：`AjaxStatusMessageKeys`、`AjaxStatusInterface#getMessageKey()`；`I18nResponseBuilder` 主链路经语义键命中 `messages_*.properties`
- `AjaxStatusEnum` 补齐 `common.*` 消息键；`AjaxStatusRegistry.registerAll` 同步注册 messageKey

## 2026-06-21

- README 补充「设计模式与设计思路」：Adapter、Strategy、责任链、Registry、Facade、防腐层等及分层职责说明
- 新建 `hongz-ztools-response`：响应结构目录（`ResponseCatalog` / `ResponseDescriptor`）与归一化（`UnifiedResponse` / `ResponseAdapter`）
- 内置 IVICS `AjaxResult`、`DataListInfo` 及 Map、HTTP 状态载体等预设（`ResponsePresets`）
- Spring `ResponseEntity` 反射适配（无 `spring-web` 编译依赖）
- 单元测试 `ResponseAdaptersTest`；根 `pom.xml` 与 `hongz-ztools` 聚合已注册
- 单元测试补充注释与 `@DisplayName`：每个用例标明所测返回结构（Map、IVICS AjaxResult/DataListInfo、HTTP 载体等）
