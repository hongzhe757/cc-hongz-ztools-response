package cc.hongz.ztools.response.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * common 包其他类的单元测试.
 * <p>
 * 测试 CursorPageData、KeysetPageData、DataListInfo。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("Common 包其他类测试")
class CommonOtherClassesTest {

    // ==================== DataListInfo 测试 ====================

    @Test
    @DisplayName("DataListInfo.of(total, totalPage, list) - 创建完整分页数据")
    void testDataListInfoOfFull() {
        List<String> list = Arrays.asList("a", "b", "c");
        DataListInfo info = DataListInfo.of(100, 10, list);

        assertEquals(100, info.getTotal());
        assertEquals(10, info.getTotalPage());
        assertEquals(3, info.getList().size());
        assertEquals("a", info.getList().get(0));
    }

    @Test
    @DisplayName("DataListInfo.of(list) - 创建仅含数据的对象")
    void testDataListInfoOfListOnly() {
        List<String> list = Arrays.asList("x", "y");
        DataListInfo info = DataListInfo.of(list);

        assertEquals(0, info.getTotal());
        assertEquals(0, info.getTotalPage());
        assertEquals(2, info.getList().size());
    }

    @Test
    @DisplayName("DataListInfo.empty() - 创建空对象")
    void testDataListInfoEmpty() {
        DataListInfo info = DataListInfo.empty();

        assertEquals(0, info.getTotal());
        assertEquals(0, info.getTotalPage());
        assertNotNull(info.getList());
        assertTrue(info.getList().isEmpty());
    }

    @Test
    @DisplayName("DataListInfo 构造函数")
    void testDataListInfoConstructors() {
        // 三参数构造
        DataListInfo info1 = new DataListInfo(50, 5, List.of("a"));
        assertEquals(50, info1.getTotal());
        assertEquals(5, info1.getTotalPage());

        // 单参数构造
        DataListInfo info2 = new DataListInfo(List.of("b"));
        assertEquals(0, info2.getTotal());
        assertEquals(1, info2.getList().size());

        // 无参构造
        DataListInfo info3 = new DataListInfo();
        assertEquals(0, info3.getTotal());
        assertNull(info3.getList());
    }

    @Test
    @DisplayName("DataListInfo Getter/Setter")
    void testDataListInfoGettersSetters() {
        DataListInfo info = new DataListInfo();
        info.setTotal(200);
        info.setTotalPage(20);
        info.setList(List.of("test"));

        assertEquals(200, info.getTotal());
        assertEquals(20, info.getTotalPage());
        assertEquals(1, info.getList().size());
    }

    // ==================== CursorPageData 测试 ====================

    @Test
    @DisplayName("CursorPageData.of(records, nextCursor, hasMore, size) - 完整参数")
    void testCursorPageDataOfFull() {
        List<String> records = Arrays.asList("item1", "item2");
        String nextCursor = "cursor_123";
        boolean hasMore = true;
        int size = 10;

        CursorPageData<String> page = CursorPageData.of(records, nextCursor, hasMore, size);

        assertEquals(2, page.getRecords().size());
        assertEquals(nextCursor, page.getNextCursor());
        assertTrue(page.isHasMore());
        assertEquals(size, page.getSize());
    }

    @Test
    @DisplayName("CursorPageData.of(records, nextCursor, size) - 自动推断 hasMore")
    void testCursorPageDataOfAutoInfer() {
        List<String> records = List.of("item1");

        // 有游标，hasMore 应为 true
        CursorPageData<String> page1 = CursorPageData.of(records, "cursor_abc", 10);
        assertTrue(page1.isHasMore());

        // 无游标，hasMore 应为 false
        CursorPageData<String> page2 = CursorPageData.of(records, null, 10);
        assertFalse(page2.isHasMore());

        // 空字符串游标，hasMore 应为 false
        CursorPageData<String> page3 = CursorPageData.of(records, "", 10);
        assertFalse(page3.isHasMore());
    }

    @Test
    @DisplayName("CursorPageData.empty() - 创建空对象")
    void testCursorPageDataEmpty() {
        CursorPageData<Object> page = CursorPageData.empty();

        assertNotNull(page.getRecords());
        assertTrue(page.getRecords().isEmpty());
        assertNull(page.getNextCursor());
        assertFalse(page.isHasMore());
        assertEquals(10, page.getSize());
    }

    @Test
    @DisplayName("CursorPageData.hasNext() - 判断是否有下一页")
    void testCursorPageDataHasNext() {
        // hasMore=true 且 nextCursor 不为 null
        CursorPageData<String> page1 = CursorPageData.of(List.of("a"), "cursor", true, 10);
        assertTrue(page1.hasNext());

        // hasMore=true 但 nextCursor 为 null
        CursorPageData<String> page2 = CursorPageData.of(List.of("a"), null, true, 10);
        assertFalse(page2.hasNext());

        // hasMore=false
        CursorPageData<String> page3 = CursorPageData.of(List.of("a"), "cursor", false, 10);
        assertFalse(page3.hasNext());
    }

    @Test
    @DisplayName("CursorPageData.getCount() - 获取记录数")
    void testCursorPageDataGetCount() {
        CursorPageData<String> page1 = CursorPageData.of(Arrays.asList("a", "b", "c"), null, false, 10);
        assertEquals(3, page1.getCount());

        CursorPageData<String> page2 = CursorPageData.of(List.of(), null, false, 10);
        assertEquals(0, page2.getCount());

        // records 为 null
        CursorPageData<String> page3 = new CursorPageData<>();
        assertEquals(0, page3.getCount());
    }

