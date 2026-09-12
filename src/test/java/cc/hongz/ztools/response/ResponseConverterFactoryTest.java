package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ResponseConverterFactory} 响应转换器工厂的单元测试.
 * <p>
 * 测试工厂模式的注册、获取、转换等功能。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ResponseConverterFactory 转换器工厂测试")
class ResponseConverterFactoryTest {

    @BeforeEach
    void setUp() {
        // 每个测试前清空并重新注册默认策略
        ResponseConverterFactory.clear();
    }

    @AfterEach
    void tearDown() {
        // 每个测试后清理，避免测试间相互影响
        ResponseConverterFactory.clear();
    }

    @Test
    @DisplayName("获取预定义策略：Result → AjaxResult")
    void testGetStrategyResultToAjaxResult() {
        // When
        ResponseConversionStrategy<Result, AjaxResult> strategy = 
            ResponseConverterFactory.getStrategy(Result.class, AjaxResult.class);

        // Then
        assertNotNull(strategy, "应该存在 Result → AjaxResult 的预定义策略");
    }

    @Test
    @DisplayName("获取预定义策略：AjaxResult → Result")
    void testGetStrategyAjaxResultToResult() {
        // When
        ResponseConversionStrategy<AjaxResult, Result> strategy = 
            ResponseConverterFactory.getStrategy(AjaxResult.class, Result.class);

        // Then
        assertNotNull(strategy, "应该存在 AjaxResult → Result 的预定义策略");
    }

    @Test
    @DisplayName("检查策略是否存在")
    void testHasStrategy() {
        // Then
        assertTrue(ResponseConverterFactory.hasStrategy(Result.class, AjaxResult.class));
        assertTrue(ResponseConverterFactory.hasStrategy(AjaxResult.class, Result.class));
        assertFalse(ResponseConverterFactory.hasStrategy(String.class, Integer.class));
    }

    @Test
    @DisplayName("执行转换：Result → AjaxResult")
    void testConvertResultToAjaxResult() {
        // Given
        Result<String> result = Result.success("测试数据");

        // When
        AjaxResult ajaxResult = ResponseConverterFactory.convert(result, AjaxResult.class);

        // Then
        assertNotNull(ajaxResult);
        assertTrue(ajaxResult.isSuccess());
        assertEquals("200", ajaxResult.getCode());
        assertEquals("操作成功", ajaxResult.getMsg());
        assertEquals("测试数据", ajaxResult.getData());
    }

    @Test
    @DisplayName("执行转换：AjaxResult → Result")
    void testConvertAjaxResultToResult() {
        // Given
        AjaxResult ajaxResult = AjaxResult.success("200", "操作成功", "测试数据");

        // When
        Result<String> result = ResponseConverterFactory.convert(ajaxResult, Result.class);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("测试数据", result.getData());
    }

    @Test
    @DisplayName("转换失败：未注册的策略")
    void testConvertUnregisteredStrategy() {
        // Given
        String source = "测试字符串";

        // When & Then
        assertThrows(UnsupportedOperationException.class, () -> {
            ResponseConverterFactory.convert(source, Integer.class);
        }, "未注册的转换策略应该抛出 UnsupportedOperationException");
    }

    @Test
    @DisplayName("转换失败：源对象为空")
    void testConvertNullSource() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverterFactory.convert(null, AjaxResult.class);
        }, "源对象为空应该抛出 IllegalArgumentException");
    }

    @Test
    @DisplayName("转换失败：目标类型为空")
    void testConvertNullTargetType() {
        // Given
        Result<String> result = Result.success("测试");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverterFactory.convert(result, null);
        }, "目标类型为空应该抛出 IllegalArgumentException");
    }

    @Test
    @DisplayName("注册自定义策略")
    void testRegisterCustomStrategy() {
        // Given
        ResponseConversionStrategy<String, Integer> customStrategy = source -> source.length();

        // When
        ResponseConverterFactory.register(String.class, Integer.class, customStrategy);

        // Then
        assertTrue(ResponseConverterFactory.hasStrategy(String.class, Integer.class));
        ResponseConversionStrategy<String, Integer> retrieved = 
            ResponseConverterFactory.getStrategy(String.class, Integer.class);
        assertNotNull(retrieved);
        assertEquals(5, retrieved.convert("hello"));
    }

    @Test
    @DisplayName("注册策略：参数为空校验")
    void testRegisterNullParameters() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverterFactory.register(null, AjaxResult.class, source -> null);
        }, "源类型为空应该抛出异常");

        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverterFactory.register(Result.class, null, source -> null);
        }, "目标类型为空应该抛出异常");

        assertThrows(IllegalArgumentException.class, () -> {
            ResponseConverterFactory.register(Result.class, AjaxResult.class, null);
        }, "策略为空应该抛出异常");
    }

    @Test
    @DisplayName("移除策略")
    void testUnregister() {
        // Given
        assertTrue(ResponseConverterFactory.hasStrategy(Result.class, AjaxResult.class));

        // When
        ResponseConverterFactory.unregister(Result.class, AjaxResult.class);

        // Then
        assertFalse(ResponseConverterFactory.hasStrategy(Result.class, AjaxResult.class));
    }

    @Test
    @DisplayName("获取策略数量")
    void testGetStrategyCount() {
        // When
        int count = ResponseConverterFactory.getStrategyCount();

        // Then
        assertTrue(count > 0, "默认应该注册多个转换策略");
    }

    @Test
    @DisplayName("清空所有策略后重新注册默认策略")
    void testClear() {
        // Given
        int originalCount = ResponseConverterFactory.getStrategyCount();
        assertTrue(originalCount > 0);

        // When
        ResponseConverterFactory.clear();

        // Then
        int newCount = ResponseConverterFactory.getStrategyCount();
        assertEquals(originalCount, newCount, "clear() 后应该重新注册默认策略");
    }
}
