package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.database.ElasticsearchResponse;
import cc.hongz.ztools.response.database.MongoDbResponse;
import cc.hongz.ztools.response.database.RedisResponse;
import cc.hongz.ztools.response.framework.ActuatorHealthResponse;
import cc.hongz.ztools.response.framework.MyBatisPlusPageResponse;
import cc.hongz.ztools.response.framework.OAuth2TokenResponse;
import cc.hongz.ztools.response.framework.PageHelperPageResponse;
import cc.hongz.ztools.response.generic.JsonResponse;
import cc.hongz.ztools.response.generic.MapResponse;
import cc.hongz.ztools.response.http.JdkHttpResponseWrapper;
import cc.hongz.ztools.response.http.OkHttpResponseWrapper;
import cc.hongz.ztools.response.http.SpringResponseEntityWrapper;
import cc.hongz.ztools.response.common.DataListInfo;
import cc.hongz.ztools.response.mq.KafkaConsumerRecordResponse;
import cc.hongz.ztools.response.mq.RabbitMqRpcResponse;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.protocol.SseEvent;
import cc.hongz.ztools.response.protocol.WebSocketMessage;
import cc.hongz.ztools.response.protocol.XmlToJsonResponse;
import cc.hongz.ztools.response.reactive.ReactiveAjaxResult;
import cc.hongz.ztools.response.reactive.ReactiveCollectionResult;
import cc.hongz.ztools.response.reactive.ReactivePageResult;
import cc.hongz.ztools.response.standard.AliResponse;
import cc.hongz.ztools.response.standard.BaiduResponse;
import cc.hongz.ztools.response.standard.GraphQLResponse;
import cc.hongz.ztools.response.standard.GrpcResponse;
import cc.hongz.ztools.response.standard.ProblemDetail;
import cc.hongz.ztools.response.standard.TencentResponse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import okhttp3.Response;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应结构工厂 - 统一入口.
 * <p>
 * 提供所有响应结构的快速创建方法，作为整个响应模块的统一门面（Facade）。
 * 通过此类可以方便地创建任何类型的响应结构，无需记住各个类的具体位置。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 创建 AJAX 响应
 * AjaxResult ajax = ResponseFactory.ajaxSuccess(data);
 *
 * // 创建原生 Result
 * Result<User> result = ResponseFactory.resultSuccess(user);
 *
 * // 创建 RFC 7807 Problem Detail
 * ProblemDetail problem = ResponseFactory.problemBadRequest("参数无效");
 *
 * // 创建 HTTP 客户端包装
 * JdkHttpResponseWrapper<User> wrapper = ResponseFactory.jdkHttpWrapper(response, user);
 *
 * // 创建响应式结果
 * ReactiveAjaxResult<User> reactive = ResponseFactory.reactiveAjax(mono);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public final class ResponseFactory {

    private ResponseFactory() {
        // 工具类，禁止实例化
    }

    // ==================== AJAX 风格响应 ====================

    /**
     * 创建 AJAX 成功响应.
     *
     * @param data 数据
     * @return AjaxResult
     */
    public static AjaxResult ajaxSuccess(final Object data) {
        return AjaxResult.success(data);
    }

    /**
     * 创建 AJAX 成功响应（带消息）.
     *
     * @param msg  消息
     * @param data 数据
     * @return AjaxResult
     */
    public static AjaxResult ajaxSuccess(final String msg, final Object data) {
        return AjaxResult.success(msg, data);
    }

    /**
     * 创建 AJAX 错误响应.
     *
     * @param msg 错误消息
     * @return AjaxResult
     */
    public static AjaxResult ajaxError(final String msg) {
        return AjaxResult.error(msg);
    }

    /**
     * 创建 AJAX 错误响应（带状态码）.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return AjaxResult
     */
    public static AjaxResult ajaxError(final String code, final String msg) {
        return AjaxResult.error(code, msg);
    }

    // ==================== Java 原生风格响应 ====================

    /**
     * 创建原生 Result 成功响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return Result
     */
    public static <T> Result<T> resultSuccess(final T data) {
        return Result.success(data);
    }

    /**
     * 创建原生 Result 失败响应.
     *
     * @param code    状态码
     * @param message 消息
     * @param <T>     数据类型
     * @return Result
     */
    public static <T> Result<T> resultFailure(final int code, final String message) {
        return Result.failure(code, message);
    }

    // ==================== 标准协议响应 ====================

    /**
     * 创建阿里巴巴风格响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return AliResponse
     */
    public static <T> AliResponse<T> aliSuccess(final T data) {
        return AliResponse.success(data);
    }

    /**
     * 创建腾讯风格响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return TencentResponse
     */
    public static <T> TencentResponse<T> tencentSuccess(final T data) {
        return TencentResponse.success(data);
    }

    /**
     * 创建百度风格响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return BaiduResponse
     */
    public static <T> BaiduResponse<T> baiduSuccess(final T data) {
        return BaiduResponse.success(data);
    }

    /**
     * 创建 RFC 7807 Problem Detail.
     *
     * @param status 状态码
     * @param title  标题
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail problem(final int status, final String title, final String detail) {
        return ProblemDetail.of(status, title, detail);
    }

    /**
     * 创建 400 Bad Request Problem Detail.
     *
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail problemBadRequest(final String detail) {
        return ProblemDetail.badRequest(detail);
    }

    /**
     * 创建 404 Not Found Problem Detail.
     *
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail problemNotFound(final String detail) {
        return ProblemDetail.notFound(detail);
    }

    /**
     * 创建 GraphQL 响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> graphqlSuccess(final T data) {
        return GraphQLResponse.success(data);
    }

    /**
     * 创建 gRPC 响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return GrpcResponse
     */
    public static <T> GrpcResponse<T> grpcSuccess(final T data) {
        return GrpcResponse.success(data);
    }

    // ==================== HTTP 客户端响应包装 ====================

    /**
     * 创建 JDK HttpClient 响应包装.
     *
     * @param response JDK HttpResponse
     * @param body     响应体
     * @param <T>      响应体类型
     * @return JdkHttpResponseWrapper
     */
    public static <T> JdkHttpResponseWrapper<T> jdkHttpWrapper(
            final HttpResponse<?> response,
            final T body
        ) {
        return JdkHttpResponseWrapper.from(response, body);
    }

    /**
     * 创建 OkHttp 响应包装.
     *
     * @param response OkHttp Response
     * @param body     响应体
     * @param <T>      响应体类型
     * @return OkHttpResponseWrapper
     */
    public static <T> OkHttpResponseWrapper<T> okHttpWrapper(final Response response, final T body) {
        return OkHttpResponseWrapper.from(response, body);
    }

    /**
     * 创建 Spring ResponseEntity 包装.
     *
     * @param responseEntity Spring ResponseEntity
     * @param <T>            响应体类型
     * @return SpringResponseEntityWrapper
     */
    public static <T> SpringResponseEntityWrapper<T> springResponseWrapper(final ResponseEntity<T> responseEntity) {
        return SpringResponseEntityWrapper.from(responseEntity);
    }

    // ==================== 框架特定响应 ====================

    /**
     * 创建 Actuator 健康检查响应.
     *
     * @return ActuatorHealthResponse
     */
    public static ActuatorHealthResponse actuatorUp() {
        return ActuatorHealthResponse.up();
    }

    /**
     * 创建 OAuth2 Token 响应.
     *
     * @param accessToken 访问令牌
     * @param tokenType   令牌类型
     * @param expiresIn   过期时间（秒）
     * @return OAuth2TokenResponse
     */
    public static OAuth2TokenResponse oauth2Token(
            final String accessToken,
            final String tokenType,
            final Long expiresIn
        ) {
        return OAuth2TokenResponse.success(accessToken, tokenType, expiresIn);
    }

    /**
     * 从 MyBatis-Plus IPage 创建分页响应.
     *
     * @param page MyBatis-Plus IPage
     * @param <T>  数据类型
     * @return MyBatisPlusPageResponse
     */
    public static <T> MyBatisPlusPageResponse<T> mybatisPlusPage(final IPage<T> page) {
        return MyBatisPlusPageResponse.from(page);
    }

    /**
     * 从 PageHelper PageInfo 创建分页响应.
     *
     * @param pageInfo PageHelper PageInfo
     * @param <T>      数据类型
     * @return PageHelperPageResponse
     */
    public static <T> PageHelperPageResponse<T> pageHelperPage(final PageInfo<T> pageInfo) {
        return PageHelperPageResponse.from(pageInfo);
    }

    // ==================== 消息队列响应 ====================

    /**
     * 创建 RabbitMQ RPC 响应.
     *
     * @param correlationId 关联 ID
     * @param replyTo       回复队列
     * @param data          数据
     * @param <T>           数据类型
     * @return RabbitMqRpcResponse
     */
    public static <T> RabbitMqRpcResponse<T> rabbitmqRpc(
            final String correlationId,
            final String replyTo,
            final T data
        ) {
        return RabbitMqRpcResponse.of(correlationId, replyTo, data);
    }

    /**
     * 从 Kafka ConsumerRecord 创建响应.
     *
     * @param record Kafka ConsumerRecord
     * @param <K>    Key 类型
     * @param <V>    Value 类型
     * @return KafkaConsumerRecordResponse
     */
    public static <K, V> KafkaConsumerRecordResponse<K, V> kafkaRecord(
            final ConsumerRecord<K,
            V> record
        ) {
        return KafkaConsumerRecordResponse.from(record);
    }

    // ==================== 数据库/缓存响应 ====================

    /**
     * 创建 Redis GET 响应.
     *
     * @param key   键
     * @param value 值
     * @param <T>   数据类型
     * @return RedisResponse
     */
    public static <T> RedisResponse<T> redisGet(final String key, final T value) {
        return RedisResponse.get(key, value);
    }

    /**
     * 创建 Elasticsearch 搜索响应.
     *
     * @param totalHits 总命中数
     * @param maxScore  最大得分
     * @param hits      命中列表
     * @param took      耗时
     * @param <T>       数据类型
     * @return ElasticsearchResponse
     */
    public static <T> ElasticsearchResponse<T> elasticsearchSearch(
            final long totalHits,
            final Float maxScore,
            final List<ElasticsearchResponse.Hit<T>> hits,
            final long took
        ) {
        return ElasticsearchResponse.search(totalHits, maxScore, hits, took);
    }

    /**
     * 创建 MongoDB 查询响应.
     *
     * @param results    结果列表
     * @param totalCount 总数
     * @param <T>        数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> mongodbFind(final List<T> results, final Long totalCount) {
        return MongoDbResponse.find(results, totalCount);
    }

    // ==================== 其他协议响应 ====================

    /**
     * 创建 WebSocket 文本消息.
     *
     * @param payload 消息内容
     * @param <T>     数据类型
     * @return WebSocketMessage
     */
    public static <T> WebSocketMessage<T> websocketText(final T payload) {
        return WebSocketMessage.text(payload);
    }

    /**
     * 创建 SSE 事件.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return SseEvent
     */
    public static <T> SseEvent<T> sseMessage(final T data) {
        return SseEvent.message(data);
    }

    /**
     * 创建 XML 转 JSON 响应.
     *
     * @param rootElement 根元素名称
     * @param data        转换后的数据
     * @param <T>         数据类型
     * @return XmlToJsonResponse
     */
    public static <T> XmlToJsonResponse<T> xmlToJson(final String rootElement, final T data) {
        return XmlToJsonResponse.of(rootElement, data);
    }

    // ==================== 响应式响应 ====================

    /**
     * 创建响应式 AJAX 结果.
     *
     * @param data 数据（Mono）
     * @param <T>  数据类型
     * @return ReactiveAjaxResult
     */
    public static <T> ReactiveAjaxResult<T> reactiveAjax(final Mono<T> data) {
        return ReactiveAjaxResult.success(data);
    }

    /**
     * 创建响应式集合结果.
     *
     * @param data 数据流（Flux）
     * @param <T>  数据类型
     * @return ReactiveCollectionResult
     */
    public static <T> ReactiveCollectionResult<T> reactiveCollection(final Flux<T> data) {
        return ReactiveCollectionResult.success(data);
    }

    /**
     * 创建响应式分页结果.
     *
     * @param records 数据流
     * @param total   总数（Mono）
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return ReactivePageResult
     */
    public static <T> ReactivePageResult<T> reactivePage(
            final Flux<T> records,
            final Mono<Long> total,
            final Integer current,
            final Integer size
        ) {
        return ReactivePageResult.success(records, total, current, size);
    }

    // ==================== 通用数据模型 ====================

    /**
     * 创建分页数据信息.
     *
     * @param total     总记录数
     * @param totalPage 总页数
     * @param list      数据列表
     * @return DataListInfo
     */
    public static DataListInfo dataListInfo(final int total, final int totalPage, final List<?> list) {
        return DataListInfo.of(total, totalPage, list);
    }

    /**
     * 创建空的分页数据信息.
     *
     * @return DataListInfo
     */
    public static DataListInfo emptyDataListInfo() {
        return DataListInfo.empty();
    }

    // ==================== 通用/动态响应 ====================

    /**
     * 创建 JSON 字符串成功响应.
     *
     * @param jsonData JSON 字符串数据
     * @return JsonResponse
     */
    public static JsonResponse jsonResponseSuccess(final String jsonData) {
        return JsonResponse.success(jsonData);
    }

    /**
     * 创建 JSON 字符串错误响应.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return JsonResponse
     */
    public static JsonResponse jsonResponseError(final String code, final String msg) {
        return JsonResponse.error(code, msg);
    }

    /**
     * 创建 Map 形式成功响应.
     *
     * @param data Map 数据
     * @return MapResponse
     */
    public static MapResponse mapResponseSuccess(final Map<String, Object> data) {
        return MapResponse.success(data);
    }

    /**
     * 创建 Map 形式错误响应.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return MapResponse
     */
    public static MapResponse mapResponseError(final String code, final String msg) {
        return MapResponse.error(code, msg);
    }

    /**
     * 创建 Map 响应构建器.
     *
     * @return MapResponse.Builder
     */
    public static MapResponse.Builder mapResponseBuilder() {
        return MapResponse.builder();
    }
}
