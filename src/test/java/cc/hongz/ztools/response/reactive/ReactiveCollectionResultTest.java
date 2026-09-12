package cc.hongz.ztools.response.reactive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ReactiveCollectionResult 单元测试.
 * <p>
 * 测试响应式集合结果包装器的工厂方法和判断方法。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ReactiveCollectionResult 测试")
class ReactiveCollectionResultTest {

    // ==================== success() 工厂方法测试 ====================

    @Test
    @DisplayName("success(Flux) 创建成功集合响应")
    void testSuccessWithFlux() {
        // Given
        Flux<String> data = Flux.just("a", "b", "c");

        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.success(data);

        // Then
        assertEquals("200", result.getCode());
        assertEquals("查询成功", result.getMsg());
        assertNotNull(result.getData());
        assertTrue(result.isSuccess());
        assertFalse(result.hasTotal());
        
        List<String> collected = result.getData().collectList().block();
        assertEquals(3, collected.size());
        assertEquals("a", collected.get(0));
    }

    @Test
    @DisplayName("success(Flux, total) 带总数的集合响应")
    void testSuccessWithTotal() {
        // Given
        Flux<Integer> data = Flux.just(1, 2, 3);

        // When
        ReactiveCollectionResult<Integer> result = ReactiveCollectionResult.success(data, 100L);

        // Then
        assertEquals("200", result.getCode());
        assertTrue(result.hasTotal());
        assertEquals(100L, result.getTotal());
    }

    @Test
    @DisplayName("success(Flux.empty()) 空数据流")
    void testSuccessEmptyFlux() {
        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.success(Flux.empty());

        // Then
        assertTrue(result.isSuccess());
        List<String> collected = result.getData().collectList().block();
        assertTrue(collected.isEmpty());
    }

    // ==================== error() 工厂方法测试 ====================

    @Test
    @DisplayName("error() 创建错误集合响应")
    void testError() {
        // When
        ReactiveCollectionResult<Object> result = ReactiveCollectionResult.error("500", "查询失败");

        // Then
        assertEquals("500", result.getCode());
        assertEquals("查询失败", result.getMsg());
        assertFalse(result.isSuccess());
        assertFalse(result.hasTotal());
        
        List<Object> collected = result.getData().collectList().block();
        assertTrue(collected.isEmpty());
    }

    // ==================== fromIterable() 工厂方法测试 ====================

    @Test
    @DisplayName("fromIterable() 从 Iterable 创建")
    void testFromIterable() {
        // Given
        List<String> items = Arrays.asList("x", "y", "z");

        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.fromIterable(items);

        // Then
        assertTrue(result.isSuccess());
        List<String> collected = result.getData().collectList().block();
        assertEquals(3, collected.size());
        assertEquals("x", collected.get(0));
    }

    @Test
    @DisplayName("fromIterable() 空 Iterable")
    void testFromIterableEmpty() {
        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.fromIterable(List.of());

        // Then
        assertTrue(result.isSuccess());
        List<String> collected = result.getData().collectList().block();
        assertTrue(collected.isEmpty());
    }

    // ==================== fromArray() 工厂方法测试 ====================

    @Test
    @DisplayName("fromArray() 从数组创建")
    void testFromArray() {
        // When
        ReactiveCollectionResult<Integer> result = ReactiveCollectionResult.fromArray(1, 2, 3, 4);

        // Then
        assertTrue(result.isSuccess());
        List<Integer> collected = result.getData().collectList().block();
        assertEquals(4, collected.size());
        assertEquals(1, collected.get(0));
    }

    @Test
    @DisplayName("fromArray() 空数组")
    void testFromArrayEmpty() {
        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.fromArray();

        // Then
        assertTrue(result.isSuccess());
        List<String> collected = result.getData().collectList().block();
        assertTrue(collected.isEmpty());
    }

    // ==================== isSuccess() 判断测试 ====================

    @Test
    @DisplayName("isSuccess() code=200 返回 true")
    void testIsSuccessCode200() {
        ReactiveCollectionResult<Object> result = ReactiveCollectionResult.success(Flux.empty());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=0 返回 true")
    void testIsSuccessCode0() {
        ReactiveCollectionResult<Object> result = new ReactiveCollectionResult<>();
        result.setCode("0");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=success 大小写不敏感")
    void testIsSuccessCodeSuccessIgnoreCase() {
        ReactiveCollectionResult<Object> result = new ReactiveCollectionResult<>();
        result.setCode("SUCCESS");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=500 返回 false")
    void testIsSuccessCode500() {
        ReactiveCollectionResult<Object> result = ReactiveCollectionResult.error("500", "error");
        assertFalse(result.isSuccess());
    }

    // ==================== hasTotal() 判断测试 ====================

    @Test
    @DisplayName("hasTotal() 有总数返回 true")
    void testHasTotalTrue() {
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.success(Flux.empty(), 50L);
        assertTrue(result.hasTotal());
    }

    @Test
    @DisplayName("hasTotal() 无总数返回 false")
    void testHasTotalFalse() {
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.success(Flux.empty());
        assertFalse(result.hasTotal());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder 构建集合响应")
    void testBuilder() {
        // When
        ReactiveCollectionResult<String> result = ReactiveCollectionResult.<String>builder()
                .code("200")
                .msg("OK")
                .data(Flux.just("item"))
                .total(1L)
                .build();

        // Then
        assertEquals("200", result.getCode());
        assertEquals("OK", result.getMsg());
        assertTrue(result.hasTotal());
        assertEquals(1L, result.getTotal());
    }
}
