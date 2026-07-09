package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * GraphQL 响应结构.
 * <p>
 * GraphQL 查询响应的标准格式，包含 data、errors 和 extensions。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see <a href="https://spec.graphql.org/">GraphQL Specification</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GraphQLResponse<T> {

    /**
     * 查询结果数据.
     */
    private T data;

    /**
     * 错误列表.
     */
    private List<GraphQLError> errors;

    /**
     * 扩展信息（如 tracing、缓存等）.
     */
    private Map<String, Object> extensions;

    /**
     * GraphQL 错误对象.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GraphQLError {
        /**
         * 错误消息.
         */
        private String message;

        /**
         * 错误位置列表.
         */
        private List<Location> locations;

        /**
         * 错误路径.
         */
        private List<Object> path;

        /**
         * 扩展信息.
         */
        private Map<String, Object> extensions;
    }

    /**
     * 错误位置.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        /**
         * 行号.
         */
        private int line;

        /**
         * 列号.
         */
        private int column;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> success(final T data) {
        return GraphQLResponse.<T>builder()
                .data(data)
                .build();
    }

    /**
     * 创建带扩展的成功响应.
     *
     * @param data       数据
     * @param extensions 扩展信息
     * @param <T>        数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> success(final T data, final Map<String, Object> extensions) {
        return GraphQLResponse.<T>builder()
                .data(data)
                .extensions(extensions)
                .build();
    }

    /**
     * 创建错误响应.
     *
     * @param errors 错误列表
     * @param <T>    数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> error(final List<GraphQLError> errors) {
        return GraphQLResponse.<T>builder()
                .errors(errors)
                .build();
    }

    /**
     * 创建部分错误响应（既有数据又有错误）.
     *
     * @param data   数据
     * @param errors 错误列表
     * @param <T>    数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> partial(final T data, final List<GraphQLError> errors) {
        return GraphQLResponse.<T>builder()
                .data(data)
                .errors(errors)
                .build();
    }

    /**
     * 创建单个错误响应.
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return GraphQLResponse
     */
    public static <T> GraphQLResponse<T> error(final String message) {
        GraphQLError error = new GraphQLError(message, null, null, null);
        return error(List.of(error));
    }
}
