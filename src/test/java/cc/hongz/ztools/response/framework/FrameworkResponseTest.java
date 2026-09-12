package cc.hongz.ztools.response.framework;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * framework 包响应类的单元测试.
 * <p>
 * 测试 ActuatorHealthResponse、OAuth2TokenResponse 等框架特定响应。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("Framework 包响应类测试")
class FrameworkResponseTest {

    // ==================== ActuatorHealthResponse 测试 ====================

    @Test
    @DisplayName("ActuatorHealthResponse.up() - 创建 UP 状态响应")
    void testActuatorHealthResponseUp() {
        ActuatorHealthResponse response = ActuatorHealthResponse.up();

        assertNotNull(response);
        assertEquals("UP", response.getStatus());
        assertTrue(response.isHealthy());
        assertNull(response.getComponents());
    }

    @Test
    @DisplayName("ActuatorHealthResponse.down() - 创建 DOWN 状态响应")
    void testActuatorHealthResponseDown() {
        ActuatorHealthResponse response = ActuatorHealthResponse.down();

        assertEquals("DOWN", response.getStatus());
        assertFalse(response.isHealthy());
    }

    @Test
    @DisplayName("ActuatorHealthResponse.of(status, components) - 带组件状态的响应")
    void testActuatorHealthResponseOf() {
        ActuatorHealthResponse.ComponentHealth dbHealth = new ActuatorHealthResponse.ComponentHealth(
                "UP",
                Map.of("database", "MySQL", "connectionTime", 5)
        );
        ActuatorHealthResponse.ComponentHealth diskHealth = new ActuatorHealthResponse.ComponentHealth(
                "DOWN",
                Map.of("freeSpace", "100MB", "threshold", "1GB")
        );

        Map<String, ActuatorHealthResponse.ComponentHealth> components = Map.of(
                "db", dbHealth,
                "diskSpace", diskHealth
        );

        ActuatorHealthResponse response = ActuatorHealthResponse.of("DOWN", components);

        assertEquals("DOWN", response.getStatus());
        assertFalse(response.isHealthy());
        assertNotNull(response.getComponents());
        assertEquals(2, response.getComponents().size());
        assertEquals("UP", response.getComponents().get("db").getStatus());
        assertEquals("DOWN", response.getComponents().get("diskSpace").getStatus());
    }

    @Test
    @DisplayName("ActuatorHealthResponse.isHealthy() - 判断是否健康")
    void testActuatorHealthResponseIsHealthy() {
        assertTrue(ActuatorHealthResponse.up().isHealthy());
        assertFalse(ActuatorHealthResponse.down().isHealthy());

        // 大小写不敏感
        ActuatorHealthResponse customUp = ActuatorHealthResponse.builder().status("up").build();
        assertTrue(customUp.isHealthy());

        ActuatorHealthResponse unknown = ActuatorHealthResponse.builder().status("UNKNOWN").build();
        assertFalse(unknown.isHealthy());
    }

    @Test
    @DisplayName("ActuatorHealthResponse.ComponentHealth - 组件健康信息")
    void testActuatorHealthResponseComponentHealth() {
        Map<String, Object> details = Map.of(
                "version", "8.0.30",
                "validationQuery", "SELECT 1"
        );

        ActuatorHealthResponse.ComponentHealth health = new ActuatorHealthResponse.ComponentHealth("UP", details);

        assertEquals("UP", health.getStatus());
        assertNotNull(health.getDetails());
        assertEquals("8.0.30", health.getDetails().get("version"));
    }

    @Test
    @DisplayName("ActuatorHealthResponse Builder 模式")
    void testActuatorHealthResponseBuilder() {
        ActuatorHealthResponse response = ActuatorHealthResponse.builder()
                .status("OUT_OF_SERVICE")
                .components(Map.of())
                .build();

        assertEquals("OUT_OF_SERVICE", response.getStatus());
        assertFalse(response.isHealthy());
        assertNotNull(response.getComponents());
    }

    @Test
    @DisplayName("ActuatorHealthResponse Getter/Setter")
    void testActuatorHealthResponseGettersSetters() {
        ActuatorHealthResponse response = new ActuatorHealthResponse();
        response.setStatus("UP");
        response.setComponents(Map.of("test", new ActuatorHealthResponse.ComponentHealth("UP", null)));

        assertEquals("UP", response.getStatus());
        assertNotNull(response.getComponents());
    }

    // ==================== OAuth2TokenResponse 测试 ====================

