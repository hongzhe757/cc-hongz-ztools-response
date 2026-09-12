package cc.hongz.ztools.response.i18n;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * classpath 消息资源文件（messages_zh_CN / messages_en_US）加载与内容校验.
 */
@DisplayName("消息资源文件测试")
class MessagesResourceTest {

    private ResourceBundleMessageSource messageSource;

    @BeforeEach
    void setUp() {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(false);
    }

    @Test
    @DisplayName("中文 - common.success")
    void testChineseCommonSuccess() {
        String message = messageSource.getMessage("common.success", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("操作成功", message);
    }

    @Test
    @DisplayName("英文 - common.success")
    void testEnglishCommonSuccess() {
        String message = messageSource.getMessage("common.success", null, Locale.US);
        assertEquals("Operation successful", message);
    }

    @Test
    @DisplayName("中文 - common.param.invalid 参数替换")
    void testChineseParamInvalidWithArgs() {
        String message = messageSource.getMessage(
                "common.param.invalid", new Object[]{"username"}, Locale.SIMPLIFIED_CHINESE);
        assertEquals("参数无效：username", message);
    }

    @Test
    @DisplayName("英文 - common.param.invalid 参数替换")
    void testEnglishParamInvalidWithArgs() {
        String message = messageSource.getMessage(
                "common.param.invalid", new Object[]{"username"}, Locale.US);
        assertEquals("Invalid parameter: username", message);
    }

    @Test
    @DisplayName("中文 - common.not.found")
    void testChineseNotFound() {
        String message = messageSource.getMessage("common.not.found", null, Locale.SIMPLIFIED_CHINESE);
        assertEquals("资源不存在", message);
    }

    @Test
    @DisplayName("英文 - common.not.found")
    void testEnglishNotFound() {
        String message = messageSource.getMessage("common.not.found", null, Locale.US);
        assertEquals("Resource not found", message);
    }

    @Test
    @DisplayName("中文 - common.conflict 参数替换")
    void testChineseConflictWithArgs() {
        String message = messageSource.getMessage(
                "common.conflict", new Object[]{"用户名已存在"}, Locale.SIMPLIFIED_CHINESE);
        assertEquals("资源冲突：用户名已存在", message);
    }

    @Test
    @DisplayName("英文 - common.conflict 参数替换")
    void testEnglishConflictWithArgs() {
        String message = messageSource.getMessage(
                "common.conflict", new Object[]{"username exists"}, Locale.US);
        assertEquals("Resource conflict: username exists", message);
    }
}
