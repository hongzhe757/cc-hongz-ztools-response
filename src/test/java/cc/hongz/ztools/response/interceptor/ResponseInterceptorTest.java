package cc.hongz.ztools.response.interceptor;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.custom.CustomResponse;
import cc.hongz.ztools.response.interceptor.SkipResponseIntercept;
import cc.hongz.ztools.response.interceptor.strategy.EntityToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.MapToAjaxResultStrategy;
import cc.hongz.ztools.response.interceptor.strategy.StringToAjaxResultStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link ResponseInterceptor} 单元测试.
 */
@DisplayName("ResponseInterceptor 测试")
class ResponseInterceptorTest {

    static class Product {
        private final String name;

        Product(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private InterceptorProperties properties;
    private ResponseInterceptor interceptor;

    @BeforeEach
    void setUp() {
        properties = new InterceptorProperties();
        final List<ResponseInterceptStrategy> strategies = Arrays.asList(
                new MapToAjaxResultStrategy(properties),
                new StringToAjaxResultStrategy(properties),
                new EntityToAjaxResultStrategy(properties)
        );
        interceptor = new ResponseInterceptor(strategies, properties);
    }

    @Test
    @DisplayName("beforeBodyWrite - POJO 自动包装为 AjaxResult")
    void testWrapPojo() throws Exception {
        final MethodParameter parameter = methodParameter("getProduct");
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        final ServletServerHttpRequest request = new ServletServerHttpRequest(servletRequest);

        final Object result = interceptor.beforeBodyWrite(
                new Product("phone"),
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                request,
                null
        );

        assertInstanceOf(AjaxResult.class, result);
        assertNotNull(((AjaxResult) result).getData());
    }

    @Test
    @DisplayName("beforeBodyWrite - AjaxResult 直接放行")
    void testPassThroughAjaxResult() throws Exception {
        final MethodParameter parameter = methodParameter("getAjax");
        final AjaxResult body = AjaxResult.success("ok");
        final Object result = interceptor.beforeBodyWrite(
                body,
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        );
        assertSame(body, result);
    }

    @Test
    @DisplayName("strictMode - 非目标格式抛出异常")
    void testStrictModeThrows() throws Exception {
        properties.setStrictMode(true);
        final MethodParameter parameter = methodParameter("getProduct");

        assertThrows(IllegalStateException.class, () -> interceptor.beforeBodyWrite(
                new Product("x"),
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        ));
    }

    @Test
    @DisplayName("strictMode - CUSTOM 风格禁止 AjaxResult")
    void testStrictModeRejectsAjaxResultInCustomStyle() throws Exception {
        properties.setStrictMode(true);
        properties.setResponseStyle(InterceptorProperties.ResponseStyle.CUSTOM);
        final MethodParameter parameter = methodParameter("getAjax");

        assertThrows(IllegalStateException.class, () -> interceptor.beforeBodyWrite(
                AjaxResult.success("data"),
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        ));
    }

    @Test
    @DisplayName("supports - 禁用拦截器时返回 false")
    void testSupportsWhenDisabled() throws Exception {
        properties.setEnabled(false);
        final MethodParameter parameter = methodParameter("getProduct");
        assertFalse(interceptor.supports(parameter, StringHttpMessageConverter.class));
    }

    @Test
    @DisplayName("supports - AjaxResult 返回类型不拦截")
    void testSupportsSkipsAjaxResultReturnType() throws Exception {
        final MethodParameter parameter = methodParameter("getAjax");
        assertFalse(interceptor.supports(parameter, StringHttpMessageConverter.class));
    }

    @Test
    @DisplayName("defaultAction=PASS - 无策略时放行原始对象")
    void testDefaultActionPass() throws Exception {
        properties.setDefaultAction(InterceptorProperties.DefaultAction.PASS);
        interceptor = new ResponseInterceptor(List.of(), properties);
        final MethodParameter parameter = methodParameter("getProduct");
        final Product product = new Product("raw");

        final Object result = interceptor.beforeBodyWrite(
                product,
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        );
        assertSame(product, result);
    }

    @Test
    @DisplayName("supports - @SkipResponseIntercept 方法级跳过")
    void testSupportsSkipsWhenMethodAnnotated() throws Exception {
        final MethodParameter parameter = methodParameter("getSkipped");
        assertFalse(interceptor.supports(parameter, StringHttpMessageConverter.class));
    }

    @Test
    @DisplayName("beforeBodyWrite - null body 包装为空成功 AjaxResult")
    void testWrapNullBody() throws Exception {
        final MethodParameter parameter = methodParameter("getProduct");
        final Object result = interceptor.beforeBodyWrite(
                null,
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        );
        assertInstanceOf(AjaxResult.class, result);
        assertEquals("200", ((AjaxResult) result).getCode());
    }

    @Test
    @DisplayName("beforeBodyWrite - CUSTOM 风格 POJO 包装为 CustomResponse")
    void testCustomStyleWrapsPojo() throws Exception {
        properties.setResponseStyle(InterceptorProperties.ResponseStyle.CUSTOM);
        final MethodParameter parameter = methodParameter("getProduct");

        final Object result = interceptor.beforeBodyWrite(
                new Product("book"),
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        );

        assertInstanceOf(CustomResponse.class, result);
        assertEquals(200, ((CustomResponse<?>) result).getCode());
    }

    @Test
    @DisplayName("defaultAction=THROW - 无策略时抛出异常")
    void testDefaultActionThrow() throws Exception {
        properties.setDefaultAction(InterceptorProperties.DefaultAction.THROW);
        interceptor = new ResponseInterceptor(List.of(), properties);
        final MethodParameter parameter = methodParameter("getProduct");

        assertThrows(IllegalStateException.class, () -> interceptor.beforeBodyWrite(
                new Product("x"),
                parameter,
                MediaType.APPLICATION_JSON,
                StringHttpMessageConverter.class,
                new ServletServerHttpRequest(new MockHttpServletRequest()),
                null
        ));
    }

    private MethodParameter methodParameter(final String methodName) throws Exception {
        return new MethodParameter(DummyController.class.getMethod(methodName), -1);
    }

    static class DummyController {

        public Product getProduct() {
            return null;
        }

        public AjaxResult getAjax() {
            return null;
        }

        @SkipResponseIntercept("测试跳过")
        public Product getSkipped() {
            return null;
        }
    }
}
