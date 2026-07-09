package cc.hongz.ztools.response.generic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Map 形式响应包装器.
 * <p>
 * 用于返回动态键值对数据的场景，常见于：
 * </p>
 * <ul>
 *   <li>动态字段响应（字段不固定）</li>
 *   <li>配置信息返回</li>
 *   <li>聚合多个数据源的结果</li>
 *   <li>NoSQL 数据库查询结果（如 MongoDB、Redis Hash）</li>
 *   <li>ETL 数据处理中间结果</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 创建简单的 Map 响应
 * Map<String, Object> data = new HashMap<>();
 * data.put("userId", 123L);
 * data.put("userName", "张三");
 * data.put("roles", Arrays.asList("ADMIN", "USER"));
 * MapResponse response = MapResponse.success(data);
 *
 * // 2. 链式添加数据
 * MapResponse response = MapResponse.builder()
 *     .put("status", "active")
 *     .put("count", 10)
 *     .put("items", itemList)
 *     .build();
 *
 * // 3. 错误响应
 * MapResponse error = MapResponse.error("404", "资源不存在");
 *
 * // 4. 从现有 Map 创建
 * Map<String, Object> existingMap = getSomeData();
 * MapResponse response = MapResponse.of(existingMap);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapResponse {

    /**
     * 状态码.
     *
     * @since 1.0.0-SNAPSHOT
     */
    private String code;

    /**
     * 消息.
     *
     * @since 1.0.0-SNAPSHOT
     */
    private String msg;

    /**
     * Map 形式的数据.
     * <p>
     * 支持任意类型的键值对，通常使用 String 作为键。
     * </p>
     *
     * @since 1.0.0-SNAPSHOT
     */
    private Map<String, Object> data;

    /**
     * 创建成功响应.
     *
     * @param data Map 数据
     * @return MapResponse
     */
    public static MapResponse success(final Map<String, Object> data) {
        return new MapResponse("200", "操作成功", data);
    }

    /**
     * 创建成功响应（带消息）.
     *
     * @param msg  消息
     * @param data Map 数据
     * @return MapResponse
     */
    public static MapResponse success(final String msg, final Map<String, Object> data) {
        return new MapResponse("200", msg, data);
    }

    /**
     * 创建成功响应（完整参数）.
     *
     * @param code 状态码
     * @param msg  消息
     * @param data Map 数据
     * @return MapResponse
     */
    public static MapResponse success(final String code, final String msg, final Map<String, Object> data) {
        return new MapResponse(code, msg, data);
    }

    /**
     * 创建错误响应.
     *
     * @param msg 错误消息
     * @return MapResponse
     */
    public static MapResponse error(final String msg) {
        return new MapResponse("500", msg, null);
    }

    /**
     * 创建错误响应（带状态码）.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return MapResponse
     */
    public static MapResponse error(final String code, final String msg) {
        return new MapResponse(code, msg, null);
    }

    /**
     * 创建错误响应（带数据）.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @param data 错误详情数据
     * @return MapResponse
     */
    public static MapResponse error(final String code, final String msg, final Map<String, Object> data) {
        return new MapResponse(code, msg, data);
    }

    /**
     * 从现有 Map 创建响应.
     *
     * @param map 现有的 Map 数据
     * @return MapResponse
     */
    public static MapResponse of(final Map<String, Object> map) {
        return new MapResponse("200", "操作成功", map);
    }

    /**
     * 创建空的 Map 响应.
     *
     * @return MapResponse
     */
    public static MapResponse empty() {
        return new MapResponse("200", "操作成功", new HashMap<>());
    }

    /**
     * 判断是否成功.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return "200".equals(code);
    }

    /**
     * 获取指定键的值.
     *
     * @param key 键
     * @param <T> 值类型
     * @return 值对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String key) {
        if (data == null) {
            return null;
        }
        return (T) data.get(key);
    }

    /**
     * 获取指定键的值（带默认值）.
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return 值对象或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(final String key, final T defaultValue) {
        if (data == null || !data.containsKey(key)) {
            return defaultValue;
        }
        return (T) data.get(key);
    }

    /**
     * 检查是否包含指定键.
     *
     * @param key 键
     * @return 包含返回 true
     */
    public boolean containsKey(final String key) {
        return data != null && data.containsKey(key);
    }

    /**
     * 获取数据大小.
     *
     * @return Map 中的键值对数量
     */
    public int size() {
        return data != null ? data.size() : 0;
    }

    /**
     * 检查数据是否为空.
     *
     * @return 为空返回 true
     */
    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * 向数据中添加键值对.
     * <p>
     * 如果 data 为 null，会自动初始化一个新的 HashMap。
     * </p>
     *
     * @param key   键
     * @param value 值
     * @return 当前对象（支持链式调用）
     */
    public MapResponse put(final String key, final Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
        return this;
    }

    /**
     * 批量添加键值对.
     *
     * @param map 要添加的 Map
     * @return 当前对象（支持链式调用）
     */
    public MapResponse putAll(final Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return this;
        }
        if (data == null) {
            data = new HashMap<>();
        }
        data.putAll(map);
        return this;
    }

    /**
     * 移除指定键.
     *
     * @param key 键
     * @return 被移除的值
     */
    public Object remove(final String key) {
        if (data == null) {
            return null;
        }
        return data.remove(key);
    }

    /**
     * 清空数据.
     *
     * @return 当前对象（支持链式调用）
     */
    public MapResponse clear() {
        if (data != null) {
            data.clear();
        }
        return this;
    }

    /**
     * 创建构建器.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * MapResponse 构建器.
     */
    public static class Builder {
        private String code = "200";
        private String msg = "操作成功";
        private final Map<String, Object> data = new HashMap<>();

        /**
         * 设置状态码.
         *
         * @param code 状态码
         * @return Builder
         */
        public Builder code(final String code) {
            this.code = code;
            return this;
        }

        /**
         * 设置消息.
         *
         * @param msg 消息
         * @return Builder
         */
        public Builder msg(final String msg) {
            this.msg = msg;
            return this;
        }

        /**
         * 添加键值对.
         *
         * @param key   键
         * @param value 值
         * @return Builder
         */
        public Builder put(final String key, final Object value) {
            this.data.put(key, value);
            return this;
        }

        /**
         * 批量添加键值对.
         *
         * @param map Map 数据
         * @return Builder
         */
        public Builder putAll(final Map<String, Object> map) {
            if (map != null) {
                this.data.putAll(map);
            }
            return this;
        }

        /**
         * 构建 MapResponse.
         *
         * @return MapResponse
         */
        public MapResponse build() {
            return new MapResponse(code, msg, data);
        }
    }
}
