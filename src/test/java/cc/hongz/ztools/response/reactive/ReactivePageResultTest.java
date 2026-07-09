package cc.hongz.ztools.response.reactive;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReactivePageResult 单元测试.
 * <p>
 * 测试响应式分页结果包装器的工厂方法和判断方法。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("ReactivePageResult 测试")
class ReactivePageResultTest {

    // ==================== success(Flux, Mono<Long>, current, size) 测试 ====================

    @Test
    @DisplayName("success(Flux, Mono<Long>, current, size) 创建分页响应")
    void testSuccess_WithMonoTotal() {
        // Given
        Flux<String> records = Flux.just("a", "b", "c");
        Mono<Long> total = Mono.just(100L);

        // When
        ReactivePageResult<String> result = ReactivePageResult.success(records, total, 1, 10);

        // Then
        assertEquals("200", result.getCode());
        assertEquals("查询成功", result.getMsg());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
        assertTrue(result.isSuccess());
        
        // 验证总数
        assertEquals(100L, result.getTotal().block());
        // 验证总页数计算: ceil(100/10) = 10
        assertEquals(10, result.getPages().block());
    }

    @Test
    @DisplayName("success(Flux, Long, current, size) 带固定总数的分页响应")
    void testSuccess_WithFixedTotal() {
        // Given
        Flux<Integer> records = Flux.just(1, 2, 3, 4, 5);

        // When
        ReactivePageResult<Integer> result = ReactivePageResult.success(records, 50L, 2, 10);

        // Then
        assertEquals("200", result.getCode());
        assertEquals(2, result.getCurrent());
        assertEquals(10, result.getSize());
        assertEquals(50L, result.getTotal().block());
        // ceil(50/10) = 5
        assertEquals(5, result.getPages().block());
    }

    @Test
    @DisplayName("success() 总页数向上取整")
    void testSuccess_PagesCeiling() {
        // Given: 55条记录，每页10条 → 6页
        Flux<String> records = Flux.empty();

        // When
        ReactivePageResult<String> result = ReactivePageResult.success(records, 55L, 1, 10);

        // Then
        assertEquals(6, result.getPages().block());
    }

    // ==================== error() 工厂方法测试 ====================

    @Test
    @DisplayName("error() 创建错误分页响应")
    void testError() {
        // When
        ReactivePageResult<Object> result = ReactivePageResult.error("500", "查询失败");

        // Then
        assertEquals("500", result.getCode());
        assertEquals("查询失败", result.getMsg());
        assertFalse(result.isSuccess());
        assertEquals(0L, result.getTotal().block());
        assertEquals(0, result.getPages().block());
        
        List<Object> collected = result.getRecords().collectList().block();
        assertTrue(collected.isEmpty());
    }

    // ==================== isSuccess() 判断测试 ====================

    @Test
    @DisplayName("isSuccess() code=200 返回 true")
    void testIsSuccess_Code200() {
        ReactivePageResult<Object> result = ReactivePageResult.success(Flux.empty(), 0L, 1, 10);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=0 返回 true")
    void testIsSuccess_Code0() {
        ReactivePageResult<Object> result = new ReactivePageResult<>();
        result.setCode("0");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=SUCCESS 大小写不敏感")
    void testIsSuccess_CodeSuccessIgnoreCase() {
        ReactivePageResult<Object> result = new ReactivePageResult<>();
        result.setCode("SUCCESS");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() code=404 返回 false")
    void testIsSuccess_Code404() {
        ReactivePageResult<Object> result = ReactivePageResult.error("404", "not found");
        assertFalse(result.isSuccess());
    }

    // ==================== hasNext() 判断测试 ====================

    @Test
    @DisplayName("hasNext() 有下一页返回 true")
    void testHasNext_True() {
        // Given: 当前第1页，共5页
        ReactivePageResult<String> result = ReactivePageResult.success(Flux.empty(), 50L, 1, 10);

        // When & Then
        assertTrue(result.hasNext().block());
    }

    @Test
    @DisplayName("hasNext() 最后一页返回 false")
    void testHasNext_False_LastPage() {
        // Given: 当前第5页，共5页
        ReactivePageResult<String> result = ReactivePageResult.success(Flux.empty(), 50L, 5, 10);

        // When & Then
        assertFalse(result.hasNext().block());
    }

    @Test
    @DisplayName("hasNext() current=null 返回 false")
    void testHasNext_NullCurrent() {
        // Given
        ReactivePageResult<String> result = new ReactivePageResult<>();
        result.setCode("200");
        result.setPages(Mono.just(5));
        result.setCurrent(null);

        // When & Then
        assertFalse(result.hasNext().block());
    }

    // ==================== hasPrevious() 判断测试 ====================

    @Test
    @DisplayName("hasPrevious() 第一页返回 false")
    void testHasPrevious_FirstPage() {
        ReactivePageResult<String> result = ReactivePageResult.success(Flux.empty(), 50L, 1, 10);
        assertFalse(result.hasPrevious());
    }

    @Test
    @DisplayName("hasPrevious() 第二页返回 true")
    void testHasPrevious_SecondPage() {
        ReactivePageResult<String> result = ReactivePageResult.success(Flux.empty(), 50L, 2, 10);
        assertTrue(result.hasPrevious());
    }

    @Test
    @DisplayName("hasPrevious() current=null 返回 false")
    void testHasPrevious_NullCurrent() {
        ReactivePageResult<String> result = new ReactivePageResult<>();
        result.setCurrent(null);
        assertFalse(result.hasPrevious());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder 构建分页响应")
    void testBuilder() {
        // When
        ReactivePageResult<String> result = ReactivePageResult.<String>builder()
                .code("200")
                .msg("OK")
                .records(Flux.just("item"))
                .total(Mono.just(1L))
                .current(1)
                .size(10)
                .pages(Mono.just(1))
                .build();

        // Then
        assertEquals("200", result.getCode());
        assertEquals("OK", result.getMsg());
        assertEquals(1, result.getCurrent());
        assertEquals(10, result.getSize());
    }

    @Test
    @DisplayName("NoArgsConstructor 创建空响应")
    void testNoArgsConstructor() {
        // When
        ReactivePageResult<String> result = new ReactivePageResult<>();

        // Then
        assertNull(result.getCode());
        assertNull(result.getMsg());
        assertNull(result.getRecords());
        assertNull(result.getTotal());
        assertNull(result.getCurrent());
        assertNull(result.getSize());
        assertNull(result.getPages());
    }
}
