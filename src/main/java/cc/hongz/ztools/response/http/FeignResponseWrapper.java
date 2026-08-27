package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import feign.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feign Client 响应包装类.
 * <p>
 * 将 Feign {@link Response} 转换为统一的 JSON 格式。
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
@SuppressWarnings("checkstyle:MagicNumber")
public class FeignResponseWrapper<T> {

    /**
     * HTTP 状态码.
     */
    private int status;

    /**
     * 原因短语.
     */
    private String reason;

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
     * @param status  状态码
     * @param reason  原因短语
     * @param body    响应体
     * @param headers 响应头
     * @param <T>     响应体类型
     * @return FeignResponseWrapper
     */
    public static <T> FeignResponseWrapper<T> success(
            final int status,
            final String reason,
            final T body,
            final Map<String,
            List<String>> headers
        ) {
        return FeignResponseWrapper.<T>builder()
                .status(status)
                .reason(reason)
                .body(body)
                .headers(headers)
                .successful(status >= 200 && status < 300)
                .build();
    }

    /**
     * 从 Feign Response 创建包装.
     *
     * @param response Feign Response
     * @param body     解析后的响应体
     * @param <T>      响应体类型
     * @return FeignResponseWrapper
     */
    public static <T> FeignResponseWrapper<T> from(final Response response, final T body) {
        // 转换 headers 类型：Collection -> List
        Map<String, List<String>> convertedHeaders = new HashMap<>();
        if (response.headers() != null) {
            for (Map.Entry<String, Collection<String>> entry : response.headers().entrySet()) {
                convertedHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return FeignResponseWrapper.<T>builder()
                .status(response.status())
                .reason(response.reason())
                .body(body)
                .headers(convertedHeaders)
                .successful(response.status() >= 200 && response.status() < 300)
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
        return status >= 400 && status < 500;
    }

    /**
     * 判断是否为服务器错误（5xx）.
     *
     * @return 是返回 true
     */
    public boolean isServerError() {
        return status >= 500;
    }
}
