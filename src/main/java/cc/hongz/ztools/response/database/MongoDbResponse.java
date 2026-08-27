package cc.hongz.ztools.response.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MongoDB 响应.
 * <p>
 * MongoDB 操作结果的标准格式。
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
public class MongoDbResponse<T> {

    /**
     * 操作是否成功.
     */
    private boolean acknowledged;

    /**
     * 匹配的记录数.
     */
    private long matchedCount;

    /**
     * 修改的记录数.
     */
    private long modifiedCount;

    /**
     * 删除的记录数.
     */
    private long deletedCount;

    /**
     * 插入的 ID.
     */
    private Object insertedId;

    /**
     * 查询结果列表.
     */
    private List<T> results;

    /**
     * 总记录数（分页时）.
     */
    private Long totalCount;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建插入响应.
     *
     * @param insertedId 插入的 ID
     * @param <T>        数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> insert(final Object insertedId) {
        return MongoDbResponse.<T>builder()
                .acknowledged(true)
                .insertedId(insertedId)
                .build();
    }

    /**
     * 创建更新响应.
     *
     * @param matchedCount  匹配数
     * @param modifiedCount 修改数
     * @param <T>           数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> update(final long matchedCount, final long modifiedCount) {
        return MongoDbResponse.<T>builder()
                .acknowledged(true)
                .matchedCount(matchedCount)
                .modifiedCount(modifiedCount)
                .build();
    }

    /**
     * 创建删除响应.
     *
     * @param deletedCount 删除数
     * @param <T>          数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> delete(final long deletedCount) {
        return MongoDbResponse.<T>builder()
                .acknowledged(true)
                .deletedCount(deletedCount)
                .build();
    }

    /**
     * 创建查询响应.
     *
     * @param results    结果列表
     * @param totalCount 总数
     * @param <T>        数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> find(final List<T> results, final Long totalCount) {
        return MongoDbResponse.<T>builder()
                .acknowledged(true)
                .results(results)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 创建未确认的响应.
     *
     * @param <T> 数据类型
     * @return MongoDbResponse
     */
    public static <T> MongoDbResponse<T> unacknowledged() {
        return MongoDbResponse.<T>builder()
                .acknowledged(false)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断操作是否被确认.
     *
     * @return 确认返回 true
     */
    public boolean isAcknowledged() {
        return acknowledged;
    }

    /**
     * 判断是否有结果.
     *
     * @return 有返回 true
     */
    public boolean hasResults() {
        return results != null && !results.isEmpty();
    }
}
