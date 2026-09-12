package cc.hongz.ztools.response.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 数据库响应测试类.
 * <p>
 * 测试 Redis、MongoDB、Elasticsearch 响应对象。
 * </p>
 *
 * @author hongzhe.qin
 */
@DisplayName("数据库响应测试")
class DatabaseResponseTest {

    // ==================== RedisResponse 测试 ====================

    @Test
    @DisplayName("Redis GET 操作 - 键存在")
    void testRedisGetExists() {
        RedisResponse<String> response = RedisResponse.get("user:123", "张三");

        assertTrue(response.isSuccess());
        assertTrue(response.exists());
        assertEquals("user:123", response.getKey());
        assertEquals("张三", response.getValue());
        assertNull(response.getTtl());
    }

    @Test
    @DisplayName("Redis GET 操作 - 键不存在")
    void testRedisGetNotExists() {
        RedisResponse<Object> response = RedisResponse.get("nonexistent", null);

        // get() 方法总是返回 success=true，但 exists() 检查 value 是否为 null
        assertTrue(response.isSuccess());
        assertFalse(response.exists());
        assertEquals("nonexistent", response.getKey());
        assertNull(response.getValue());
    }

    @Test
    @DisplayName("Redis SET 操作")
    void testRedisSet() {
        RedisResponse<Object> response = RedisResponse.set("session:abc");

        assertTrue(response.isSuccess());
        assertEquals("session:abc", response.getKey());
        assertNull(response.getValue());
    }

    @Test
    @DisplayName("Redis 带 TTL 的操作")
    void testRedisWithTtl() {
        RedisResponse<String> response = RedisResponse.withTtl("cache:key", "value", 3600L);

        assertTrue(response.isSuccess());
        assertTrue(response.hasTtl());
        assertEquals("cache:key", response.getKey());
        assertEquals("value", response.getValue());
        assertEquals(3600L, response.getTtl());
    }

    @Test
    @DisplayName("Redis 错误响应")
    void testRedisError() {
        RedisResponse<Object> response = RedisResponse.error("error:key", "连接超时");

        assertFalse(response.isSuccess());
        assertEquals("error:key", response.getKey());
        assertEquals("连接超时", response.getMessage());
    }

    @Test
    @DisplayName("Redis TTL 判断逻辑")
    void testRedisTtlLogic() {
        // 有 TTL 且大于 0
        RedisResponse<String> resp1 = RedisResponse.withTtl("key1", "val", 100L);
        assertTrue(resp1.hasTtl());

        // TTL 为 0
        RedisResponse<String> resp2 = RedisResponse.<String>builder()
                .success(true)
                .key("key2")
                .value("val")
                .ttl(0L)
                .build();
        assertFalse(resp2.hasTtl());

        // TTL 为 null
        RedisResponse<String> resp3 = RedisResponse.get("key3", "val");
        assertFalse(resp3.hasTtl());
    }

    // ==================== MongoDbResponse 测试 ====================

    @Test
    @DisplayName("MongoDB 插入操作")
    void testMongoInsert() {
        MongoDbResponse<String> response = MongoDbResponse.insert("doc_id_123");

        assertTrue(response.isAcknowledged());
        assertEquals("doc_id_123", response.getInsertedId());
        assertEquals(0, response.getMatchedCount());
        assertEquals(0, response.getModifiedCount());
        assertEquals(0, response.getDeletedCount());
    }

    @Test
    @DisplayName("MongoDB 更新操作")
    void testMongoUpdate() {
        MongoDbResponse<Object> response = MongoDbResponse.update(5, 3);

        assertTrue(response.isAcknowledged());
        assertEquals(5, response.getMatchedCount());
        assertEquals(3, response.getModifiedCount());
        assertNull(response.getInsertedId());
    }

    @Test
    @DisplayName("MongoDB 删除操作")
    void testMongoDelete() {
        MongoDbResponse<Object> response = MongoDbResponse.delete(10);

        assertTrue(response.isAcknowledged());
        assertEquals(10, response.getDeletedCount());
        assertEquals(0, response.getMatchedCount());
    }

