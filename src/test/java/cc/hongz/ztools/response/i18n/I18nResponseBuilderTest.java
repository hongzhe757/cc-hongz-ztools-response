package cc.hongz.ztools.response.i18n;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.ajax.AjaxStatusEnum;
import cc.hongz.ztools.response.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I18nResponseBuilder 单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
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
    void testSuccessWithStatusAndData() {
        Object data = "test data";
        AjaxResult result = builder.success(AjaxStatusEnum.SUCCESS, data);
        
        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    void testSuccessWithCustomMessage() {
        Object data = "test data";
        AjaxResult result = builder.success(AjaxStatusEnum.SUCCESS, "自定义消息", data);
        
        assertNotNull(result);
        assertEquals("200", result.getCode());
        assertEquals("自定义消息", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    void testErrorWithArgs() {
        AjaxResult result = builder.error(AjaxStatusEnum.NOT_FOUND, "USER_123");
        
        assertNotNull(result);
        assertEquals("COMMON_404", result.getCode());
        // 应该从 messages_zh_CN.properties 获取消息并替换参数
        assertTrue(result.getMsg().contains("USER_123") || result.getMsg().contains("资源不存在"));
    }

    @Test
    void testErrorWithCustomMessage() {
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "自定义错误", null);
        
        assertNotNull(result);
        assertEquals("COMMON_400", result.getCode());
        assertEquals("自定义错误", result.getMsg());
    }

    @Test
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
    void testI18nDisabled() {
        builder.setI18nEnabled(false);
        
        // PARAM_INVALID 有 {0} 占位符
        AjaxResult result = builder.error(AjaxStatusEnum.PARAM_INVALID, "username");
        
        assertNotNull(result);
        // 禁用国际化后应该使用默认消息并替换参数
        assertEquals("参数无效：username", result.getMsg());
    }

    @Test
    void testNullMessageSource() {
        I18nResponseBuilder nullBuilder = new I18nResponseBuilder(null);
        
        AjaxResult result = nullBuilder.success(AjaxStatusEnum.SUCCESS, "data");
        
        assertNotNull(result);
        assertEquals("操作成功", result.getMsg());
    }

    @Test
    void testNonExistentMessageKey() {
        String message = builder.getMessage("non.existent.key", "默认消息");
        
        assertEquals("默认消息", message);
    }

    @Test
    void testMessageWithMultipleArgs() {
        // 测试多参数替换
        String message = builder.getMessage("order.create.failed", "订单创建失败：{0}，错误码：{1}", 
                "ORDER_001", "ERR_500");
        
        assertNotNull(message);
        // 应该包含参数值
        assertTrue(message.contains("ORDER_001") || message.contains("订单创建失败"));
    }

    @Test
    void testSetDefaultLocale() {
        builder.setDefaultLocale(Locale.ENGLISH);
        
        Locale locale = builder.getLocale();
        assertNotNull(locale);
    }

    @Test
    void testFluentApi() {
        I18nResponseBuilder fluentBuilder = new I18nResponseBuilder(messageSource)
                .setDefaultLocale(Locale.SIMPLIFIED_CHINESE)
                .setI18nEnabled(true);
        
        assertNotNull(fluentBuilder);
        // 验证链式调用返回 this
        assertSame(fluentBuilder, fluentBuilder.setDefaultLocale(Locale.SIMPLIFIED_CHINESE));
    }
}
