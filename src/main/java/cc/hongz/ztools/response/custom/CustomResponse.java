package cc.hongz.ztools.response.custom;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义响应结构示例 - 演示如何扩展自定义返回格式.
 * <p>
 * 这是一个完整的自定义响应结构示例，展示了如何定义符合特定业务需求的响应格式。
 * 你可以根据实际需求修改字段名称、类型和结构。
 * </p>
 *
 * <h3>设计说明：</h3>
 * <ul>
 *   <li><strong>状态码</strong>：使用 int 类型，便于数值比较</li>
 *   <li><strong>消息</strong>：支持国际化 key 或直接文本</li>
 *   <li><strong>数据</strong>：泛型支持任意数据类型</li>
 *   <li><strong>元数据</strong>：额外的上下文信息（如 traceId、timestamp）</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 创建成功响应
 * CustomResponse<User> response = CustomResponse.success(user);
 *
 * // 2. 创建失败响应
 * CustomResponse<Void> error = CustomResponse.error(404, "用户不存在");
 *
 * // 3. 链式构建
 * CustomResponse<Order> custom = CustomResponse.<Order>builder()
 *     .code(200)
 *     .message("查询成功")
 *     .data(order)
 *     .traceId("abc-123")
 *     .build();
 * }</pre>
 *
 * @param <T> 数据类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see CustomResponseConverter
 */
@Data
@Accessors(chain = true)
public class CustomResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码.
     * <p>
     * 200=成功, 4xx=客户端错误, 5xx=服务器错误
     * </p>
     */
    private int code;

    /**
     * 响应消息.
     * <p>
     * 可以是直接的文本消息，也可以是国际化 key
     * </p>
     */
    private String message;

    /**
     * 响应数据.
     * <p>
     * 泛型支持任意数据类型
     * </p>
     */
    private T data;

    /**
     * 时间戳（毫秒）.
     */
    private long timestamp;

    /**
     * 链路追踪ID.
     * <p>
     * 用于分布式系统中的请求追踪
     * </p>
     */
    private String traceId;

    /**
     * 额外元数据.
     * <p>
     * 存储额外的上下文信息，如分页信息、权限信息等
     * </p>
     */
    private Map<String, Object> metadata;

    public CustomResponse() {
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }

    /**
     * 创建成功响应.
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> success(T data) {
        return success(200, "操作成功", data);
    }

    /**
     * 创建成功响应（带消息）.
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> success(String message, T data) {
        return success(200, message, data);
    }

    /**
     * 创建成功响应（完整参数）.
     *
     * @param code    状态码
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> success(int code, String message, T data) {
        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 创建失败响应.
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * 创建失败响应（带数据）.
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误详情数据
     * @param <T>     数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> error(int code, String message, T data) {
        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * 判断是否成功.
     *
     * @return 如果 code == 200 则返回 true
     */
    public boolean isSuccess() {
        return this.code == 200;
    }

    /**
     * 添加元数据.
     *
     * @param key   键
     * @param value 值
     * @return this（支持链式调用）
     */
    public CustomResponse<T> addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        return this;
    }

    /**
     * 获取元数据.
     *
     * @param key 键
     * @param <V> 值类型
     * @return 元数据值
     */
    @SuppressWarnings("unchecked")
    public <V> V getMetadata(String key) {
        if (this.metadata == null) {
            return null;
        }
        return (V) this.metadata.get(key);
    }

    /**
     * 创建构建器.
     *
     * @param <T> 数据类型
     * @return Builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * 构建器类.
     *
     * @param <T> 数据类型
     */
    public static class Builder<T> {
        private final CustomResponse<T> response;

        public Builder() {
            this.response = new CustomResponse<>();
        }

        public Builder<T> code(int code) {
            this.response.setCode(code);
            return this;
        }

        public Builder<T> message(String message) {
            this.response.setMessage(message);
            return this;
        }

        public Builder<T> data(T data) {
            this.response.setData(data);
            return this;
        }

        public Builder<T> traceId(String traceId) {
            this.response.setTraceId(traceId);
            return this;
        }

        public Builder<T> metadata(String key, Object value) {
            this.response.addMetadata(key, value);
            return this;
        }

        public CustomResponse<T> build() {
            return this.response;
        }
    }
}
