package cc.hongz.ztools.response.framework;

import cc.hongz.ztools.response.common.DataListInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MyBatis-Plus 分页响应适配器.
 * <p>
 * 将 MyBatis-Plus {@link com.baomidou.mybatisplus.core.metadata.IPage} 转换为 {@link DataListInfo}。
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
public class MyBatisPlusPageResponse<T> {

    /**
     * 总记录数.
     */
    private long total;

    /**
     * 每页大小.
     */
    private long size;

    /**
     * 当前页码.
     */
    private long current;

    /**
     * 总页数.
     */
    private long pages;

    /**
     * 数据列表.
     */
    private List<T> records;

    // ==================== 静态工厂方法 ====================

    /**
     * 从 MyBatis-Plus IPage 创建.
     *
     * @param page MyBatis-Plus IPage
     * @param <T>  数据类型
     * @return MyBatisPlusPageResponse
     */
    public static <T> MyBatisPlusPageResponse<T> from(final com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return MyBatisPlusPageResponse.<T>builder()
                .total(page.getTotal())
                .size(page.getSize())
                .current(page.getCurrent())
                .pages(page.getPages())
                .records(page.getRecords())
                .build();
    }

    /**
     * 转换为 DataListInfo.
     *
     * @return DataListInfo
     */
    public DataListInfo toDataListInfo() {
        return DataListInfo.of((int) total, (int) pages, records);
    }

    /**
     * 创建自定义的分页响应.
     *
     * @param total   总记录数
     * @param size    每页大小
     * @param current 当前页码
     * @param pages   总页数
     * @param records 数据列表
     * @param <T>     数据类型
     * @return MyBatisPlusPageResponse
     */
    public static <T> MyBatisPlusPageResponse<T> of(final long total, final long size, final long current, final long pages, final List<T> records) {
        return MyBatisPlusPageResponse.<T>builder()
                .total(total)
                .size(size)
                .current(current)
                .pages(pages)
                .records(records)
                .build();
    }
}
