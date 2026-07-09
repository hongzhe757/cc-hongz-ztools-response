package cc.hongz.ztools.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 键集分页数据包装器.
 * <p>
 * 基于唯一键的分页方式，性能最优，完全避免深分页问题。
 * 适用于大数据集、订单列表、交易流水等场景。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 单键键集（ID）
 * KeysetPageData<Order> keysetPage = KeysetPageData.of(orders, lastOrderId, hasMore);
 * AjaxResult<KeysetPageData<Order>> response = AjaxResult.success(keysetPage);
 *
 * // 2. 复合键集（category + id）
 * String compositeKey = "electronics_1000";
 * KeysetPageData<Product> keysetPage = KeysetPageData.of(products, compositeKey, hasMore);
 * Result<KeysetPageData<Product>> result = Result.success(keysetPage);
 *
 * // 3. 从数据库查询结果构建
 * List<User> users = userMapper.selectByKeyset(lastKey, limit);
 * String nextKey = users.isEmpty() ? null : users.get(users.size() - 1).getId();
 * KeysetPageData<User> keysetPage = KeysetPageData.of(users, nextKey, users.size() == limit);
 * }</pre>
 *
 * @param <T> 数据类型
 * @author hongz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeysetPageData<T> {

    /**
     * 数据列表.
     */
    private List<T> records;

    /**
     * 下一页的起始键值.
     * <p>
     * 通常是上一页最后一条记录的唯一键值（如 ID）。
     * 用于下一次查询的 WHERE 条件：WHERE id > lastKey
     * </p>
     */
    private String nextKey;

    /**
     * 是否有更多数据.
     */
    private boolean hasMore;

    /**
     * 每页大小.
     */
    private int size;

    /**
     * 创建键集分页数据.
     *
     * @param records 数据列表
     * @param nextKey 下一页起始键值
     * @param hasMore 是否有更多数据
     * @param size    每页大小
     * @param <T>     数据类型
     * @return KeysetPageData
     */
    public static <T> KeysetPageData<T> of(final List<T> records, final String nextKey, 
                                           final boolean hasMore, final int size) {
        return new KeysetPageData<>(records, nextKey, hasMore, size);
    }

    /**
     * 创建键集分页数据（自动推断 hasMore）.
     *
     * @param records 数据列表
     * @param nextKey 下一页起始键值
     * @param size    每页大小
     * @param <T>     数据类型
     * @return KeysetPageData
     */
    public static <T> KeysetPageData<T> of(final List<T> records, final String nextKey, final int size) {
        boolean hasMore = nextKey != null && !nextKey.isEmpty();
        return new KeysetPageData<>(records, nextKey, hasMore, size);
    }

    /**
     * 创建空的键集分页数据.
     *
     * @param <T> 数据类型
     * @return KeysetPageData
     */
    public static <T> KeysetPageData<T> empty() {
        return new KeysetPageData<>(List.of(), null, false, 10);
    }

    /**
     * 判断是否有下一页.
     *
     * @return 有下一页返回 true
     */
    public boolean hasNext() {
        return hasMore && nextKey != null;
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
