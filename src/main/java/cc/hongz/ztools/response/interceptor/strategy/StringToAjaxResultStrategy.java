package cc.hongz.ztools.response.interceptor.strategy;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.custom.CustomResponseConverter;
import cc.hongz.ztools.response.interceptor.InterceptorProperties;
import cc.hongz.ztools.response.interceptor.ResponseInterceptStrategy;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 字符串转目标响应格式策略.
 * <p>
 * 将纯文本或 JSON 字符串包装为统一响应；JSON 解析留给前端或后续扩展，当前整体作为 data 返回。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class StringToAjaxResultStrategy implements ResponseInterceptStrategy {

    private final InterceptorProperties properties;

    /**
     * 创建字符串转换策略.
     *
     * @param properties 拦截器配置
     */
    public StringToAjaxResultStrategy(final InterceptorProperties properties) {
        this.properties = properties != null ? properties : new InterceptorProperties();
    }

    @Override
    public boolean supports(final Class<?> responseType) {
        return String.class.equals(responseType);
    }

    @Override
    public Object handle(final Object response, final HttpServletRequest request) {
        final String text = (String) response;
        final AjaxResult ajaxResult = AjaxResult.success("操作成功", text);
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())) {
            return CustomResponseConverter.fromAjaxResult(ajaxResult);
        }
        return ajaxResult;
    }
}
