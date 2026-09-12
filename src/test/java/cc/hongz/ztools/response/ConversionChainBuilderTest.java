package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ConversionChainBuilder} 链式转换构建器的单元测试.
 * <p>
 * 测试链式转换、自定义转换函数、条件转换等功能。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ConversionChainBuilder 链式转换构建器测试")
class ConversionChainBuilderTest {

    @Test
    @DisplayName("from - 设置源对象")
    void testFrom() {
        String source = "test";
        ConversionChainBuilder builder = new ConversionChainBuilder().from(source);

        assertNotNull(builder);
        assertEquals(0, builder.getStepCount());
    }

    @Test
    @DisplayName("transform - 添加自定义转换函数")
    void testTransform() {
        String result = new ConversionChainBuilder()
                .from("hello")
                .<String, String>transform(s -> s.toUpperCase())
                .execute();

        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("transform - 多步转换")
    void testTransformMultipleSteps() {
        Integer result = new ConversionChainBuilder()
                .from("hello")
                .<String, Integer>transform(s -> s.length())
                .<Integer, Integer>transform(len -> len * 2)
                .execute();

        assertEquals(10, result);
    }

    @Test
    @DisplayName("transformIf - 条件转换 (条件为真)")
    void testTransformIfConditionTrue() {
        String result = new ConversionChainBuilder()
                .from("hello")
                .<String>transformIf(s -> s.length() > 3, s -> s.toUpperCase())
                .execute();

        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("transformIf - 条件转换 (条件为假)")
    void testTransformIfConditionFalse() {
        String result = new ConversionChainBuilder()
                .from("hi")
                .<String>transformIf(s -> s.length() > 3, s -> s.toUpperCase())
                .execute();

        assertEquals("hi", result);
    }

    @Test
    @DisplayName("to - 类型转换 (相同类型)")
    void testToSameType() {
        String source = "test";
        String result = new ConversionChainBuilder()
                .from(source)
                .to(String.class)
                .execute();

        assertSame(source, result);
    }

    @Test
    @DisplayName("to - 不支持的自动转换抛出异常")
    void testToUnsupportedConversion() {
        assertThrows(UnsupportedOperationException.class, () -> {
            new ConversionChainBuilder()
                    .from("string")
                    .to(Integer.class)
                    .execute();
        });
    }

    @Test
    @DisplayName("thenTo - to 的别名")
    void testThenTo() {
        String result = new ConversionChainBuilder()
                .from("test")
                .thenTo(String.class)
                .execute();

        assertEquals("test", result);
    }

    @Test
    @DisplayName("execute - 未设置源对象抛出异常")
    void testExecuteNoSource() {
        assertThrows(IllegalStateException.class, () -> {
            new ConversionChainBuilder().execute();
        });
    }

    @Test
    @DisplayName("executeAs - 执行并返回指定类型")
    void testExecuteAs() {
        String result = new ConversionChainBuilder()
                .from("hello")
                .<String, String>transform(s -> s.toUpperCase())
                .executeAs(String.class);

        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("executeAs - 类型不匹配抛出异常")
    void testExecuteAsTypeMismatch() {
        assertThrows(ClassCastException.class, () -> {
            new ConversionChainBuilder()
                    .from("hello")
                    .<String, Integer>transform(s -> s.length())
                    .executeAs(String.class);
        });
    }

    @Test
    @DisplayName("getStepCount - 获取步骤数量")
    void testGetStepCount() {
        ConversionChainBuilder builder = new ConversionChainBuilder()
                .from("test")
                .<String, String>transform(s -> s.toUpperCase())
                .<String, String>transform(s -> s + "!");

        assertEquals(2, builder.getStepCount());
    }

    @Test
    @DisplayName("isEmpty - 检查是否为空")
    void testIsEmpty() {
        ConversionChainBuilder emptyBuilder = new ConversionChainBuilder();
        assertTrue(emptyBuilder.isEmpty());

        ConversionChainBuilder nonEmptyBuilder = new ConversionChainBuilder()
                .from("test")
                .<String, String>transform(s -> s);
        assertFalse(nonEmptyBuilder.isEmpty());
    }

    @Test
    @DisplayName("复杂链式转换 - Result → AjaxResult → 修改")
    void testComplexChain() {
        Result<String> source = Result.success("原始数据");

        AjaxResult result = new ConversionChainBuilder()
                .from(source)
                .<Result<String>, AjaxResult>transform(ResponseConverter::toAjaxResult)
                .<AjaxResult, AjaxResult>transform(ajax -> AjaxResult.success(ajax.getCode(),
                    "Modified: " + ajax.getMsg(), ajax.getData()))
                .execute();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMsg().startsWith("Modified:"));
        assertEquals("原始数据", result.getData());
    }

    @Test
    @DisplayName("链式转换 - 字符串处理管道")
    void testStringProcessingPipeline() {
        String result = new ConversionChainBuilder()
                .from("  Hello World  ")
                .<String, String>transform(String::trim)
                .<String, String>transform(String::toLowerCase)
                .<String, String>transform(s -> s.replace(" ", "_"))
                .execute();

        assertEquals("hello_world", result);
    }

    @Test
    @DisplayName("链式转换 - 数值计算管道")
    void testNumericCalculationPipeline() {
        Integer result = new ConversionChainBuilder()
                .from(5)
                .<Integer, Integer>transform(n -> n * 2)
                .<Integer, Integer>transform(n -> n + 10)
                .<Integer, Integer>transform(n -> n / 2)
                .execute();

        assertEquals(10, result); // (5 * 2 + 10) / 2 = 10
    }

    @Test
    @DisplayName("混合使用 transform 和 transformIf")
    void testMixedTransformAndTransformIf() {
        String result = new ConversionChainBuilder()
                .from("hello")
                .<String, String>transform(s -> s + " world")
                .<String>transformIf(s -> s.length() > 10, String::toUpperCase)
                .<String, String>transform(s -> s + "!")
                .execute();

        assertEquals("HELLO WORLD!", result);
    }
}
