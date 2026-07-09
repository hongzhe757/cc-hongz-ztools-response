package cc.hongz.ztools.response.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * XML 转 JSON 响应.
 * <p>
 * 将 XML 响应转换为 JSON 格式的包装类。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class XmlToJsonResponse<T> {

    /**
     * 根元素名称.
     */
    private String rootElement;

    /**
     * 转换后的数据.
     */
    private T data;

    /**
     * XML 属性映射.
     */
    private Map<String, String> attributes;

    /**
     * 命名空间映射.
     */
    private Map<String, String> namespaces;

    /**
     * 原始 XML 字符串（可选）.
     */
    private String originalXml;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建简单的 XML 转 JSON 响应.
     *
     * @param rootElement 根元素名称
     * @param data        转换后的数据
     * @param <T>         数据类型
     * @return XmlToJsonResponse
     */
    public static <T> XmlToJsonResponse<T> of(final String rootElement, final T data) {
        return XmlToJsonResponse.<T>builder()
                .rootElement(rootElement)
                .data(data)
                .build();
    }

    /**
     * 创建带属性的响应.
     *
     * @param rootElement 根元素名称
     * @param data        转换后的数据
     * @param attributes  属性映射
     * @param <T>         数据类型
     * @return XmlToJsonResponse
     */
    public static <T> XmlToJsonResponse<T> withAttributes(final String rootElement, final T data, final Map<String, String> attributes) {
        return XmlToJsonResponse.<T>builder()
                .rootElement(rootElement)
                .data(data)
                .attributes(attributes)
                .build();
    }

    /**
     * 创建完整的响应.
     *
     * @param rootElement 根元素名称
     * @param data        转换后的数据
     * @param attributes  属性映射
     * @param namespaces  命名空间映射
     * @param originalXml 原始 XML
     * @param <T>         数据类型
     * @return XmlToJsonResponse
     */
    public static <T> XmlToJsonResponse<T> full(final String rootElement, final T data, final Map<String, String> attributes, final Map<String, String> namespaces, final String originalXml) {
        return XmlToJsonResponse.<T>builder()
                .rootElement(rootElement)
                .data(data)
                .attributes(attributes)
                .namespaces(namespaces)
                .originalXml(originalXml)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否有属性.
     *
     * @return 有返回 true
     */
    public boolean hasAttributes() {
        return attributes != null && !attributes.isEmpty();
    }

    /**
     * 判断是否有命名空间.
     *
     * @return 有返回 true
     */
    public boolean hasNamespaces() {
        return namespaces != null && !namespaces.isEmpty();
    }

    /**
     * 判断是否保留了原始 XML.
     *
     * @return 是返回 true
     */
    public boolean hasOriginalXml() {
        return originalXml != null && !originalXml.isEmpty();
    }
}
