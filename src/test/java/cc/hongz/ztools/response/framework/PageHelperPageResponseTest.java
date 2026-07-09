package cc.hongz.ztools.response.framework;

import cc.hongz.ztools.response.common.DataListInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageHelperPageResponse 单元测试.
 * <p>
 * 测试 of() 工厂方法和 toDataListInfo() 转换方法。
 * from(PageInfo) 方法因依赖 PageHelper（optional）不在测试范围。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("PageHelperPageResponse 测试")
class PageHelperPageResponseTest {

    // ==================== of() 工厂方法测试 ====================

    @Test
    @DisplayName("of() 创建分页响应")
    void testOf() {
        // Given
        List<String> list = Arrays.asList("item1", "item2", "item3");

        // When
        PageHelperPageResponse<String> response = PageHelperPageResponse.of(
                100L, 10, 1, 10, list
        );

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getTotal());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPages());
        assertEquals(3, response.getList().size());
        assertEquals("item1", response.getList().get(0));
    }

    @Test
    @DisplayName("of() 空列表")
    void testOf_EmptyList() {
        // When
        PageHelperPageResponse<String> response = PageHelperPageResponse.of(
                0L, 10, 1, 0, Collections.emptyList()
        );

        // Then
        assertEquals(0L, response.getTotal());
        assertTrue(response.getList().isEmpty());
    }

    @Test
    @DisplayName("of() null 列表")
    void testOf_NullList() {
        // When
        PageHelperPageResponse<String> response = PageHelperPageResponse.of(
                0L, 10, 1, 0, null
        );

        // Then
        assertNull(response.getList());
    }

    // ==================== toDataListInfo() 转换测试 ====================

    @Test
    @DisplayName("toDataListInfo() 转换为 DataListInfo")
    void testToDataListInfo() {
        // Given
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        PageHelperPageResponse<Integer> response = PageHelperPageResponse.of(
                50L, 10, 2, 5, list
        );

        // When
        DataListInfo dataListInfo = response.toDataListInfo();

        // Then
        assertNotNull(dataListInfo);
        assertEquals(50, dataListInfo.getTotal());
        assertEquals(5, dataListInfo.getTotalPage());
        assertEquals(5, dataListInfo.getList().size());
        assertEquals(1, dataListInfo.getList().get(0));
    }

    @Test
    @DisplayName("toDataListInfo() 空数据转换")
    void testToDataListInfo_Empty() {
        // Given
        PageHelperPageResponse<String> response = PageHelperPageResponse.of(
                0L, 10, 1, 0, Collections.emptyList()
        );

        // When
        DataListInfo dataListInfo = response.toDataListInfo();

        // Then
        assertEquals(0, dataListInfo.getTotal());
        assertEquals(0, dataListInfo.getTotalPage());
        assertTrue(dataListInfo.getList().isEmpty());
    }

    // ==================== Builder 测试 ====================

    @Test
    @DisplayName("Builder 构建分页响应")
    void testBuilder() {
        // When
        PageHelperPageResponse<String> response = PageHelperPageResponse.<String>builder()
                .total(200L)
                .pageSize(20)
                .pageNum(3)
                .pages(10)
                .list(List.of("a", "b"))
                .build();

        // Then
        assertEquals(200L, response.getTotal());
        assertEquals(20, response.getPageSize());
        assertEquals(3, response.getPageNum());
        assertEquals(10, response.getPages());
        assertEquals(2, response.getList().size());
    }

    @Test
    @DisplayName("NoArgsConstructor 创建空响应")
    void testNoArgsConstructor() {
        // When
        PageHelperPageResponse<String> response = new PageHelperPageResponse<>();

        // Then
        assertEquals(0L, response.getTotal());
        assertEquals(0, response.getPageSize());
        assertEquals(0, response.getPageNum());
        assertEquals(0, response.getPages());
        assertNull(response.getList());
    }

    // ==================== Setter/Getter 链式调用测试 ====================

    @Test
    @DisplayName("Setter 测试")
    void testSetter() {
        // When
        PageHelperPageResponse<String> response = new PageHelperPageResponse<>();
        response.setTotal(100L);
        response.setPageSize(10);
        response.setPageNum(1);
        response.setPages(10);
        response.setList(List.of("data"));

        // Then
        assertEquals(100L, response.getTotal());
        assertEquals(10, response.getPageSize());
        assertEquals(1, response.getPageNum());
        assertEquals(10, response.getPages());
        assertEquals(1, response.getList().size());
    }
}
