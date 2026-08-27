package cc.hongz.ztools.response.framework;

import cc.hongz.ztools.response.common.DataListInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PageHelper 分页响应适配器.
 * <p>
 * 将 PageHelper {@link com.github.pagehelper.PageInfo} 转换为 {@link DataListInfo}。
 * </p>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageHelperPageResponse<T> {

    /**
     * 总记录数.
     */
    private long total;

    /**
     * 每页大小.
     */
    private int pageSize;

    /**
     * 当前页码.
     */
    private int pageNum;

    /**
     * 总页数.
     */
    private int pages;

    /**
     * 数据列表.
     */
    private List<T> list;

    // ==================== 静态工厂方法 ====================

    /**
     * 从 PageHelper PageInfo 创建.
     *
     * @param pageInfo PageHelper PageInfo
     * @param <T>      数据类型
     * @return PageHelperPageResponse
     */
    public static <T> PageHelperPageResponse<T> from(final com.github.pagehelper.PageInfo<T> pageInfo) {
        return PageHelperPageResponse.<T>builder()
                .total(pageInfo.getTotal())
                .pageSize(pageInfo.getPageSize())
                .pageNum(pageInfo.getPageNum())
                .pages(pageInfo.getPages())
                .list(pageInfo.getList())
                .build();
    }

    /**
     * 转换为 DataListInfo.
     *
     * @return DataListInfo
     */
    public DataListInfo toDataListInfo() {
        return DataListInfo.of((int) total, pages, list);
    }

    /**
     * 创建自定义的分页响应.
     *
     * @param total    总记录数
     * @param pageSize 每页大小
     * @param pageNum  当前页码
     * @param pages    总页数
     * @param list     数据列表
     * @param <T>      数据类型
     * @return PageHelperPageResponse
     */
    public static <T> PageHelperPageResponse<T> of(
            final long total,
            final int pageSize,
            final int pageNum,
            final int pages,
            final List<T> list
        ) {
        return PageHelperPageResponse.<T>builder()
                .total(total)
                .pageSize(pageSize)
                .pageNum(pageNum)
                .pages(pages)
                .list(list)
                .build();
    }
}
