package cc.hongz.ztools.response.standard;

import cc.hongz.ztools.response.http.HttpStatusCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * RFC 7807 Problem Details 响应结构.
 * <p>
 * HTTP API 错误响应标准格式，使用 application/problem+json 媒体类型。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7807">RFC 7807</a>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDetail {

    /**
     * 错误类型 URI（标识错误类型的唯一标识符）.
     */
    private String type;

    /**
     * 简短的人类可读标题.
     */
    private String title;

    /**
     * HTTP 状态码.
     */
    private int status;

    /**
     * 详细的人类可读错误描述.
     */
    private String detail;

    /**
     * 发生错误的实例 URI.
     */
    private String instance;

    /**
     * 扩展字段（可选）.
     */
    private Map<String, Object> extensions;

    /**
     * 验证错误列表（可选）.
     */
    private List<ValidationError> errors;

    /**
     * 验证错误项.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ValidationError {
        /**
         * 字段名.
         */
        private String field;

        /**
         * 错误消息.
         */
        private String message;

        /**
         * 拒绝的值.
         */
        private Object rejectedValue;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建简单的错误详情.
     *
     * @param status 状态码
     * @param title  标题
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail of(final int status, final String title, final String detail) {
        return ProblemDetail.builder()
                .status(status)
                .title(title)
                .detail(detail)
                .build();
    }

    /**
     * 创建带类型的错误详情.
     *
     * @param type   错误类型 URI
     * @param status 状态码
     * @param title  标题
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail of(final String type, final int status, final String title, final String detail) {
        return ProblemDetail.builder()
                .type(type)
                .status(status)
                .title(title)
                .detail(detail)
                .build();
    }

    /**
     * 创建 400 Bad Request 错误.
     *
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail badRequest(final String detail) {
        return of(400, "Bad Request", detail);
    }

    /**
     * 创建 404 Not Found 错误.
     *
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail notFound(final String detail) {
        return of(404, "Not Found", detail);
    }

    /**
     * 创建 500 Internal Server Error 错误.
     *
     * @param detail 详细描述
     * @return ProblemDetail
     */
    public static ProblemDetail internalError(final String detail) {
        return of(500, "Internal Server Error", detail);
    }

    /**
     * 使用 HTTP 状态码创建错误详情.
     *
     * @param status HTTP 状态码
     * @param detail 详细描述
     * @return ProblemDetail
     * @since 1.0.0-SNAPSHOT
     */
    public static ProblemDetail from(final HttpStatusCode status, final String detail) {
        return of(status.getCode(), status.getReasonPhrase(), detail);
    }

    /**
     * 使用 HTTP 状态码创建带类型的错误详情.
     *
     * @param type   错误类型 URI
     * @param status HTTP 状态码
     * @param detail 详细描述
     * @return ProblemDetail
     * @since 1.0.0-SNAPSHOT
     */
    public static ProblemDetail from(final String type, final HttpStatusCode status, final String detail) {
        return of(type, status.getCode(), status.getReasonPhrase(), detail);
    }

    /**
     * 使用 HTTP 状态码创建完整的错误详情.
     *
     * @param status   HTTP 状态码
     * @param title    标题（如果为 null，则使用状态码的原因短语）
     * @param detail   详细描述
     * @param instance 实例 URI
     * @return ProblemDetail
     * @since 1.0.0-SNAPSHOT
     */
    public static ProblemDetail from(final HttpStatusCode status, final String title, 
                                     final String detail, final String instance) {
        return ProblemDetail.builder()
                .status(status.getCode())
                .title(title != null ? title : status.getReasonPhrase())
                .detail(detail)
                .instance(instance)
                .build();
    }
}