    @Test
    @DisplayName("CursorPageData Getter/Setter")
    void testCursorPageDataGettersSetters() {
        CursorPageData<String> page = new CursorPageData<>();
        page.setRecords(List.of("test"));
        page.setNextCursor("cursor_xyz");
        page.setHasMore(true);
        page.setSize(20);

        assertEquals(1, page.getRecords().size());
        assertEquals("cursor_xyz", page.getNextCursor());
        assertTrue(page.isHasMore());
        assertEquals(20, page.getSize());
    }

    // ==================== KeysetPageData 测试 ====================

    @Test
    @DisplayName("KeysetPageData.of(records, nextKey, hasMore, size) - 完整参数")
    void testKeysetPageDataOfFull() {
        List<String> records = Arrays.asList("order1", "order2");
        String nextKey = "key_456";
        boolean hasMore = true;
        int size = 20;

        KeysetPageData<String> page = KeysetPageData.of(records, nextKey, hasMore, size);

        assertEquals(2, page.getRecords().size());
        assertEquals(nextKey, page.getNextKey());
        assertTrue(page.isHasMore());
        assertEquals(size, page.getSize());
    }

    @Test
    @DisplayName("KeysetPageData.of(records, nextKey, size) - 自动推断 hasMore")
    void testKeysetPageDataOfAutoInfer() {
        List<String> records = List.of("order1");

        // 有键值，hasMore 应为 true
        KeysetPageData<String> page1 = KeysetPageData.of(records, "key_789", 20);
        assertTrue(page1.isHasMore());

        // 无键值，hasMore 应为 false
        KeysetPageData<String> page2 = KeysetPageData.of(records, null, 20);
        assertFalse(page2.isHasMore());

        // 空字符串键值，hasMore 应为 false
        KeysetPageData<String> page3 = KeysetPageData.of(records, "", 20);
        assertFalse(page3.isHasMore());
    }

    @Test
    @DisplayName("KeysetPageData.empty() - 创建空对象")
    void testKeysetPageDataEmpty() {
        KeysetPageData<Object> page = KeysetPageData.empty();

        assertNotNull(page.getRecords());
        assertTrue(page.getRecords().isEmpty());
        assertNull(page.getNextKey());
        assertFalse(page.isHasMore());
        assertEquals(10, page.getSize());
    }

    @Test
    @DisplayName("KeysetPageData.hasNext() - 判断是否有下一页")
    void testKeysetPageDataHasNext() {
        // hasMore=true 且 nextKey 不为 null
        KeysetPageData<String> page1 = KeysetPageData.of(List.of("a"), "key", true, 10);
        assertTrue(page1.hasNext());

        // hasMore=true 但 nextKey 为 null
        KeysetPageData<String> page2 = KeysetPageData.of(List.of("a"), null, true, 10);
        assertFalse(page2.hasNext());

        // hasMore=false
        KeysetPageData<String> page3 = KeysetPageData.of(List.of("a"), "key", false, 10);
        assertFalse(page3.hasNext());
    }

    @Test
    @DisplayName("KeysetPageData.getCount() - 获取记录数")
    void testKeysetPageDataGetCount() {
        KeysetPageData<String> page1 = KeysetPageData.of(Arrays.asList("a", "b"), null, false, 10);
        assertEquals(2, page1.getCount());

        KeysetPageData<String> page2 = KeysetPageData.of(List.of(), null, false, 10);
        assertEquals(0, page2.getCount());

        // records 为 null
        KeysetPageData<String> page3 = new KeysetPageData<>();
        assertEquals(0, page3.getCount());
    }

    @Test
    @DisplayName("KeysetPageData Getter/Setter")
    void testKeysetPageDataGettersSetters() {
        KeysetPageData<String> page = new KeysetPageData<>();
        page.setRecords(List.of("test"));
        page.setNextKey("key_abc");
        page.setHasMore(true);
        page.setSize(15);

        assertEquals(1, page.getRecords().size());
        assertEquals("key_abc", page.getNextKey());
        assertTrue(page.isHasMore());
        assertEquals(15, page.getSize());
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("CursorPageData 与 KeysetPageData 对比")
    void testCursorVsKeyset() {
        // 两者结构相似，但用途不同
        List<String> data = List.of("item1", "item2");

        CursorPageData<String> cursorPage = CursorPageData.of(data, "timestamp_cursor", true, 10);
        KeysetPageData<String> keysetPage = KeysetPageData.of(data, "id_key", true, 10);

        assertEquals(cursorPage.getCount(), keysetPage.getCount());
        assertEquals(cursorPage.isHasMore(), keysetPage.isHasMore());
        assertEquals(cursorPage.getSize(), keysetPage.getSize());

        // 但游标和键值字段名不同
        assertEquals("timestamp_cursor", cursorPage.getNextCursor());
        assertEquals("id_key", keysetPage.getNextKey());
    }

    @Test
    @DisplayName("大数据量分页")
    void testLargeDataset() {
        List<Integer> largeList = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add(i);
        }

        CursorPageData<Integer> cursorPage = CursorPageData.of(largeList, "next", true, 1000);
        assertEquals(1000, cursorPage.getCount());
        assertTrue(cursorPage.hasNext());

        KeysetPageData<Integer> keysetPage = KeysetPageData.of(largeList, "last_id", true, 1000);
        assertEquals(1000, keysetPage.getCount());
        assertTrue(keysetPage.hasNext());
    }
}
