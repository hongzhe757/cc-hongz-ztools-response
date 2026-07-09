package cc.hongz.ztools.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页数据包装器.
 * <p>
 * 提供标准的分页数据结构，可与任何响应类型组合使用。
 * 支持传统的 Offset/Limit 分页方式。
 * </p>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 与 AjaxResult 组合
 * PageData<User> pageData = PageData.of(users, 100, 1, 10);
 * AjaxResult<PageData<User>> response = AjaxResult.success(pageData);
 *
 * // 2. 与 Result 组合
 * Result<PageData<Order>> result = Result.success(PageData.of(orders, total, pageNum, pageSize));
 *
 * // 3. 从 MyBatis-Plus IPage 转换
 * IPage<User> iPage = userService.page(query);
 * PageData<User> pageData = PageData.from(iPage);
 * AjaxResult<PageData<User>> response = AjaxResult.success(pageData);
 * }</pre>
 *
 * @param <T> 数据类型
 * @author hongz
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageData<T> {

    /**
     * 数据列表.
     */
    private List<T> records;

    /**
     * 总记录数.
     */
    private long total;

    /**
     * 当前页码（从 1 开始）.
     */
    private int current;

    /**
     * 每页大小.
     */
    private int size;

    /**
     * 总页数.
     */
    private int pages;

    /**
     * 创建分页数据.
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页大小
     * @param <T>     数据类型
     * @return PageData
     */
    public static <T> PageData<T> of(final List<T> records, final long total, final int current, final int size) {
        int pages = (int) Math.ceil((double) total / size);
        return new PageData<>(records, total, current, size, pages);
    }

    /**
     * 创建空的分页数据.
     *
     * @param <T> 数据类型
     * @return PageData
     */
    public static <T> PageData<T> empty() {
        return new PageData<>(List.of(), 0L, 1, 10, 0);
    }

    /**
     * 从 MyBatis-Plus IPage 转换.
     *
     * @param page MyBatis-Plus IPage
     * @param <T>  数据类型
     * @return PageData
     */
    public static <T> PageData<T> from(final com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageData<>(
                page.getRecords(),
                page.getTotal(),
                (int) page.getCurrent(),
                (int) page.getSize(),
                (int) page.getPages()
        );
    }

    /**
     * 从 PageHelper PageInfo 转换.
     *
     * @param pageInfo PageHelper PageInfo
     * @param <T>      数据类型
     * @return PageData
     */
    public static <T> PageData<T> from(final com.github.pagehelper.PageInfo<T> pageInfo) {
        return new PageData<>(
                pageInfo.getList(),
                pageInfo.getTotal(),
                pageInfo.getPageNum(),
                pageInfo.getPageSize(),
                pageInfo.getPages()
        );
    }

    /**
     * 转换为 DataListInfo.
     *
     * @return DataListInfo
     */
    public DataListInfo toDataListInfo() {
        return DataListInfo.of((int) total, pages, records);
    }

    /**
     * 判断是否有下一页.
     *
     * @return 有下一页返回 true
     */
    public boolean hasNext() {
        return current < pages;
    }

    /**
     * 判断是否有上一页.
     *
     * @return 有上一页返回 true
     */
    public boolean hasPrevious() {
        return current > 1;
    }

    /**
     * 判断是否为第一页.
     *
     * @return 是第一页返回 true
     */
    public boolean isFirst() {
        return current == 1;
    }

    /**
     * 判断是否为最后一页.
     *
     * @return 是最后一页返回 true
     */
    public boolean isLast() {
        return current >= pages;
    }
}
