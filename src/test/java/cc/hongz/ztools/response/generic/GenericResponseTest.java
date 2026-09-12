package cc.hongz.ztools.response.generic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 通用响应测试类.
 * <p>
 * 测试 JsonResponse 和 MapResponse。
 * </p>
 *
 * @author hongzhe.qin
 */
@DisplayName("通用响应测试")
class GenericResponseTest {

    // ==================== JsonResponse 测试 ====================

    @Test
    @DisplayName("JsonResponse - 成功响应")
    void testJsonResponseSuccess() {
        String json = "{\"id\":1,\"name\":\"张三\"}";
        JsonResponse response = JsonResponse.success(json);

        assertTrue(response.isSuccess());
        assertEquals("200", response.getCode());
        assertEquals("操作成功", response.getMsg());
        assertEquals(json, response.getJsonData());
        assertTrue(response.hasData());
    }

    @Test
    @DisplayName("JsonResponse - 带消息的成功响应")
    void testJsonResponseSuccessWithMsg() {
        String json = "{\"count\":10}";
        JsonResponse response = JsonResponse.success("查询成功", json);

        assertTrue(response.isSuccess());
        assertEquals("200", response.getCode());
        assertEquals("查询成功", response.getMsg());
        assertEquals(json, response.getJsonData());
    }

    @Test
    @DisplayName("JsonResponse - 完整参数的成功响应")
    void testJsonResponseSuccessFull() {
        String json = "{\"status\":\"ok\"}";
        JsonResponse response = JsonResponse.success("201", "创建成功", json);

        // isSuccess() 只检查 code == "200"
        assertFalse(response.isSuccess());
        assertEquals("201", response.getCode());
        assertEquals("创建成功", response.getMsg());
        assertEquals(json, response.getJsonData());
    }

    @Test
    @DisplayName("JsonResponse - 错误响应")
    void testJsonResponseError() {
        JsonResponse response = JsonResponse.error("服务器内部错误");

        assertFalse(response.isSuccess());
        assertEquals("500", response.getCode());
        assertEquals("服务器内部错误", response.getMsg());
        assertNull(response.getJsonData());
        assertFalse(response.hasData());
    }

    @Test
    @DisplayName("JsonResponse - 带状态码的错误响应")
    void testJsonResponseErrorWithCode() {
        JsonResponse response = JsonResponse.error("404", "资源不存在");

        assertFalse(response.isSuccess());
        assertEquals("404", response.getCode());
        assertEquals("资源不存在", response.getMsg());
        assertNull(response.getJsonData());
    }

    @Test
    @DisplayName("JsonResponse - 带数据的错误响应")
    void testJsonResponseErrorWithData() {
        String errorDetail = "{\"field\":\"email\",\"reason\":\"invalid\"}";
        JsonResponse response = JsonResponse.error("400", "参数错误", errorDetail);

        assertFalse(response.isSuccess());
        assertEquals("400", response.getCode());
        assertEquals(errorDetail, response.getJsonData());
        assertTrue(response.hasData());
    }

    @Test
    @DisplayName("JsonResponse - getJsonDataOrDefault")
    void testJsonResponseGetJsonDataOrDefault() {
        // 有数据时返回实际数据
        JsonResponse resp1 = JsonResponse.success("{\"key\":\"value\"}");
        assertEquals("{\"key\":\"value\"}", resp1.getJsonDataOrDefault());

        // 无数据时返回空对象
        JsonResponse resp2 = JsonResponse.error("error");
        assertEquals("{}", resp2.getJsonDataOrDefault());
    }

    @Test
    @DisplayName("JsonResponse - hasData 判断")
    void testJsonResponseHasData() {
        assertTrue(JsonResponse.success("{}").hasData());
        assertTrue(JsonResponse.success("{\"a\":1}").hasData());
        assertFalse(JsonResponse.error("error").hasData());
        assertFalse(JsonResponse.success(null).hasData());
    }

    // ==================== MapResponse 测试 ====================

    @Test
    @DisplayName("MapResponse - 成功响应")
    void testMapResponseSuccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", 123L);
        data.put("userName", "张三");

        MapResponse response = MapResponse.success(data);

