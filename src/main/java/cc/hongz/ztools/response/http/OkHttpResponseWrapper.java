package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * OkHttp 响应包装类.
 * <p>
 * 将 OkHttp {@link okhttp3.Response} 转换为统一的 JSON 格式。
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
public class OkHttpResponseWrapper<T> {

    /**
     * HTTP 状态码.
     */
    private int code;

    /**
     * 状态消息.
     */
    private String message;

    /**
     * 响应体.
     */
    private T body;

    /**
     * 响应头.
     */
    private Map<String, List<String>> headers;

    /**
     * 是否成功（2xx 状态码）.
     */
    private boolean successful;

    /**
     * 请求 URL.
     */
    private String url;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的响应包装.
     *
     * @param code    状态码
     * @param message 状态消息
     * @param body    响应体
     * @param headers 响应头
     * @param <T>     响应体类型
     * @return OkHttpResponseWrapper
     */
    public static <T> OkHttpResponseWrapper<T> success(final int code, final String message, final T body, final Map<String, List<String>> headers) {
        return OkHttpResponseWrapper.<T>builder()
                .code(code)
                .message(message)
                .body(body)
                .headers(headers)
                .successful(code >= 200 && code < 300)
                .build();
    }

    /**
     * 从 OkHttp Response 创建包装.
     *
     * @param response OkHttp Response
     * @param body     解析后的响应体
     * @param <T>      响应体类型
     * @return OkHttpResponseWrapper
     */
    public static <T> OkHttpResponseWrapper<T> from(final okhttp3.Response response, final T body) {
        return OkHttpResponseWrapper.<T>builder()
                .code(response.code())
                .message(response.message())
                .body(body)
                .headers(response.headers().toMultimap())
                .successful(response.isSuccessful())
                .url(response.request().url().toString())
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
     * 判断是否为重定向（3xx）.
     *
     * @return 是返回 true
     */
    public boolean isRedirect() {
        return code >= 300 && code < 400;
    }

    /**
     * 判断是否为客户端错误（4xx）.
     *
     * @return 是返回 true
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 判断是否为服务器错误（5xx）.
     *
     * @return 是返回 true
     */
    public boolean isServerError() {
        return code >= 500;
    }
}
