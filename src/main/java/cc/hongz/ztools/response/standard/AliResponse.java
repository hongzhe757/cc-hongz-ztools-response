package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里巴巴风格响应结构.
 * <p>
 * 适用于阿里系项目，使用 success/errorCode/errorMsg/content 字段。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AliResponse<T> {

    /**
     * 是否成功.
     */
    private boolean success;

    /**
     * 错误码（成功时为 null）.
     */
    private String errorCode;

    /**
     * 错误消息（成功时为 null）.
     */
    private String errorMsg;

    /**
     * 数据内容包装.
     */
    private ContentWrapper<T> content;

    /**
     * 内容包装器.
     *
     * @param <T> 数据类型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContentWrapper<T> {
        /**
         * 实际数据.
         */
        private T data;
    }

    // ==================== 成功响应方法 ====================

    /**
     * 返回成功响应（无数据）.
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> AliResponse<T> success() {
        return success(null);
    }

    /**
     * 返回成功响应（带数据）.
     *
     * @param data 数据内容
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> AliResponse<T> success(final T data) {
        return new AliResponse<>(true, null, null, new ContentWrapper<>(data));
    }

    // ==================== 失败响应方法 ====================

    /**
     * 返回失败响应.
     *
     * @param errorCode 错误码
     * @param errorMsg  错误消息
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> AliResponse<T> error(final String errorCode, final String errorMsg) {
        return new AliResponse<>(false, errorCode, errorMsg, null);
    }

    /**
     * 返回失败响应（带数据）.
     *
     * @param errorCode 错误码
     * @param errorMsg  错误消息
     * @param data      数据内容
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> AliResponse<T> error(final String errorCode, final String errorMsg, final T data) {
        return new AliResponse<>(false, errorCode, errorMsg, new ContentWrapper<>(data));
    }
}
