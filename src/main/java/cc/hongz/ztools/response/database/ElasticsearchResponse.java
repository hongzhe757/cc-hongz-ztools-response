package cc.hongz.ztools.response.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch 响应.
 * <p>
 * Elasticsearch 搜索结果的标准格式。
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
public class ElasticsearchResponse<T> {

    /**
     * 总命中数.
     */
    private long totalHits;

    /**
     * 最大得分.
     */
    private Float maxScore;

    /**
     * 命中结果列表.
     */
    private List<Hit<T>> hits;

    /**
     * 聚合结果.
     */
    private Map<String, Object> aggregations;

    /**
     * 是否超时.
     */
    private boolean timedOut;

    /**
     * 耗时（毫秒）.
     */
    private long took;

    /**
     * 命中结果.
     *
     * @param <T> 数据类型
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hit<T> {
        /**
         * 索引名.
         */
        private String index;

        /**
         * 文档 ID.
         */
        private String id;

        /**
         * 得分.
         */
        private Float score;

        /**
         * 源数据.
         */
        private T source;

        /**
         * 高亮字段.
         */
        private Map<String, List<String>> highlight;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建搜索响应.
     *
     * @param totalHits 总命中数
     * @param maxScore  最大得分
     * @param hits      命中列表
     * @param took      耗时
     * @param <T>       数据类型
     * @return ElasticsearchResponse
     */
    public static <T> ElasticsearchResponse<T> search(
            final long totalHits,
            final Float maxScore,
            final List<Hit<T>> hits,
            final long took
        ) {
        return ElasticsearchResponse.<T>builder()
                .totalHits(totalHits)
                .maxScore(maxScore)
                .hits(hits)
                .took(took)
                .timedOut(false)
                .build();
    }

    /**
     * 创建带聚合的响应.
     *
     * @param totalHits    总命中数
     * @param maxScore     最大得分
     * @param hits         命中列表
     * @param aggregations 聚合结果
     * @param took         耗时
     * @param <T>          数据类型
     * @return ElasticsearchResponse
     */
    public static <T> ElasticsearchResponse<T> withAggregations(
            final long totalHits,
            final Float maxScore,
            final List<Hit<T>> hits,
            final Map<String,
            Object> aggregations,
            final long took
        ) {
        return ElasticsearchResponse.<T>builder()
                .totalHits(totalHits)
                .maxScore(maxScore)
                .hits(hits)
                .aggregations(aggregations)
                .took(took)
                .timedOut(false)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否有命中结果.
     *
     * @return 有返回 true
     */
    public boolean hasHits() {
        return hits != null && !hits.isEmpty();
    }

    /**
     * 判断是否有聚合结果.
     *
     * @return 有返回 true
     */
    public boolean hasAggregations() {
        return aggregations != null && !aggregations.isEmpty();
    }
}
