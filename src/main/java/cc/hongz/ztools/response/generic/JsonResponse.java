package cc.hongz.ztools.response.generic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSON 字符串响应包装器.
 * <p>
 * 用于直接返回 JSON 字符串的场景，常见于：
 * </p>
 * <ul>
 *   <li>遗留系统对接（已序列化的 JSON 字符串）</li>
 *   <li>动态生成的 JSON 内容</li>
 *   <li>第三方 API 透传</li>
 *   <li>避免重复序列化/反序列化开销</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 直接包装 JSON 字符串
 * String json = "{\"code\":\"200\",\"msg\":\"success\",\"data\":{\"id\":1}}";
 * JsonResponse response = JsonResponse.of(json);
 *
 * // 2. 从对象序列化后包装
 * User user = new User(1L, "张三");
 * String userJson = JSON.toJSONString(user);
 * JsonResponse response = JsonResponse.success(userJson);
 *
 * // 3. 错误响应
 * JsonResponse error = JsonResponse.error("500", "服务器内部错误");
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonResponse {

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
     * JSON 字符串数据.
     * <p>
     * 注意：此字段存储的是 JSON 格式的字符串，不是对象。
     * </p>
     *
     * @since 1.0.0-SNAPSHOT
     */
    private String jsonData;

    /**
     * 创建成功响应.
     *
     * @param jsonData JSON 字符串数据
     * @return JsonResponse
     */
    public static JsonResponse success(final String jsonData) {
        return new JsonResponse("200", "操作成功", jsonData);
    }

    /**
     * 创建成功响应（带消息）.
     *
     * @param msg      消息
     * @param jsonData JSON 字符串数据
     * @return JsonResponse
     */
    public static JsonResponse success(final String msg, final String jsonData) {
        return new JsonResponse("200", msg, jsonData);
    }

    /**
     * 创建成功响应（完整参数）.
     *
     * @param code     状态码
     * @param msg      消息
     * @param jsonData JSON 字符串数据
     * @return JsonResponse
     */
    public static JsonResponse success(final String code, final String msg, final String jsonData) {
        return new JsonResponse(code, msg, jsonData);
    }

    /**
     * 创建错误响应.
     *
     * @param msg 错误消息
     * @return JsonResponse
     */
    public static JsonResponse error(final String msg) {
        return new JsonResponse("500", msg, null);
    }

    /**
     * 创建错误响应（带状态码）.
     *
     * @param code 状态码
     * @param msg  错误消息
     * @return JsonResponse
     */
    public static JsonResponse error(final String code, final String msg) {
        return new JsonResponse(code, msg, null);
    }

    /**
     * 创建错误响应（完整参数）.
     *
     * @param code     状态码
     * @param msg      错误消息
     * @param jsonData JSON 字符串数据（可选）
     * @return JsonResponse
     */
    public static JsonResponse error(final String code, final String msg, final String jsonData) {
        return new JsonResponse(code, msg, jsonData);
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
     * 获取 JSON 数据（如果为空则返回空对象字符串）.
     *
     * @return JSON 字符串
     */
    public String getJsonDataOrDefault() {
        return jsonData != null ? jsonData : "{}";
    }

    /**
     * 检查是否有数据.
     *
     * @return 有数据返回 true
     */
    public boolean hasData() {
        return jsonData != null && !jsonData.isEmpty();
    }
}