    @Test
    @DisplayName("OAuth2TokenResponse.success(...) - 创建成功令牌响应")
    void testOAuth2TokenResponseSuccess() {
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
        String tokenType = "Bearer";
        Long expiresIn = 3600L;

        OAuth2TokenResponse response = OAuth2TokenResponse.success(accessToken, tokenType, expiresIn);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(tokenType, response.getTokenType());
        assertEquals(expiresIn, response.getExpiresIn());
        assertNull(response.getRefreshToken());
        assertNull(response.getScope());
        assertFalse(response.hasRefreshToken());
    }

    @Test
    @DisplayName("OAuth2TokenResponse.withRefresh(...) - 带刷新令牌的响应")
    void testOAuth2TokenResponseWithRefresh() {
        String accessToken = "access_token_123";
        String tokenType = "Bearer";
        Long expiresIn = 7200L;
        String refreshToken = "refresh_token_456";

        OAuth2TokenResponse response = OAuth2TokenResponse.withRefresh(accessToken, tokenType, expiresIn, refreshToken);

        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
        assertTrue(response.hasRefreshToken());
    }

    @Test
    @DisplayName("OAuth2TokenResponse.full(...) - 完整令牌响应")
    void testOAuth2TokenResponseFull() {
        String accessToken = "access_token";
        String tokenType = "Bearer";
        Long expiresIn = 3600L;
        String refreshToken = "refresh_token";
        String scope = "read write";

        OAuth2TokenResponse response = OAuth2TokenResponse.full(accessToken, tokenType, expiresIn, refreshToken, scope);

        assertEquals(accessToken, response.getAccessToken());
        assertEquals(tokenType, response.getTokenType());
        assertEquals(expiresIn, response.getExpiresIn());
        assertEquals(refreshToken, response.getRefreshToken());
        assertEquals(scope, response.getScope());
        assertTrue(response.hasRefreshToken());
    }

    @Test
    @DisplayName("OAuth2TokenResponse.hasRefreshToken() - 判断是否有刷新令牌")
    void testOAuth2TokenResponseHasRefreshToken() {
        OAuth2TokenResponse resp1 = OAuth2TokenResponse.success("token", "Bearer", 3600L);
        assertFalse(resp1.hasRefreshToken());

        OAuth2TokenResponse resp2 = OAuth2TokenResponse.withRefresh("token", "Bearer", 3600L, "refresh");
        assertTrue(resp2.hasRefreshToken());

        // 空字符串
        OAuth2TokenResponse resp3 = OAuth2TokenResponse.builder()
                .accessToken("token")
                .refreshToken("")
                .build();
        assertFalse(resp3.hasRefreshToken());

        // null
        OAuth2TokenResponse resp4 = OAuth2TokenResponse.builder()
                .accessToken("token")
                .refreshToken(null)
                .build();
        assertFalse(resp4.hasRefreshToken());
    }

    @Test
    @DisplayName("OAuth2TokenResponse Builder 模式")
    void testOAuth2TokenResponseBuilder() {
        OAuth2TokenResponse response = OAuth2TokenResponse.builder()
                .accessToken("custom_token")
                .tokenType("MAC")
                .expiresIn(1800L)
                .refreshToken("custom_refresh")
                .scope("admin")
                .build();

        assertEquals("custom_token", response.getAccessToken());
        assertEquals("MAC", response.getTokenType());
        assertEquals(1800L, response.getExpiresIn());
        assertEquals("custom_refresh", response.getRefreshToken());
        assertEquals("admin", response.getScope());
    }

    @Test
    @DisplayName("OAuth2TokenResponse Getter/Setter")
    void testOAuth2TokenResponseGettersSetters() {
        OAuth2TokenResponse response = new OAuth2TokenResponse();
        response.setAccessToken("at_123");
        response.setTokenType("Bearer");
        response.setExpiresIn(900L);
        response.setRefreshToken("rt_456");
        response.setScope("openid profile");

        assertEquals("at_123", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(900L, response.getExpiresIn());
        assertEquals("rt_456", response.getRefreshToken());
        assertEquals("openid profile", response.getScope());
    }

    // ==================== 边界情况测试 ====================

    @Test
    @DisplayName("OAuth2TokenResponse - 过期时间为 null")
    void testOAuth2TokenResponseNullExpiresIn() {
        OAuth2TokenResponse response = OAuth2TokenResponse.success("token", "Bearer", null);

        assertEquals("token", response.getAccessToken());
        assertNull(response.getExpiresIn());
    }

    @Test
    @DisplayName("ActuatorHealthResponse - 空组件映射")
    void testActuatorHealthResponseEmptyComponents() {
        ActuatorHealthResponse response = ActuatorHealthResponse.of("UP", Map.of());

        assertEquals("UP", response.getStatus());
        assertTrue(response.isHealthy());
        assertNotNull(response.getComponents());
        assertTrue(response.getComponents().isEmpty());
    }
}
