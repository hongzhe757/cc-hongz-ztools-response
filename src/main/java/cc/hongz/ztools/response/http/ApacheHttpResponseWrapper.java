package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Apache HttpClient 响应包装类.
 * <p>
 * 将 Apache HttpClient {@link org.apache.http.HttpResponse} 转换为统一的 JSON 格式。
 * </p>
 *
 * @param <T> 响应体类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApacheHttpResponseWrapper<T> {

    /**
     * HTTP 状态码.
     */
    private int statusCode;

    /**
     * 原因短语.
     */
    private String reasonPhrase;

    /**
     * 响应体.
     */
    private T body;

    /**
     * 响应头.
     */
    private Map<String, List<String>> headers;

    /**
     * 协议版本.
     */
    private String protocolVersion;

    /**
     * 是否成功（2xx 状态码）.
     */
    private boolean successful;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的响应包装.
     *
     * @param statusCode   状态码
     * @param reasonPhrase 原因短语
     * @param body         响应体
     * @param headers      响应头
     * @param <T>          响应体类型
     * @return ApacheHttpResponseWrapper
     */
    public static <T> ApacheHttpResponseWrapper<T> success(final int statusCode, final String reasonPhrase, final T body, final Map<String, List<String>> headers) {
        return ApacheHttpResponseWrapper.<T>builder()
                .statusCode(statusCode)
                .reasonPhrase(reasonPhrase)
                .body(body)
                .headers(headers)
                .successful(statusCode >= 200 && statusCode < 300)
                .build();
    }

    /**
     * 从 Apache HttpResponse 创建包装.
     *
     * @param response Apache HttpResponse
     * @param body     解析后的响应体
     * @param headers  响应头映射
     * @param <T>      响应体类型
     * @return ApacheHttpResponseWrapper
     */
    public static <T> ApacheHttpResponseWrapper<T> from(final org.apache.http.HttpResponse response, final T body, final Map<String, List<String>> headers) {
        return ApacheHttpResponseWrapper.<T>builder()
                .statusCode(response.getStatusLine().getStatusCode())
                .reasonPhrase(response.getStatusLine().getReasonPhrase())
                .body(body)
                .headers(headers)
                .protocolVersion(response.getStatusLine().getProtocolVersion().toString())
                .successful(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() < 300)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为成功响应.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return successful;
    }

    /**
     * 判断是否为客户端错误（4xx）.
     *
     * @return 是返回 true
     */
    public boolean isClientError() {
        return statusCode >= 400 && statusCode < 500;
    }

    /**
     * 判断是否为服务器错误（5xx）.
     *
     * @return 是返回 true
     */
    public boolean isServerError() {
        return statusCode >= 500;
    }
}
