package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResponseConversionStrategy 接口单元测试.
 * <p>
 * 测试策略接口的默认方法：andThen() 和 identity()。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ResponseConversionStrategy 策略接口测试")
class ResponseConversionStrategyTest {

    @Test
    @DisplayName("identity() 返回源对象不变")
    void testIdentity() {
        // Given
        AjaxResult ajax = AjaxResult.success("test data");
        ResponseConversionStrategy<AjaxResult, AjaxResult> strategy = ResponseConversionStrategy.identity();

        // When
        AjaxResult result = strategy.convert(ajax);

        // Then
        assertSame(ajax, result);
    }

    @Test
    @DisplayName("identity() 对 null 输入返回 null")
    void testIdentity_Null() {
        // Given
        ResponseConversionStrategy<AjaxResult, AjaxResult> strategy = ResponseConversionStrategy.identity();

        // When
        AjaxResult result = strategy.convert(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("andThen() 组合两个策略")
    void testAndThen() {
        // Given: Result → AjaxResult → String
        ResponseConversionStrategy<Result<String>, AjaxResult> first = r -> 
            AjaxResult.success(String.valueOf(r.getCode()), r.getMessage(), r.getData());
        ResponseConversionStrategy<AjaxResult, String> second = ajax -> 
            "CODE:" + ajax.getCode() + ",MSG:" + ajax.getMsg();

        ResponseConversionStrategy<Result<String>, String> combined = first.andThen(second);

        // When
        Result<String> source = Result.success(200, "OK", "data");
        String result = combined.convert(source);

        // Then
        assertEquals("CODE:200,MSG:OK", result);
    }

    @Test
    @DisplayName("andThen() 链式组合多个策略")
    void testAndThen_Chain() {
        // Given: Integer → String → Integer (length) → Boolean (>3)
        ResponseConversionStrategy<Integer, String> intToStr = i -> "num:" + i;
        ResponseConversionStrategy<String, Integer> strToInt = s -> s.length();
        ResponseConversionStrategy<Integer, Boolean> intToBool = i -> i > 5;

        ResponseConversionStrategy<Integer, Boolean> pipeline = intToStr
                .andThen(strToInt)
                .andThen(intToBool);

        // When & Then
        assertTrue(pipeline.convert(100));   // "num:100".length()=7 > 5 → true
        assertFalse(pipeline.convert(1));    // "num:1".length()=5 > 5 → false
    }

    @Test
    @DisplayName("andThen() 传入 null 抛出异常")
    void testAndThen_NullAfter() {
        // Given
        ResponseConversionStrategy<String, String> strategy = s -> s.toUpperCase();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> strategy.andThen(null));
    }

    @Test
    @DisplayName("Lambda 表达式作为策略")
    void testLambdaStrategy() {
        // Given
        ResponseConversionStrategy<String, Integer> lengthStrategy = String::length;

        // When
        Integer result = lengthStrategy.convert("hello");

        // Then
        assertEquals(5, result);
    }

    @Test
    @DisplayName("方法引用作为策略")
    void testMethodReferenceStrategy() {
        // Given
        ResponseConversionStrategy<String, String> upperStrategy = String::toUpperCase;

        // When
        String result = upperStrategy.convert("hello");

        // Then
        assertEquals("HELLO", result);
    }

    @Test
    @DisplayName("复杂业务场景：Result 到自定义格式")
    void testComplexBusinessScenario() {
        // Given: 模拟业务转换逻辑
        ResponseConversionStrategy<Result<Object>, String> toJsonLike = result -> {
            if (result.isSuccess()) {
                return "{\"code\":" + result.getCode() + ",\"data\":\"" + result.getData() + "\"}";
            } else {
                return "{\"error\":\"" + result.getMessage() + "\"}";
            }
        };

        // When & Then
        Result<Object> success = Result.success(200, "OK", "user123");
        assertEquals("{\"code\":200,\"data\":\"user123\"}", toJsonLike.convert(success));

        Result<Object> failure = Result.failure(500, "服务器错误");
        assertEquals("{\"error\":\"服务器错误\"}", toJsonLike.convert(failure));
    }
}
