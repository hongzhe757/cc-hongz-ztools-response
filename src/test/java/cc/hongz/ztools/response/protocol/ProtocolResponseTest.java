package cc.hongz.ztools.response.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * protocol 包响应类的单元测试.
 * <p>
 * 测试 SseEvent、WebSocketMessage、XmlToJsonResponse。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("Protocol 包响应类测试")
class ProtocolResponseTest {

    // ==================== SseEvent 测试 ====================

    @Test
    @DisplayName("SseEvent.message(data) - 创建默认消息事件")
    void testSseEventMessage() {
        String data = "SSE 数据";
        SseEvent<String> event = SseEvent.message(data);

        assertNotNull(event);
        assertEquals("message", event.getEvent());
        assertEquals(data, event.getData());
        assertNull(event.getId());
        assertNull(event.getRetry());
    }

    @Test
    @DisplayName("SseEvent.of(event, data) - 创建自定义类型事件")
    void testSseEventOf() {
        String eventType = "notification";
        String data = "通知内容";
        SseEvent<String> event = SseEvent.of(eventType, data);

        assertEquals(eventType, event.getEvent());
        assertEquals(data, event.getData());
    }

    @Test
    @DisplayName("SseEvent.withId(id, event, data) - 创建带 ID 的事件")
    void testSseEventWithId() {
        String id = "event-123";
        String eventType = "update";
        String data = "更新数据";
        SseEvent<String> event = SseEvent.withId(id, eventType, data);

        assertEquals(id, event.getId());
        assertEquals(eventType, event.getEvent());
        assertEquals(data, event.getData());
    }

    @Test
    @DisplayName("SseEvent.withRetry(event, data, retry) - 创建带重试的事件")
    void testSseEventWithRetry() {
        String eventType = "heartbeat";
        String data = "心跳";
        Long retry = 5000L;
        SseEvent<String> event = SseEvent.withRetry(eventType, data, retry);

        assertEquals(eventType, event.getEvent());
        assertEquals(data, event.getData());
        assertEquals(retry, event.getRetry());
    }

    @Test
    @DisplayName("SseEvent.toSseString() - 转换为 SSE 格式字符串")
    void testSseEventToSseString() {
        SseEvent<String> event = SseEvent.<String>builder()
                .id("123")
                .event("message")
                .data("hello")
                .retry(3000L)
                .build();

        String sseString = event.toSseString();

        assertNotNull(sseString);
        assertTrue(sseString.contains("id: 123"));
        assertTrue(sseString.contains("event: message"));
        assertTrue(sseString.contains("retry: 3000"));
        assertTrue(sseString.contains("data: hello"));
        assertTrue(sseString.endsWith("\n\n"));
    }

    @Test
    @DisplayName("SseEvent.toSseString() - 部分字段为空")
    void testSseEventToSseStringPartialFields() {
        SseEvent<String> event = SseEvent.message("only data");
        String sseString = event.toSseString();

        assertTrue(sseString.contains("event: message"));
        assertTrue(sseString.contains("data: only data"));
        assertFalse(sseString.contains("id:"));
        assertFalse(sseString.contains("retry:"));
    }

    @Test
    @DisplayName("SseEvent Builder 模式")
    void testSseEventBuilder() {
        SseEvent<Map<String, Object>> event = SseEvent.<Map<String, Object>>builder()
                .id("evt-001")
                .event("data-update")
                .data(Map.of("key", "value"))
                .retry(10000L)
                .build();

        assertEquals("evt-001", event.getId());
        assertEquals("data-update", event.getEvent());
        assertNotNull(event.getData());
        assertEquals(10000L, event.getRetry());
    }

    @Test
    @DisplayName("SseEvent Getter/Setter")
    void testSseEventGettersSetters() {
        SseEvent<String> event = new SseEvent<>();
        event.setId("id-1");
        event.setEvent("custom");
        event.setData("payload");
        event.setRetry(2000L);

        assertEquals("id-1", event.getId());
        assertEquals("custom", event.getEvent());
        assertEquals("payload", event.getData());
        assertEquals(2000L, event.getRetry());
    }

    // ==================== WebSocketMessage 测试 ====================

    @Test
    @DisplayName("WebSocketMessage.text(payload) - 创建文本消息")
    void testWebSocketMessageText() {
        String payload = "Hello WebSocket";
        WebSocketMessage<String> message = WebSocketMessage.text(payload);

        assertNotNull(message);
        assertEquals("text", message.getType());
        assertEquals(payload, message.getPayload());
        assertNotNull(message.getTimestamp());
        assertTrue(message.isText());
        assertFalse(message.isClose());
    }

