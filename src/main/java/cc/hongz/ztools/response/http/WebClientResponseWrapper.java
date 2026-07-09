package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Spring WebClient 响应包装类.
 * <p>
 * 将 Spring WebClient {@link org.springframework.web.reactive.function.client.ClientResponse} 转换为统一的 JSON 格式。
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
public class WebClientResponseWrapper<T> {

    /**
     * HTTP 状态码.
     */
    private int statusCodeValue;

    /**
     * 状态码名称.
     */
    private String statusCodeName;

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

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的响应包装.
     *
     * @param statusCodeValue 状态码值
     * @param statusCodeName  状态码名称
     * @param body            响应体
     * @param headers         响应头
     * @param <T>             响应体类型
     * @return WebClientResponseWrapper
     */
    public static <T> WebClientResponseWrapper<T> success(final int statusCodeValue, final String statusCodeName, final T body, final Map<String, List<String>> headers) {
        return WebClientResponseWrapper.<T>builder()
                .statusCodeValue(statusCodeValue)
                .statusCodeName(statusCodeName)
                .body(body)
                .headers(headers)
                .successful(statusCodeValue >= 200 && statusCodeValue < 300)
                .build();
    }

    /**
     * 从 ClientResponse 创建包装.
     *
     * @param response Spring WebClient ClientResponse
     * @param body     解析后的响应体
     * @param <T>      响应体类型
     * @return WebClientResponseWrapper
     */
    public static <T> WebClientResponseWrapper<T> from(final org.springframework.web.reactive.function.client.ClientResponse response, final T body) {
        return WebClientResponseWrapper.<T>builder()
                .statusCodeValue(response.statusCode().value())
                .statusCodeName(response.statusCode().toString())
                .body(body)
                .headers(response.headers().asHttpHeaders())
                .successful(response.statusCode().is2xxSuccessful())
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
        return statusCodeValue >= 400 && statusCodeValue < 500;
    }

    /**
     * 判断是否为服务器错误（5xx）.
     *
     * @return 是返回 true
     */
    public boolean isServerError() {
        return statusCodeValue >= 500;
    }
}
