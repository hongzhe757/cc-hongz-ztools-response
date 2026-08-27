package cc.hongz.ztools.response.i18n;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.ajax.AjaxStatusEnum;
import cc.hongz.ztools.response.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I18nResponseBuilder 单元测试。
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("国际化响应构建器测试")
class I18nResponseBuilderTest {

    private I18nResponseBuilder builder;
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        this.messageSource = source;
        this.builder = new I18nResponseBuilder(messageSource);
    }

    @Test
    @DisplayName("成功响应带状态和数据测试")
    void testSuccessWithStatusAndData() {
        Object data = "test data";
        AjaxResult result = builder.success(AjaxStatusEnum.SUCCESS, data);

        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("成功响应带自定义消息测试")
    void testSuccessWithCustomMessage() {
        Object data = "test data";
        AjaxResult result = builder.success(AjaxStatusEnum.SUCCESS, "自定义消息", data);

        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("自定义消息", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("错误响应带参数测试")
    void testErrorWithArgs() {
        AjaxResult result = builder.error(AjaxStatusEnum.NOT_FOUND, "USER_123");

        assertNotNull(result);
        assertEquals("COMMON_404", result.getCode());
        // 应该从 messages_zh_CN.properties 获取消息并替换参数
        assertTrue(result.getMsg().contains("USER_123") || result.getMsg().contains("资源不存在"));
    }

    @Test
    @DisplayName("错误响应带自定义消息测试")
    void testErrorWithCustomMessage() {
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "自定义错误", null);

        assertNotNull(result);
        assertEquals("COMMON_400", result.getCode());
        assertEquals("自定义错误", result.getMsg());
    }

    @Test
    @DisplayName("从异常构建测试")
    void testFromException() {
        BusinessException exception = new BusinessException(
                AjaxStatusEnum.NOT_FOUND, "admin");

        AjaxResult result = builder.fromException(exception);

        assertNotNull(result);
        assertEquals("COMMON_404", result.getCode());
        // 消息应该包含参数
        assertNotNull(result.getMsg());
    }

    @Test
    @DisplayName("从异常构建带数据测试")
    void testFromExceptionWithData() {
        BusinessException exception = new BusinessException(
                AjaxStatusEnum.FORBIDDEN, "admin");
        Object extraData = "extra info";

        AjaxResult result = builder.fromException(exception, extraData);

        assertNotNull(result);
        assertEquals("COMMON_403", result.getCode());
        assertEquals(extraData, result.getData());
    }

    @Test
    @DisplayName("带区域获取消息测试")
    void testGetMessageWithLocale() {
        // 测试中文
        String zhMessage = builder.getMessage("common.success", "默认消息");
        assertNotNull(zhMessage);

        // 测试英文（需要设置 Locale）
        builder.setDefaultLocale(Locale.US);
        String enMessage = builder.getMessage("common.success", "Default message");
        assertNotNull(enMessage);
    }

    @Test
    @DisplayName("禁用国际化测试")
    void testI18nDisabled() {
        builder.setI18nEnabled(false);

        // PARAM_INVALID 有 {0} 占位符
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "username");

        assertNotNull(result);
        // 禁用国际化后应该使用默认消息并替换参数
        assertEquals("参数无效：username", result.getMsg());
    }

    @Test
    @DisplayName("空消息源测试")
    void testNullMessageSource() {
        I18nResponseBuilder nullBuilder = new I18nResponseBuilder(null);

        AjaxResult result = nullBuilder.success(AjaxStatusEnum.SUCCESS, "data");

        assertNotNull(result);
        assertEquals("操作成功", result.getMsg());
    }

    @Test
    @DisplayName("不存在的消息键测试")
    void testNonExistentMessageKey() {
        String message = builder.getMessage("non.existent.key", "默认消息");

        assertEquals("默认消息", message);
    }

    @Test
    @DisplayName("多参数消息替换测试")
    void testMessageWithMultipleArgs() {
        // 测试多参数替换
        String message = builder.getMessage("order.create.failed", "订单创建失败：{0}，错误码：{1}",
                "ORDER_001", "ERR_500");

        assertNotNull(message);
        // 应该包含参数值
        assertTrue(message.contains("ORDER_001") || message.contains("订单创建失败"));
    }

    @Test
    @DisplayName("设置默认区域测试")
    void testSetDefaultLocale() {
        builder.setDefaultLocale(Locale.ENGLISH);

        Locale locale = builder.getLocale();
        assertNotNull(locale);
    }

    @Test
    @DisplayName("链式调用测试")
    void testFluentApi() {
        I18nResponseBuilder fluentBuilder = new I18nResponseBuilder(messageSource)
                .setDefaultLocale(Locale.SIMPLIFIED_CHINESE)
                .setI18nEnabled(true);

        assertNotNull(fluentBuilder);
        // 验证链式调用返回 this
        assertSame(fluentBuilder, fluentBuilder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE));
    }
}
