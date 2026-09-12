package cc.hongz.ztools.response.ajax.example;

import cc.hongz.ztools.status.AjaxStatusInterface;
import cc.hongz.ztools.status.AjaxStatusRegistry;
import cc.hongz.ztools.status.AjaxStatuses;
import cc.hongz.ztools.status.example.UserAjaxStatus;
import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.status.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自定义 AJAX 业务状态码完整示例测试.
 * <p>
 * 演示自定义 AjaxStatusInterface 的定义、注册、查询、与 AjaxResult 集成等全流程。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("自定义 AJAX 业务状态码示例测试")
class CustomAjaxStatusExampleTest {

    private AjaxStatusRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AjaxStatusRegistry();
    }

    // ==================== 1. 自定义枚举基本属性测试 ====================

    @Nested
    @DisplayName("1. 自定义枚举基本属性")
    class BasicPropertyTests {

        @Test
        @DisplayName("UserAjaxStatus - USER_NOT_FOUND 属性正确")
        void testUserNotFound() {
            AjaxStatusInterface code = UserAjaxStatus.USER_NOT_FOUND;

            assertEquals("USER_404", code.getCode());
            assertEquals("用户不存在：{0}", code.getDefaultMessage());
            assertEquals(HttpStatusCode.NOT_FOUND, code.getHttpStatus());
            assertFalse(code.isSuccess());
            assertEquals("USER", code.getDomain());
            assertEquals("404", code.getCodeNumber());
        }

        @Test
        @DisplayName("UserAjaxStatus - USER_SUCCESS 是成功状态")
        void testUserSuccess() {
            AjaxStatusInterface code = UserAjaxStatus.USER_SUCCESS;

            assertEquals("USER_000", code.getCode());
            assertEquals("用户操作成功", code.getDefaultMessage());
            assertEquals(HttpStatusCode.OK, code.getHttpStatus());
            assertTrue(code.isSuccess());
            assertEquals("USER", code.getDomain());
            assertEquals("000", code.getCodeNumber());
        }

        @Test
        @DisplayName("UserAjaxStatus - LOGIN_FAILED 映射到 401")
        void testLoginFailed() {
            AjaxStatusInterface code = UserAjaxStatus.LOGIN_FAILED;

            assertEquals("USER_401", code.getCode());
            assertEquals(HttpStatusCode.UNAUTHORIZED, code.getHttpStatus());
            assertFalse(code.isSuccess());
        }

        @Test
        @DisplayName("所有 UserAjaxStatus 的 domain 都是 USER")
        void testAllDomainsAreUser() {
            for (UserAjaxStatus code : UserAjaxStatus.values()) {
                assertEquals("USER", code.getDomain(),
                    "Expected domain 'USER' for " + code.name());
            }
        }

        @Test
        @DisplayName("所有 UserAjaxStatus 的 prefix 都是 USER")
        void testAllPrefixesAreUser() {
            for (UserAjaxStatus code : UserAjaxStatus.values()) {
                assertEquals("USER", code.getPrefix(),
                    "Expected prefix 'USER' for " + code.name());
            }
        }

        @Test
        @DisplayName("UserAjaxStatus.PrefixHolder.PREFIX 常量值正确")
        void testPrefixConstant() {
            assertEquals("USER", UserAjaxStatus.PrefixHolder.PREFIX);
        }

        @Test
        @DisplayName("code 由 AjaxStatuses.buildCode 构建")
        void testCodeBuiltByBuildCode() {
            assertEquals(
                AjaxStatuses.buildCode(UserAjaxStatus.PrefixHolder.PREFIX, "404"),
                UserAjaxStatus.USER_NOT_FOUND.getCode()
            );
            assertEquals(
                AjaxStatuses.buildCode(UserAjaxStatus.PrefixHolder.PREFIX, "000"),
                UserAjaxStatus.USER_SUCCESS.getCode()
            );
        }
    }

    // ==================== 2. 注册表集成测试 ====================

    @Nested
    @DisplayName("2. 注册表集成")
    class RegistryIntegrationTests {

        @Test
        @DisplayName("registerAll - 批量注册用户模块状态码")
        void testRegisterAll() {
            registry.registerAll(UserAjaxStatus.class);

            assertEquals(8, registry.size());
            assertTrue(registry.contains("USER_404"));
            assertTrue(registry.contains("USER_403"));
            assertTrue(registry.contains("USER_401"));
            assertTrue(registry.contains("USER_409"));
            assertTrue(registry.contains("USER_422"));
            assertTrue(registry.contains("USER_423"));
            assertTrue(registry.contains("USER_424"));
            assertTrue(registry.contains("USER_000"));
        }

        @Test
        @DisplayName("getByCode - 通过 code 字符串查询自定义状态码")
        void testGetByCode() {
            registry.registerAll(UserAjaxStatus.class);

            AjaxStatusInterface found = registry.getByCode("USER_404");
            assertNotNull(found);
            assertEquals(UserAjaxStatus.USER_NOT_FOUND, found);
            assertEquals("用户不存在：{0}", found.getDefaultMessage());
        }

        @Test
        @DisplayName("混合注册 - AjaxStatusEnum + UserAjaxStatus 共存")
        void testMixedRegistration() {
            registry.init(); // 自动注册 AjaxStatusEnum
            registry.registerAll(UserAjaxStatus.class);

            // AjaxStatusEnum (13) + UserAjaxStatus (8) = 21
            assertEquals(21, registry.size());

            // 两种状态码都能查到
            assertNotNull(registry.getByCode("COMMON_000"));
            assertNotNull(registry.getByCode("USER_404"));
        }

        @Test
        @DisplayName("手动注册单个自定义状态码")
        void testRegisterSingle() {
            registry.register(UserAjaxStatus.USER_NOT_FOUND);

            assertEquals(1, registry.size());
            assertTrue(registry.contains("USER_404"));
            assertFalse(registry.contains("USER_403"));
        }

        @Test
        @DisplayName("getByPrefix - 按前缀查询用户模块状态码")
        void testGetByPrefix() {
            registry.registerAll(UserAjaxStatus.class);

            var userCodes = registry.getByPrefix("USER");
            assertEquals(8, userCodes.size());

            var commonCodes = registry.getByPrefix("COMMON");
            assertTrue(commonCodes.isEmpty());
        }

        @Test
        @DisplayName("containsPrefix - 检查前缀是否存在")
        void testContainsPrefix() {
            registry.registerAll(UserAjaxStatus.class);

            assertTrue(registry.containsPrefix("USER"));
            assertFalse(registry.containsPrefix("ORDER"));
        }
    }

    // ==================== 3. 与 AjaxResult 集成测试 ====================

    @Nested
    @DisplayName("3. AjaxResult 集成")
    class AjaxResultIntegrationTests {

        @Test
        @DisplayName("error(AjaxStatus) - 使用自定义状态码创建错误响应")
        void testErrorWithCustomCode() {
            AjaxResult result = AjaxResult.error(UserAjaxStatus.USER_DISABLED, "admin");

            assertEquals("USER_403", result.getCode());
            assertEquals("用户已被禁用，请联系管理员", result.getMsg());
            assertEquals("admin", result.getData());
            assertFalse(result.isSuccess());
        }

        @Test
        @DisplayName("error(AjaxStatus, Object[]) - 参数替换")
        void testErrorWithParamSubstitution() {
            AjaxResult result = AjaxResult.error(
                UserAjaxStatus.USER_NOT_FOUND,
                new Object[]{"U10086"}
            );

            assertEquals("USER_404", result.getCode());
            assertEquals("用户不存在：U10086", result.getMsg());
        }

        @Test
        @DisplayName("error(AjaxStatus, String, Object) - 自定义消息和数据")
        void testErrorWithCustomMessageAndData() {
            AjaxResult result = AjaxResult.error(
                UserAjaxStatus.INVALID_EMAIL,
                "请检查邮箱格式",
                "test@invalid"
            );

            assertEquals("USER_422", result.getCode());
            assertEquals("请检查邮箱格式", result.getMsg());
            assertEquals("test@invalid", result.getData());
        }

        @Test
        @DisplayName("success(AjaxStatus, data) - 使用自定义成功状态码")
        void testSuccessWithCustomCode() {
            AjaxResult result = AjaxResult.success(UserAjaxStatus.USER_SUCCESS, "userId=123");

            assertEquals("USER_000", result.getCode());
            assertEquals("用户操作成功", result.getMsg());
            assertEquals("userId=123", result.getData());
        }

        @Test
        @DisplayName("多参数替换 - USER_ALREADY_EXISTS")
        void testMultiParamSubstitution() {
            // USER_ALREADY_EXISTS 默认消息: "用户已存在：{0}"
            AjaxResult result = AjaxResult.error(
                UserAjaxStatus.USER_ALREADY_EXISTS,
                new Object[]{"zhangsan"}
            );

            assertEquals("USER_409", result.getCode());
            assertEquals("用户已存在：zhangsan", result.getMsg());
        }
    }

    // ==================== 4. 实际使用场景模拟 ====================

    @Nested
    @DisplayName("4. 实际使用场景模拟")
    class RealWorldScenarioTests {

        @Test
        @DisplayName("场景：用户登录失败 → 返回标准化错误响应")
        void testLoginFailureScenario() {
            // Service 层返回
            AjaxResult result = AjaxResult.error(UserAjaxStatus.LOGIN_FAILED, (String) null);

            assertEquals("USER_401", result.getCode());
            assertEquals("用户名或密码错误", result.getMsg());
            assertFalse(result.isSuccess());
        }

        @Test
        @DisplayName("场景：查找用户 → 用户不存在 → 带 userId 的错误信息")
        void testFindUserNotFoundScenario() {
            String userId = "U99999";
            AjaxResult result = AjaxResult.error(
                UserAjaxStatus.USER_NOT_FOUND,
                new Object[]{userId}
            );

            assertEquals("USER_404", result.getCode());
            assertEquals("用户不存在：U99999", result.getMsg());
        }

        @Test
        @DisplayName("场景：注册冲突 → 通过注册表动态获取状态码")
        void testRegistrationConflictViaRegistry() {
            // 初始化注册表
            registry.registerAll(UserAjaxStatus.class);

            // 通过 code 字符串动态获取（适用于配置化/跨服务场景）
            AjaxStatusInterface code = registry.getByCode("USER_409");
            assertNotNull(code);

            AjaxResult result = AjaxResult.error(code, new Object[]{"lisi"});
            assertEquals("USER_409", result.getCode());
            assertEquals("用户已存在：lisi", result.getMsg());
        }

        @Test
        @DisplayName("场景：验证码校验失败")
        void testCaptchaInvalidScenario() {
            AjaxResult result = AjaxResult.error(UserAjaxStatus.CAPTCHA_INVALID, (String) null);

            assertEquals("USER_424", result.getCode());
            assertEquals("验证码错误或已过期", result.getMsg());
        }
    }
}
