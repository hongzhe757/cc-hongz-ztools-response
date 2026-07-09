package cc.hongz.ztools.response.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * RocketMQ 响应.
 * <p>
 * RocketMQ 消息响应的标准格式。
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
public class RocketMqResponse<T> {

    /**
     * 消息 ID.
     */
    private String msgId;

    /**
     * Topic.
     */
    private String topic;

    /**
     * Tag.
     */
    private String tag;

    /**
     * Key.
     */
    private String key;

    /**
     * 消息体.
     */
    private T body;

    /**
     * 属性.
     */
    private Map<String, String> properties;

    /**
     * 发送状态（SEND_OK/FLUSH_DISK_TIMEOUT/FLUSH_SLAVE_TIMEOUT/SLAVE_NOT_AVAILABLE）.
     */
    private String sendStatus;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的发送响应.
     *
     * @param msgId      消息 ID
     * @param topic      Topic
     * @param tag        Tag
     * @param body       消息体
     * @param sendStatus 发送状态
     * @param <T>        数据类型
     * @return RocketMqResponse
     */
    public static <T> RocketMqResponse<T> success(final String msgId, final String topic, final String tag, final T body, final String sendStatus) {
        return RocketMqResponse.<T>builder()
                .msgId(msgId)
                .topic(topic)
                .tag(tag)
                .body(body)
                .sendStatus(sendStatus)
                .build();
    }

    /**
     * 从 SendResult 创建.
     *
     * @param sendResult RocketMQ SendResult
     * @param body       消息体
     * @param <T>        数据类型
     * @return RocketMqResponse
     */
    public static <T> RocketMqResponse<T> from(final org.apache.rocketmq.client.producer.SendResult sendResult, final T body) {
        return RocketMqResponse.<T>builder()
                .msgId(sendResult.getMsgId())
                .topic(sendResult.getMessageQueue().getTopic())
                .sendStatus(sendResult.getSendStatus().name())
                .body(body)
                .build();
    }

    /**
     * 从 MessageExt 创建（消费时）.
     *
     * @param messageExt RocketMQ MessageExt
     * @param body       解析后的消息体
     * @param <T>        数据类型
     * @return RocketMqResponse
     */
    public static <T> RocketMqResponse<T> from(final org.apache.rocketmq.common.message.MessageExt messageExt, final T body) {
        return RocketMqResponse.<T>builder()
                .msgId(messageExt.getMsgId())
                .topic(messageExt.getTopic())
                .tag(messageExt.getTags())
                .key(messageExt.getKeys())
                .body(body)
                .properties(messageExt.getProperties())
                .build();
    }
}
