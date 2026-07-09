package cc.hongz.ztools.response.ajax.example;

import cc.hongz.ztools.response.ajax.AjaxStatusInterface;
import cc.hongz.ztools.response.ajax.AjaxStatuses;
import cc.hongz.ztools.response.http.HttpStatusCode;

/**
 * 用户模块 AJAX 业务状态码示例.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public enum UserAjaxStatus implements AjaxStatusInterface {

    USER_NOT_FOUND(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "404"),
            "user.not.found", "用户不存在：{0}", HttpStatusCode.NOT_FOUND),

    USER_DISABLED(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "403"),
            "user.disabled", "用户已被禁用，请联系管理员", HttpStatusCode.FORBIDDEN),

    LOGIN_FAILED(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "401"),
            "user.login.failed", "用户名或密码错误", HttpStatusCode.UNAUTHORIZED),

    USER_ALREADY_EXISTS(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "409"),
            "user.already.exists", "用户已存在：{0}", HttpStatusCode.CONFLICT),

    INVALID_EMAIL(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "422"),
            "user.email.invalid", "邮箱格式不正确：{0}", HttpStatusCode.BAD_REQUEST),

    PHONE_ALREADY_BOUND(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "423"),
            "user.phone.bound", "手机号已被绑定：{0}", HttpStatusCode.CONFLICT),

    CAPTCHA_INVALID(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "424"),
            "user.captcha.invalid", "验证码错误或已过期", HttpStatusCode.BAD_REQUEST),

    USER_SUCCESS(AjaxStatuses.buildCode(PrefixHolder.PREFIX, "000"),
            "common.success", "用户操作成功", HttpStatusCode.OK);

    public static final class PrefixHolder {
        public static final String PREFIX = "USER";
        private PrefixHolder() { }
    }

    private final String code;
    private final String messageKey;
    private final String defaultMessage;
    private final HttpStatusCode httpStatus;

    UserAjaxStatus(final String code, final String messageKey,
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
