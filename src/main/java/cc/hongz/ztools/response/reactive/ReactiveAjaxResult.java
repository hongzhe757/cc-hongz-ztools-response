package cc.hongz.ztools.response.reactive;

import cc.hongz.ztools.response.ajax.AjaxResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * 响应式 AJAX 结果包装器.
 * <p>
 * 将 {@link AjaxResult} 包装为响应式类型 {@link Mono}，适用于 Spring WebFlux 等响应式框架。
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
public class ReactiveAjaxResult<T> {

    /**
     * 状态码.
     */
    private String code;

    /**
     * 消息.
     */
    private String msg;

    /**
     * 数据（Mono 类型）.
     */
    private Mono<T> data;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的响应式结果.
     *
     * @param data 数据（Mono）
     * @param <T>  数据类型
     * @return ReactiveAjaxResult
     */
    public static <T> ReactiveAjaxResult<T> success(final Mono<T> data) {
        return ReactiveAjaxResult.<T>builder()
                .code("200")
                .msg("操作成功")
                .data(data)
                .build();
    }

    /**
     * 创建成功的响应式结果（带消息）.
     *
     * @param msg  消息
     * @param data 数据（Mono）
     * @param <T>  数据类型
     * @return ReactiveAjaxResult
     */
    public static <T> ReactiveAjaxResult<T> success(final String msg, final Mono<T> data) {
        return ReactiveAjaxResult.<T>builder()
                .code("200")
                .msg(msg)
                .data(data)
                .build();
    }

    /**
     * 创建错误的响应式结果.
     *
     * @param code 错误码
     * @param msg  错误消息
     * @param <T>  数据类型
     * @return ReactiveAjaxResult
     */
    public static <T> ReactiveAjaxResult<T> error(final String code, final String msg) {
        return ReactiveAjaxResult.<T>builder()
                .code(code)
                .msg(msg)
                .data(Mono.empty())
                .build();
    }

    /**
     * 从 AjaxResult 转换为响应式结果.
     *
     * @param ajaxResult AjaxResult
     * @param data       数据（Mono）
     * @param <T>        数据类型
     * @return ReactiveAjaxResult
     */
    public static <T> ReactiveAjaxResult<T> from(final AjaxResult ajaxResult, final Mono<T> data) {
        return ReactiveAjaxResult.<T>builder()
                .code(ajaxResult.getCode())
                .msg(ajaxResult.getMsg())
                .data(data)
                .build();
    }

    /**
     * 转换为 AjaxResult（阻塞获取数据）.
     * <p>
     * 注意：此方法会阻塞等待 Mono 完成，仅在必要时使用。
     * </p>
     *
     * @return AjaxResult
     */
    public AjaxResult toAjaxResult() {
        Object resolvedData = data != null ? data.block() : null;
        return new AjaxResult(code, msg, resolvedData);
    }

    /**
     * 转换为 Mono<AjaxResult>.
     *
     * @return Mono<AjaxResult>
     */
    public Mono<AjaxResult> toMono() {
        if (data == null) {
            return Mono.just(new AjaxResult(code, msg, null));
        }
        return data.map(d -> new AjaxResult(code, msg, d))
                .defaultIfEmpty(new AjaxResult(code, msg, null));
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
}
