package cc.hongz.ztools.response.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Server-Sent Events (SSE) 响应.
 * <p>
 * SSE 事件的标准格式。
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
public class SseEvent<T> {

    /**
     * 事件 ID（可选）.
     */
    private String id;

    /**
     * 事件类型（默认为 "message"）.
     */
    private String event;

    /**
     * 重试时间（毫秒，可选）.
     */
    private Long retry;

    /**
     * 数据内容.
     */
    private T data;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建默认消息事件.
     *
     * @param data 数据内容
     * @param <T>  数据类型
     * @return SseEvent
     */
    public static <T> SseEvent<T> message(final T data) {
        return SseEvent.<T>builder()
                .event("message")
                .data(data)
                .build();
    }

    /**
     * 创建自定义类型事件.
     *
     * @param event 事件类型
     * @param data  数据内容
     * @param <T>   数据类型
     * @return SseEvent
     */
    public static <T> SseEvent<T> of(final String event, final T data) {
        return SseEvent.<T>builder()
                .event(event)
                .data(data)
                .build();
    }

    /**
     * 创建带 ID 的事件.
     *
     * @param id    事件 ID
     * @param event 事件类型
     * @param data  数据内容
     * @param <T>   数据类型
     * @return SseEvent
     */
    public static <T> SseEvent<T> withId(final String id, final String event, final T data) {
        return SseEvent.<T>builder()
                .id(id)
                .event(event)
                .data(data)
                .build();
    }

    /**
     * 创建带重试的事件.
     *
     * @param event 事件类型
     * @param data  数据内容
     * @param retry 重试时间（毫秒）
     * @param <T>   数据类型
     * @return SseEvent
     */
    public static <T> SseEvent<T> withRetry(final String event, final T data, final Long retry) {
        return SseEvent.<T>builder()
                .event(event)
                .data(data)
                .retry(retry)
                .build();
    }

    /**
     * 转换为 SSE 格式字符串.
     *
     * @return SSE 格式字符串
     */
    public String toSseString() {
        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append("id: ").append(id).append("\n");
        }
        if (event != null) {
            sb.append("event: ").append(event).append("\n");
        }
        if (retry != null) {
            sb.append("retry: ").append(retry).append("\n");
        }
        if (data != null) {
            sb.append("data: ").append(data).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}
