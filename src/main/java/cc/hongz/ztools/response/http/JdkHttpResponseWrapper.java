package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * JDK HttpClient 响应包装类.
 * <p>
 * 将 {@link java.net.http.HttpResponse} 转换为统一的 JSON 格式。
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
public class JdkHttpResponseWrapper<T> {

    /**
     * HTTP 状态码.
     */
    private int statusCode;

    /**
     * 响应体.
     */
    private T body;

    /**
     * 响应头.
     */
    private Map<String, List<String>> headers;

    /**
     * 请求 URI.
     */
    private String uri;

    /**
     * HTTP 版本.
     */
    private String version;

    /**
     * 是否成功（2xx 状态码）.
     */
    private boolean successful;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的响应包装.
     *
     * @param statusCode 状态码
     * @param body       响应体
     * @param headers    响应头
     * @param <T>        响应体类型
     * @return JdkHttpResponseWrapper
     */
    public static <T> JdkHttpResponseWrapper<T> success(final int statusCode, final T body, final Map<String, List<String>> headers) {
        return JdkHttpResponseWrapper.<T>builder()
                .statusCode(statusCode)
                .body(body)
                .headers(headers)
                .successful(statusCode >= 200 && statusCode < 300)
                .build();
    }

    /**
     * 从 JDK HttpResponse 创建包装.
     *
     * @param response JDK HttpResponse
     * @param body     解析后的响应体
     * @param <T>      响应体类型
     * @return JdkHttpResponseWrapper
     */
    public static <T> JdkHttpResponseWrapper<T> from(final java.net.http.HttpResponse<?> response, final T body) {
        return JdkHttpResponseWrapper.<T>builder()
                .statusCode(response.statusCode())
                .body(body)
                .headers(response.headers().map())
                .uri(response.uri().toString())
                .version(response.version().name())
                .successful(response.statusCode() >= 200 && response.statusCode() < 300)
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
