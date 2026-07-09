package cc.hongz.ztools.response.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * WebSocket 消息响应.
 * <p>
 * WebSocket 消息的标准格式。
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
public class WebSocketMessage<T> {

    /**
     * 消息类型（text/binary/ping/pong/close）.
     */
    private String type;

    /**
     * 消息内容.
     */
    private T payload;

    /**
     * 消息 ID（可选）.
     */
    private String messageId;

    /**
     * 时间戳.
     */
    private Long timestamp;

    /**
     * 元数据.
     */
    private Map<String, Object> metadata;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建文本消息.
     *
     * @param payload 消息内容
     * @param <T>     数据类型
     * @return WebSocketMessage
     */
    public static <T> WebSocketMessage<T> text(final T payload) {
        return WebSocketMessage.<T>builder()
                .type("text")
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建带 ID 的消息.
     *
     * @param messageId 消息 ID
     * @param payload   消息内容
     * @param <T>       数据类型
     * @return WebSocketMessage
     */
    public static <T> WebSocketMessage<T> withId(final String messageId, final T payload) {
        return WebSocketMessage.<T>builder()
                .type("text")
                .messageId(messageId)
                .payload(payload)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建关闭消息.
     *
     * @param code   关闭码
     * @param reason 原因
     * @return WebSocketMessage
     */
    public static WebSocketMessage<String> close(final int code, final String reason) {
        return WebSocketMessage.<String>builder()
                .type("close")
                .payload(code + ":" + reason)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建 Ping 消息.
     *
     * @return WebSocketMessage
     */
    public static WebSocketMessage<String> ping() {
        return WebSocketMessage.<String>builder()
                .type("ping")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 创建 Pong 消息.
     *
     * @return WebSocketMessage
     */
    public static WebSocketMessage<String> pong() {
        return WebSocketMessage.<String>builder()
                .type("pong")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为文本消息.
     *
     * @return 是返回 true
     */
    public boolean isText() {
        return "text".equalsIgnoreCase(type);
    }

    /**
     * 判断是否为关闭消息.
     *
     * @return 是返回 true
     */
    public boolean isClose() {
        return "close".equalsIgnoreCase(type);
    }
}
