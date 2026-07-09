package cc.hongz.ztools.response.interceptor.strategy;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.custom.CustomResponse;
import cc.hongz.ztools.response.custom.CustomResponseConverter;
import cc.hongz.ztools.response.interceptor.InterceptorProperties;
import cc.hongz.ztools.response.interceptor.ResponseInterceptStrategy;
import cc.hongz.ztools.response.interceptor.ResponseInterceptSupport;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 实体类转目标响应格式策略.
 * <p>
 * 将普通 POJO、集合等自动包装为 {@link AjaxResult#success(Object)} 或 {@link CustomResponse#success(Object)}。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class EntityToAjaxResultStrategy implements ResponseInterceptStrategy {

    private final InterceptorProperties properties;

    /**
     * 创建实体包装策略.
     *
     * @param properties 拦截器配置
     */
    public EntityToAjaxResultStrategy(final InterceptorProperties properties) {
        this.properties = properties != null ? properties : new InterceptorProperties();
    }

    @Override
    public boolean supports(final Class<?> responseType) {
        if (responseType == null || responseType.isPrimitive()) {
            return false;
        }
        if (ResponseInterceptSupport.isStandardResponse(responseType)
                || ResponseInterceptSupport.isStreamingResponse(responseType)
                || ResponseInterceptSupport.isFrameworkType(responseType)
                || ResponseInterceptSupport.isExcludedType(responseType, properties)) {
            return false;
        }
        return !Map.class.isAssignableFrom(responseType) && !String.class.equals(responseType);
    }

    @Override
    public Object handle(final Object response, final HttpServletRequest request) {
        if (response == null) {
            return wrap(null);
        }
        return wrap(response);
    }

    private Object wrap(final Object body) {
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())) {
            return body == null ? CustomResponse.success(null) : CustomResponse.success(body);
        }
        return body == null ? AjaxResult.success() : AjaxResult.success(body);
    }
}
