package cc.hongz.ztools.response.config;

import cc.hongz.ztools.status.AjaxStatusRegistry;
import cc.hongz.ztools.response.handler.GlobalExceptionHandler;
import cc.hongz.ztools.response.i18n.I18nResponseBuilder;
import cc.hongz.ztools.response.interceptor.InterceptorProperties;
import cc.hongz.ztools.response.interceptor.ResponseInterceptor;
import cc.hongz.ztools.response.interceptor.strategy.EntityToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.MapToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.StringToAjaxResultStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link ResponseAutoConfiguration} Spring Bean 注册测试.
 */
@DisplayName("ResponseAutoConfiguration 测试")
class ResponseAutoConfigurationTest {

    private AnnotationConfigApplicationContext context;

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @DisplayName("加载配置类 - 核心 Bean 均已注册")
    void testBeansRegistered() {
        context = new AnnotationConfigApplicationContext(ResponseAutoConfiguration.class);

        assertNotNull(context.getBean(ResponseProperties.class));
        assertNotNull(context.getBean(InterceptorProperties.class));
        assertNotNull(context.getBean(ResourceBundleMessageSource.class));
        assertNotNull(context.getBean(AjaxStatusRegistry.class));
        assertNotNull(context.getBean(I18nResponseBuilder.class));
        assertNotNull(context.getBean(GlobalExceptionHandler.class));
        assertNotNull(context.getBean(MapToAjaxResultStrategy.class));
        assertNotNull(context.getBean(StringToAjaxResultStrategy.class));
        assertNotNull(context.getBean(EntityToAjaxResultStrategy.class));
        assertNotNull(context.getBean(ResponseInterceptor.class));
    }

    @Test
    @DisplayName("AjaxStatusRegistry - init 后包含 COMMON 状态码")
    void testAjaxStatusRegistryInitialized() {
        context = new AnnotationConfigApplicationContext(ResponseAutoConfiguration.class);
        AjaxStatusRegistry registry = context.getBean(AjaxStatusRegistry.class);

        assertTrue(registry.size() > 0);
        assertTrue(registry.contains("COMMON_000"));
        assertTrue(registry.contains("COMMON_404"));
    }

    @Test
    @DisplayName("I18nResponseBuilder - 绑定 MessageSource 并可解析中文消息")
    void testI18nBuilderWiredWithMessageSource() {
        context = new AnnotationConfigApplicationContext(ResponseAutoConfiguration.class);
        I18nResponseBuilder builder = context.getBean(I18nResponseBuilder.class);

        String message = builder.getMessage("common.success", "fallback");
        assertEquals("操作成功", message);
    }

    @Test
    @DisplayName("ResponseProperties - 关闭注册表时 registry 为空")
    void testRegistryDisabledViaCustomProperties() {
        context = new AnnotationConfigApplicationContext(ResponseAutoConfiguration.class);
        ResponseProperties properties = context.getBean(ResponseProperties.class);
        properties.setAjaxStatusRegistryEnabled(false);

        AjaxStatusRegistry emptyRegistry = new AjaxStatusRegistry();
        assertEquals(0, emptyRegistry.size());
    }
}
