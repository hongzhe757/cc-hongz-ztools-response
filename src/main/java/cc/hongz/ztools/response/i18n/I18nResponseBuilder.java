package cc.hongz.ztools.response.i18n;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.status.AjaxStatusInterface;
import cc.hongz.ztools.status.AjaxStatusMessageKeys;
import cc.hongz.ztools.response.exception.BusinessException;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * 国际化响应构建器.
 * <p>
 * 结合业务状态码和 Spring MessageSource，生成多语言响应。
 * 支持从消息资源文件中获取国际化消息，并替换参数占位符。
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
 *
 * // 4. 从异常创建响应
 * BusinessException ex = new BusinessException(AjaxStatusEnum.PARAM_INVALID, "username");
 * AjaxResult error = responseBuilder.fromException(ex);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class I18nResponseBuilder {

    /**
     * Spring 消息源.
     */
    private final MessageSource messageSource;

    /**
     * 默认语言环境.
     */
    private Locale defaultLocale = Locale.SIMPLIFIED_CHINESE;

    /**
     * 是否启用国际化.
     */
    private boolean i18nEnabled = true;

    /**
     * 创建国际化响应构建器.
     *
     * @param messageSource Spring 消息源
     */
    public I18nResponseBuilder(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 设置默认语言环境.
     *
     * @param locale 默认语言环境
     * @return this
     */
    public I18nResponseBuilder setDefaultLocale(final Locale locale) {
        this.defaultLocale = locale;
        return this;
    }

    /**
     * 设置是否启用国际化.
     *
     * @param enabled 是否启用
     * @return this
     */
    public I18nResponseBuilder setI18nEnabled(final boolean enabled) {
        this.i18nEnabled = enabled;
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
        String message = resolveMessage(status, status.getDefaultMessage());
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
        String message = resolveMessage(status, status.getDefaultMessage(), args);
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
        String message = resolveMessage(status, status.getDefaultMessage(), args);
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
        String message = resolveMessage(status, status.getDefaultMessage(), args);
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
        if (!i18nEnabled || messageSource == null) {
            return formatMessage(defaultMessage, args);
        }

        Locale locale = getLocale();
        try {
            return messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (NoSuchMessageException e) {
            return formatMessage(defaultMessage, args);
        }
    }

    /**
     * 按业务状态码解析国际化消息.
     *
     * @param status         业务状态码
     * @param defaultMessage 回退默认消息
     * @param args           消息参数
     * @return 国际化消息
     */
    private String resolveMessage(final AjaxStatusInterface status,
                                  final String defaultMessage,
                                  final Object... args) {
        final String messageKey = AjaxStatusMessageKeys.resolveKey(status);
        return getMessage(messageKey, defaultMessage, args);
    }

    /**
     * 获取当前语言环境.
     *
     * @return 语言环境
     */
    protected Locale getLocale() {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return locale != null ? locale : defaultLocale;
        } catch (Exception e) {
            return defaultLocale;
        }
    }

    /**
     * 格式化消息（替换占位符）.
     *
     * @param message 消息模板
     * @param args    参数
     * @return 格式化后的消息
     */
    private String formatMessage(final String message, final Object... args) {
        if (message == null) {
            return "";
        }
        if (args == null || args.length == 0) {
            return message;
        }
        String result = message;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return result;
    }
}
