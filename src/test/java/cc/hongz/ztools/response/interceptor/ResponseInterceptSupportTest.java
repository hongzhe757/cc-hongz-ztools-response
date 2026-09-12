package cc.hongz.ztools.response.interceptor;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.custom.CustomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link ResponseInterceptSupport} 单元测试.
 */
@DisplayName("ResponseInterceptSupport 测试")
class ResponseInterceptSupportTest {

    static class SampleUser {
        private final String name;

        SampleUser(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    @DisplayName("isStandardResponse - AjaxResult/Result/ProblemDetail/ResponseEntity")
    void testIsStandardResponse() {
        assertTrue(ResponseInterceptSupport.isStandardResponse(AjaxResult.class));
        assertTrue(ResponseInterceptSupport.isStandardResponse(Result.class));
        assertTrue(ResponseInterceptSupport.isStandardResponse(ResponseEntity.class));
        assertFalse(ResponseInterceptSupport.isStandardResponse(SampleUser.class));
    }

    @Test
    @DisplayName("isTargetResponse - 按响应风格判断")
    void testIsTargetResponse() {
        assertTrue(ResponseInterceptSupport.isTargetResponse(
                AjaxResult.class, InterceptorProperties.ResponseStyle.AJAX_RESULT));
        assertFalse(ResponseInterceptSupport.isTargetResponse(
                CustomResponse.class, InterceptorProperties.ResponseStyle.AJAX_RESULT));
        assertTrue(ResponseInterceptSupport.isTargetResponse(
                CustomResponse.class, InterceptorProperties.ResponseStyle.CUSTOM));
    }

    @Test
    @DisplayName("isStreamingResponse - InputStream 与 byte[]")
    void testIsStreamingResponse() {
        assertTrue(ResponseInterceptSupport.isStreamingResponse(InputStream.class));
        assertTrue(ResponseInterceptSupport.isStreamingResponse(byte[].class));
        assertFalse(ResponseInterceptSupport.isStreamingResponse(String.class));
    }

    @Test
    @DisplayName("shouldInterceptReturnType - POJO 需要拦截，AjaxResult 放行")
    void testShouldInterceptReturnType() {
        final InterceptorProperties properties = new InterceptorProperties();
        assertTrue(ResponseInterceptSupport.shouldInterceptReturnType(SampleUser.class, properties));
        assertFalse(ResponseInterceptSupport.shouldInterceptReturnType(AjaxResult.class, properties));
        assertFalse(ResponseInterceptSupport.shouldInterceptReturnType(Void.class, properties));
    }

    @Test
    @DisplayName("isAjaxLikeMap - 检测 code/msg 字段")
    void testIsAjaxLikeMap() {
        final Map<String, Object> ajaxLike = new HashMap<>();
        ajaxLike.put("code", "200");
        ajaxLike.put("msg", "ok");
        assertTrue(ResponseInterceptSupport.isAjaxLikeMap(ajaxLike));

        final Map<String, Object> plain = Map.of("id", 1);
        assertFalse(ResponseInterceptSupport.isAjaxLikeMap(plain));
    }

    @Test
    @DisplayName("isCollectionType - List 为集合")
    void testIsCollectionType() {
        assertTrue(ResponseInterceptSupport.isCollectionType(List.class));
        assertFalse(ResponseInterceptSupport.isCollectionType(Map.class));
    }
}
