package cc.hongz.ztools.response.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ResponseProperties} 单元测试.
 */
@DisplayName("ResponseProperties 测试")
class ResponsePropertiesTest {

    @Test
    @DisplayName("默认值 - i18n 启用、zh_CN、messages basename")
    void testDefaults() {
        ResponseProperties properties = new ResponseProperties();

        assertTrue(properties.isI18nEnabled());
        assertEquals("zh_CN", properties.getDefaultLocale());
        assertArrayEquals(new String[]{"messages"}, properties.getMessageBasenames());
        assertTrue(properties.isAjaxStatusRegistryEnabled());
    }

    @Test
    @DisplayName("setMessageBasenames - 返回副本防止外部修改")
    void testMessageBasenamesDefensiveCopy() {
        ResponseProperties properties = new ResponseProperties();
        String[] basenames = properties.getMessageBasenames();
        basenames[0] = "changed";

        assertEquals("messages", properties.getMessageBasenames()[0]);
    }
}
