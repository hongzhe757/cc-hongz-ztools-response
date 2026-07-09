package cc.hongz.ztools.response.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页数据列表信息.
 * <p>
 * 用于封装分页查询结果，包含总记录数、总页数和数据列表。
 * </p>
 *
 * @author hongzhe.qin
 */
@Data
@NoArgsConstructor
public class DataListInfo {

    /**
     * 总记录数.
     */
    private int total;

    /**
     * 总页数.
     */
    private int totalPage;

    /**
     * 数据内容.
     */
    private List<?> list;

    /**
     * 构造分页数据列表.
     *
     * @param totals     数据总数
     * @param totalPages 总页数
     * @param lists      数据列表
     */
    public DataListInfo(final int totals,
                        final int totalPages,
                        final List<?> lists) {
        this.total = totals;
        this.totalPage = totalPages;
        this.list = lists;
    }

    /**
     * 构造仅含数据列表的对象.
     *
     * @param lists 数据列表
     */
    public DataListInfo(final List<?> lists) {
        this.list = lists;
    }

    /**
     * 创建空的分页数据对象.
     *
     * @return 空的 DataListInfo
     */
    public static DataListInfo empty() {
        return new DataListInfo(0, 0, List.of());
    }

    /**
     * 创建仅含数据的对象（不分页）.
     *
     * @param list 数据列表
     * @return DataListInfo
     */
    public static DataListInfo of(final List<?> list) {
        return new DataListInfo(list);
    }

    /**
     * 创建完整的分页数据对象.
     *
     * @param total      总记录数
     * @param totalPage  总页数
     * @param list       数据列表
     * @return DataListInfo
     */
    public static DataListInfo of(final int total, final int totalPage, final List<?> list) {
        return new DataListInfo(total, totalPage, list);
    }
}
