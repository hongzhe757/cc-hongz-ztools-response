package cc.hongz.ztools.response.standard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 标准响应类（AliResponse、TencentResponse、BaiduResponse）的单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("标准响应类测试")
class StandardResponseTest {

    // ==================== AliResponse 测试 ====================

    @Test
    @DisplayName("AliResponse.success() - 无数据")
    void testAliResponseSuccessNoData() {
        // When
        AliResponse<Void> response = AliResponse.success();

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNull(response.getErrorCode());
        assertNull(response.getErrorMsg());
        assertNotNull(response.getContent());
        assertNull(response.getContent().getData());
    }

    @Test
    @DisplayName("AliResponse.success(data) - 带数据")
    void testAliResponseSuccessWithData() {
        // Given
        String data = "阿里数据";

        // When
        AliResponse<String> response = AliResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertNull(response.getErrorCode());
        assertNull(response.getErrorMsg());
        assertNotNull(response.getContent());
        assertEquals(data, response.getContent().getData());
    }

    @Test
    @DisplayName("AliResponse.error() - 错误响应")
    void testAliResponseError() {
        // Given
        String errorCode = "400";
        String errorMsg = "参数错误";

        // When
        AliResponse<Void> response = AliResponse.error(errorCode, errorMsg);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(errorMsg, response.getErrorMsg());
        assertNull(response.getContent());
    }

    @Test
    @DisplayName("AliResponse.error() - 带数据的错误响应")
    void testAliResponseErrorWithData() {
        // Given
        String errorCode = "500";
        String errorMsg = "系统错误";
        Map<String, Object> errorData = Map.of("traceId", "abc123");

        // When
        AliResponse<Map<String, Object>> response = AliResponse.error(errorCode, errorMsg, errorData);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(errorMsg, response.getErrorMsg());
        assertNotNull(response.getContent());
        assertEquals(errorData, response.getContent().getData());
    }

    // ==================== TencentResponse 测试 ====================

    @Test
    @DisplayName("TencentResponse.success() - 无数据")
    void testTencentResponseSuccessNoData() {
        // When
        TencentResponse<Void> response = TencentResponse.success();

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getRet());
        assertEquals("ok", response.getMsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("TencentResponse.success(data) - 带数据")
    void testTencentResponseSuccessWithData() {
        // Given
        String data = "腾讯数据";

        // When
        TencentResponse<String> response = TencentResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(0, response.getRet());
        assertEquals("ok", response.getMsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("TencentResponse.success(msg, data) - 自定义消息")
    void testTencentResponseSuccessCustomMessage() {
        // Given
        String msg = "操作成功";
        String data = "数据";

        // When
        TencentResponse<String> response = TencentResponse.success(msg, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(0, response.getRet());
        assertEquals(msg, response.getMsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("TencentResponse.success(ret, msg, data) - 完整参数")
    void testTencentResponseSuccessFullParams() {
        // Given
        int ret = 0;
        String msg = "成功";
        String data = "数据";

        // When
        TencentResponse<String> response = TencentResponse.success(ret, msg, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(ret, response.getRet());
        assertEquals(msg, response.getMsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("TencentResponse.error() - 错误响应")
    void testTencentResponseError() {
        // Given
        int ret = -1;
        String msg = "系统错误";

        // When
        TencentResponse<Void> response = TencentResponse.error(ret, msg);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(ret, response.getRet());
        assertEquals(msg, response.getMsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("TencentResponse.error() - 带数据的错误响应")
    void testTencentResponseErrorWithData() {
        // Given
        int ret = -2;
        String msg = "业务错误";
        Map<String, Object> errorData = Map.of("detail", "详细信息");

        // When
        TencentResponse<Map<String, Object>> response = TencentResponse.error(ret, msg, errorData);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(ret, response.getRet());
        assertEquals(msg, response.getMsg());
        assertEquals(errorData, response.getData());
    }

    // ==================== BaiduResponse 测试 ====================

    @Test
    @DisplayName("BaiduResponse.success() - 无数据")
    void testBaiduResponseSuccessNoData() {
        // When
        BaiduResponse<Void> response = BaiduResponse.success();

        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrno());
        assertEquals("", response.getErrmsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("BaiduResponse.success(data) - 带数据")
    void testBaiduResponseSuccessWithData() {
        // Given
        String data = "百度数据";

        // When
        BaiduResponse<String> response = BaiduResponse.success(data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrno());
        assertEquals("", response.getErrmsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("BaiduResponse.success(errmsg, data) - 自定义消息")
    void testBaiduResponseSuccessCustomMessage() {
        // Given
        String errmsg = "操作成功";
        String data = "数据";

        // When
        BaiduResponse<String> response = BaiduResponse.success(errmsg, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(0, response.getErrno());
        assertEquals(errmsg, response.getErrmsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("BaiduResponse.success(errno, errmsg, data) - 完整参数")
    void testBaiduResponseSuccessFullParams() {
        // Given
        int errno = 0;
        String errmsg = "成功";
        String data = "数据";

        // When
        BaiduResponse<String> response = BaiduResponse.success(errno, errmsg, data);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(errno, response.getErrno());
        assertEquals(errmsg, response.getErrmsg());
        assertEquals(data, response.getData());
    }

    @Test
    @DisplayName("BaiduResponse.error() - 错误响应")
    void testBaiduResponseError() {
        // Given
        int errno = -1;
        String errmsg = "系统错误";

        // When
        BaiduResponse<Void> response = BaiduResponse.error(errno, errmsg);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errno, response.getErrno());
        assertEquals(errmsg, response.getErrmsg());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("BaiduResponse.error() - 带数据的错误响应")
    void testBaiduResponseErrorWithData() {
        // Given
        int errno = -2;
        String errmsg = "业务错误";
        Map<String, Object> errorData = Map.of("detail", "详细信息");

        // When
        BaiduResponse<Map<String, Object>> response = BaiduResponse.error(errno, errmsg, errorData);

        // Then
        assertFalse(response.isSuccess());
        assertEquals(errno, response.getErrno());
        assertEquals(errmsg, response.getErrmsg());
        assertEquals(errorData, response.getData());
    }

    // ==================== isSuccess() 判断逻辑测试 ====================

    @Test
    @DisplayName("AliResponse.isSuccess() - 正确判断")
    void testAliResponseIsSuccess() {
        // Given
        AliResponse<String> successResponse = AliResponse.success("数据");
        AliResponse<String> errorResponse = AliResponse.error("400", "错误");

        // Then
        assertTrue(successResponse.isSuccess());
        assertFalse(errorResponse.isSuccess());
    }

    @Test
    @DisplayName("TencentResponse.isSuccess() - 正确判断")
    void testTencentResponseIsSuccess() {
        // Given
        TencentResponse<String> successResponse = TencentResponse.success("数据");
        TencentResponse<String> errorResponse = TencentResponse.error(-1, "错误");

        // Then
        assertTrue(successResponse.isSuccess());
        assertFalse(errorResponse.isSuccess());
    }

    @Test
    @DisplayName("BaiduResponse.isSuccess() - 正确判断")
    void testBaiduResponseIsSuccess() {
        // Given
        BaiduResponse<String> successResponse = BaiduResponse.success("数据");
        BaiduResponse<String> errorResponse = BaiduResponse.error(-1, "错误");

        // Then
        assertTrue(successResponse.isSuccess());
        assertFalse(errorResponse.isSuccess());
    }
}
