package cc.hongz.ztools.response.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Redis 响应.
 * <p>
 * Redis 操作结果的标准格式。
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
public class RedisResponse<T> {

    /**
     * 键.
     */
    private String key;

    /**
     * 值.
     */
    private T value;

    /**
     * 过期时间（秒，-1 表示永不过期）.
     */
    private Long ttl;

    /**
     * 操作是否成功.
     */
    private boolean success;

    /**
     * 消息.
     */
    private String message;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的 GET 响应.
     *
     * @param key   键
     * @param value 值
     * @param <T>   数据类型
     * @return RedisResponse
     */
    public static <T> RedisResponse<T> get(final String key, final T value) {
        return RedisResponse.<T>builder()
                .key(key)
                .value(value)
                .success(true)
                .build();
    }

    /**
     * 创建成功的 SET 响应.
     *
     * @param key 键
     * @param <T> 数据类型
     * @return RedisResponse
     */
    public static <T> RedisResponse<T> set(final String key) {
        return RedisResponse.<T>builder()
                .key(key)
                .success(true)
                .message("OK")
                .build();
    }

    /**
     * 创建带 TTL 的响应.
     *
     * @param key   键
     * @param value 值
     * @param ttl   过期时间（秒）
     * @param <T>   数据类型
     * @return RedisResponse
     */
    public static <T> RedisResponse<T> withTtl(final String key, final T value, final Long ttl) {
        return RedisResponse.<T>builder()
                .key(key)
                .value(value)
                .ttl(ttl)
                .success(true)
                .build();
    }

    /**
     * 创建失败响应.
     *
     * @param key     键
     * @param message 错误消息
     * @param <T>     数据类型
     * @return RedisResponse
     */
    public static <T> RedisResponse<T> error(final String key, final String message) {
        return RedisResponse.<T>builder()
                .key(key)
                .success(false)
                .message(message)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否存在.
     *
     * @return 存在返回 true
     */
    public boolean exists() {
        return value != null;
    }

    /**
     * 判断是否有 TTL.
     *
     * @return 有返回 true
     */
    public boolean hasTtl() {
        return ttl != null && ttl > 0;
    }
}