    @Test
    @DisplayName("MongoDB 查询操作")
    void testMongoFind() {
        List<String> results = Arrays.asList("doc1", "doc2", "doc3");
        MongoDbResponse<String> response = MongoDbResponse.find(results, 100L);

        assertTrue(response.isAcknowledged());
        assertTrue(response.hasResults());
        assertEquals(3, response.getResults().size());
        assertEquals(100L, response.getTotalCount());
        assertEquals("doc1", response.getResults().get(0));
    }

    @Test
    @DisplayName("MongoDB 未确认操作")
    void testMongoUnacknowledged() {
        MongoDbResponse<Object> response = MongoDbResponse.unacknowledged();

        assertFalse(response.isAcknowledged());
        assertEquals(0, response.getMatchedCount());
        assertNull(response.getInsertedId());
    }

    @Test
    @DisplayName("MongoDB 空结果查询")
    void testMongoFindEmpty() {
        MongoDbResponse<String> response = MongoDbResponse.find(List.of(), 0L);

        assertTrue(response.isAcknowledged());
        assertFalse(response.hasResults());
        assertTrue(response.getResults().isEmpty());
        assertEquals(0L, response.getTotalCount());
    }

    // ==================== ElasticsearchResponse 测试 ====================

    @Test
    @DisplayName("Elasticsearch 搜索操作")
    void testEsSearch() {
        ElasticsearchResponse.Hit<String> hit1 = new ElasticsearchResponse.Hit<>(
                "products", "doc1", 1.5f, "product_data_1", null
        );
        ElasticsearchResponse.Hit<String> hit2 = new ElasticsearchResponse.Hit<>(
                "products", "doc2", 1.2f, "product_data_2", null
        );
        List<ElasticsearchResponse.Hit<String>> hits = Arrays.asList(hit1, hit2);

        ElasticsearchResponse<String> response = ElasticsearchResponse.search(100, 1.5f, hits, 50);

        assertEquals(100, response.getTotalHits());
        assertEquals(1.5f, response.getMaxScore());
        assertTrue(response.hasHits());
        assertEquals(2, response.getHits().size());
        assertEquals(50, response.getTook());
        assertFalse(response.isTimedOut());
        assertFalse(response.hasAggregations());
    }

    @Test
    @DisplayName("Elasticsearch 带聚合的搜索")
    void testEsSearchWithAggregations() {
        ElasticsearchResponse.Hit<Integer> hit = new ElasticsearchResponse.Hit<>(
                "orders", "order1", 1.0f, 12345, null
        );
        Map<String, Object> aggregations = Map.of(
                "avg_price", 99.99,
                "total_count", 500
        );

        ElasticsearchResponse<Integer> response = ElasticsearchResponse.withAggregations(
                500, 1.0f, List.of(hit), aggregations, 120
        );

        assertTrue(response.hasHits());
        assertTrue(response.hasAggregations());
        assertEquals(500, response.getTotalHits());
        assertEquals(99.99, response.getAggregations().get("avg_price"));
        assertEquals(120, response.getTook());
    }

    @Test
    @DisplayName("Elasticsearch 无命中结果")
    void testEsNoHits() {
        ElasticsearchResponse<Object> response = ElasticsearchResponse.search(0, null, List.of(), 10);

        assertEquals(0, response.getTotalHits());
        assertFalse(response.hasHits());
        assertNull(response.getMaxScore());
        assertTrue(response.getHits().isEmpty());
    }

    @Test
    @DisplayName("Elasticsearch Hit 对象")
    void testEsHitObject() {
        Map<String, List<String>> highlight = Map.of(
                "title", List.of("<em>测试</em>标题")
        );

        ElasticsearchResponse.Hit<String> hit = new ElasticsearchResponse.Hit<>(
                "articles", "art_001", 2.5f, "文章内容", highlight
        );

        assertEquals("articles", hit.getIndex());
        assertEquals("art_001", hit.getId());
        assertEquals(2.5f, hit.getScore());
        assertEquals("文章内容", hit.getSource());
        assertNotNull(hit.getHighlight());
        assertTrue(hit.getHighlight().containsKey("title"));
    }
}
