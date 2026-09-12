package cc.hongz.ztools.response.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void testJdkHttpSuccess() {
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
    void testJdkHttpClientError() {
        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.success(404, null, null);

        assertEquals(404, wrapper.getStatusCode());
        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("JdkHttpResponseWrapper - success 工厂方法 (500)")
    void testJdkHttpServerError() {
        JdkHttpResponseWrapper<String> wrapper = JdkHttpResponseWrapper.success(500, "error", null);

        assertEquals(500, wrapper.getStatusCode());
        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }

    @Test
    @DisplayName("JdkHttpResponseWrapper - builder 模式")
    void testJdkHttpBuilder() {
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
    void testSpringSuccess() {
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
    void testSpringClientError() {
        SpringResponseEntityWrapper<String> wrapper = SpringResponseEntityWrapper.success(403, "Forbidden", null, null);

        assertEquals(403, wrapper.getStatusCodeValue());
        assertEquals("Forbidden", wrapper.getStatusCodeName());
        assertFalse(wrapper.isSuccess());
        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("SpringResponseEntityWrapper - success 工厂方法 (503)")
    void testSpringServerError() {
        SpringResponseEntityWrapper<String> wrapper =
            SpringResponseEntityWrapper.success(503, "Service Unavailable", "error", null);

        assertEquals(503, wrapper.getStatusCodeValue());
        assertFalse(wrapper.isSuccess());
        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }

    @Test
    @DisplayName("SpringResponseEntityWrapper - builder 模式")
    void testSpringBuilder() {
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
    void testApacheBuilderAndJudgment() {
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
    void testApacheClientError() {
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
    void testOkHttpBuilderAndJudgment() {
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
    void testOkHttpServerError() {
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
    void testFeignBuilderAndJudgment() {
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
    void testFeignUnauthorized() {
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
    void testWebClientBuilderAndJudgment() {
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
    void testWebClientBoundarySuccess() {
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
    void testWebClientBoundaryRedirect() {
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
    void testWebClientBoundary399() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(399)
                .successful(false)
                .build();

        assertFalse(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 400 (客户端错误)")
    void testWebClientBoundary400() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(400)
                .successful(false)
                .build();

        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 499 (客户端错误)")
    void testWebClientBoundary499() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(499)
                .successful(false)
                .build();

        assertTrue(wrapper.isClientError());
        assertFalse(wrapper.isServerError());
    }

    @Test
    @DisplayName("WebClientResponseWrapper - 边界值 500 (服务器错误)")
    void testWebClientBoundary500() {
        WebClientResponseWrapper<String> wrapper = WebClientResponseWrapper.<String>builder()
                .statusCodeValue(500)
                .successful(false)
                .build();

        assertFalse(wrapper.isClientError());
        assertTrue(wrapper.isServerError());
    }
}
