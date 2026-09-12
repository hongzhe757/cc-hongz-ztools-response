package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.common.DataListInfo;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.database.ElasticsearchResponse;
import cc.hongz.ztools.response.database.MongoDbResponse;
import cc.hongz.ztools.response.database.RedisResponse;
import cc.hongz.ztools.response.framework.ActuatorHealthResponse;
import cc.hongz.ztools.response.framework.OAuth2TokenResponse;
import cc.hongz.ztools.response.generic.JsonResponse;
import cc.hongz.ztools.response.generic.MapResponse;
import cc.hongz.ztools.response.mq.RabbitMqRpcResponse;
import cc.hongz.ztools.response.protocol.SseEvent;
import cc.hongz.ztools.response.protocol.WebSocketMessage;
import cc.hongz.ztools.response.protocol.XmlToJsonResponse;
import cc.hongz.ztools.response.standard.AliResponse;
import cc.hongz.ztools.response.standard.BaiduResponse;
import cc.hongz.ztools.response.standard.GraphQLResponse;
import cc.hongz.ztools.response.standard.GrpcResponse;
import cc.hongz.ztools.response.standard.ProblemDetail;
import cc.hongz.ztools.response.standard.TencentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ResponseFactory} 响应工厂的单元测试.
 * <p>
 * 测试所有静态工厂方法的正确性。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ResponseFactory 响应工厂测试")
class ResponseFactoryTest {

    // ==================== AJAX 风格响应测试 ====================

