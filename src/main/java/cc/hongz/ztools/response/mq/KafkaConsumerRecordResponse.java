package cc.hongz.ztools.response.mq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Map;

/**
 * Kafka Consumer Record 响应.
 * <p>
 * Kafka 消费者记录的标准格式。
 * </p>
 *
 * @param <K> Key 类型
 * @param <V> Value 类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KafkaConsumerRecordResponse<K, V> {

    /**
     * Topic 名称.
     */
    private String topic;

    /**
     * 分区号.
     */
    private int partition;

    /**
     * 偏移量.
     */
    private long offset;

    /**
     * 时间戳.
     */
    private long timestamp;

    /**
     * Key.
     */
    private K key;

    /**
     * Value.
     */
    private V value;

    /**
     * Headers.
     */
    private Map<String, byte[]> headers;

    // ==================== 静态工厂方法 ====================

    /**
     * 从 ConsumerRecord 创建.
     *
     * @param record Kafka ConsumerRecord
     * @param <K>    Key 类型
     * @param <V>    Value 类型
     * @return KafkaConsumerRecordResponse
     */
    public static <K, V> KafkaConsumerRecordResponse<K, V> from(
            final ConsumerRecord<K,
            V> record
        ) {
        return KafkaConsumerRecordResponse.<K, V>builder()
                .topic(record.topic())
                .partition(record.partition())
                .offset(record.offset())
                .timestamp(record.timestamp())
                .key(record.key())
                .value(record.value())
                .build();
    }

    /**
     * 创建自定义的记录响应.
     *
     * @param topic     Topic
     * @param partition 分区
     * @param offset    偏移量
     * @param timestamp 时间戳
     * @param key       Key
     * @param value     Value
     * @param <K>       Key 类型
     * @param <V>       Value 类型
     * @return KafkaConsumerRecordResponse
     */
    public static <K, V> KafkaConsumerRecordResponse<K, V> of(
            final String topic,
            final int partition,
            final long offset,
            final long timestamp,
            final K key,
            final V value
        ) {
        return KafkaConsumerRecordResponse.<K, V>builder()
                .topic(topic)
                .partition(partition)
                .offset(offset)
                .timestamp(timestamp)
                .key(key)
                .value(value)
                .build();
    }
}
