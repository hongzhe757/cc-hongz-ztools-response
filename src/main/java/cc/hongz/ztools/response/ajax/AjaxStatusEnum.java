package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.http.HttpStatusCode;

/**
 * 通用 AJAX 业务状态码枚举.
 * <p>
 * 提供系统级别的通用 AJAX 业务状态码，适用于各种业务场景。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public enum AjaxStatusEnum implements AjaxStatusInterface {

    /**
     * 操作成功.
     */
    SUCCESS(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "000"),
            "common.success", "操作成功", HttpStatusCode.OK),

    /**
     * 参数无效.
     */
    PARAM_INVALID(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "400"),
            "common.param.invalid", "参数无效：{0}", HttpStatusCode.BAD_REQUEST),

    /**
     * 未授权.
     */
    UNAUTHORIZED(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "401"),
            "common.unauthorized", "未授权", HttpStatusCode.UNAUTHORIZED),

    /**
     * 禁止访问.
     */
    FORBIDDEN(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "403"),
            "common.forbidden", "禁止访问", HttpStatusCode.FORBIDDEN),

    /**
     * 资源不存在.
     */
    NOT_FOUND(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "404"),
            "common.not.found", "资源不存在", HttpStatusCode.NOT_FOUND),

    /**
     * 方法不允许.
     */
    METHOD_NOT_ALLOWED(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "405"),
            "common.method.not.allowed", "方法不允许", HttpStatusCode.METHOD_NOT_ALLOWED),

    /**
     * 请求超时.
     */
    REQUEST_TIMEOUT(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "408"),
            "common.timeout", "请求超时", HttpStatusCode.REQUEST_TIMEOUT),

    /**
     * 冲突.
     */
    CONFLICT(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "409"),
            "common.conflict", "资源冲突", HttpStatusCode.CONFLICT),

    /**
     * 请求过多（限流）.
     */
    TOO_MANY_REQUESTS(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "429"),
            "common.too.many.requests", "请求过于频繁，请稍后再试", HttpStatusCode.TOO_MANY_REQUESTS),

    /**
     * 系统内部错误.
     */
    INTERNAL_ERROR(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "500"),
            "common.internal.error", "系统内部错误", HttpStatusCode.INTERNAL_SERVER_ERROR),

    /**
     * 功能未实现.
     */
    NOT_IMPLEMENTED(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "501"),
            null, "功能未实现", HttpStatusCode.NOT_IMPLEMENTED),

    /**
     * 服务暂不可用.
     */
    SERVICE_UNAVAILABLE(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "503"),
            "common.service.unavailable", "服务暂不可用，请稍后重试", HttpStatusCode.SERVICE_UNAVAILABLE),

    /**
     * 网关超时.
     */
    GATEWAY_TIMEOUT(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "504"),
            "common.timeout", "网关超时", HttpStatusCode.GATEWAY_TIMEOUT);

    /**
     * 通用 AJAX 业务状态码前缀常量持有者.
     */
    public static final class PrefixHolder {
        /** 通用业务状态码前缀. */
        public static final String PREFIX = "COMMON";
        private PrefixHolder() { }
    }

    private final String code;
    private final String messageKey;
    private final String defaultMessage;
    private final HttpStatusCode httpStatus;

    AjaxStatusEnum(final String code, final String messageKey,
                   final String defaultMessage, final HttpStatusCode httpStatus) {
        this.code = code;
        this.messageKey = messageKey;
        this.defaultMessage = defaultMessage;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public HttpStatusCode getHttpStatus() {
        return httpStatus;
    }
}
