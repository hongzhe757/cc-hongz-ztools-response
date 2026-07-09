package cc.hongz.ztools.response.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link Result} 原生响应结果的单元测试.
 * <p>
 * 测试成功、失败、快捷方法等功能。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("Result 原生响应结果测试")
class ResultTest {

    // ==================== 成功响应测试 ====================

    @Test
    @DisplayName("success() - 无数据")
    void testSuccess_NoData() {
        // When
        Result<Void> result = Result.success();

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(data) - 带数据")
    void testSuccess_WithData() {
        // Given
        String data = "测试数据";

        // When
        Result<String> result = Result.success(data);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(code, message, data) - 完整参数")
    void testSuccess_FullParams() {
        // Given
        int code = 201;
        String message = "创建成功";
        Map<String, Object> data = Map.of("id", 1, "name", "test");

        // When
        Result<Map<String, Object>> result = Result.success(code, message, data);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
        assertEquals(data, result.getData());
    }

    // ==================== 失败响应测试 ====================

    @Test
    @DisplayName("failure() - 默认错误")
    void testFailure_Default() {
        // When
        Result<Void> result = Result.failure();

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMessage());
    }

    @Test
    @DisplayName("failure(message) - 自定义消息")
    void testFailure_WithMessage() {
        // Given
        String message = "参数校验失败";

        // When
        Result<Void> result = Result.failure(message);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(500, result.getCode());
        assertEquals(message, result.getMessage());
    }

    @Test
    @DisplayName("failure(code, message) - 自定义状态码和消息")
    void testFailure_WithCodeAndMessage() {
        // Given
        int code = 400;
        String message = "请求参数错误";

        // When
        Result<Void> result = Result.failure(code, message);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals(message, result.getMessage());
    }

    // ==================== isSuccess() 判断逻辑测试 ====================

    @Test
    @DisplayName("isSuccess() - 200 状态码为成功")
    void testIsSuccess_Code200() {
        // Given
        Result<String> result = Result.success(200, "成功", null);

        // Then
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() - 非 200 状态码为失败")
    void testIsSuccess_Non200Code() {
        // Given
        Result<String> result = Result.failure(404, "未找到");

        // Then
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() - 其他成功状态码（2xx）")
    void testIsSuccess_Other2xxCodes() {
        // Given - 201 Created
        Result<String> result201 = Result.success(201, "已创建", null);
        // Given - 204 No Content
        Result<String> result204 = Result.success(204, "无内容", null);

        // Then - 只有 200 被认为是成功
        assertTrue(result201.isSuccess());
        assertTrue(result204.isSuccess());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Getter/Setter 正常工作")
    void testGettersAndSetters() {
        // Given
        Result<String> result = new Result<>();
        result.setCode(201);
        result.setMessage("已创建");
        result.setData("新资源");

        // Then
        assertEquals(201, result.getCode());
        assertEquals("已创建", result.getMessage());
        assertEquals("新资源", result.getData());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("null 数据")
    void testNullData() {
        // When
        Result<String> result = Result.success(null);

        // Then
        assertTrue(result.isSuccess());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("空字符串消息")
    void testEmptyMessage() {
        // When
        Result<Void> result = Result.success(200, "", null);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("", result.getMessage());
    }

    @Test
    @DisplayName("复杂对象作为数据")
    void testComplexDataObject() {
        // Given
        Map<String, Object> complexData = Map.of(
            "users", java.util.List.of("user1", "user2"),
            "total", 2,
            "page", 1
        );

        // When
        Result<Map<String, Object>> result = Result.success(complexData);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(complexData, result.getData());
    }

    @Test
    @DisplayName("负数状态码")
    void testNegativeCode() {
        // When
        Result<Void> result = Result.failure(-1, "系统内部错误");

        // Then
        assertFalse(result.isSuccess());
        assertEquals(-1, result.getCode());
        assertEquals("系统内部错误", result.getMessage());
    }

    // ==================== equals/hashCode 测试 ====================

    @Test
    @DisplayName("equals - 相同内容应该相等")
    void testEquals_SameContent() {
        // Given
        Result<String> result1 = Result.success(200, "成功", "数据");
        Result<String> result2 = Result.success(200, "成功", "数据");

        // Then
        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
    }

    @Test
    @DisplayName("equals - 不同内容应该不等")
    void testEquals_DifferentContent() {
        // Given
        Result<String> result1 = Result.success(200, "成功", "数据1");
        Result<String> result2 = Result.success(200, "成功", "数据2");

        // Then
        assertNotEquals(result1, result2);
    }

    // ==================== toString 测试 ====================

    @Test
    @DisplayName("toString 包含所有字段")
    void testToString() {
        // Given
        Result<String> result = Result.success(200, "成功", "数据");

        // When
        String str = result.toString();

        // Then
        assertNotNull(str);
        assertTrue(str.contains("200"));
        assertTrue(str.contains("成功"));
        assertTrue(str.contains("数据"));
    }
}
