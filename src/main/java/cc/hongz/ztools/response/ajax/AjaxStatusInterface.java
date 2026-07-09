package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.http.HttpStatusCode;

/**
 * AJAX 业务状态码接口.
 * <p>
 * 定义 AJAX 业务状态码的标准契约，支持不同业务域的状态码规范。
 * 业务状态码与 HTTP 状态码解耦，但可映射到对应的 HTTP 状态码。
 * </p>
 * <p>
 * 格式建议：{DOMAIN}_{CODE}，例如 USER_001, ORDER_200
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public interface AjaxStatusInterface {
    
    /**
     * 获取业务状态码.
     * <p>
     * 格式建议：{DOMAIN}_{CODE}，例如 USER_001, ORDER_200
     * </p>
     *
     * @return 业务状态码
     */
    String getCode();
    
    /**
     * 获取默认消息.
     *
     * @return 默认消息文本
     */
    String getDefaultMessage();

    /**
     * 获取国际化消息键（MessageSource key）.
     * <p>
     * 对应 {@code messages_xx_XX.properties} 中的键，如 {@code user.not.found}。
     * 默认返回 {@code null}，由 {@link AjaxStatusMessageKeys} 按业务码解析或回退。
     * </p>
     *
     * @return 消息键；未配置时返回 {@code null}
     */
    default String getMessageKey() {
        return null;
    }
    
    /**
     * 获取对应的 HTTP 状态码.
     *
     * @return HTTP 状态码
     */
    HttpStatusCode getHttpStatus();
    
    /**
     * 判断是否为成功状态.
     * <p>
     * 默认实现基于 HTTP 状态码的成功范围（2xx）
     * </p>
     *
     * @return 成功返回 true
     */
    default boolean isSuccess() {
        return getHttpStatus().isSuccess();
    }
    
    /**
     * 获取业务状态码前缀.
     * <p>
     * 前缀用于标识业务域，例如 USER、ORDER、COMMON。
     * 默认实现从 {@link #getCode()} 中提取下划线前的部分。
     * 子类可覆盖此方法以自定义前缀逻辑。
     * </p>
     *
     * @return 业务状态码前缀，如果无法提取则返回 "COMMON"
     */
    default String getPrefix() {
        return getDomain();
    }

    /**
     * 获取业务域.
     * <p>
     * 从业务状态码中提取业务域标识。
     * 例如：USER_001 的业务域为 USER
     * </p>
     *
     * @return 业务域标识，如果无法提取则返回 "COMMON"
     */
    default String getDomain() {
        final String code = getCode();
        final int underscoreIndex = code.indexOf('_');
        return underscoreIndex > 0 ? code.substring(0, underscoreIndex) : "COMMON";
    }
    
    /**
     * 获取业务码数字部分.
     * <p>
     * 从业务状态码中提取数字部分。
     * 例如：USER_001 的数字部分为 001
     * </p>
     *
     * @return 业务码数字部分，如果无法提取则返回原码
     */
    default String getCodeNumber() {
        final String code = getCode();
        final int underscoreIndex = code.indexOf('_');
        return underscoreIndex > 0 && underscoreIndex < code.length() - 1 
                ? code.substring(underscoreIndex + 1) 
                : code;
    }
}
