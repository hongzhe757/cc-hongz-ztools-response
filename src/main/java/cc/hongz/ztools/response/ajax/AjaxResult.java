package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.http.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AJAX 标准消息体.
 * <p>
 * 适用于前后端分离的 Web 应用，返回 JSON 格式的响应数据。
 * </p>
 *
 * @author hongzhe.qin
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AjaxResult {

    /**
     * 状态码.
     *
     * @mock 200
     * @since 1.0.0-SNAPSHOT
     */
    private String code;

    /**
     * 消息内容.
     *
     * @mock 操作成功
     * @since 1.0.0-SNAPSHOT
     */
    private String msg;

    /**
     * 数据体.
     *
     * @since 1.0.0-SNAPSHOT
     */
    private Object data;

    // ==================== 成功响应方法 ====================

    /**
     * 返回成功消息（默认消息）.
     *
     * @return 成功消息
     */
    public static AjaxResult success() {
        return success("操作成功");
    }

    /**
     * 返回成功消息（自定义消息）.
     *
     * @param msg 指定消息内容
     * @return 成功消息
     */
    public static AjaxResult success(final String msg) {
        return success(msg, null);
    }

    /**
     * 返回成功消息（带数据）.
     *
     * @param data 数据内容
     * @return 成功消息
     */
    public static AjaxResult success(final Object data) {
        return success("操作成功", data);
    }

    /**
     * 返回成功消息（自定义消息和数据）.
     *
     * @param msg  消息内容
     * @param data 数据内容
     * @return 成功消息
     */
    public static AjaxResult success(final String msg, final Object data) {
        return success("200", msg, data);
    }

    /**
     * 自定义成功消息.
     *
     * @param code 成功消息码
     * @param msg  消息内容
     * @param data 数据
     * @return 消息内容
     */
    public static AjaxResult success(final String code,
                                     final String msg,
                                     final Object data) {
        return new AjaxResult(code, msg, data == null ? new Object() : data);
    }

    /**
     * 使用 HTTP 状态码返回成功消息.
     *
     * @param status HTTP 状态码
     * @param data   数据内容
     * @return 成功消息
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult success(final HttpStatusCode status, final Object data) {
        return success(String.valueOf(status.getCode()), status.getReasonPhrase(), data);
    }

    /**
     * 使用 HTTP 状态码返回成功消息（自定义消息）.
     *
     * @param status HTTP 状态码
     * @param msg    消息内容
     * @param data   数据内容
     * @return 成功消息
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult success(final HttpStatusCode status, final String msg, final Object data) {
        return success(String.valueOf(status.getCode()), msg, data);
    }

    /**
     * 使用业务状态码创建成功响应.
     *
     * @param businessCode 业务状态码
     * @param data         数据
     * @return AjaxResult
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult success(final AjaxStatusInterface businessCode, final Object data) {
        return new AjaxResult(
            businessCode.getCode(),
            businessCode.getDefaultMessage(),
            data == null ? new Object() : data
        );
    }

    /**
     * 使用业务状态码创建成功响应（自定义消息）.
     *
     * @param businessCode 业务状态码
     * @param msg          消息内容
     * @param data         数据
     * @return AjaxResult
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult success(final AjaxStatusInterface businessCode, final String msg, final Object data) {
        return new AjaxResult(
            businessCode.getCode(),
            msg,
            data == null ? new Object() : data
        );
    }

    // ==================== 失败响应方法 ====================

    /**
     * 返回失败消息（默认消息）.
     *
     * @return 失败消息
     */
    public static AjaxResult error() {
        return error("操作失败");
    }

    /**
     * 返回失败消息（自定义消息）.
     *
     * @param msg 消息内容
     * @return 失败消息
     */
    public static AjaxResult error(final String msg) {
        return error(msg, null);
    }

    /**
     * 返回失败消息（带数据）.
     *
     * @param data 数据内容
     * @return 失败消息
     */
    public static AjaxResult error(final Object data) {
        return error("操作失败", data);
    }

    /**
     * 返回失败消息（自定义消息和数据）.
     *
     * @param msg  消息内容
     * @param data 数据
     * @return 失败消息
     */
    public static AjaxResult error(final String msg, final Object data) {
        return error("500", msg, data);
    }

    /**
     * 自定义错误消息.
     *
     * @param code 消息码
     * @param msg  消息内容
     * @param data 数据
     * @return 消息内容
     */
    public static AjaxResult error(final String code,
                                   final String msg,
                                   final Object data) {
        return new AjaxResult(code, msg, data == null ? new Object() : data);
    }

    /**
     * 使用 HTTP 状态码返回错误消息.
     *
     * @param status HTTP 状态码
     * @param detail 详细错误信息
     * @return 错误消息
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult error(final HttpStatusCode status, final String detail) {
        return error(String.valueOf(status.getCode()), status.getReasonPhrase(), detail);
    }

    /**
     * 使用 HTTP 状态码返回错误消息（带数据）.
     *
     * @param status HTTP 状态码
     * @param msg    消息内容
     * @param data   数据内容
     * @return 错误消息
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult error(final HttpStatusCode status, final String msg, final Object data) {
        return error(String.valueOf(status.getCode()), msg, data);
    }

    /**
     * 使用业务状态码创建错误响应.
     *
     * @param businessCode 业务状态码
     * @param detail       详细错误信息
     * @return AjaxResult
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult error(final AjaxStatusInterface businessCode, final String detail) {
        return new AjaxResult(
            businessCode.getCode(),
            businessCode.getDefaultMessage(),
            detail == null ? new Object() : detail
        );
    }

    /**
     * 使用业务状态码创建错误响应（带参数替换）.
     * <p>
     * 支持使用 {0}, {1} 等占位符进行消息格式化
     * </p>
     *
     * @param businessCode 业务状态码
     * @param args         消息参数
     * @return AjaxResult
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult error(final AjaxStatusInterface businessCode, final Object... args) {
        String message = businessCode.getDefaultMessage();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(args[i]));
            }
        }
        return new AjaxResult(
            businessCode.getCode(),
            message,
            new Object()
        );
    }

    /**
     * 使用业务状态码创建错误响应（自定义消息和数据）.
     *
     * @param businessCode 业务状态码
     * @param msg          消息内容
     * @param data         数据
     * @return AjaxResult
     * @since 1.0.0-SNAPSHOT
     */
    public static AjaxResult error(final AjaxStatusInterface businessCode, final String msg, final Object data) {
        return new AjaxResult(
            businessCode.getCode(),
            msg,
            data == null ? new Object() : data
        );
    }

    /**
     * 特殊错误消息（支持占位符替换）.
     *
     * @param code 消息码
     * @param msg  消息内容（可包含 N 占位符）
     * @param no   占位符替换值
     * @param data 数据
     * @return 特殊错误消息
     */
    public static AjaxResult error(final String code,
                                   final String msg,
                                   final String no,
                                   final Object data) {
        String formattedMsg = msg.replace("N", no);
        return new AjaxResult(code, formattedMsg, data == null ? new Object() : data);
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
