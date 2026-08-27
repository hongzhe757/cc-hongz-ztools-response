package cc.hongz.ztools.response.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Java 原生风格的通用返回结果.
 * <p>
 * 适用于传统的 Java 应用或非 Web 场景，使用泛型提供类型安全。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("checkstyle:MagicNumber")
public class Result<T> {

    /**
     * 是否成功.
     */
    private boolean success;

    /**
     * 状态码.
     */
    private int code;

    /**
     * 消息内容.
     */
    private String message;

    /**
     * 数据体.
     */
    private T data;

    // ==================== 成功响应方法 ====================

    /**
     * 返回成功结果（无数据）.
     *
     * @param <T> 数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果（带数据）.
     *
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(final T data) {
        return success("操作成功", data);
    }

    /**
     * 返回成功结果（自定义消息和数据）.
     *
     * @param message 消息内容
     * @param data    数据内容
     * @param <T>     数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(final String message, final T data) {
        return new Result<>(true, 200, message, data);
    }

    /**
     * 返回成功结果（完整参数）.
     *
     * @param code    状态码
     * @param message 消息内容
     * @param data    数据内容
     * @param <T>     数据类型
     * @return 成功结果
     */
    public static <T> Result<T> success(final int code, final String message, final T data) {
        return new Result<>(true, code, message, data);
    }

    // ==================== 失败响应方法 ====================

    /**
     * 返回失败结果（默认消息）.
     *
     * @param <T> 数据类型
     * @return 失败结果
     */
    public static <T> Result<T> failure() {
        return failure("操作失败");
    }

    /**
     * 返回失败结果（自定义消息）.
     *
     * @param message 消息内容
     * @param <T>     数据类型
     * @return 失败结果
     */
    public static <T> Result<T> failure(final String message) {
        return failure(500, message);
    }

    /**
     * 返回失败结果（自定义状态码和消息）.
     *
     * @param code    状态码
     * @param message 消息内容
     * @param <T>     数据类型
     * @return 失败结果
     */
    public static <T> Result<T> failure(final int code, final String message) {
        return new Result<>(false, code, message, null);
    }

    /**
     * 返回失败结果（完整参数）.
     *
     * @param code    状态码
     * @param message 消息内容
     * @param data    数据内容
     * @param <T>     数据类型
     * @return 失败结果
     */
    public static <T> Result<T> failure(final int code, final String message, final T data) {
        return new Result<>(false, code, message, data);
    }

    // ==================== 便捷方法 ====================

    /**
     * 根据布尔值返回成功或失败.
     *
     * @param condition 条件
     * @param data      成功时的数据
     * @param errorMsg  失败时的消息
     * @param <T>       数据类型
     * @return 结果
     */
    public static <T> Result<T> of(final boolean condition, final T data, final String errorMsg) {
        return condition ? success(data) : failure(errorMsg);
    }
}
