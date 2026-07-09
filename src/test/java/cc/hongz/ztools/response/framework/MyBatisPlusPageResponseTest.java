package cc.hongz.ztools.response.framework;

import cc.hongz.ztools.response.common.DataListInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyBatisPlusPageResponse 单元测试.
 * <p>
 * 测试 of() 工厂方法和 toDataListInfo() 转换方法。
 * from(IPage) 方法因依赖 MyBatis-Plus（optional）不在测试范围。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("MyBatisPlusPageResponse 测试")
class MyBatisPlusPageResponseTest {

    // ==================== of() 工厂方法测试 ====================

    @Test
    @DisplayName("of() 创建分页响应")
    void testOf() {
        // Given
        List<String> records = Arrays.asList("item1", "item2", "item3");

        // When
        MyBatisPlusPageResponse<String> response = MyBatisPlusPageResponse.of(
                100L, 10L, 1L, 10L, records
        );

        // Then
        assertNotNull(response);
        assertEquals(100L, response.getTotal());
        assertEquals(10L, response.getSize());
        assertEquals(1L, response.getCurrent());
        assertEquals(10L, response.getPages());
        assertEquals(3, response.getRecords().size());
        assertEquals("item1", response.getRecords().get(0));
    }

    @Test
    @DisplayName("of() 空记录列表")
    void testOf_EmptyRecords() {
        // When
        MyBatisPlusPageResponse<String> response = MyBatisPlusPageResponse.of(
                0L, 10L, 1L, 0L, Collections.emptyList()
        );

        // Then
        assertEquals(0L, response.getTotal());
        assertTrue(response.getRecords().isEmpty());
    }

    @Test
    @DisplayName("of() null 记录列表")
    void testOf_NullRecords() {
        // When
        MyBatisPlusPageResponse<String> response = MyBatisPlusPageResponse.of(
                0L, 10L, 1L, 0L, null
        );

        // Then
        assertNull(response.getRecords());
    }

    // ==================== toDataListInfo() 转换测试 ====================

    @Test
    @DisplayName("toDataListInfo() 转换为 DataListInfo")
    void testToDataListInfo() {
        // Given
        List<Integer> records = Arrays.asList(1, 2, 3, 4, 5);
        MyBatisPlusPageResponse<Integer> response = MyBatisPlusPageResponse.of(
                50L, 10L, 2L, 5L, records
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
        MyBatisPlusPageResponse<String> response = MyBatisPlusPageResponse.of(
                0L, 10L, 1L, 0L, Collections.emptyList()
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
        MyBatisPlusPageResponse<String> response = MyBatisPlusPageResponse.<String>builder()
                .total(200L)
                .size(20L)
                .current(3L)
                .pages(10L)
                .records(List.of("a", "b"))
                .build();

        // Then
        assertEquals(200L, response.getTotal());
        assertEquals(20L, response.getSize());
        assertEquals(3L, response.getCurrent());
        assertEquals(10L, response.getPages());
        assertEquals(2, response.getRecords().size());
    }

    @Test
    @DisplayName("NoArgsConstructor 创建空响应")
    void testNoArgsConstructor() {
        // When
        MyBatisPlusPageResponse<String> response = new MyBatisPlusPageResponse<>();

        // Then
        assertEquals(0L, response.getTotal());
        assertEquals(0L, response.getSize());
        assertEquals(0L, response.getCurrent());
        assertEquals(0L, response.getPages());
        assertNull(response.getRecords());
    }

    // ==================== Setter/Getter 链式调用测试 ====================

    @Test
    @DisplayName("Setter 测试")
    void testSetter() {
        // When
        MyBatisPlusPageResponse<String> response = new MyBatisPlusPageResponse<>();
        response.setTotal(100L);
        response.setSize(10L);
        response.setCurrent(1L);
        response.setPages(10L);
        response.setRecords(List.of("data"));

        // Then
        assertEquals(100L, response.getTotal());
        assertEquals(10L, response.getSize());
        assertEquals(1L, response.getCurrent());
        assertEquals(10L, response.getPages());
        assertEquals(1, response.getRecords().size());
    }
}
