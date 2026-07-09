package cc.hongz.ztools.response.reactive;

import cc.hongz.ztools.response.ajax.AjaxResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReactiveAjaxResult 单元测试.
 * <p>
 * 测试响应式 AJAX 结果包装器的工厂方法、转换方法和判断方法。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ReactiveAjaxResult 测试")
class ReactiveAjaxResultTest {

    // ==================== success() 工厂方法测试 ====================

    @Test
    @DisplayName("success(Mono) 创建成功响应")
    void testSuccess_WithMono() {
        // Given
        Mono<String> data = Mono.just("test data");

        // When
        ReactiveAjaxResult<String> result = ReactiveAjaxResult.success(data);

        // Then
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertNotNull(result.getData());
        assertTrue(result.isSuccess());
        assertEquals("test data", result.getData().block());
    }

    @Test
    @DisplayName("success(msg, Mono) 带消息的成功响应")
    void testSuccess_WithMsgAndMono() {
        // Given
        Mono<Integer> data = Mono.just(42);

        // When
        ReactiveAjaxResult<Integer> result = ReactiveAjaxResult.success("查询成功", data);

        // Then
        assertEquals("200", result.getCode());
        assertEquals("查询成功", result.getMsg());
        assertEquals(42, result.getData().block());
    }

    @Test
    @DisplayName("success(Mono.empty()) 空数据成功响应")
    void testSuccess_EmptyMono() {
        // When
        ReactiveAjaxResult<String> result = ReactiveAjaxResult.success(Mono.empty());

        // Then
        assertTrue(result.isSuccess());
        assertNull(result.getData().block());
    }

    // ==================== error() 工厂方法测试 ====================

    @Test
    @DisplayName("error() 创建错误响应")
    void testError() {
        // When
        ReactiveAjaxResult<Object> result = ReactiveAjaxResult.error("500", "服务器内部错误");

        // Then
        assertEquals("500", result.getCode());
        assertEquals("服务器内部错误", result.getMsg());
        assertFalse(result.isSuccess());
        assertNotNull(result.getData());
        assertNull(result.getData().block());
    }

    @Test
    @DisplayName("error() 客户端错误")
    void testError_ClientError() {
        // When
        ReactiveAjaxResult<Void> result = ReactiveAjaxResult.error("404", "资源不存在");

        // Then
        assertEquals("404", result.getCode());
        assertFalse(result.isSuccess());
    }

    // ==================== from() 工厂方法测试 ====================

    @Test
    @DisplayName("from(AjaxResult, Mono) 从 AjaxResult 转换")
    void testFrom() {
        // Given
        AjaxResult ajax = AjaxResult.success("200", "OK", null);
        Mono<String> data = Mono.just("converted data");

        // When
        ReactiveAjaxResult<String> result = ReactiveAjaxResult.from(ajax, data);

        // Then
        assertEquals("200", result.getCode());
        assertEquals("OK", result.getMsg());
        assertEquals("converted data", result.getData().block());
    }

    // ==================== toAjaxResult() 转换测试 ====================

    @Test
    @DisplayName("toAjaxResult() 转换为 AjaxResult")
    void testToAjaxResult() {
        // Given
        ReactiveAjaxResult<String> reactive = ReactiveAjaxResult.success(Mono.just("data"));

        // When
        AjaxResult ajax = reactive.toAjaxResult();

        // Then
        assertNotNull(ajax);
        assertEquals("200", ajax.getCode());
        assertEquals("操作成功", ajax.getMsg());
        assertEquals("data", ajax.getData());
    }

    @Test
    @DisplayName("toAjaxResult() null data 转换")
    void testToAjaxResult_NullData() {
        // Given
        ReactiveAjaxResult<String> reactive = new ReactiveAjaxResult<>();
        reactive.setCode("200");
        reactive.setMsg("OK");
        reactive.setData(null);

        // When
        AjaxResult ajax = reactive.toAjaxResult();

        // Then
        assertEquals("200", ajax.getCode());
        assertNull(ajax.getData());
    }

    // ==================== toMono() 转换测试 ====================

    @Test
    @DisplayName("toMono() 转换为 Mono<AjaxResult>")
    void testToMono() {
        // Given
        ReactiveAjaxResult<String> reactive = ReactiveAjaxResult.success(Mono.just("mono data"));

        // When
        Mono<AjaxResult> mono = reactive.toMono();
        AjaxResult ajax = mono.block();

        // Then
        assertNotNull(ajax);
        assertEquals("200", ajax.getCode());
        assertEquals("mono data", ajax.getData());
    }

    @Test
    @DisplayName("toMono() null data 时返回空数据的 AjaxResult")
    void testToMono_NullData() {
        // Given
        ReactiveAjaxResult<String> reactive = new ReactiveAjaxResult<>();
        reactive.setCode("200");
        reactive.setMsg("OK");
        reactive.setData(null);

        // When
        AjaxResult ajax = reactive.toMono().block();

        // Then
        assertNotNull(ajax);
        assertNull(ajax.getData());
    }

    // ==================== isSuccess() 判断测试 ====================

    @Test
    @DisplayName("isSuccess() code=200 返回 true")
    void testIsSuccess_Code200() {
        ReactiveAjaxResult<Object> result = ReactiveAjaxResult.success(Mono.empty());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=0 返回 true")
    void testIsSuccess_Code0() {
        ReactiveAjaxResult<Object> result = new ReactiveAjaxResult<>();
        result.setCode("0");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=success 返回 true")
    void testIsSuccess_CodeSuccess() {
        ReactiveAjaxResult<Object> result = new ReactiveAjaxResult<>();
        result.setCode("success");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=SUCCESS 大小写不敏感")
    void testIsSuccess_CodeSuccessUpperCase() {
        ReactiveAjaxResult<Object> result = new ReactiveAjaxResult<>();
        result.setCode("SUCCESS");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=500 返回 false")
    void testIsSuccess_Code500() {
        ReactiveAjaxResult<Object> result = ReactiveAjaxResult.error("500", "error");
        assertFalse(result.isSuccess());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder 构建响应")
    void testBuilder() {
        // When
        ReactiveAjaxResult<String> result = ReactiveAjaxResult.<String>builder()
                .code("201")
                .msg("Created")
                .data(Mono.just("new resource"))
                .build();

        // Then
        assertEquals("201", result.getCode());
        assertEquals("Created", result.getMsg());
        assertEquals("new resource", result.getData().block());
    }
}