    @Test
    @DisplayName("WebSocketMessage.withId(messageId, payload) - 创建带 ID 的消息")
    void testWebSocketMessageWithId() {
        String messageId = "msg-456";
        String payload = "带 ID 的消息";
        WebSocketMessage<String> message = WebSocketMessage.withId(messageId, payload);

        assertEquals("text", message.getType());
        assertEquals(messageId, message.getMessageId());
        assertEquals(payload, message.getPayload());
        assertNotNull(message.getTimestamp());
    }

    @Test
    @DisplayName("WebSocketMessage.close(code, reason) - 创建关闭消息")
    void testWebSocketMessageClose() {
        int code = 1000;
        String reason = "Normal Closure";
        WebSocketMessage<String> message = WebSocketMessage.close(code, reason);

        assertEquals("close", message.getType());
        assertTrue(message.getPayload().contains(String.valueOf(code)));
        assertTrue(message.getPayload().contains(reason));
        assertTrue(message.isClose());
        assertFalse(message.isText());
    }

    @Test
    @DisplayName("WebSocketMessage.ping() - 创建 Ping 消息")
    void testWebSocketMessagePing() {
        WebSocketMessage<String> message = WebSocketMessage.ping();

        assertEquals("ping", message.getType());
        assertNull(message.getPayload());
        assertNotNull(message.getTimestamp());
    }

    @Test
    @DisplayName("WebSocketMessage.pong() - 创建 Pong 消息")
    void testWebSocketMessagePong() {
        WebSocketMessage<String> message = WebSocketMessage.pong();

        assertEquals("pong", message.getType());
        assertNull(message.getPayload());
        assertNotNull(message.getTimestamp());
    }

    @Test
    @DisplayName("WebSocketMessage.isText() - 判断是否为文本消息")
    void testWebSocketMessageIsText() {
        assertTrue(WebSocketMessage.text("data").isText());
        assertFalse(WebSocketMessage.ping().isText());
        assertFalse(WebSocketMessage.close(1000, "reason").isText());
    }

    @Test
    @DisplayName("WebSocketMessage.isClose() - 判断是否为关闭消息")
    void testWebSocketMessageIsClose() {
        assertTrue(WebSocketMessage.close(1000, "reason").isClose());
        assertFalse(WebSocketMessage.text("data").isClose());
        assertFalse(WebSocketMessage.ping().isClose());
    }

    @Test
    @DisplayName("WebSocketMessage Builder 模式")
    void testWebSocketMessageBuilder() {
        Map<String, Object> metadata = Map.of("userId", 123L);
        WebSocketMessage<String> message = WebSocketMessage.<String>builder()
                .type("text")
                .payload("消息内容")
                .messageId("msg-789")
                .timestamp(1234567890L)
                .metadata(metadata)
                .build();

        assertEquals("text", message.getType());
        assertEquals("消息内容", message.getPayload());
        assertEquals("msg-789", message.getMessageId());
        assertEquals(1234567890L, message.getTimestamp());
        assertEquals(metadata, message.getMetadata());
    }

    @Test
    @DisplayName("WebSocketMessage Getter/Setter")
    void testWebSocketMessageGettersSetters() {
        WebSocketMessage<String> message = new WebSocketMessage<>();
        message.setType("binary");
        message.setPayload("binary-data");
        message.setMessageId("msg-000");
        message.setTimestamp(9999999999L);
        message.setMetadata(Map.of("key", "value"));

        assertEquals("binary", message.getType());
        assertEquals("binary-data", message.getPayload());
        assertEquals("msg-000", message.getMessageId());
        assertEquals(9999999999L, message.getTimestamp());
        assertNotNull(message.getMetadata());
    }

    // ==================== XmlToJsonResponse 测试 ====================

    @Test
    @DisplayName("XmlToJsonResponse.of(rootElement, data) - 创建简单响应")
    void testXmlToJsonResponseOf() {
        String rootElement = "root";
        Map<String, Object> data = Map.of("key", "value");
        XmlToJsonResponse<Map<String, Object>> response = XmlToJsonResponse.of(rootElement, data);

        assertNotNull(response);
        assertEquals(rootElement, response.getRootElement());
        assertEquals(data, response.getData());
        assertFalse(response.hasAttributes());
        assertFalse(response.hasNamespaces());
        assertFalse(response.hasOriginalXml());
    }

    @Test
    @DisplayName("XmlToJsonResponse.withAttributes(...) - 创建带属性的响应")
    void testXmlToJsonResponseWithAttributes() {
        String rootElement = "user";
        String data = "张三";
        Map<String, String> attributes = Map.of("id", "123", "type", "admin");

        XmlToJsonResponse<String> response = XmlToJsonResponse.withAttributes(rootElement, data, attributes);

        assertEquals(rootElement, response.getRootElement());
        assertEquals(data, response.getData());
        assertTrue(response.hasAttributes());
        assertEquals(2, response.getAttributes().size());
        assertEquals("123", response.getAttributes().get("id"));
    }

