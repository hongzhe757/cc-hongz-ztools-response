package cc.hongz.ztools.response.custom;

import cc.hongz.ztools.response.ResponseConverterFactory;
import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomResponse 转换器单元测试.
 * <p>
 * 展示了如何为自定义响应结构编写完整的单元测试。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("CustomResponse 转换器测试")
class CustomResponseConverterTest {

    @BeforeEach
    void setUp() {
        // 每个测试前注册转换策略
        CustomResponseConverter.registerAll();
    }

    @Test
    @DisplayName("AjaxResult → CustomResponse 转换成功")
    void testFromAjaxResult_Success() {
        // Given
        AjaxResult ajax = AjaxResult.success("200", "操作成功", "test data");

        // When
        CustomResponse<Object> custom = CustomResponseConverter.fromAjaxResult(ajax);

        // Then
        assertNotNull(custom);
        assertEquals(200, custom.getCode());
        assertEquals("操作成功", custom.getMessage());
        assertEquals("test data", custom.getData());
        assertTrue(custom.isSuccess());
        assertNotNull(custom.getTraceId());
        assertTrue(custom.getTimestamp() > 0);
    }

    @Test
    @DisplayName("AjaxResult → CustomResponse 转换失败")
    void testFromAjaxResult_Error() {
        // Given: 使用三参数版本明确指定 code=404
        AjaxResult ajax = AjaxResult.error("404", "用户不存在", null);

        // When
        CustomResponse<Object> custom = CustomResponseConverter.fromAjaxResult(ajax);

        // Then
        assertNotNull(custom);
        assertEquals(404, custom.getCode());
        assertEquals("用户不存在", custom.getMessage());
        assertFalse(custom.isSuccess());
    }

    @Test
    @DisplayName("AjaxResult 为空时返回错误响应")
    void testFromAjaxResult_Null() {
        // When
        CustomResponse<Object> custom = CustomResponseConverter.fromAjaxResult(null);

        // Then
        assertNotNull(custom);
        assertEquals(500, custom.getCode());
        assertEquals("响应对象为空", custom.getMessage());
    }

    @Test
    @DisplayName("Result → CustomResponse 转换成功")
    void testFromResult_Success() {
        // Given
        Result<String> result = Result.success(200, "查询成功", "data");

        // When
        CustomResponse<String> custom = CustomResponseConverter.fromResult(result);

        // Then
        assertNotNull(custom);
        assertEquals(200, custom.getCode());
        assertEquals("查询成功", custom.getMessage());
        assertEquals("data", custom.getData());
        assertTrue(custom.isSuccess());
    }

    @Test
    @DisplayName("Result → CustomResponse 转换失败")
    void testFromResult_Failure() {
        // Given
        Result<Void> result = Result.failure(500, "服务器错误");

        // When
        CustomResponse<Void> custom = CustomResponseConverter.fromResult(result);

        // Then
        assertNotNull(custom);
        assertEquals(500, custom.getCode());
        assertEquals("服务器错误", custom.getMessage());
        assertFalse(custom.isSuccess());
    }

    @Test
    @DisplayName("CustomResponse → AjaxResult 转换成功")
    void testToAjaxResult_Success() {
        // Given
        CustomResponse<String> custom = CustomResponse.success("test data");

        // When
        AjaxResult ajax = CustomResponseConverter.toAjaxResult(custom);

        // Then
        assertNotNull(ajax);
        assertEquals("200", ajax.getCode());
        assertEquals("操作成功", ajax.getMsg());
        assertEquals("test data", ajax.getData());
        assertTrue(ajax.isSuccess());
    }

    @Test
    @DisplayName("CustomResponse → AjaxResult 转换失败")
    void testToAjaxResult_Error() {
        // Given
        CustomResponse<Void> custom = CustomResponse.error(404, "资源未找到");

        // When
        AjaxResult ajax = CustomResponseConverter.toAjaxResult(custom);

        // Then
        assertNotNull(ajax);
        assertEquals("404", ajax.getCode());
        assertEquals("资源未找到", ajax.getMsg());
        assertFalse(ajax.isSuccess());
    }

