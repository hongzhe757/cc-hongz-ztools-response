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
 * Map 转目标响应格式策略.
 * <p>
 * 若 Map 已包含 {@code code}/{@code msg} 字段，则按 AJAX 风格解析；否则整体作为 data 包装。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class MapToAjaxResultStrategy implements ResponseInterceptStrategy {

    private final InterceptorProperties properties;

    /**
     * 创建 Map 转换策略.
     *
     * @param properties 拦截器配置
     */
    public MapToAjaxResultStrategy(final InterceptorProperties properties) {
        this.properties = properties != null ? properties : new InterceptorProperties();
    }

    @Override
    public boolean supports(final Class<?> responseType) {
        return Map.class.isAssignableFrom(responseType);
    }

    @Override
    public Object handle(final Object response, final HttpServletRequest request) {
        final Map<?, ?> map = (Map<?, ?>) response;
        final AjaxResult ajaxResult = toAjaxResult(map);
        return wrapForStyle(ajaxResult);
    }

    private AjaxResult toAjaxResult(final Map<?, ?> map) {
        if (ResponseInterceptSupport.isAjaxLikeMap(map)) {
            final Object data = map.containsKey("data") ? map.get("data") : map;
            return AjaxResult.success(
                    String.valueOf(map.get("code")),
                    String.valueOf(map.get("msg")),
                    data
            );
        }
        return AjaxResult.success(map);
    }

    private Object wrapForStyle(final AjaxResult ajaxResult) {
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())) {
            return CustomResponseConverter.fromAjaxResult(ajaxResult);
        }
        return ajaxResult;
    }
}
