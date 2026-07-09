package cc.hongz.ztools.response.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * RabbitMQ RPC 响应.
 * <p>
 * RabbitMQ RPC 模式的标准响应格式。
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
public class RabbitMqRpcResponse<T> {

    /**
     * 关联 ID（用于匹配请求）.
     */
    private String correlationId;

    /**
     * 回复队列.
     */
    private String replyTo;

    /**
     * 响应数据.
     */
    private T data;

    /**
     * 消息属性.
     */
    private Map<String, Object> properties;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建 RPC 响应.
     *
     * @param correlationId 关联 ID
     * @param replyTo       回复队列
     * @param data          响应数据
     * @param <T>           数据类型
     * @return RabbitMqRpcResponse
     */
    public static <T> RabbitMqRpcResponse<T> of(final String correlationId, final String replyTo, final T data) {
        return RabbitMqRpcResponse.<T>builder()
                .correlationId(correlationId)
                .replyTo(replyTo)
                .data(data)
                .build();
    }

    /**
     * 从 AMQP BasicProperties 创建.
     *
     * @param correlationId 关联 ID
     * @param replyTo       回复队列
     * @param data          响应数据
     * @param properties    消息属性
     * @param <T>           数据类型
     * @return RabbitMqRpcResponse
     */
    public static <T> RabbitMqRpcResponse<T> withProperties(final String correlationId, final String replyTo, final T data, final Map<String, Object> properties) {
        return RabbitMqRpcResponse.<T>builder()
                .correlationId(correlationId)
                .replyTo(replyTo)
                .data(data)
                .properties(properties)
                .build();
    }
}
