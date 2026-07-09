package cc.hongz.ztools.response.interceptor.strategy;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.custom.CustomResponse;
import cc.hongz.ztools.response.interceptor.InterceptorProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 内置响应拦截策略单元测试.
 */
@DisplayName("响应拦截策略测试")
class ResponseInterceptStrategyTest {

    static class OrderDto {
        private final Long id;

        OrderDto(final Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    @Test
    @DisplayName("EntityToAjaxResultStrategy - POJO 包装为 AjaxResult")
    void testEntityStrategyWrapsPojo() {
        final EntityToAjaxResultStrategy strategy =
                new EntityToAjaxResultStrategy(new InterceptorProperties());
        assertTrue(strategy.supports(OrderDto.class));

        final AjaxResult result = (AjaxResult) strategy.handle(new OrderDto(100L), null);
        assertEquals("200", result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("MapToAjaxResultStrategy - 普通 Map 整体作为 data")
    void testMapStrategyPlainMap() {
        final MapToAjaxResultStrategy strategy =
                new MapToAjaxResultStrategy(new InterceptorProperties());
        final Map<String, Object> map = Map.of("id", 1, "name", "test");

        final AjaxResult result = (AjaxResult) strategy.handle(map, null);
        assertEquals(map, result.getData());
    }

    @Test
    @DisplayName("MapToAjaxResultStrategy - AJAX 风格 Map 解析 code/msg/data")
    void testMapStrategyAjaxLikeMap() {
        final MapToAjaxResultStrategy strategy =
                new MapToAjaxResultStrategy(new InterceptorProperties());
        final Map<String, Object> map = new HashMap<>();
        map.put("code", "COMMON_000");
        map.put("msg", "操作成功");
        map.put("data", "payload");

        final AjaxResult result = (AjaxResult) strategy.handle(map, null);
        assertEquals("COMMON_000", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals("payload", result.getData());
    }

    @Test
    @DisplayName("StringToAjaxResultStrategy - 字符串作为 data")
    void testStringStrategy() {
        final StringToAjaxResultStrategy strategy =
                new StringToAjaxResultStrategy(new InterceptorProperties());
        final AjaxResult result = (AjaxResult) strategy.handle("hello", null);
        assertEquals("hello", result.getData());
    }

    @Test
    @DisplayName("EntityToAjaxResultStrategy - CUSTOM 风格输出 CustomResponse")
    void testEntityStrategyCustomStyle() {
        final InterceptorProperties properties = new InterceptorProperties();
        properties.setResponseStyle(InterceptorProperties.ResponseStyle.CUSTOM);
        final EntityToAjaxResultStrategy strategy = new EntityToAjaxResultStrategy(properties);

        final CustomResponse<?> response = (CustomResponse<?>) strategy.handle(new OrderDto(1L), null);
        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
    }
}