        assertTrue(response.isSuccess());
        assertEquals("200", response.getCode());
        assertEquals("操作成功", response.getMsg());
        assertEquals(2, response.size());
        assertFalse(response.isEmpty());
    }

    @Test
    @DisplayName("MapResponse - 链式添加数据")
    void testMapResponseChainPut() {
        MapResponse response = new MapResponse()
                .put("status", "active")
                .put("count", 10)
                .put("items", java.util.Arrays.asList("a", "b"));

        assertEquals(3, response.size());
        assertEquals("active", response.get("status"));
        assertEquals(10, response.<Integer>get("count"));
    }

    @Test
    @DisplayName("MapResponse - Builder 模式")
    void testMapResponseBuilder() {
        MapResponse response = MapResponse.builder()
                .code("201")
                .msg("创建成功")
                .put("id", 1L)
                .put("name", "测试")
                .build();

        // isSuccess() 只检查 code == "200"
        assertFalse(response.isSuccess());
        assertEquals("201", response.getCode());
        assertEquals("创建成功", response.getMsg());
        assertEquals(2, response.size());
        assertEquals(1L, response.<Long>get("id"));
    }

    @Test
    @DisplayName("MapResponse - 获取值")
    void testMapResponseGetValue() {
        Map<String, Object> data = Map.of("key1", "value1", "key2", 100);
        MapResponse response = MapResponse.success(data);

        assertEquals("value1", response.<String>get("key1"));
        assertEquals(100, response.<Integer>get("key2"));
        assertNull(response.get("nonexistent"));
    }

    @Test
    @DisplayName("MapResponse - 获取值带默认值")
    void testMapResponseGetOrDefault() {
        Map<String, Object> data = Map.of("existing", "value");
        MapResponse response = MapResponse.success(data);

        assertEquals("value", response.getOrDefault("existing", "default"));
        assertEquals("default", response.getOrDefault("missing", "default"));
    }

    @Test
    @DisplayName("MapResponse - containsKey")
    void testMapResponseContainsKey() {
        Map<String, Object> data = Map.of("key1", "val1");
        MapResponse response = MapResponse.success(data);

        assertTrue(response.containsKey("key1"));
        assertFalse(response.containsKey("key2"));
    }

    @Test
    @DisplayName("MapResponse - 移除键")
    void testMapResponseRemove() {
        MapResponse response = MapResponse.empty()
                .put("key1", "val1")
                .put("key2", "val2");

        assertEquals(2, response.size());
        Object removed = response.remove("key1");
        assertEquals("val1", removed);
        assertEquals(1, response.size());
        assertFalse(response.containsKey("key1"));
    }

    @Test
    @DisplayName("MapResponse - 清空数据")
    void testMapResponseClear() {
        MapResponse response = MapResponse.empty()
                .put("a", 1)
                .put("b", 2);

        assertEquals(2, response.size());
        response.clear();
        assertEquals(0, response.size());
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("MapResponse - 批量添加")
    void testMapResponsePutAll() {
        Map<String, Object> additional = Map.of("x", 10, "y", 20);
        MapResponse response = MapResponse.empty()
                .put("a", 1)
                .putAll(additional);

        assertEquals(3, response.size());
        assertTrue(response.containsKey("x"));
        assertTrue(response.containsKey("y"));
    }

    @Test
    @DisplayName("MapResponse - 错误响应")
    void testMapResponseError() {
        MapResponse response = MapResponse.error("404", "未找到");

        assertFalse(response.isSuccess());
        assertEquals("404", response.getCode());
        assertEquals("未找到", response.getMsg());
        assertTrue(response.isEmpty());
    }

    @Test
    @DisplayName("MapResponse - of 工厂方法")
    void testMapResponseOf() {
        Map<String, Object> data = Map.of("test", "data");
        MapResponse response = MapResponse.of(data);

        assertTrue(response.isSuccess());
        assertEquals("200", response.getCode());
        assertEquals(1, response.size());
    }

    @Test
    @DisplayName("MapResponse - empty 工厂方法")
    void testMapResponseEmpty() {
        MapResponse response = MapResponse.empty();

        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertTrue(response.isEmpty());
        assertEquals(0, response.size());
    }
}
