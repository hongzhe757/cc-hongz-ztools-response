package cc.hongz.ztools.response.framework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Spring Data REST 响应.
 * <p>
 * Spring Data REST 的标准响应格式，包含 HAL 链接。
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
public class SpringDataRestResponse<T> {

    /**
     * 嵌入的资源.
     */
    private Embedded<T> embedded;

    /**
     * 链接.
     */
    private Map<String, Link> links;

    /**
     * 页面信息（分页时）.
     */
    private Page page;

    /**
     * 嵌入资源包装.
     *
     * @param <T> 数据类型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Embedded<T> {
        /**
         * 资源列表.
         */
        private List<T> items;
    }

    /**
     * 链接对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Link {
        /**
         * 链接地址.
         */
        private String href;

        /**
         * 是否模板.
         */
        private Boolean templated;
    }

    /**
     * 分页信息.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Page {
        /**
         * 当前页大小.
         */
        private int size;

        /**
         * 总元素数.
         */
        private long totalElements;

        /**
         * 总页数.
         */
        private int totalPages;

        /**
         * 当前页码.
         */
        private int number;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建单个资源响应.
     *
     * @param data  数据
     * @param links 链接映射
     * @param <T>   数据类型
     * @return SpringDataRestResponse
     */
    public static <T> SpringDataRestResponse<T> ofResource(final T data, final Map<String, Link> links) {
        return SpringDataRestResponse.<T>builder()
                .embedded(new Embedded<>(List.of(data)))
                .links(links)
                .build();
    }

    /**
     * 创建集合资源响应.
     *
     * @param items 数据列表
     * @param links 链接映射
     * @param <T>   数据类型
     * @return SpringDataRestResponse
     */
    public static <T> SpringDataRestResponse<T> ofCollection(final List<T> items, final Map<String, Link> links) {
        return SpringDataRestResponse.<T>builder()
                .embedded(new Embedded<>(items))
                .links(links)
                .build();
    }

    /**
     * 创建分页响应.
     *
     * @param items 数据列表
     * @param links 链接映射
     * @param page  分页信息
     * @param <T>   数据类型
     * @return SpringDataRestResponse
     */
    public static <T> SpringDataRestResponse<T> ofPage(
            final List<T> items,
            final Map<String,
            Link> links,
            final Page page
        ) {
        return SpringDataRestResponse.<T>builder()
                .embedded(new Embedded<>(items))
                .links(links)
                .page(page)
                .build();
    }
}
