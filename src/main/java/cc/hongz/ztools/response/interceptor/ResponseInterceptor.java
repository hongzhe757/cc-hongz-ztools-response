package cc.hongz.ztools.response.interceptor;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.custom.CustomResponse;
import cc.hongz.ztools.response.custom.CustomResponseConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collections;
import java.util.List;

/**
 * 响应体拦截器.
 * <p>
 * 实现 {@link ResponseBodyAdvice}，在响应写出前将非标准返回值自动包装为
 * {@link AjaxResult} 或 {@link CustomResponse}（由 {@link InterceptorProperties.ResponseStyle} 决定）。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {

    private final List<ResponseInterceptStrategy> strategies;
    private final InterceptorProperties properties;

    /**
     * 创建响应拦截器.
     *
     * @param strategies 拦截策略列表（按顺序匹配）
     * @param properties 拦截器配置
     */
    public ResponseInterceptor(final List<ResponseInterceptStrategy> strategies,
                               final InterceptorProperties properties) {
        this.strategies = strategies != null ? strategies : Collections.emptyList();
        this.properties = properties != null ? properties : new InterceptorProperties();
    }

    @Override
    public boolean supports(final MethodParameter returnType,
                            final Class<? extends HttpMessageConverter<?>> converterType) {
        if (!properties.isEnabled()) {
            return false;
        }
        if (hasSkipAnnotation(returnType)) {
            return false;
        }
        return ResponseInterceptSupport.shouldInterceptReturnType(returnType.getParameterType(), properties);
    }

    @Override
    public Object beforeBodyWrite(final Object body,
                                  final MethodParameter returnType,
                                  final MediaType selectedContentType,
                                  final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  final ServerHttpRequest request,
                                  final ServerHttpResponse response) {
        if (body == null) {
            return wrapNullBody();
        }

        final Class<?> bodyType = body.getClass();
        if (ResponseInterceptSupport.isTargetResponse(bodyType, properties.getResponseStyle())) {
            return body;
        }
        if (properties.isStrictMode()) {
            enforceStrictMode(bodyType);
        }
        if (ResponseInterceptSupport.isStandardResponse(bodyType)
                || ResponseInterceptSupport.isStreamingResponse(bodyType)) {
            return body;
        }

        final HttpServletRequest servletRequest = extractServletRequest(request);
        for (ResponseInterceptStrategy strategy : strategies) {
            if (strategy.supports(bodyType)) {
                return strategy.handle(body, servletRequest);
            }
        }
        return defaultHandle(body);
    }

    private Object wrapNullBody() {
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())) {
            return CustomResponse.success(null);
        }
        return AjaxResult.success();
    }

    private void enforceStrictMode(final Class<?> bodyType) {
        if (!properties.isStrictMode()) {
            return;
        }
        if (InterceptorProperties.ResponseStyle.AJAX_RESULT.equals(properties.getResponseStyle())
                && CustomResponse.class.isAssignableFrom(bodyType)) {
            throw new IllegalStateException(
                    "Strict mode (AJAX_RESULT): CustomResponse is not allowed. Use AjaxResult instead.");
        }
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())
                && AjaxResult.class.isAssignableFrom(bodyType)) {
            throw new IllegalStateException(
                    "Strict mode (CUSTOM): AjaxResult is not allowed. Use CustomResponse instead.");
        }
        throw new IllegalStateException(
                "Strict mode enabled: response must match style "
                        + properties.getResponseStyle()
                        + ", but got: "
                        + bodyType.getName()
                        + ". Disable strict mode to auto-wrap.");
    }

    private Object defaultHandle(final Object body) {
        return switch (properties.getDefaultAction()) {
            case PASS -> body;
            case THROW -> throw new IllegalStateException(
                    "No intercept strategy matched response type: " + body.getClass().getName());
            case WRAP -> wrapBody(body);
        };
    }

    private Object wrapBody(final Object body) {
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())) {
            if (body instanceof AjaxResult ajaxResult) {
                return CustomResponseConverter.fromAjaxResult(ajaxResult);
            }
            return CustomResponse.success(body);
        }
        if (body instanceof CustomResponse<?> customResponse) {
            return CustomResponseConverter.toAjaxResult(customResponse);
        }
        return AjaxResult.success(body);
    }

    private HttpServletRequest extractServletRequest(final ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            return servletRequest.getServletRequest();
        }
        return null;
    }

    private boolean hasSkipAnnotation(final MethodParameter returnType) {
        return returnType.hasMethodAnnotation(SkipResponseIntercept.class)
                || returnType.getContainingClass().isAnnotationPresent(SkipResponseIntercept.class);
    }
}
