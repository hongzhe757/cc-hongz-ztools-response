package cc.hongz.ztools.response.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * gRPC JSON Transcoding 响应结构.
 * <p>
 * gRPC 转 REST/JSON 时的响应格式，使用 google.rpc.Status 错误格式。
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
public class GrpcResponse<T> {

    /**
     * gRPC 状态码（0 表示成功）.
     */
    private int code;

    /**
     * 错误消息.
     */
    private String message;

    /**
     * 响应数据（成功时直接返回 Protobuf message 的 JSON 映射）.
     */
    private T data;

    /**
     * 错误详情列表.
     */
    private List<ErrorDetail> details;

    /**
     * 错误详情.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorDetail {
        /**
         * 类型 URL.
         */
        private String type;

        /**
         * 值.
         */
        private Map<String, Object> value;
    }

    // ==================== gRPC 状态码常量 ====================

    /** OK - 操作成功 */
    public static final int STATUS_OK = 0;
    /** CANCELLED - 操作被取消 */
    public static final int STATUS_CANCELLED = 1;
    /** UNKNOWN - 未知错误 */
    public static final int STATUS_UNKNOWN = 2;
    /** INVALID_ARGUMENT - 无效参数 */
    public static final int STATUS_INVALID_ARGUMENT = 3;
    /** DEADLINE_EXCEEDED - 超时 */
    public static final int STATUS_DEADLINE_EXCEEDED = 4;
    /** NOT_FOUND - 未找到 */
    public static final int STATUS_NOT_FOUND = 5;
    /** ALREADY_EXISTS - 已存在 */
    public static final int STATUS_ALREADY_EXISTS = 6;
    /** PERMISSION_DENIED - 权限拒绝 */
    public static final int STATUS_PERMISSION_DENIED = 7;
    /** RESOURCE_EXHAUSTED - 资源耗尽 */
    public static final int STATUS_RESOURCE_EXHAUSTED = 8;
    /** FAILED_PRECONDITION - 前置条件失败 */
    public static final int STATUS_FAILED_PRECONDITION = 9;
    /** ABORTED - 中止 */
    public static final int STATUS_ABORTED = 10;
    /** OUT_OF_RANGE - 超出范围 */
    public static final int STATUS_OUT_OF_RANGE = 11;
    /** UNIMPLEMENTED - 未实现 */
    public static final int STATUS_UNIMPLEMENTED = 12;
    /** INTERNAL - 内部错误 */
    public static final int STATUS_INTERNAL = 13;
    /** UNAVAILABLE - 不可用 */
    public static final int STATUS_UNAVAILABLE = 14;
    /** DATA_LOSS - 数据丢失 */
    public static final int STATUS_DATA_LOSS = 15;
    /** UNAUTHENTICATED - 未认证 */
    public static final int STATUS_UNAUTHENTICATED = 16;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功响应.
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return GrpcResponse
     */
    public static <T> GrpcResponse<T> success(final T data) {
        return GrpcResponse.<T>builder()
                .code(STATUS_OK)
                .message("OK")
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（自定义状态码）.
     *
     * @param code 状态码
     * @param data 数据
     * @param <T>  数据类型
     * @return GrpcResponse
     */
    public static <T> GrpcResponse<T> success(final int code, final T data) {
        return GrpcResponse.<T>builder()
                .code(code)
                .message("OK")
                .data(data)
                .build();
    }

    /**
     * 创建错误响应.
     *
     * @param code    状态码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return GrpcResponse
     */
    public static <T> GrpcResponse<T> error(final int code, final String message) {
        return GrpcResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 创建带详情的错误响应.
     *
     * @param code    状态码
     * @param message 错误消息
     * @param details 错误详情
     * @param <T>     数据类型
     * @return GrpcResponse
     */
    public static <T> GrpcResponse<T> error(final int code, final String message, final List<ErrorDetail> details) {
        return GrpcResponse.<T>builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否为成功响应.
     *
     * @return 成功返回 true
     */
    public boolean isSuccess() {
        return code == STATUS_OK;
    }
}
