package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * JSON:API 规范响应结构.
 * <p>
 * 遵循 JSON:API 标准的 REST API 响应格式。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see <a href="https://jsonapi.org/">JSON:API Specification</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonApiResponse<T> {

    /**
     * 数据（单个资源或资源集合）.
     */
    private Object data;

    /**
     * 错误列表.
     */
    private List<ErrorObject> errors;

    /**
     * 元数据.
     */
    private Map<String, Object> meta;

    /**
     * 链接.
     */
    private Links links;

    /**
     * 包含的相关资源.
     */
    private List<ResourceObject> included;

    /**
     * 资源对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResourceObject {
        /**
         * 资源类型.
         */
        private String type;

        /**
         * 资源 ID.
         */
        private String id;

        /**
         * 属性.
         */
        private Map<String, Object> attributes;

        /**
         * 关系.
         */
        private Map<String, Relationship> relationships;

        /**
         * 链接.
         */
        private Links links;
    }

    /**
     * 关系对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Relationship {
        /**
         * 关联数据.
         */
        private Object data;

        /**
         * 链接.
         */
        private Links links;
    }

    /**
     * 错误对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorObject {
        /**
         * 状态码.
         */
        private String status;

        /**
         * 错误码.
         */
        private String code;

        /**
         * 标题.
         */
        private String title;

        /**
         * 详细描述.
         */
        private String detail;

        /**
         * 错误源.
         */
        private ErrorSource source;

        /**
         * 元数据.
         */
        private Map<String, Object> meta;
    }

    /**
     * 错误源.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorSource {
        /**
         * JSON Pointer.
         */
        private String pointer;

        /**
         * 参数.
         */
        private String parameter;
    }

    /**
     * 链接对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Links {
        /**
         * 自身链接.
         */
        private String self;

        /**
         * 相关资源链接.
         */
        private String related;

        /**
         * 下一页链接.
         */
        private String next;

        /**
         * 上一页链接.
         */
        private String prev;

        /**
         * 第一页链接.
         */
        private String first;

        /**
         * 最后一页链接.
         */
        private String last;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建单个资源响应.
     *
     * @param resource 资源对象
     * @param <T>      数据类型
     * @return JsonApiResponse
     */
    public static <T> JsonApiResponse<T> ofResource(final ResourceObject resource) {
        return JsonApiResponse.<T>builder()
                .data(resource)
                .build();
    }

    /**
     * 创建资源集合响应.
     *
     * @param resources 资源列表
     * @param <T>       数据类型
     * @return JsonApiResponse
     */
    public static <T> JsonApiResponse<T> ofCollection(final List<ResourceObject> resources) {
        return JsonApiResponse.<T>builder()
                .data(resources)
                .build();
    }

    /**
     * 创建错误响应.
     *
     * @param errors 错误列表
     * @param <T>    数据类型
     * @return JsonApiResponse
     */
    public static <T> JsonApiResponse<T> ofErrors(final List<ErrorObject> errors) {
        return JsonApiResponse.<T>builder()
                .errors(errors)
                .build();
    }
}
