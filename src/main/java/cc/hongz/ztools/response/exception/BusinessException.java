package cc.hongz.ztools.response.exception;

import cc.hongz.ztools.status.AjaxStatusInterface;

/**
 * 业务异常.
 * <p>
 * 携带业务状态码的异常类，用于在 Service 层抛出业务相关的异常。
 * 支持参数化消息模板，配合 I18nResponseBuilder 实现国际化。
 * </p>
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 1. 使用 AjaxStatusEnum
 * throw new BusinessException(AjaxStatusEnum.NOT_FOUND, "USER_123");
 *
 * // 2. 使用自定义状态码
 * throw new BusinessException(UserAjaxStatus.USER_DISABLED, "admin");
 *
 * // 3. 多参数替换
 * throw new BusinessException(AjaxStatusEnum.PARAM_INVALID, "username", "password");
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码.
     */
    private final AjaxStatusInterface status;

    /**
     * 消息参数（用于替换消息模板中的占位符）.
     */
    private final Object[] args;

    /**
     * 创建业务异常.
     *
     * @param status 业务状态码
     * @param args   消息参数
     */
    public BusinessException(final AjaxStatusInterface status, final Object... args) {
        super(buildMessage(status, args));
        this.status = status;
        this.args = args;
    }

    /**
     * 创建业务异常（带原因）.
     *
     * @param status 业务状态码
     * @param cause  原始异常
     * @param args   消息参数
     */
    public BusinessException(final AjaxStatusInterface status, final Throwable cause, final Object... args) {
        super(buildMessage(status, args), cause);
        this.status = status;
        this.args = args;
    }

    /**
     * 获取业务状态码.
     *
     * @return 业务状态码
     */
    public AjaxStatusInterface getStatus() {
        return status;
    }

    /**
     * 获取消息参数.
     *
     * @return 消息参数数组
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 获取业务状态码字符串.
     *
     * @return 状态码字符串
     */
    public String getCode() {
        return status.getCode();
    }

    /**
     * 获取默认消息（未格式化）.
     *
     * @return 默认消息模板
     */
    public String getDefaultMessage() {
        return status.getDefaultMessage();
    }

    /**
     * 构建异常消息.
     * <p>
     * 将消息模板中的占位符 {0}, {1} 等替换为实际参数值。
     * </p>
     *
     * @param status 业务状态码
     * @param args   消息参数
     * @return 格式化后的消息
     */
    private static String buildMessage(final AjaxStatusInterface status, final Object... args) {
        if (status == null) {
            return "Unknown business error";
        }
        String message = status.getDefaultMessage();
        if (args != null && args.length > 0 && message != null) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(args[i]));
            }
        }
        return message;
    }
}
