package cc.hongz.ztools.response.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 响应拦截器配置属性.
 * <p>
 * 对应配置前缀 {@code hongz.response.interceptor}。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class InterceptorProperties {

    /**
     * 是否启用响应拦截器.
     */
    private boolean enabled = true;

    /**
     * 响应风格（与目标格式互斥）.
     */
    private ResponseStyle responseStyle = ResponseStyle.AJAX_RESULT;

    /**
     * 无匹配策略时的默认动作.
     */
    private DefaultAction defaultAction = DefaultAction.WRAP;

    /**
     * 严格模式：非目标响应格式时抛出异常.
     */
    private boolean strictMode = false;

    /**
     * 需要排除的包路径前缀.
     */
    private List<String> excludePackages = new ArrayList<>(Arrays.asList(
            "org.springframework",
            "javax.",
            "jakarta."
    ));

    /**
     * 需要排除的完整类名.
     */
    private List<String> excludeClasses = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public ResponseStyle getResponseStyle() {
        return responseStyle;
    }

    public void setResponseStyle(final ResponseStyle responseStyle) {
        this.responseStyle = responseStyle != null ? responseStyle : ResponseStyle.AJAX_RESULT;
    }

    public DefaultAction getDefaultAction() {
        return defaultAction;
    }

    public void setDefaultAction(final DefaultAction defaultAction) {
        this.defaultAction = defaultAction != null ? defaultAction : DefaultAction.WRAP;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    public void setStrictMode(final boolean strictMode) {
        this.strictMode = strictMode;
    }

    public List<String> getExcludePackages() {
        return excludePackages;
    }

    public void setExcludePackages(final List<String> excludePackages) {
        this.excludePackages = excludePackages != null ? excludePackages : new ArrayList<>();
    }

    public List<String> getExcludeClasses() {
        return excludeClasses;
    }

    public void setExcludeClasses(final List<String> excludeClasses) {
        this.excludeClasses = excludeClasses != null ? excludeClasses : new ArrayList<>();
    }

    /**
     * 响应风格枚举（互斥）.
     */
    public enum ResponseStyle {
        /** 统一使用 AjaxResult（推荐）. */
        AJAX_RESULT,
        /** 统一使用 CustomResponse. */
        CUSTOM
    }

    /**
     * 默认拦截动作.
     */
    public enum DefaultAction {
        /** 包装为目标响应格式. */
        WRAP,
        /** 放行原始响应. */
        PASS,
        /** 抛出异常. */
        THROW
    }
}
