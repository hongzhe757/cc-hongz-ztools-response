package cc.hongz.ztools.response.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link HttpStatusCode} 单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("HttpStatusCode 测试")
class HttpStatusCodeTest {

    // ==================== 状态码分类测试 ====================

    @Test
    @DisplayName("isInformational() - 1xx 状态码")
    void testIsInformational() {
        assertTrue(HttpStatusCode.CONTINUE.isInformational());
        assertTrue(HttpStatusCode.SWITCHING_PROTOCOLS.isInformational());
        assertFalse(HttpStatusCode.OK.isInformational());
    }

    @Test
    @DisplayName("isSuccess() - 2xx 状态码")
    void testIsSuccess() {
        assertTrue(HttpStatusCode.OK.isSuccess());
        assertTrue(HttpStatusCode.CREATED.isSuccess());
        assertTrue(HttpStatusCode.NO_CONTENT.isSuccess());
        assertFalse(HttpStatusCode.BAD_REQUEST.isSuccess());
    }

    @Test
    @DisplayName("isRedirection() - 3xx 状态码")
    void testIsRedirection() {
        assertTrue(HttpStatusCode.MOVED_PERMANENTLY.isRedirection());
        assertTrue(HttpStatusCode.FOUND.isRedirection());
        assertTrue(HttpStatusCode.NOT_MODIFIED.isRedirection());
        assertFalse(HttpStatusCode.OK.isRedirection());
    }

    @Test
    @DisplayName("isClientError() - 4xx 状态码")
    void testIsClientError() {
        assertTrue(HttpStatusCode.BAD_REQUEST.isClientError());
        assertTrue(HttpStatusCode.UNAUTHORIZED.isClientError());
        assertTrue(HttpStatusCode.NOT_FOUND.isClientError());
        assertFalse(HttpStatusCode.OK.isClientError());
        assertFalse(HttpStatusCode.INTERNAL_SERVER_ERROR.isClientError());
    }

    @Test
    @DisplayName("isServerError() - 5xx 状态码")
    void testIsServerError() {
        assertTrue(HttpStatusCode.INTERNAL_SERVER_ERROR.isServerError());
        assertTrue(HttpStatusCode.BAD_GATEWAY.isServerError());
        assertTrue(HttpStatusCode.SERVICE_UNAVAILABLE.isServerError());
        assertFalse(HttpStatusCode.OK.isServerError());
        assertFalse(HttpStatusCode.BAD_REQUEST.isServerError());
    }

    @Test
    @DisplayName("isError() - 4xx 或 5xx 状态码")
    void testIsError() {
        assertTrue(HttpStatusCode.BAD_REQUEST.isError());
        assertTrue(HttpStatusCode.NOT_FOUND.isError());
        assertTrue(HttpStatusCode.INTERNAL_SERVER_ERROR.isError());
        assertFalse(HttpStatusCode.OK.isError());
        assertFalse(HttpStatusCode.MOVED_PERMANENTLY.isError());
    }

    // ==================== valueOf() 测试 ====================

    @Test
    @DisplayName("valueOf(int) - 获取已知状态码")
    void testValueOf_KnownCodes() {
        assertEquals(HttpStatusCode.OK, HttpStatusCode.valueOf(200));
        assertEquals(HttpStatusCode.CREATED, HttpStatusCode.valueOf(201));
        assertEquals(HttpStatusCode.BAD_REQUEST, HttpStatusCode.valueOf(400));
        assertEquals(HttpStatusCode.NOT_FOUND, HttpStatusCode.valueOf(404));
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, HttpStatusCode.valueOf(500));
    }

    @Test
    @DisplayName("valueOf(int) - 未知状态码返回 null")
    void testValueOf_UnknownCode() {
        assertNull(HttpStatusCode.valueOf(999));
        assertNull(HttpStatusCode.valueOf(600));
    }

    // ==================== isValid() 测试 ====================

    @Test
    @DisplayName("isValid(int) - 有效状态码")
    void testIsValid_ValidCodes() {
        assertTrue(HttpStatusCode.isValid(200));
        assertTrue(HttpStatusCode.isValid(404));
        assertTrue(HttpStatusCode.isValid(500));
    }

    @Test
    @DisplayName("isValid(int) - 无效状态码")
    void testIsValid_InvalidCodes() {
        assertFalse(HttpStatusCode.isValid(999));
        assertFalse(HttpStatusCode.isValid(600));
    }

    // ==================== getter 方法测试 ====================

    @Test
    @DisplayName("getCode() - 获取状态码数值")
    void testGetCode() {
        assertEquals(200, HttpStatusCode.OK.getCode());
        assertEquals(404, HttpStatusCode.NOT_FOUND.getCode());
        assertEquals(500, HttpStatusCode.INTERNAL_SERVER_ERROR.getCode());
    }

    @Test
    @DisplayName("getReasonPhrase() - 获取原因短语")
    void testGetReasonPhrase() {
        assertEquals("OK", HttpStatusCode.OK.getReasonPhrase());
        assertEquals("Not Found", HttpStatusCode.NOT_FOUND.getReasonPhrase());
        assertEquals("Internal Server Error", HttpStatusCode.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    // ==================== toString() 测试 ====================

    @Test
    @DisplayName("toString() - 格式化输出")
    void testToString() {
        assertEquals("200 OK", HttpStatusCode.OK.toString());
        assertEquals("404 Not Found", HttpStatusCode.NOT_FOUND.toString());
        assertEquals("500 Internal Server Error", HttpStatusCode.INTERNAL_SERVER_ERROR.toString());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("边界值 - 最小和最大状态码")
    void testBoundaryValues() {
        // 最小的 1xx
        assertTrue(HttpStatusCode.CONTINUE.isInformational());
        assertEquals(100, HttpStatusCode.CONTINUE.getCode());
        
        // 最大的 5xx
        assertTrue(HttpStatusCode.NETWORK_AUTHENTICATION_REQUIRED.isServerError());
        assertEquals(511, HttpStatusCode.NETWORK_AUTHENTICATION_REQUIRED.getCode());
    }

    @Test
    @DisplayName("特殊状态码 - 418 I'm a teapot")
    void testSpecialStatusCodes() {
        assertEquals(418, HttpStatusCode.IM_A_TEAPOT.getCode());
        assertEquals("I'm a teapot", HttpStatusCode.IM_A_TEAPOT.getReasonPhrase());
        assertTrue(HttpStatusCode.IM_A_TEAPOT.isClientError());
    }
}
