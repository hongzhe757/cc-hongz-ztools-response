package cc.hongz.ztools.response.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@link PageData} 分页数据单元测试.
 * <p>
 * 测试分页数据的创建、计算和判断方法。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("PageData 分页数据测试")
class PageDataTest {

    @Test
    @DisplayName("of - 创建分页数据")
    void testOf() {
        List<String> records = Arrays.asList("item1", "item2", "item3");
        
        PageData<String> pageData = PageData.of(records, 100L, 1, 10);
        
        assertEquals(3, pageData.getRecords().size());
        assertEquals(100L, pageData.getTotal());
        assertEquals(1, pageData.getCurrent());
        assertEquals(10, pageData.getSize());
        assertEquals(10, pageData.getPages()); // 100/10 = 10页
    }

    @Test
    @DisplayName("of - 总页数向上取整")
    void testOfPagesCeiling() {
        List<Integer> records = Arrays.asList(1, 2, 3, 4, 5);
        
        PageData<Integer> pageData = PageData.of(records, 23L, 1, 10);
        
        assertEquals(3, pageData.getPages()); // 23/10 = 2.3 → 3页
    }

    @Test
    @DisplayName("empty - 创建空分页数据")
    void testEmpty() {
        PageData<Object> pageData = PageData.empty();
        
        assertNotNull(pageData.getRecords());
        assertTrue(pageData.getRecords().isEmpty());
        assertEquals(0L, pageData.getTotal());
        assertEquals(1, pageData.getCurrent());
        assertEquals(10, pageData.getSize());
        assertEquals(0, pageData.getPages());
    }

    @Test
    @DisplayName("hasNext - 有下一页判断")
    void testHasNext() {
        PageData<String> firstPage = PageData.of(List.of("a"), 30L, 1, 10);
        assertTrue(firstPage.hasNext()); // 第1页，共3页
        
        PageData<String> middlePage = PageData.of(List.of("b"), 30L, 2, 10);
        assertTrue(middlePage.hasNext()); // 第2页，共3页
        
        PageData<String> lastPage = PageData.of(List.of("c"), 30L, 3, 10);
        assertFalse(lastPage.hasNext()); // 第3页，共3页
    }

    @Test
    @DisplayName("hasPrevious - 有上一页判断")
    void testHasPrevious() {
        PageData<String> firstPage = PageData.of(List.of("a"), 30L, 1, 10);
        assertFalse(firstPage.hasPrevious()); // 第1页没有上一页
        
        PageData<String> secondPage = PageData.of(List.of("b"), 30L, 2, 10);
        assertTrue(secondPage.hasPrevious()); // 第2页有上一页
        
        PageData<String> thirdPage = PageData.of(List.of("c"), 30L, 3, 10);
        assertTrue(thirdPage.hasPrevious()); // 第3页有上一页
    }

    @Test
    @DisplayName("isFirst - 是否为第一页")
    void testIsFirst() {
        PageData<String> firstPage = PageData.of(List.of("a"), 30L, 1, 10);
        assertTrue(firstPage.isFirst());
        
        PageData<String> otherPage = PageData.of(List.of("b"), 30L, 2, 10);
        assertFalse(otherPage.isFirst());
    }

    @Test
    @DisplayName("isLast - 是否为最后一页")
    void testIsLast() {
        PageData<String> lastPage = PageData.of(List.of("c"), 30L, 3, 10);
        assertTrue(lastPage.isLast());
        
        PageData<String> firstPage = PageData.of(List.of("a"), 30L, 1, 10);
        assertFalse(firstPage.isLast());
    }

    @Test
    @DisplayName("toDataListInfo - 转换为 DataListInfo")
    void testToDataListInfo() {
        List<String> records = Arrays.asList("x", "y", "z");
        PageData<String> pageData = PageData.of(records, 50L, 2, 10);
        
        DataListInfo info = pageData.toDataListInfo();
        
        assertNotNull(info);
        assertEquals(50, info.getTotal());
        assertEquals(5, info.getTotalPage());
        assertEquals(3, info.getList().size());
    }

    @Test
    @DisplayName("边界情况 - 单条记录")
    void testSingleRecord() {
        PageData<String> pageData = PageData.of(List.of("single"), 1L, 1, 10);
        
        assertEquals(1, pageData.getRecords().size());
        assertEquals(1L, pageData.getTotal());
        assertEquals(1, pageData.getPages());
        assertTrue(pageData.isFirst());
        assertTrue(pageData.isLast());
        assertFalse(pageData.hasNext());
        assertFalse(pageData.hasPrevious());
    }

    @Test
    @DisplayName("边界情况 - 零记录")
    void testZeroRecords() {
        PageData<Object> pageData = PageData.of(List.of(), 0L, 1, 10);
        
        assertTrue(pageData.getRecords().isEmpty());
        assertEquals(0L, pageData.getTotal());
        assertEquals(0, pageData.getPages());
        assertTrue(pageData.isFirst());
        assertTrue(pageData.isLast());
    }

    @Test
    @DisplayName("大数据量分页")
    void testLargeDataset() {
        PageData<Integer> pageData = PageData.of(List.of(1), 1000000L, 50000, 20);
        
        assertEquals(1000000L, pageData.getTotal());
        assertEquals(50000, pageData.getCurrent());
        assertEquals(20, pageData.getSize());
        assertEquals(50000, pageData.getPages()); // 1000000/20 = 50000
        assertFalse(pageData.hasNext());
        assertTrue(pageData.hasPrevious());
    }
}
