package cc.hongz.ztools.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 游标分页数据包装器.
 * <p>
 * 基于游标的分页方式，适用于无限滚动、Feed 流等场景。
 * 性能优于传统的 Offset/Limit 分页，特别是在大数据集和深度分页时。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 与 AjaxResult 组合（时间戳游标）
 * CursorPageData<Post> cursorPage = CursorPageData.of(posts, nextCursor, hasMore);
 * AjaxResult<CursorPageData<Post>> response = AjaxResult.success(cursorPage);
 *
 * // 2. ID 游标
 * CursorPageData<Order> cursorPage = CursorPageData.of(orders, lastOrderId, hasMore);
 * Result<CursorPageData<Order>> result = Result.success(cursorPage);
 *
 * // 3. 复合游标（时间戳 + ID）
 * String compositeCursor = lastTimestamp + "_" + lastId;
 * CursorPageData<Comment> cursorPage = CursorPageData.of(comments, compositeCursor, hasMore);
 * }</pre>
 *
 * @param <T> 数据类型
 * @author hongz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageData<T> {

    /**
     * 数据列表.
     */
    private List<T> records;

    /**
     * 下一页游标.
     * <p>
     * 可以是：
     * - 时间戳字符串（如 "2024-01-15T10:30:00"）
     * - ID 值（如 "1000"）
     * - Base64 编码的复合键
     * - null 表示没有更多数据
     * </p>
     */
    private String nextCursor;

    /**
     * 是否有更多数据.
     */
    private boolean hasMore;

    /**
     * 每页大小.
     */
    private int size;

    /**
     * 创建游标分页数据.
     *
     * @param records    数据列表
     * @param nextCursor 下一页游标
     * @param hasMore    是否有更多数据
     * @param size       每页大小
     * @param <T>        数据类型
     * @return CursorPageData
     */
    public static <T> CursorPageData<T> of(final List<T> records, final String nextCursor, 
                                           final boolean hasMore, final int size) {
        return new CursorPageData<>(records, nextCursor, hasMore, size);
    }

    /**
     * 创建游标分页数据（自动推断 hasMore）.
     *
     * @param records    数据列表
     * @param nextCursor 下一页游标
     * @param size       每页大小
     * @param <T>        数据类型
     * @return CursorPageData
     */
    public static <T> CursorPageData<T> of(final List<T> records, final String nextCursor, final int size) {
        boolean hasMore = nextCursor != null && !nextCursor.isEmpty();
        return new CursorPageData<>(records, nextCursor, hasMore, size);
    }

    /**
     * 创建空的游标分页数据.
     *
     * @param <T> 数据类型
     * @return CursorPageData
     */
    public static <T> CursorPageData<T> empty() {
        return new CursorPageData<>(List.of(), null, false, 10);
    }

    /**
     * 判断是否有下一页.
     *
     * @return 有下一页返回 true
     */
    public boolean hasNext() {
        return hasMore && nextCursor != null;
    }

    /**
     * 获取当前页的记录数.
     *
     * @return 记录数
     */
    public int getCount() {
        return records == null ? 0 : records.size();
    }
}
