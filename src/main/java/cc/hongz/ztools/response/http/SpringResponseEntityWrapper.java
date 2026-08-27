package cc.hongz.ztools.response.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Spring ResponseEntity 响应包装类.
 * <p>
 * 将 Spring {@link ResponseEntity} 转换为统一的 JSON 格式。
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
public class SpringResponseEntityWrapper<T> {

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
     * @return SpringResponseEntityWrapper
     */
    public static <T> SpringResponseEntityWrapper<T> success(
            final int statusCodeValue,
            final String statusCodeName,
            final T body,
            final Map<String,
            List<String>> headers
        ) {
        return SpringResponseEntityWrapper.<T>builder()
                .statusCodeValue(statusCodeValue)
                .statusCodeName(statusCodeName)
                .body(body)
                .headers(headers)
                .successful(statusCodeValue >= 200 && statusCodeValue < 300)
                .build();
    }

    /**
     * 从 Spring ResponseEntity 创建包装.
     *
     * @param responseEntity Spring ResponseEntity
     * @param <T>            响应体类型
     * @return SpringResponseEntityWrapper
     */
    public static <T> SpringResponseEntityWrapper<T> from(final ResponseEntity<T> responseEntity) {
        return SpringResponseEntityWrapper.<T>builder()
                .statusCodeValue(responseEntity.getStatusCodeValue())
                .statusCodeName(responseEntity.getStatusCode().toString())
                .body(responseEntity.getBody())
                .headers(responseEntity.getHeaders())
                .successful(responseEntity.getStatusCode().is2xxSuccessful())
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
