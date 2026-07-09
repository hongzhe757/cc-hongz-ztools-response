package cc.hongz.ztools.response.interceptor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 响应拦截策略接口.
 * <p>
 * 定义如何将非标准 Controller 返回值转换为目标响应格式（如 {@link cc.hongz.ztools.response.ajax.AjaxResult}）。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public interface ResponseInterceptStrategy {

    /**
     * 判断是否支持处理该响应类型.
     *
     * @param responseType 响应类型
     * @return 支持返回 true
     */
    boolean supports(Class<?> responseType);

    /**
     * 处理响应.
     *
     * @param response 原始响应对象
     * @param request  HTTP 请求（可为 null，单元测试场景）
     * @return 处理后的响应
     */
    Object handle(Object response, HttpServletRequest request);
}
