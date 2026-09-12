package cc.hongz.ztools.response.i18n;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.status.AjaxStatusEnum;
import cc.hongz.ztools.response.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link I18nResponseBuilder} 与 classpath 消息资源、Locale 切换的集成测试.
 */
@DisplayName("I18nResponseBuilder 集成测试")
class I18nResponseBuilderIntegrationTest {

    private ResourceBundleMessageSource messageSource;
    private I18nResponseBuilder builder;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        builder = new I18nResponseBuilder(messageSource);
        builder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("LocaleContextHolder 中文 - common.param.invalid 语义键")
    void testSemanticKeyChineseViaLocaleContext() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        String message = builder.getMessage("common.param.invalid", "参数无效：{0}", "email");
        assertEquals("参数无效：email", message);
    }

    @Test
    @DisplayName("LocaleContextHolder 英文 - common.param.invalid 语义键")
    void testSemanticKeyEnglishViaLocaleContext() {
        LocaleContextHolder.setLocale(Locale.US);
        String message = builder.getMessage("common.param.invalid", "Invalid parameter: {0}", "email");
        assertEquals("Invalid parameter: email", message);
    }

    @Test
    @DisplayName("业务状态码键不存在时回退 defaultMessage 并替换参数")
    void testStatusCodeKeyFallbackWithArgs() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "username");
        assertEquals("COMMON_400", result.getCode());
        assertEquals("参数无效：username", result.getMsg());
    }

    @Test
    @DisplayName("英文 Locale - AjaxStatusEnum.NOT_FOUND 命中 common.not.found")
    void testNotFoundEnglishLocale() {
        LocaleContextHolder.setLocale(Locale.US);
        AjaxResult result = builder.error(AjaxStatusEnum.NOT_FOUND);
        assertEquals("COMMON_404", result.getCode());
        assertEquals("Resource not found", result.getMsg());
    }

    @Test
    @DisplayName("fromException - 英文 BusinessException 国际化")
    void testFromExceptionEnglish() {
        LocaleContextHolder.setLocale(Locale.US);
        BusinessException ex = new BusinessException(AjaxStatusEnum.FORBIDDEN);
        AjaxResult result = builder.fromException(ex);
        assertEquals("COMMON_403", result.getCode());
        assertEquals("Forbidden, insufficient permissions", result.getMsg());
    }

    @Test
    @DisplayName("fromException - BusinessException 参数替换")
    void testFromExceptionWithArgs() {
        BusinessException ex = new BusinessException(AjaxStatusEnum.CONFLICT, "用户名已存在");
        AjaxResult result = builder.fromException(ex);
        assertEquals("COMMON_409", result.getCode());
        assertEquals("资源冲突：用户名已存在", result.getMsg());
    }

    @Test
    @DisplayName("error(AjaxStatusEnum) - COMMON 键映射 properties 中文")
    void testCommonStatusI18nMessage() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        AjaxResult result = builder.error(AjaxStatusEnum.NOT_FOUND);
        assertEquals("COMMON_404", result.getCode());
        assertEquals("资源不存在", result.getMsg());
    }

    @Test
    @DisplayName("getMessage - common.param.invalid 中英文切换")
    void testParamInvalidLocaleSwitch() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        assertEquals("参数无效：email", builder.getMessage(
                "common.param.invalid", "参数无效：{0}", "email"));

        LocaleContextHolder.setLocale(Locale.US);
        assertEquals("Invalid parameter: email", builder.getMessage(
                "common.param.invalid", "Invalid parameter: {0}", "email"));
    }

    @Test
    @DisplayName("禁用 i18n 后使用 defaultLocale 格式化")
    void testI18nDisabledUsesFormatMessage() {
        builder.setI18nEnabled(false);
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "fieldA");
        assertEquals("参数无效：fieldA", result.getMsg());
    }

    @Test
    @DisplayName("success - 状态码键存在时使用 i18n 消息")
    void testSuccessUsesI18nMessage() {
        AjaxResult result = builder.success(AjaxStatusEnum.SUCCESS, "payload");
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals("payload", result.getData());
    }
}
