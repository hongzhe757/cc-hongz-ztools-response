package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.ajax.example.UserAjaxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AjaxStatusMessageKeys} 单元测试.
 */
@DisplayName("AjaxStatusMessageKeys 测试")
class AjaxStatusMessageKeysTest {

    @Test
    @DisplayName("AjaxStatusEnum - COMMON_404 映射 common.not.found")
    void testCommonNotFoundMapping() {
        assertEquals("common.not.found", AjaxStatusMessageKeys.resolveKey(AjaxStatusEnum.NOT_FOUND));
    }

    @Test
    @DisplayName("AjaxStatusEnum - COMMON_400 映射 common.param.invalid")
    void testCommonParamInvalidMapping() {
        assertEquals("common.param.invalid", AjaxStatusMessageKeys.resolveKey(AjaxStatusEnum.PARAM_INVALID));
    }

    @Test
    @DisplayName("AjaxStatusEnum - 无 messageKey 时回退业务码")
    void testFallbackToBusinessCode() {
        assertEquals("COMMON_501", AjaxStatusMessageKeys.resolveKey(AjaxStatusEnum.NOT_IMPLEMENTED));
    }

    @Test
    @DisplayName("UserAjaxStatus - USER_404 映射 user.not.found")
    void testUserNotFoundMapping() {
        assertEquals("user.not.found", AjaxStatusMessageKeys.resolveKey(UserAjaxStatus.USER_NOT_FOUND));
    }

    @Test
    @DisplayName("register - 动态注册自定义映射")
    void testDynamicRegister() {
        AjaxStatusMessageKeys.register("ORDER_404", "order.not.found");
        assertEquals("order.not.found", AjaxStatusMessageKeys.lookup("ORDER_404"));
    }

    @Test
    @DisplayName("registerAll - 批量注册枚举 messageKey")
    void testRegisterAllEnum() {
        AjaxStatusMessageKeys.register("TEMP_001", "temp.key");
        AjaxStatusMessageKeys.registerAll(UserAjaxStatus.class);
        assertEquals("user.disabled", AjaxStatusMessageKeys.lookup("USER_403"));
    }
}
