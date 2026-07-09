package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 腾讯风格响应结构.
 * <p>
 * 适用于腾讯系项目，使用 ret/msg/data 字段。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TencentResponse<T> {

    /**
     * 返回码（0 表示成功）.
     */
    private int ret;

    /**
     * 消息内容.
     */
    private String msg;

    /**
     * 数据体.
     */
    private T data;

    // ==================== 成功响应方法 ====================

    /**
     * 返回成功响应（无数据）.
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> TencentResponse<T> success() {
        return success(null);
    }

    /**
     * 返回成功响应（带数据）.
     *
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> TencentResponse<T> success(final T data) {
        return new TencentResponse<>(0, "ok", data);
    }

    /**
     * 返回成功响应（自定义消息）.
     *
     * @param msg  消息内容
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> TencentResponse<T> success(final String msg, final T data) {
        return new TencentResponse<>(0, msg, data);
    }

    /**
     * 返回成功响应（完整参数）.
     *
     * @param ret  返回码
     * @param msg  消息内容
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> TencentResponse<T> success(final int ret, final String msg, final T data) {
        return new TencentResponse<>(ret, msg, data);
    }

    // ==================== 失败响应方法 ====================

    /**
     * 返回失败响应.
     *
     * @param ret 返回码（非 0）
     * @param msg 错误消息
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> TencentResponse<T> error(final int ret, final String msg) {
        return new TencentResponse<>(ret, msg, null);
    }

    /**
     * 返回失败响应（带数据）.
     *
     * @param ret  返回码（非 0）
     * @param msg  错误消息
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 失败响应
     */
    public static <T> TencentResponse<T> error(final int ret, final String msg, final T data) {
        return new TencentResponse<>(ret, msg, data);
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为成功响应.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return ret == 0;
    }
}
