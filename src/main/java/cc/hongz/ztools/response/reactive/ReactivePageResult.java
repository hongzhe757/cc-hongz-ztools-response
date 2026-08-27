package cc.hongz.ztools.response.reactive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 响应式分页结果包装器.
 * <p>
 * 使用响应式类型包装分页数据，适用于 Spring WebFlux 的分页查询场景。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReactivePageResult<T> {

    /**
     * 状态码.
     */
    private String code;

    /**
     * 消息.
     */
    private String msg;

    /**
     * 数据流（Flux 类型）.
     */
    private Flux<T> records;

    /**
     * 总记录数（Mono 类型，异步获取）.
     */
    private Mono<Long> total;

    /**
     * 当前页码.
     */
    private Integer current;

    /**
     * 每页大小.
     */
    private Integer size;

    /**
     * 总页数（Mono 类型，异步计算）.
     */
    private Mono<Integer> pages;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的分页响应.
     *
     * @param records 数据流
     * @param total   总数（Mono）
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return ReactivePageResult
     */
    public static <T> ReactivePageResult<T> success(
            final Flux<T> records,
            final Mono<Long> total,
            final Integer current,
            final Integer size
        ) {
        return ReactivePageResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .records(records)
                .total(total)
                .current(current)
                .size(size)
                .pages(total.map(t -> (int) Math.ceil((double) t / size)))
                .build();
    }

    /**
     * 创建带固定总数的分页响应.
     *
     * @param records 数据流
     * @param total   总数
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return ReactivePageResult
     */
    public static <T> ReactivePageResult<T> success(
            final Flux<T> records,
            final Long total,
            final Integer current,
            final Integer size
        ) {
        return ReactivePageResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .records(records)
                .total(Mono.just(total))
                .current(current)
                .size(size)
                .pages(Mono.just((int) Math.ceil((double) total / size)))
                .build();
    }

    /**
     * 创建错误的分页响应.
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return ReactivePageResult
     */
    public static <T> ReactivePageResult<T> error(final String code, final String msg) {
        return ReactivePageResult.<T>builder()
                .code(code)
                .msg(msg)
                .records(Flux.empty())
                .total(Mono.just(0L))
                .pages(Mono.just(0))
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为成功响应.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return "200".equals(code) || "0".equals(code) || "success".equalsIgnoreCase(code);
    }

    /**
     * 判断是否有下一页.
     *
     * @return 有返回 true
     */
    public Mono<Boolean> hasNext() {
        return pages.map(p -> current != null && current < p);
    }

    /**
     * 判断是否有上一页.
     *
     * @return 有返回 true
     */
    public boolean hasPrevious() {
        return current != null && current > 1;
    }
}
