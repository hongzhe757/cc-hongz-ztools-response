package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.http.HttpStatusCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AjaxStatusInterface} 和 {@link AjaxStatusEnum} 单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("AjaxStatusInterface 测试")
class AjaxStatusTest {

    private AjaxStatusRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AjaxStatusRegistry();
    }

    // ==================== AjaxStatusEnum 测试 ====================

    @Test
    @DisplayName("AjaxStatusEnum - SUCCESS messageKey")
    void testCommonAjaxStatus_MessageKey() {
        assertEquals("common.success", AjaxStatusEnum.SUCCESS.getMessageKey());
        assertEquals("common.not.found", AjaxStatusEnum.NOT_FOUND.getMessageKey());
        assertNull(AjaxStatusEnum.NOT_IMPLEMENTED.getMessageKey());
    }

    @Test
    @DisplayName("AjaxStatusEnum - SUCCESS")
    void testCommonAjaxStatus_Success() {
        AjaxStatusInterface code = AjaxStatusEnum.SUCCESS;
        
        assertEquals("COMMON_000", code.getCode());
        assertEquals("操作成功", code.getDefaultMessage());
        assertEquals(HttpStatusCode.OK, code.getHttpStatus());
        assertTrue(code.isSuccess());
        assertEquals("COMMON", code.getDomain());
        assertEquals("000", code.getCodeNumber());
    }

    @Test
    @DisplayName("AjaxStatusEnum - PARAM_INVALID")
    void testCommonAjaxStatus_ParamInvalid() {
        AjaxStatusInterface code = AjaxStatusEnum.PARAM_INVALID;
        
        assertEquals("COMMON_400", code.getCode());
        assertEquals("参数无效：{0}", code.getDefaultMessage());
        assertEquals(HttpStatusCode.BAD_REQUEST, code.getHttpStatus());
        assertFalse(code.isSuccess());
        assertEquals("COMMON", code.getDomain());
        assertEquals("400", code.getCodeNumber());
    }

    @Test
    @DisplayName("AjaxStatusEnum - NOT_FOUND")
    void testCommonAjaxStatus_NotFound() {
        AjaxStatusInterface code = AjaxStatusEnum.NOT_FOUND;
        
        assertEquals("COMMON_404", code.getCode());
        assertEquals("资源不存在", code.getDefaultMessage());
        assertEquals(HttpStatusCode.NOT_FOUND, code.getHttpStatus());
        assertFalse(code.isSuccess());
    }

    @Test
    @DisplayName("AjaxStatusEnum - INTERNAL_ERROR")
    void testCommonAjaxStatus_InternalError() {
        AjaxStatusInterface code = AjaxStatusEnum.INTERNAL_ERROR;
        
        assertEquals("COMMON_500", code.getCode());
        assertEquals("系统内部错误", code.getDefaultMessage());
        assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR, code.getHttpStatus());
        assertFalse(code.isSuccess());
    }

    // ==================== AjaxStatusRegistry 测试 ====================

    @Test
    @DisplayName("register() - 注册单个业务状态码")
    void testRegister_Single() {
        registry.register(AjaxStatusEnum.SUCCESS);
        
        assertTrue(registry.contains("COMMON_000"));
        assertEquals(AjaxStatusEnum.SUCCESS, registry.getByCode("COMMON_000"));
    }

    @Test
    @DisplayName("registerAll() - 批量注册枚举")
    void testRegisterAll() {
        registry.registerAll(AjaxStatusEnum.class);
        
        assertTrue(registry.contains("COMMON_000"));
        assertTrue(registry.contains("COMMON_400"));
        assertTrue(registry.contains("COMMON_404"));
        assertTrue(registry.contains("COMMON_500"));
        assertEquals(13, registry.size());
    }

    @Test
    @DisplayName("init() - 初始化注册通用状态码")
    void testInit() {
        registry.init();
        
        assertTrue(registry.contains("COMMON_000"));
        assertTrue(registry.contains("COMMON_400"));
        assertTrue(registry.contains("COMMON_404"));
        assertTrue(registry.contains("COMMON_500"));
    }

    @Test
    @DisplayName("getByCode() - 查询已注册的状态码")
    void testGetByCode_Found() {
        registry.register(AjaxStatusEnum.SUCCESS);
        
        AjaxStatusInterface result = registry.getByCode("COMMON_000");
        assertNotNull(result);
        assertEquals(AjaxStatusEnum.SUCCESS, result);
    }

    @Test
    @DisplayName("getByCode() - 查询未注册的状态码")
    void testGetByCode_NotFound() {
        AjaxStatusInterface result = registry.getByCode("UNKNOWN_999");
        assertNull(result);
    }

    @Test
    @DisplayName("contains() - 检查是否已注册")
    void testContains() {
        registry.register(AjaxStatusEnum.SUCCESS);
        
        assertTrue(registry.contains("COMMON_000"));
        assertFalse(registry.contains("UNKNOWN_999"));
    }

    @Test
    @DisplayName("size() - 获取注册数量")
    void testSize() {
        assertEquals(0, registry.size());
        
        registry.register(AjaxStatusEnum.SUCCESS);
        assertEquals(1, registry.size());
        
        registry.register(AjaxStatusEnum.NOT_FOUND);
        assertEquals(2, registry.size());
    }

    @Test
    @DisplayName("clear() - 清空注册表")
    void testClear() {
        registry.registerAll(AjaxStatusEnum.class);
        assertTrue(registry.size() > 0);
        
        registry.clear();
        assertEquals(0, registry.size());
    }

    @Test
    @DisplayName("getAll() - 获取所有注册的状态码")
    void testGetAll() {
        registry.register(AjaxStatusEnum.SUCCESS);
        registry.register(AjaxStatusEnum.NOT_FOUND);
        
        var all = registry.getAll();
        assertEquals(2, all.size());
        assertTrue(all.containsKey("COMMON_000"));
        assertTrue(all.containsKey("COMMON_404"));
    }

    // ==================== 异常处理测试 ====================

    @Test
    @DisplayName("register(null) - 抛出异常")
    void testRegister_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(null);
        });
    }

    @Test
    @DisplayName("registerAll(null) - 抛出异常")
    void testRegisterAll_Null() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.registerAll(null);
        });
    }

    // ==================== AjaxStatusInterface 默认方法测试 ====================

    @Test
    @DisplayName("getDomain() - 提取业务域")
    void testGetDomain() {
        assertEquals("COMMON", AjaxStatusEnum.SUCCESS.getDomain());
        assertEquals("COMMON", AjaxStatusEnum.NOT_FOUND.getDomain());
    }

    @Test
    @DisplayName("getCodeNumber() - 提取数字部分")
    void testGetCodeNumber() {
        assertEquals("000", AjaxStatusEnum.SUCCESS.getCodeNumber());
        assertEquals("400", AjaxStatusEnum.PARAM_INVALID.getCodeNumber());
        assertEquals("404", AjaxStatusEnum.NOT_FOUND.getCodeNumber());
        assertEquals("500", AjaxStatusEnum.INTERNAL_ERROR.getCodeNumber());
    }

    @Test
    @DisplayName("isSuccess() - 基于 HTTP 状态码判断")
    void testIsSuccess() {
        assertTrue(AjaxStatusEnum.SUCCESS.isSuccess());
        assertFalse(AjaxStatusEnum.PARAM_INVALID.isSuccess());
        assertFalse(AjaxStatusEnum.NOT_FOUND.isSuccess());
        assertFalse(AjaxStatusEnum.INTERNAL_ERROR.isSuccess());
    }

    // ==================== getPrefix() 测试 ====================

    @Test
    @DisplayName("getPrefix() - 默认实现与 getDomain() 一致")
    void testGetPrefix_Default() {
        assertEquals("COMMON", AjaxStatusEnum.SUCCESS.getPrefix());
        assertEquals("COMMON", AjaxStatusEnum.NOT_FOUND.getPrefix());
        assertEquals(AjaxStatusEnum.SUCCESS.getDomain(), AjaxStatusEnum.SUCCESS.getPrefix());
    }

    @Test
    @DisplayName("AjaxStatusEnum.PrefixHolder.PREFIX 常量值正确")
    void testCommonPrefixConstant() {
        assertEquals("COMMON", AjaxStatusEnum.PrefixHolder.PREFIX);
    }

    // ==================== AjaxStatusRegistry 前缀查询测试 ====================

    @Test
    @DisplayName("getByPrefix() - 按前缀查询状态码")
    void testGetByPrefix() {
        registry.init();

        var commonCodes = registry.getByPrefix("COMMON");
        assertEquals(13, commonCodes.size());
    }

    @Test
    @DisplayName("getByPrefix() - 未注册的前缀返回空列表")
    void testGetByPrefix_Empty() {
        registry.init();

        var result = registry.getByPrefix("UNKNOWN");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getByPrefix(null) - 返回空列表")
    void testGetByPrefix_Null() {
        var result = registry.getByPrefix(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("containsPrefix() - 检查前缀是否存在")
    void testContainsPrefix() {
        registry.init();

        assertTrue(registry.containsPrefix("COMMON"));
        assertFalse(registry.containsPrefix("USER"));
    }
}
