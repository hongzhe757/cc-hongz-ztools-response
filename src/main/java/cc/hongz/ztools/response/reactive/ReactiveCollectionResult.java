package cc.hongz.ztools.response.reactive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

/**
 * 响应式集合结果包装器.
 * <p>
 * 使用 {@link Flux} 包装集合数据，适用于返回多个元素的响应式场景。
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
public class ReactiveCollectionResult<T> {

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
    private Flux<T> data;

    /**
     * 总记录数（可选）.
     */
    private Long total;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的集合响应.
     *
     * @param data 数据流（Flux）
     * @param <T>  数据类型
     * @return ReactiveCollectionResult
     */
    public static <T> ReactiveCollectionResult<T> success(final Flux<T> data) {
        return ReactiveCollectionResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .data(data)
                .build();
    }

    /**
     * 创建带总数的集合响应.
     *
     * @param data  数据流（Flux）
     * @param total 总记录数
     * @param <T>   数据类型
     * @return ReactiveCollectionResult
     */
    public static <T> ReactiveCollectionResult<T> success(final Flux<T> data, final Long total) {
        return ReactiveCollectionResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .data(data)
                .total(total)
                .build();
    }

    /**
     * 创建错误的集合响应.
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return ReactiveCollectionResult
     */
    public static <T> ReactiveCollectionResult<T> error(final String code, final String msg) {
        return ReactiveCollectionResult.<T>builder()
                .code(code)
                .msg(msg)
                .data(Flux.empty())
                .build();
    }

    /**
     * 从 Iterable 创建.
     *
     * @param iterable 可迭代对象
     * @param <T>      数据类型
     * @return ReactiveCollectionResult
     */
    public static <T> ReactiveCollectionResult<T> fromIterable(final Iterable<T> iterable) {
        return ReactiveCollectionResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .data(Flux.fromIterable(iterable))
                .build();
    }

    /**
     * 从数组创建.
     *
     * @param array 数组
     * @param <T>   数据类型
     * @return ReactiveCollectionResult
     */
    @SafeVarargs
    public static <T> ReactiveCollectionResult<T> fromArray(final T... array) {
        return ReactiveCollectionResult.<T>builder()
                .code("200")
                .msg("查询成功")
                .data(Flux.fromArray(array))
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
     * 判断是否有总数信息.
     *
     * @return 有返回 true
     */
    public boolean hasTotal() {
        return total != null;
    }
}
