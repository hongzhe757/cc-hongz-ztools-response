package cc.hongz.ztools.response.interceptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link InterceptorProperties} 单元测试.
 */
@DisplayName("InterceptorProperties 测试")
class InterceptorPropertiesTest {

    @Test
    @DisplayName("默认值 - enabled、AJAX_RESULT、WRAP、非严格模式")
    void testDefaults() {
        InterceptorProperties properties = new InterceptorProperties();

        assertTrue(properties.isEnabled());
        assertEquals(InterceptorProperties.ResponseStyle.AJAX_RESULT, properties.getResponseStyle());
        assertEquals(InterceptorProperties.DefaultAction.WRAP, properties.getDefaultAction());
        assertFalse(properties.isStrictMode());
        assertFalse(properties.getExcludePackages().isEmpty());
        assertTrue(properties.getExcludePackages().contains("org.springframework"));
    }

    @Test
    @DisplayName("setter - null 时使用安全默认值")
    void testNullSafeSetters() {
        InterceptorProperties properties = new InterceptorProperties();
        properties.setResponseStyle(null);
        properties.setDefaultAction(null);
        properties.setExcludePackages(null);
        properties.setExcludeClasses(null);

        assertEquals(InterceptorProperties.ResponseStyle.AJAX_RESULT, properties.getResponseStyle());
        assertEquals(InterceptorProperties.DefaultAction.WRAP, properties.getDefaultAction());
        assertNotNull(properties.getExcludePackages());
        assertNotNull(properties.getExcludeClasses());
    }

    @Test
    @DisplayName("excludeClasses - 可配置排除类名")
    void testExcludeClasses() {
        InterceptorProperties properties = new InterceptorProperties();
        properties.setExcludeClasses(List.of("com.example.LegacyDto"));

        assertTrue(properties.getExcludeClasses().contains("com.example.LegacyDto"));
    }
}