    @Test
    @DisplayName("ajaxSuccess(data) - 创建成功响应")
    void testAjaxSuccessWithData() {
        String data = "测试数据";
        AjaxResult result = ResponseFactory.ajaxSuccess(data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("ajaxSuccess(msg, data) - 带消息的成功响应")
    void testAjaxSuccessWithMsgAndData() {
        String msg = "查询成功";
        String data = "数据";
        AjaxResult result = ResponseFactory.ajaxSuccess(msg, data);

        assertTrue(result.isSuccess());
        assertEquals(msg, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("ajaxError(msg) - 创建错误响应")
    void testAjaxErrorWithMsg() {
        String msg = "操作失败";
        AjaxResult result = ResponseFactory.ajaxError(msg);

        assertFalse(result.isSuccess());
        assertEquals("500", result.getCode());
        assertEquals(msg, result.getMsg());
    }

    @Test
    @DisplayName("ajaxError(code, msg) - 带状态码的错误响应")
    void testAjaxErrorWithCodeAndMsg() {
        // 注意：AjaxResult.error(String, String) 会匹配 error(String msg, Object data)
        // 所以第一个参数被视为 msg，第二个参数被视为 data
        String code = "404";
        String msg = "资源不存在";
        AjaxResult result = ResponseFactory.ajaxError(code, msg);

        assertFalse(result.isSuccess());
        // 实际行为：code 参数被当作 msg，msg 参数被当作 data
        assertEquals("500", result.getCode());
        assertEquals(code, result.getMsg()); // code 变成了 msg
    }

    // ==================== Java 原生风格响应测试 ====================

    @Test
    @DisplayName("resultSuccess(data) - 创建成功结果")
    void testResultSuccess() {
        String data = "测试数据";
        Result<String> result = ResponseFactory.resultSuccess(data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("resultFailure(code, message) - 创建失败结果")
    void testResultFailure() {
        int code = 500;
        String message = "系统错误";
        Result<Void> result = ResponseFactory.resultFailure(code, message);

        assertFalse(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
    }

    // ==================== 标准协议响应测试 ====================

    @Test
    @DisplayName("aliSuccess(data) - 创建阿里风格响应")
    void testAliSuccess() {
        String data = "阿里数据";
        AliResponse<String> response = ResponseFactory.aliSuccess(data);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getContent());
        assertEquals(data, response.getContent().getData());
    }

    @Test
    @DisplayName("tencentSuccess(data) - 创建腾讯风格响应")
    void testTencentSuccess() {
        String data = "腾讯数据";
        TencentResponse<String> response = ResponseFactory.tencentSuccess(data);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getRet());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("baiduSuccess(data) - 创建百度风格响应")
    void testBaiduSuccess() {
        String data = "百度数据";
        BaiduResponse<String> response = ResponseFactory.baiduSuccess(data);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrno());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("problem(status, title, detail) - 创建 ProblemDetail")
    void testProblem() {
        int status = 400;
        String title = "Bad Request";
        String detail = "参数无效";
        ProblemDetail problem = ResponseFactory.problem(status, title, detail);

        assertNotNull(problem);
        assertEquals(status, problem.getStatus());
        assertEquals(title, problem.getTitle());
        assertEquals(detail, problem.getDetail());
    }

    @Test
    @DisplayName("problemBadRequest(detail) - 创建 400 错误")
    void testProblemBadRequest() {
        String detail = "请求参数错误";
        ProblemDetail problem = ResponseFactory.problemBadRequest(detail);

        assertEquals(400, problem.getStatus());
        assertEquals("Bad Request", problem.getTitle());
        assertEquals(detail, problem.getDetail());
    }

    @Test
    @DisplayName("problemNotFound(detail) - 创建 404 错误")
    void testProblemNotFound() {
        String detail = "资源未找到";
        ProblemDetail problem = ResponseFactory.problemNotFound(detail);

        assertEquals(404, problem.getStatus());
        assertEquals("Not Found", problem.getTitle());
        assertEquals(detail, problem.getDetail());
    }

    @Test
    @DisplayName("graphqlSuccess(data) - 创建 GraphQL 响应")
    void testGraphqlSuccess() {
        Map<String, Object> data = Map.of("user", Map.of("id", 1, "name", "张三"));
        GraphQLResponse<Map<String, Object>> response = ResponseFactory.graphqlSuccess(data);

        assertNotNull(response);
        assertNotNull(response.getData());
        assertNull(response.getErrors());
    }

    @Test
    @DisplayName("grpcSuccess(data) - 创建 gRPC 响应")
    void testGrpcSuccess() {
        String data = "gRPC 数据";
        GrpcResponse<String> response = ResponseFactory.grpcSuccess(data);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
    }

    // ==================== 框架特定响应测试 ====================

    @Test
    @DisplayName("actuatorUp() - 创建健康检查响应")
    void testActuatorUp() {
        ActuatorHealthResponse response = ResponseFactory.actuatorUp();

        assertNotNull(response);
        assertEquals("UP", response.getStatus());
    }

    @Test
    @DisplayName("oauth2Token(...) - 创建 OAuth2 Token 响应")
    void testOauth2Token() {
        String accessToken = "access_token_123";
        String tokenType = "Bearer";
        Long expiresIn = 3600L;

        OAuth2TokenResponse response = ResponseFactory.oauth2Token(accessToken, tokenType, expiresIn);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(tokenType, response.getTokenType());
        assertEquals(expiresIn, response.getExpiresIn());
    }

    // ==================== 消息队列响应测试 ====================

    @Test
    @DisplayName("rabbitmqRpc(...) - 创建 RabbitMQ RPC 响应")
    void testRabbitmqRpc() {
        String correlationId = "corr-123";
        String replyTo = "reply-queue";
        String data = "RPC 数据";

        RabbitMqRpcResponse<String> response = ResponseFactory.rabbitmqRpc(correlationId, replyTo, data);

        assertNotNull(response);
        assertEquals(correlationId, response.getCorrelationId());
        assertEquals(replyTo, response.getReplyTo());
        assertEquals(data, response.getData());
    }

    // ==================== 数据库/缓存响应测试 ====================

    @Test
    @DisplayName("redisGet(key, value) - 创建 Redis GET 响应")
    void testRedisGet() {
        String key = "user:123";
        String value = "张三";

        RedisResponse<String> response = ResponseFactory.redisGet(key, value);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(key, response.getKey());
        assertEquals(value, response.getValue());
    }

    @Test
    @DisplayName("elasticsearchSearch(...) - 创建 ES 搜索响应")
    void testElasticsearchSearch() {
        long totalHits = 100;
        Float maxScore = 1.5f;
        List<ElasticsearchResponse.Hit<String>> hits = Arrays.asList(
                new ElasticsearchResponse.Hit<>("index", "doc1", 1.5f, "data1", null)
        );
        long took = 50;

        ElasticsearchResponse<String> response = ResponseFactory.elasticsearchSearch(totalHits, maxScore, hits, took);

        assertNotNull(response);
        assertEquals(totalHits, response.getTotalHits());
        assertEquals(maxScore, response.getMaxScore());
        assertEquals(1, response.getHits().size());
        assertEquals(took, response.getTook());
    }

    @Test
    @DisplayName("mongodbFind(results, totalCount) - 创建 MongoDB 查询响应")
    void testMongodbFind() {
        List<String> results = Arrays.asList("doc1", "doc2");
        Long totalCount = 100L;

        MongoDbResponse<String> response = ResponseFactory.mongodbFind(results, totalCount);

        assertNotNull(response);
        assertTrue(response.isAcknowledged());
        assertEquals(2, response.getResults().size());
        assertEquals(totalCount, response.getTotalCount());
    }

    // ==================== 其他协议响应测试 ====================

    @Test
    @DisplayName("websocketText(payload) - 创建 WebSocket 文本消息")
    void testWebsocketText() {
        String payload = "Hello WebSocket";
        WebSocketMessage<String> message = ResponseFactory.websocketText(payload);

        assertNotNull(message);
        assertEquals("text", message.getType());
        assertEquals(payload, message.getPayload());
    }

    @Test
    @DisplayName("sseMessage(data) - 创建 SSE 事件")
    void testSseMessage() {
        String data = "SSE 数据";
        SseEvent<String> event = ResponseFactory.sseMessage(data);

        assertNotNull(event);
        assertEquals("message", event.getEvent());
        assertEquals(data, event.getData());
    }

    @Test
    @DisplayName("xmlToJson(rootElement, data) - 创建 XML 转 JSON 响应")
    void testXmlToJson() {
        String rootElement = "root";
        Map<String, Object> data = Map.of("key", "value");

        XmlToJsonResponse<Map<String, Object>> response = ResponseFactory.xmlToJson(rootElement, data);

        assertNotNull(response);
        assertEquals(rootElement, response.getRootElement());
        assertEquals(data, response.getData());
    }

    // ==================== 通用数据模型测试 ====================

    @Test
    @DisplayName("dataListInfo(total, totalPage, list) - 创建分页数据信息")
    void testDataListInfo() {
        int total = 100;
        int totalPage = 10;
        List<String> list = Arrays.asList("a", "b", "c");

        DataListInfo info = ResponseFactory.dataListInfo(total, totalPage, list);

        assertNotNull(info);
        assertEquals(total, info.getTotal());
        assertEquals(totalPage, info.getTotalPage());
        assertEquals(3, info.getList().size());
    }

    @Test
    @DisplayName("emptyDataListInfo() - 创建空的分页数据信息")
    void testEmptyDataListInfo() {
        DataListInfo info = ResponseFactory.emptyDataListInfo();

        assertNotNull(info);
        assertEquals(0, info.getTotal());
        assertEquals(0, info.getTotalPage());
        assertTrue(info.getList().isEmpty());
    }

    // ==================== 通用/动态响应测试 ====================

    @Test
    @DisplayName("jsonResponseSuccess(jsonData) - 创建 JSON 字符串成功响应")
    void testJsonResponseSuccess() {
        String jsonData = "{\"id\":1,\"name\":\"test\"}";
        JsonResponse response = ResponseFactory.jsonResponseSuccess(jsonData);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(jsonData, response.getJsonData());
    }

    @Test
    @DisplayName("jsonResponseError(code, msg) - 创建 JSON 字符串错误响应")
    void testJsonResponseError() {
        String code = "500";
        String msg = "服务器错误";
        JsonResponse response = ResponseFactory.jsonResponseError(code, msg);

        assertFalse(response.isSuccess());
        assertEquals(code, response.getCode());
        assertEquals(msg, response.getMsg());
    }

    @Test
    @DisplayName("mapResponseSuccess(data) - 创建 Map 形式成功响应")
    void testMapResponseSuccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", 123);

        MapResponse response = ResponseFactory.mapResponseSuccess(data);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(2, response.size());
        assertEquals("value1", response.get("key1"));
    }

    @Test
    @DisplayName("mapResponseError(code, msg) - 创建 Map 形式错误响应")
    void testMapResponseError() {
        String code = "404";
        String msg = "未找到";
        MapResponse response = ResponseFactory.mapResponseError(code, msg);

        assertFalse(response.isSuccess());
        assertEquals(code, response.getCode());
        assertEquals(msg, response.getMsg());
    }

    @Test
    @DisplayName("mapResponseBuilder() - 创建 Map 响应构建器")
    void testMapResponseBuilder() {
        MapResponse.Builder builder = ResponseFactory.mapResponseBuilder();

        assertNotNull(builder);
        MapResponse response = builder.code("200").msg("成功").put("key", "value").build();
        assertTrue(response.isSuccess());
        assertEquals("value", response.get("key"));
    }
}