    @Test
    @DisplayName("CustomResponse → Result 转换")
    void testToResult() {
        // Given
        CustomResponse<String> custom = CustomResponse.success("data");

        // When
        Result<String> result = CustomResponseConverter.toResult(custom);

        // Then
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("data", result.getData());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("通过工厂进行转换")
    void testConvertViaFactory() {
        // Given: AjaxResult.success("user data") 匹配 success(String msg) → data=null→new Object()
        // 使用两参数版本 success(msg, data)
        AjaxResult ajax = AjaxResult.success("ok", "user data");

        // When
        @SuppressWarnings("unchecked")
        CustomResponse<Object> custom = ResponseConverterFactory.convert(ajax, CustomResponse.class);

        // Then
        assertNotNull(custom);
        assertEquals(200, custom.getCode());
        assertEquals("user data", custom.getData());
    }

    @Test
    @DisplayName("双向转换一致性")
    void testBidirectionalConversion() {
        // Given
        CustomResponse<String> original = CustomResponse.success(200, "测试消息", "test data");

        // When: CustomResponse → AjaxResult → CustomResponse
        AjaxResult ajax = CustomResponseConverter.toAjaxResult(original);
        CustomResponse<Object> converted = CustomResponseConverter.fromAjaxResult(ajax);

        // Then
        assertEquals(original.getCode(), converted.getCode());
        assertEquals(original.getMessage(), converted.getMessage());
        assertEquals(original.getData(), converted.getData());
    }

    @Test
    @DisplayName("链式构建器测试")
    void testBuilder() {
        // When
        CustomResponse<String> response = CustomResponse.<String>builder()
                .code(200)
                .message("构建成功")
                .data("builder data")
                .traceId("trace-123")
                .metadata("key1", "value1")
                .metadata("key2", 123)
                .build();

        // Then
        assertEquals(200, response.getCode());
        assertEquals("构建成功", response.getMessage());
        assertEquals("builder data", response.getData());
        assertEquals("trace-123", response.getTraceId());
        assertEquals("value1", (String) response.getMetadata("key1"));
        assertEquals(123, (Integer) response.getMetadata("key2"));
    }

    @Test
    @DisplayName("元数据操作测试")
    void testMetadata() {
        // Given
        CustomResponse<String> response = CustomResponse.success("data");

        // When
        response.addMetadata("userId", 123L);
        response.addMetadata("userName", "张三");

        // Then
        assertEquals(123L, (Long) response.getMetadata("userId"));
        assertEquals("张三", (String) response.getMetadata("userName"));
        assertNull(response.getMetadata("nonExistent"));
    }

    @Test
    @DisplayName("策略模式测试")
    void testStrategyPattern() {
        // Given: 使用两参数版本确保 data 被正确设置
        AjaxResult ajax = AjaxResult.success("msg", "strategy test");

        // When: 使用策略接口
        var strategy = CustomResponseConverter.ajaxToCustomStrategy();
        CustomResponse<Object> custom = strategy.convert(ajax);

        // Then
        assertNotNull(custom);
        assertEquals(200, custom.getCode());
        assertEquals("strategy test", custom.getData());
    }

    @Test
    @DisplayName("策略组合测试")
    void testStrategyComposition() {
        // Given
        AjaxResult ajax = AjaxResult.success("composition test");

        // When: 组合多个策略
        var baseStrategy = CustomResponseConverter.ajaxToCustomStrategy();
        var withTimestamp = baseStrategy.andThen(resp -> {
            resp.setTimestamp(1234567890L);
            return resp;
        });

        CustomResponse<Object> custom = withTimestamp.convert(ajax);

        // Then
        assertEquals(1234567890L, custom.getTimestamp());
    }
}
