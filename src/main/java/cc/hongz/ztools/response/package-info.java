/**
 * 响应结构工具库.
 * <p>
 * 提供多种风格的响应结构定义，包括：
 * </p>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.ajax} - AJAX 风格（如 AjaxResult，推荐用于归一化）</li>
 *   <li>{@link cc.hongz.ztools.response.native} - Java 原生风格（如 Result&lt;T&gt;）</li>
 *   <li>{@link cc.hongz.ztools.response.standard} - 标准协议和行业规范（RFC 7807、JSON:API、GraphQL、gRPC、阿里/腾讯/百度风格等）</li>
 *   <li>{@link cc.hongz.ztools.response.http} - HTTP 客户端响应包装（JDK HttpClient、OkHttp、Apache HttpClient、Spring ResponseEntity/WebClient、Feign）</li>
 *   <li>{@link cc.hongz.ztools.response.framework} - 框架特定响应（Spring Boot Actuator、OAuth2、Data REST、MyBatis-Plus/PageHelper 分页）</li>
 *   <li>{@link cc.hongz.ztools.response.mq} - 消息队列响应（RabbitMQ、Kafka、RocketMQ）</li>
 *   <li>{@link cc.hongz.ztools.response.database} - 数据库/缓存响应（Redis、Elasticsearch、MongoDB）</li>
 *   <li>{@link cc.hongz.ztools.response.protocol} - 其他协议响应（WebSocket、SSE、XML 转 JSON）</li>
 *   <li>{@link cc.hongz.ztools.response.reactive} - 响应式响应结构（ReactiveAjaxResult、ReactiveCollectionResult、ReactivePageResult）</li>
 *   <li>{@link cc.hongz.ztools.response.common} - 通用响应数据结构（如 DataListInfo）</li>
 * </ul>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
package cc.hongz.ztools.response;