    @Test
    @DisplayName("XmlToJsonResponse.full(...) - 创建完整响应")
    void testXmlToJsonResponseFull() {
        String rootElement = "order";
        Map<String, Object> data = Map.of("orderId", "ORD-001");
        Map<String, String> attributes = Map.of("version", "1.0");
        Map<String, String> namespaces = Map.of("ns", "http://example.com");
        String originalXml = "<order><orderId>ORD-001</orderId></order>";

        XmlToJsonResponse<Map<String, Object>> response = XmlToJsonResponse.full(
                rootElement, data, attributes, namespaces, originalXml
        );

        assertEquals(rootElement, response.getRootElement());
        assertEquals(data, response.getData());
        assertTrue(response.hasAttributes());
        assertTrue(response.hasNamespaces());
        assertTrue(response.hasOriginalXml());
        assertEquals(originalXml, response.getOriginalXml());
    }

    @Test
    @DisplayName("XmlToJsonResponse.hasAttributes() - 判断是否有属性")
    void testXmlToJsonResponseHasAttributes() {
        XmlToJsonResponse<String> resp1 = XmlToJsonResponse.of("root", "data");
        assertFalse(resp1.hasAttributes());

        XmlToJsonResponse<String> resp2 = XmlToJsonResponse.withAttributes("root", "data", Map.of("key", "val"));
        assertTrue(resp2.hasAttributes());

        // 空 Map
        XmlToJsonResponse<String> resp3 = XmlToJsonResponse.withAttributes("root", "data", Map.of());
        assertFalse(resp3.hasAttributes());
    }

    @Test
    @DisplayName("XmlToJsonResponse.hasNamespaces() - 判断是否有命名空间")
    void testXmlToJsonResponseHasNamespaces() {
        XmlToJsonResponse<String> resp1 = XmlToJsonResponse.of("root", "data");
        assertFalse(resp1.hasNamespaces());

        XmlToJsonResponse<String> resp2 = XmlToJsonResponse.<String>builder()
                .rootElement("root")
                .data("data")
                .namespaces(Map.of("ns", "uri"))
                .build();
        assertTrue(resp2.hasNamespaces());
    }

    @Test
    @DisplayName("XmlToJsonResponse.hasOriginalXml() - 判断是否保留原始 XML")
    void testXmlToJsonResponseHasOriginalXml() {
        XmlToJsonResponse<String> resp1 = XmlToJsonResponse.of("root", "data");
        assertFalse(resp1.hasOriginalXml());

        XmlToJsonResponse<String> resp2 = XmlToJsonResponse.<String>builder()
                .rootElement("root")
                .data("data")
                .originalXml("<root>data</root>")
                .build();
        assertTrue(resp2.hasOriginalXml());

        // 空字符串
        XmlToJsonResponse<String> resp3 = XmlToJsonResponse.<String>builder()
                .rootElement("root")
                .data("data")
                .originalXml("")
                .build();
        assertFalse(resp3.hasOriginalXml());
    }

    @Test
    @DisplayName("XmlToJsonResponse Builder 模式")
    void testXmlToJsonResponseBuilder() {
        XmlToJsonResponse<String> response = XmlToJsonResponse.<String>builder()
                .rootElement("response")
                .data("成功")
                .attributes(Map.of("status", "ok"))
                .namespaces(Map.of("xmlns", "http://example.com"))
                .originalXml("<response>成功</response>")
                .build();

        assertEquals("response", response.getRootElement());
        assertEquals("成功", response.getData());
        assertTrue(response.hasAttributes());
        assertTrue(response.hasNamespaces());
        assertTrue(response.hasOriginalXml());
    }

    @Test
    @DisplayName("XmlToJsonResponse Getter/Setter")
    void testXmlToJsonResponseGettersSetters() {
        XmlToJsonResponse<String> response = new XmlToJsonResponse<>();
        response.setRootElement("test");
        response.setData("data-value");
        response.setAttributes(Map.of("attr", "val"));
        response.setNamespaces(Map.of("ns", "uri"));
        response.setOriginalXml("<test>data-value</test>");

        assertEquals("test", response.getRootElement());
        assertEquals("data-value", response.getData());
        assertNotNull(response.getAttributes());
        assertNotNull(response.getNamespaces());
        assertEquals("<test>data-value</test>", response.getOriginalXml());
    }
}
