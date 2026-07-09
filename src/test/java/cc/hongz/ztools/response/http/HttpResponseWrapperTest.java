package cc.hongz.ztools.response.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HTTP 响应包装器测试.
 * 覆盖 JdkHttpResponseWrapper, SpringResponseEntityWrapper, ApacheHttpResponseWrapper,
 * OkHttpResponseWrapper, FeignResponseWrapper, WebClientResponseWrapper
 */
@DisplayName("HTTP 响应包装器测试")
class HttpResponseWrapperTest {

    // ==================== JdkHttpResponseWrapper 测试 ====================

    @Test
    @DisplayName("JdkHttpResponseWrapper - success 工厂方法 (200)")
    void testJdkHttp_Success() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.success(200, "data", headers);

        assertEquals(200, wrapper.getStatusCode());
        assertEquals("data", wrapper.getBody());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("JdkHttpResponseWrapper - success 工厂方法 (404)")
    void testJdkHttp_ClientError() {
        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.success(404, null, null);

        assertEquals(404, wrapper.getStatusCode());
        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("JdkHttpResponseWrapper - success 工厂方法 (500)")
    void testJdkHttp_ServerError() {
        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.success(500, "error", null);

        assertEquals(500, wrapper.getStatusCode());
        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }

    @Test
    @DisplayName("JdkHttpResponseWrapper - builder 模式")
    void testJdkHttp_Builder() {
        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.<String>builder()
                .statusCode(201)
                .body("created")
                .uri("http://example.com/api")
                .version("HTTP_1_1")
                .successful(true)
                .build();

        assertEquals(201, wrapper.getStatusCode());
        assertEquals("created", wrapper.getBody());
        assertEquals("http://example.com/api", wrapper.getUri());
        assertEquals("HTTP_1_1", wrapper.getVersion());
        assertTrue(wrapper.isSuccess());
    }

    // ==================== SpringResponseEntityWrapper 测试 ====================

    @Test
    @DisplayName("SpringResponseEntityWrapper - success 工厂方法 (200)")
    void testSpring_Success() {
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singletonList("application/json"));

        SpringResponseEntityWrapper<String> wrapper = SpringResponseEntityWrapper.success(200, "OK", "data", headers);

        assertEquals(200, wrapper.getStatusCodeValue());
        assertEquals("OK", wrapper.getStatusCodeName());
        assertEquals("data", wrapper.getBody());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("SpringResponseEntityWrapper - success 工厂方法 (403)")
    void testSpring_ClientError() {
        SpringResponseEntityWrapper<String> wrapper = SpringResponseEntityWrapper.success(403, "Forbidden", null, null);

        assertEquals(403, wrapper.getStatusCodeValue());
        assertEquals("Forbidden", wrapper.getStatusCodeName());
        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("SpringResponseEntityWrapper - success 工厂方法 (503)")
    void testSpring_ServerError() {
        SpringResponseEntityWrapper<String> wrapper = SpringResponseEntityWrapper.success(503, "Service Unavailable", "error", null);

        assertEquals(503, wrapper.getStatusCodeValue());
        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }

    @Test
    @DisplayName("SpringResponseEntityWrapper - builder 模式")
    void testSpring_Builder() {
        SpringResponseEntityWrapper<String> wrapper = SpringResponseEntityWrapper.<String>builder()
                .statusCodeValue(204)
                .statusCodeName("No Content")
                .body(null)
                .successful(true)
                .build();

        assertEquals(204, wrapper.getStatusCodeValue());
        assertEquals("No Content", wrapper.getStatusCodeName());
        assertNull(wrapper.getBody());
        assertTrue(wrapper.isSuccess());
    }

    // ==================== ApacheHttpResponseWrapper 测试 ====================

    @Test
    @DisplayName("ApacheHttpResponseWrapper - builder 和判断方法")
    void testApache_BuilderAndJudgment() {
        ApacheHttpResponseWrapper<String> wrapper = ApacheHttpResponseWrapper.<String>builder()
                .statusCode(200)
                .body("apache data")
                .reasonPhrase("OK")
                .successful(true)
                .build();

        assertEquals(200, wrapper.getStatusCode());
        assertEquals("apache data", wrapper.getBody());
        assertEquals("OK", wrapper.getReasonPhrase());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("ApacheHttpResponseWrapper - 4xx 客户端错误")
    void testApache_ClientError() {
        ApacheHttpResponseWrapper<String> wrapper = ApacheHttpResponseWrapper.<String>builder()
                .statusCode(400)
                .body("bad request")
                .successful(false)
                .build();

        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    // ==================== OkHttpResponseWrapper 测试 ====================

    @Test
    @DisplayName("OkHttpResponseWrapper - builder 和判断方法")
    void testOkHttp_BuilderAndJudgment() {
        OkHttpResponseWrapper<String> wrapper = OkHttpResponseWrapper.<String>builder()
                .code(200)
                .message("OK")
                .body("okhttp data")
                .successful(true)
                .build();

        assertEquals(200, wrapper.getCode());
        assertEquals("OK", wrapper.getMessage());
        assertEquals("okhttp data", wrapper.getBody());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("OkHttpResponseWrapper - 5xx 服务器错误")
    void testOkHttp_ServerError() {
        OkHttpResponseWrapper<String> wrapper = OkHttpResponseWrapper.<String>builder()
                .code(502)
                .message("Bad Gateway")
                .body(null)
                .successful(false)
                .build();

        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }

    // ==================== FeignResponseWrapper 测试 ====================

    @Test
    @DisplayName("FeignResponseWrapper - builder 和判断方法")
    void testFeign_BuilderAndJudgment() {
        FeignResponseWrapper<String> wrapper = FeignResponseWrapper.<String>builder()
                .status(200)
                .reason("OK")
                .body("feign data")
                .successful(true)
                .build();

        assertEquals(200, wrapper.getStatus());
        assertEquals("OK", wrapper.getReason());
        assertEquals("feign data", wrapper.getBody());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("FeignResponseWrapper - 401 未授权")
    void testFeign_Unauthorized() {
        FeignResponseWrapper<String> wrapper = FeignResponseWrapper.<String>builder()
                .status(401)
                .reason("Unauthorized")
                .body(null)
                .successful(false)
                .build();

        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    // ==================== WebClientResponseWrapper 测试 ====================

    @Test
    @DisplayName("WebClientResponseWrapper - builder 和判断方法")
    void testWebClient_BuilderAndJudgment() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(200)
                .statusCodeName("OK")
                .body("webclient data")
                .successful(true)
                .build();

        assertEquals(200, wrapper.getStatusCodeValue());
        assertEquals("OK", wrapper.getStatusCodeName());
        assertEquals("webclient data", wrapper.getBody());
        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 299 (成功)")
    void testWebClient_BoundarySuccess() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(299)
                .successful(true)
                .build();

        assertTrue(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 300 (非成功)")
    void testWebClient_BoundaryRedirect() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(300)
                .successful(false)
                .build();

        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 399 (非客户端错误)")
    void testWebClient_Boundary399() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(399)
                .successful(false)
                .build();

        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 400 (客户端错误)")
    void testWebClient_Boundary400() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(400)
                .successful(false)
                .build();

        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 499 (客户端错误)")
    void testWebClient_Boundary499() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(499)
                .successful(false)
                .build();

        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 500 (服务器错误)")
    void testWebClient_Boundary500() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(500)
                .successful(false)
                .build();

        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }
}
