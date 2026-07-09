package cc.hongz.ztools.response.framework;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Spring Security OAuth2 Token 响应.
 * <p>
 * OAuth2 令牌端点的标准响应格式（RFC 6749）。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2TokenResponse {

    /**
     * 访问令牌.
     */
    private String accessToken;

    /**
     * 令牌类型（通常为 "Bearer"）.
     */
    private String tokenType;

    /**
     * 过期时间（秒）.
     */
    private Long expiresIn;

    /**
     * 刷新令牌.
     */
    private String refreshToken;

    /**
     * 作用域.
     */
    private String scope;

    // ==================== 静态工厂方法 ====================

    /**
     * 创建成功的令牌响应.
     *
     * @param accessToken 访问令牌
     * @param tokenType   令牌类型
     * @param expiresIn   过期时间（秒）
     * @return OAuth2TokenResponse
     */
    public static OAuth2TokenResponse success(final String accessToken, final String tokenType, final Long expiresIn) {
        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .build();
    }

    /**
     * 创建带刷新令牌的响应.
     *
     * @param accessToken  访问令牌
     * @param tokenType    令牌类型
     * @param expiresIn    过期时间（秒）
     * @param refreshToken 刷新令牌
     * @return OAuth2TokenResponse
     */
    public static OAuth2TokenResponse withRefresh(final String accessToken, final String tokenType, final Long expiresIn, final String refreshToken) {
        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 创建完整的令牌响应.
     *
     * @param accessToken  访问令牌
     * @param tokenType    令牌类型
     * @param expiresIn    过期时间（秒）
     * @param refreshToken 刷新令牌
     * @param scope        作用域
     * @return OAuth2TokenResponse
     */
    public static OAuth2TokenResponse full(final String accessToken, final String tokenType, final Long expiresIn, final String refreshToken, final String scope) {
        return OAuth2TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType(tokenType)
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .scope(scope)
                .build();
    }

    // ==================== 便捷判断方法 ====================

    /**
     * 判断是否有刷新令牌.
     *
     * @return 有返回 true
     */
    public boolean hasRefreshToken() {
        return refreshToken != null && !refreshToken.isEmpty();
    }
}
