package cc.hongz.ztools.response.config;

/**
 * 响应模块全局配置属性.
 * <p>
 * 对应配置前缀 {@code hongz.response}，涵盖国际化与状态码注册表开关。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class ResponseProperties {

    /**
     * 是否启用国际化.
     */
    private boolean i18nEnabled = true;

    /**
     * 默认语言区域（BCP 47，如 zh_CN）.
     */
    private String defaultLocale = "zh_CN";

    /**
     * 消息资源 basename 列表.
     */
    private String[] messageBasenames = {"messages"};

    /**
     * 是否启用 AJAX 业务状态码注册表.
     */
    private boolean ajaxStatusRegistryEnabled = true;

    public boolean isI18nEnabled() {
        return i18nEnabled;
    }

    public void setI18nEnabled(final boolean i18nEnabled) {
        this.i18nEnabled = i18nEnabled;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public String[] getMessageBasenames() {
        return messageBasenames != null ? messageBasenames.clone() : new String[]{"messages"};
    }

    public void setMessageBasenames(final String[] messageBasenames) {
        this.messageBasenames = messageBasenames != null ? messageBasenames.clone() : new String[]{"messages"};
    }

    public boolean isAjaxStatusRegistryEnabled() {
        return ajaxStatusRegistryEnabled;
    }

    public void setAjaxStatusRegistryEnabled(final boolean ajaxStatusRegistryEnabled) {
        this.ajaxStatusRegistryEnabled = ajaxStatusRegistryEnabled;
    }
}
