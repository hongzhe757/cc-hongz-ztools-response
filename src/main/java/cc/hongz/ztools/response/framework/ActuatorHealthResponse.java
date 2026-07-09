package cc.hongz.ztools.response.framework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Spring Boot Actuator 健康检查响应.
 * <p>
 * Spring Boot Actuator /health 端点的标准响应格式。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActuatorHealthResponse {

    /**
     * 整体状态（UP/DOWN/OUT_OF_SERVICE/UNKNOWN）.
     */
    private String status;

    /**
     * 各组件的详细状态.
     */
    private Map<String, ComponentHealth> components;

    /**
     * 组件健康信息.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComponentHealth {
        /**
         * 组件状态.
         */
        private String status;

        /**
         * 详细信息.
         */
        private Map<String, Object> details;
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 创建 UP 状态响应.
     *
     * @return ActuatorHealthResponse
     */
    public static ActuatorHealthResponse up() {
        return ActuatorHealthResponse.builder()
                .status("UP")
                .build();
    }

    /**
     * 创建 DOWN 状态响应.
     *
     * @return ActuatorHealthResponse
     */
    public static ActuatorHealthResponse down() {
        return ActuatorHealthResponse.builder()
                .status("DOWN")
                .build();
    }

    /**
     * 创建带组件状态的响应.
     *
     * @param status     整体状态
     * @param components 组件状态映射
     * @return ActuatorHealthResponse
     */
    public static ActuatorHealthResponse of(final String status, final Map<String, ComponentHealth> components) {
        return ActuatorHealthResponse.builder()
                .status(status)
                .components(components)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否健康.
     *
     * @return 健康返回 true
     */
    public boolean isHealthy() {
        return "UP".equalsIgnoreCase(status);
    }
}
