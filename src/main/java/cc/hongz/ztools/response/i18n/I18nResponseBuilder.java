package cc.hongz.ztools.response.i18n;

import cc.hongz.ztools.i18n.I18nMessageResolver;
import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.exception.BusinessException;
import cc.hongz.ztools.status.AjaxStatusInterface;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * 国际化响应构建器.
 * <p>
 * 结合业务状态码和 {@link I18nMessageResolver}，生成多语言响应。
 * 消息解析委托给 {@link I18nMessageResolver}（ztools-i18n 模块）。
 * </p>
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 1. 注入构建器
 * @Autowired
 * private I18nResponseBuilder responseBuilder;
 *
 * // 2. 创建成功响应
 * AjaxResult result = responseBuilder.success(AjaxStatusEnum.SUCCESS, data);
 *
 * // 3. 创建错误响应（带参数）
 * AjaxResult error = responseBuilder.error(UserAjaxStatus.USER_NOT_FOUND, "USER_123");
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class I18nResponseBuilder {

    /**
     * 国际化消息解析器.
     */
    private final I18nMessageResolver resolver;

    /**
     * 创建国际化响应构建器.
     *
     * @param messageSource Spring 消息源
     */
    public I18nResponseBuilder(final MessageSource messageSource) {
        this.resolver = new I18nMessageResolver(messageSource);
    }

    /**
     * 设置默认语言环境.
     *
     * @param locale 默认语言环境
     * @return this
     */
    public I18nResponseBuilder setDefaultLocale(final Locale locale) {
        this.resolver.setDefaultLocale(locale);
        return this;
    }

    /**
     * 设置是否启用国际化.
     *
     * @param enabled 是否启用
     * @return this
     */
    public I18nResponseBuilder setI18nEnabled(final boolean enabled) {
        this.resolver.setI18nEnabled(enabled);
        return this;
    }

    /**
     * 创建成功响应.
     *
     * @param status 业务状态码
     * @param data   数据
     * @return AjaxResult
     */
    public AjaxResult success(final AjaxStatusInterface status, final Object data) {
        String message = resolver.resolve(status);
        return AjaxResult.success(message, data);
    }

    /**
     * 创建成功响应（自定义消息）.
     *
     * @param status 业务状态码
     * @param msg    自定义消息
     * @param data   数据
     * @return AjaxResult
     */
    public AjaxResult success(final AjaxStatusInterface status, final String msg, final Object data) {
        return AjaxResult.success(msg, data);
    }

    /**
     * 创建错误响应.
     *
     * @param status 业务状态码
     * @param args   消息参数
     * @return AjaxResult
     */
    public AjaxResult error(final AjaxStatusInterface status, final Object... args) {
        String message = resolver.resolve(status, args);
        return AjaxResult.error(status.getCode(), message, null);
    }

    /**
     * 创建错误响应（自定义消息）.
     *
     * @param status 业务状态码
     * @param msg    自定义消息
     * @param data   数据
     * @return AjaxResult
     */
    public AjaxResult error(final AjaxStatusInterface status, final String msg, final Object data) {
        return AjaxResult.error(status.getCode(), msg, data);
    }

    /**
     * 从业务异常创建响应.
     *
     * @param exception 业务异常
     * @return AjaxResult
     */
    public AjaxResult fromException(final BusinessException exception) {
        AjaxStatusInterface status = exception.getStatus();
        Object[] args = exception.getArgs();
        String message = resolver.resolve(status, args);
        return AjaxResult.error(status.getCode(), message, null);
    }

    /**
     * 从业务异常创建响应（带额外数据）.
     *
     * @param exception 业务异常
     * @param data      额外数据
     * @return AjaxResult
     */
    public AjaxResult fromException(final BusinessException exception, final Object data) {
        AjaxStatusInterface status = exception.getStatus();
        Object[] args = exception.getArgs();
        String message = resolver.resolve(status, args);
        return AjaxResult.error(status.getCode(), message, data);
    }

    /**
     * 获取国际化消息.
     *
     * @param code           消息代码（对应 properties 文件中的 key）
     * @param defaultMessage 默认消息
     * @param args           消息参数
     * @return 国际化消息
     */
    public String getMessage(final String code, final String defaultMessage, final Object... args) {
        return resolver.getMessage(code, defaultMessage, args);
    }
}
