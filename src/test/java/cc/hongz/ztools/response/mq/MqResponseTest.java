package cc.hongz.ztools.response.mq;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * MQ 响应测试.
 * 覆盖 KafkaConsumerRecordResponse, RocketMqResponse, RabbitMqRpcResponse
 * 
 * 注意：这些测试不需要启动 Kafka/RocketMQ/RabbitMQ 运行时服务，
 * 因为只测试工厂方法 of()/success()/withProperties()，不测试 from(ConsumerRecord) 等需要外部依赖的方法。
 */
@DisplayName("MQ 响应测试")
class MqResponseTest {

    // ==================== KafkaConsumerRecordResponse 测试 ====================

    @Test
    @DisplayName("KafkaConsumerRecordResponse - of 工厂方法")
    void testKafkaOf() {
        KafkaConsumerRecordResponse<String, String> response = KafkaConsumerRecordResponse.of(
                "test-topic", 0, 100L, System.currentTimeMillis(), "key1", "value1"
        );

        assertEquals("test-topic", response.getTopic());
        assertEquals(0, response.getPartition());
        assertEquals(100L, response.getOffset());
        assertEquals("key1", response.getKey());
        assertEquals("value1", response.getValue());
    }

    @Test
    @DisplayName("KafkaConsumerRecordResponse - builder 模式")
    void testKafkaBuilder() {
        Map<String, byte[]> headers = new HashMap<>();
        headers.put("header1", "value1".getBytes());

        KafkaConsumerRecordResponse<Integer, String> response = KafkaConsumerRecordResponse.<Integer, String>builder()
                .topic("order-topic")
                .partition(3)
                .offset(999L)
                .timestamp(1234567890L)
                .key(123)
                .value("order-data")
                .headers(headers)
                .build();

        assertEquals("order-topic", response.getTopic());
        assertEquals(3, response.getPartition());
        assertEquals(999L, response.getOffset());
        assertEquals(1234567890L, response.getTimestamp());
        assertEquals(Integer.valueOf(123), response.getKey());
        assertEquals("order-data", response.getValue());
        assertNotNull(response.getHeaders());
        assertEquals(1, response.getHeaders().size());
    }

    @Test
    @DisplayName("KafkaConsumerRecordResponse - null key/value")
    void testKafkaNullKeyValue() {
        KafkaConsumerRecordResponse<String, String> response = KafkaConsumerRecordResponse.of(
                "topic", 0, 0L, 0L, null, null
        );

        assertNull(response.getKey());
        assertNull(response.getValue());
    }

    // ==================== RocketMqResponse 测试 ====================

    @Test
    @DisplayName("RocketMqResponse - success 工厂方法")
    void testRocketMqSuccess() {
        RocketMqResponse<String> response = RocketMqResponse.success(
                "msg-001", "test-topic", "tag-a", "message body", "SEND_OK"
        );

        assertEquals("msg-001", response.getMsgId());
        assertEquals("test-topic", response.getTopic());
        assertEquals("tag-a", response.getTag());
        assertEquals("message body", response.getBody());
        assertEquals("SEND_OK", response.getSendStatus());
    }

    @Test
    @DisplayName("RocketMqResponse - builder 模式")
    void testRocketMqBuilder() {
        Map<String, String> properties = new HashMap<>();
        properties.put("prop1", "value1");

        RocketMqResponse<String> response = RocketMqResponse.<String>builder()
                .msgId("msg-002")
                .topic("order-topic")
                .tag("order-tag")
                .key("order-key")
                .body("order body")
                .properties(properties)
                .sendStatus("SEND_OK")
                .build();

        assertEquals("msg-002", response.getMsgId());
        assertEquals("order-topic", response.getTopic());
        assertEquals("order-tag", response.getTag());
        assertEquals("order-key", response.getKey());
        assertEquals("order body", response.getBody());
        assertEquals("SEND_OK", response.getSendStatus());
        assertNotNull(response.getProperties());
        assertEquals("value1", response.getProperties().get("prop1"));
    }

    @Test
    @DisplayName("RocketMqResponse - 不同发送状态")
    void testRocketMqDifferentSendStatus() {
        RocketMqResponse<String> ok = RocketMqResponse.success("id1", "t", "tag", "body", "SEND_OK");
        RocketMqResponse<String> timeout = RocketMqResponse.success("id2", "t", "tag", "body", "FLUSH_DISK_TIMEOUT");
        RocketMqResponse<String> slaveTimeout =
            RocketMqResponse.success("id3", "t", "tag", "body", "FLUSH_SLAVE_TIMEOUT");
        RocketMqResponse<String> noSlave = RocketMqResponse.success("id4", "t", "tag", "body", "SLAVE_NOT_AVAILABLE");

        assertEquals("SEND_OK", ok.getSendStatus());
        assertEquals("FLUSH_DISK_TIMEOUT", timeout.getSendStatus());
        assertEquals("FLUSH_SLAVE_TIMEOUT", slaveTimeout.getSendStatus());
        assertEquals("SLAVE_NOT_AVAILABLE", noSlave.getSendStatus());
    }

    // ==================== RabbitMqRpcResponse 测试 ====================

    @Test
    @DisplayName("RabbitMqRpcResponse - of 工厂方法")
    void testRabbitMqOf() {
        RabbitMqRpcResponse<String> response = RabbitMqRpcResponse.of("corr-001", "reply-queue", "response data");

        assertEquals("corr-001", response.getCorrelationId());
        assertEquals("reply-queue", response.getReplyTo());
        assertEquals("response data", response.getData());
        assertNull(response.getProperties());
    }

    @Test
    @DisplayName("RabbitMqRpcResponse - withProperties 工厂方法")
    void testRabbitMqWithProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("contentType", "application/json");
        props.put("priority", 5);

        RabbitMqRpcResponse<String> response = RabbitMqRpcResponse.withProperties(
                "corr-002", "reply-queue-2", "data with props", props
        );

        assertEquals("corr-002", response.getCorrelationId());
        assertEquals("reply-queue-2", response.getReplyTo());
        assertEquals("data with props", response.getData());
        assertNotNull(response.getProperties());
        assertEquals("application/json", response.getProperties().get("contentType"));
        assertEquals(5, response.getProperties().get("priority"));
    }

    @Test
    @DisplayName("RabbitMqRpcResponse - builder 模式")
    void testRabbitMqBuilder() {
        RabbitMqRpcResponse<Integer> response = RabbitMqRpcResponse.<Integer>builder()
                .correlationId("corr-003")
                .replyTo("rpc-reply")
                .data(42)
                .build();

        assertEquals("corr-003", response.getCorrelationId());
        assertEquals("rpc-reply", response.getReplyTo());
        assertEquals(Integer.valueOf(42), response.getData());
    }

    @Test
    @DisplayName("RabbitMqRpcResponse - null data")
    void testRabbitMqNullData() {
        RabbitMqRpcResponse<String> response = RabbitMqRpcResponse.of("corr-004", "reply", null);

        assertNull(response.getData());
    }
}
