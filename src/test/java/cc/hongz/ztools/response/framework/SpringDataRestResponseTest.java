package cc.hongz.ztools.response.framework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SpringDataRestResponse 单元测试.
 * <p>
 * 测试 Spring Data REST 响应结构的工厂方法和内部类。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("SpringDataRestResponse 测试")
class SpringDataRestResponseTest {

    // ==================== ofResource 测试 ====================

    @Test
    @DisplayName("ofResource() 创建单个资源响应")
    void testOfResource() {
        // Given
        String data = "user-123";
        Map<String, SpringDataRestResponse.Link> links = new HashMap<>();
        links.put("self", new SpringDataRestResponse.Link("/api/users/123", false));

        // When
        SpringDataRestResponse<String> response = SpringDataRestResponse.ofResource(data, links);

        // Then
        assertNotNull(response);
        assertNotNull(response.getEmbedded());
        assertEquals(1, response.getEmbedded().getItems().size());
        assertEquals("user-123", response.getEmbedded().getItems().get(0));
        assertNotNull(response.getLinks());
        assertEquals("/api/users/123", response.getLinks().get("self").getHref());
        assertFalse(response.getLinks().get("self").getTemplated());
    }

    @Test
    @DisplayName("ofResource() 带模板链接")
    void testOfResourceWithTemplatedLink() {
        // Given
        Map<String, SpringDataRestResponse.Link> links = new HashMap<>();
        links.put("search", new SpringDataRestResponse.Link("/api/users{?name}", true));

        // When
        SpringDataRestResponse<String> response = SpringDataRestResponse.ofResource("user", links);

        // Then
        assertTrue(response.getLinks().get("search").getTemplated());
        assertEquals("/api/users{?name}", response.getLinks().get("search").getHref());
    }

    // ==================== ofCollection 测试 ====================

    @Test
    @DisplayName("ofCollection() 创建集合资源响应")
    void testOfCollection() {
        // Given
        List<String> items = Arrays.asList("item1", "item2", "item3");
        Map<String, SpringDataRestResponse.Link> links = Collections.singletonMap(
                "self", new SpringDataRestResponse.Link("/api/items", false)
        );

        // When
        SpringDataRestResponse<String> response = SpringDataRestResponse.ofCollection(items, links);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getEmbedded().getItems().size());
        assertEquals("item1", response.getEmbedded().getItems().get(0));
        assertNull(response.getPage());
    }

    @Test
    @DisplayName("ofCollection() 空列表")
    void testOfCollectionEmpty() {
        // When
        SpringDataRestResponse<String> response = SpringDataRestResponse.ofCollection(
                Collections.emptyList(), Collections.emptyMap()
        );

        // Then
        assertNotNull(response);
        assertTrue(response.getEmbedded().getItems().isEmpty());
    }

    // ==================== ofPage 测试 ====================

    @Test
    @DisplayName("ofPage() 创建分页响应")
    void testOfPage() {
        // Given
        List<Integer> items = Arrays.asList(1, 2, 3, 4, 5);
        Map<String, SpringDataRestResponse.Link> links = new HashMap<>();
        links.put("first", new SpringDataRestResponse.Link("/api/items?page=0&size=5", false));
        links.put("next", new SpringDataRestResponse.Link("/api/items?page=1&size=5", false));

        SpringDataRestResponse.Page page = new SpringDataRestResponse.Page(5, 100L, 20, 2);

        // When
        SpringDataRestResponse<Integer> response = SpringDataRestResponse.ofPage(items, links, page);

        // Then
        assertNotNull(response);
        assertEquals(5, response.getEmbedded().getItems().size());
        assertNotNull(response.getPage());
        assertEquals(5, response.getPage().getSize());
        assertEquals(100L, response.getPage().getTotalElements());
        assertEquals(20, response.getPage().getTotalPages());
        assertEquals(2, response.getPage().getNumber());
    }

    // ==================== 内部类测试 ====================

    @Test
    @DisplayName("Embedded 内部类测试")
    void testEmbedded() {
        // Given
        List<String> items = Arrays.asList("a", "b");
        SpringDataRestResponse.Embedded<String> embedded = new SpringDataRestResponse.Embedded<>(items);

        // Then
        assertEquals(2, embedded.getItems().size());
        assertEquals("a", embedded.getItems().get(0));
    }

    @Test
    @DisplayName("Link 内部类测试")
    void testLink() {
        // Given
        SpringDataRestResponse.Link link = new SpringDataRestResponse.Link("/api/test", true);

        // Then
        assertEquals("/api/test", link.getHref());
        assertTrue(link.getTemplated());
    }

    @Test
    @DisplayName("Page 内部类测试")
    void testPage() {
        // Given
        SpringDataRestResponse.Page page = new SpringDataRestResponse.Page(10, 50L, 5, 3);

        // Then
        assertEquals(10, page.getSize());
        assertEquals(50L, page.getTotalElements());
        assertEquals(5, page.getTotalPages());
        assertEquals(3, page.getNumber());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder 构建完整响应")
    void testBuilder() {
        // When
        SpringDataRestResponse<String> response = SpringDataRestResponse.<String>builder()
                .embedded(new SpringDataRestResponse.Embedded<>(List.of("data")))
                .links(Collections.singletonMap("self", new SpringDataRestResponse.Link("/api", false)))
                .page(new SpringDataRestResponse.Page(10, 100L, 10, 0))
                .build();

        // Then
        assertNotNull(response);
        assertEquals(1, response.getEmbedded().getItems().size());
        assertNotNull(response.getLinks());
        assertNotNull(response.getPage());
    }

    @Test
    @DisplayName("NoArgsConstructor 创建空响应")
    void testNoArgsConstructor() {
        // When
        SpringDataRestResponse<String> response = new SpringDataRestResponse<>();

        // Then
        assertNull(response.getEmbedded());
        assertNull(response.getLinks());
        assertNull(response.getPage());
    }
}
