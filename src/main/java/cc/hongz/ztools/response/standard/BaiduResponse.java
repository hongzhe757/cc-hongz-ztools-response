package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 百度风格响应结构.
 * <p>
 * 适用于百度系项目，使用 errno/errmsg/data 字段。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaiduResponse<T> {

    /**
     * 错误码（0 表示成功）.
     */
    private int errno;

    /**
     * 错误消息.
     */
    private String errmsg;

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
    public static <T> BaiduResponse<T> success() {
        return success(null);
    }

    /**
     * 返回成功响应（带数据）.
     *
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> BaiduResponse<T> success(final T data) {
        return new BaiduResponse<>(0, "", data);
    }

    /**
     * 返回成功响应（自定义消息）.
     *
     * @param errmsg 消息内容
     * @param data   数据内容
     * @param <T>    数据类型
     * @return 成功响应
     */
    public static <T> BaiduResponse<T> success(final String errmsg, final T data) {
        return new BaiduResponse<>(0, errmsg, data);
    }

    /**
     * 返回成功响应（完整参数）.
     *
     * @param errno  错误码
     * @param errmsg 消息内容
     * @param data   数据内容
     * @param <T>    数据类型
     * @return 成功响应
     */
    public static <T> BaiduResponse<T> success(final int errno, final String errmsg, final T data) {
        return new BaiduResponse<>(errno, errmsg, data);
    }

    // ==================== 失败响应方法 ====================

    /**
     * 返回失败响应.
     *
     * @param errno  错误码（非 0）
     * @param errmsg 错误消息
     * @param <T>    数据类型
     * @return 失败响应
     */
    public static <T> BaiduResponse<T> error(final int errno, final String errmsg) {
        return new BaiduResponse<>(errno, errmsg, null);
    }

    /**
     * 返回失败响应（带数据）.
     *
     * @param errno  错误码（非 0）
     * @param errmsg 错误消息
     * @param data   数据内容
     * @param <T>    数据类型
     * @return 失败响应
     */
    public static <T> BaiduResponse<T> error(final int errno, final String errmsg, final T data) {
        return new BaiduResponse<>(errno, errmsg, data);
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为成功响应.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return errno == 0;
    }
}
