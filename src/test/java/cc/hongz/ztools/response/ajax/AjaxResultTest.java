package cc.hongz.ztools.response.ajax;

import cc.hongz.ztools.response.http.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link AjaxResult} 单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@DisplayName("AjaxResult 测试")
class AjaxResultTest {

    // ==================== 成功响应测试 ====================

    @Test
    @DisplayName("success() - 默认成功")
    void testSuccess_Default() {
        AjaxResult result = AjaxResult.success();
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        // data 为 null 时保持 null（泛型化后不再用 new Object() 占位）
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(msg) - 自定义消息")
    void testSuccess_WithMessage() {
        String msg = "自定义成功消息";
        AjaxResult result = AjaxResult.success(msg);
        
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals(msg, result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(data) - 带数据")
    void testSuccess_WithData() {
        // 使用非String对象来确保调用 success(Object data) 而不是 success(String msg)
        Integer data = 12345;
        AjaxResult result = AjaxResult.success(data);
        
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(msg, data) - 自定义消息和数据")
    void testSuccess_WithMessageAndData() {
        String msg = "查询成功";
        Map<String, Object> data = Map.of("id", 1, "name", "test");
        
        AjaxResult result = AjaxResult.success(msg, data);
        
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals(msg, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(code, msg, data) - 完整参数")
    void testSuccess_FullParams() {
        String code = "201";
        String msg = "已创建";
        String data = "新资源ID";
        
        AjaxResult result = AjaxResult.success(code, msg, data);
        
        // isSuccess() 只检查 code 是否为 "200"、"0" 或 "success"
        assertFalse(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals(msg, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(code, msg, null) - null 数据保持 null")
    void testSuccess_NullDataReplaced() {
        AjaxResult result = AjaxResult.success("200", "成功", null);

        assertTrue(result.isSuccess());
        assertNull(result.getData());
    }

    // ==================== 失败响应测试 ====================

    @Test
    @DisplayName("error() - 默认错误")
    void testError_Default() {
        AjaxResult result = AjaxResult.error();
        
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("500", result.getCode());
        assertEquals("操作失败", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("error(msg) - 自定义消息")
    void testError_WithMessage() {
        String msg = "参数校验失败";
        AjaxResult result = AjaxResult.error(msg);
        
        assertFalse(result.isSuccess());
        assertEquals("500", result.getCode());
        assertEquals(msg, result.getMsg());
    }

    @Test
    @DisplayName("error(data) - 带数据")
    void testError_WithData() {
        // 使用非String对象来确保调用 error(Object data) 而不是 error(String msg)
        Integer data = 999;
        AjaxResult result = AjaxResult.error(data);
        
        assertFalse(result.isSuccess());
        assertEquals("500", result.getCode());
        assertEquals("操作失败", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("error(msg, data) - 自定义消息和数据")
    void testError_WithMessageAndData() {
        String msg = "权限不足";
        Map<String, Object> data = Map.of("requiredRole", "ADMIN");
        
        AjaxResult result = AjaxResult.error(msg, data);
        
        assertFalse(result.isSuccess());
        assertEquals("500", result.getCode());
        assertEquals(msg, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("error(code, msg, data) - 完整参数")
    void testError_FullParams() {
        String code = "403";
        String msg = "禁止访问";
        String data = "需要VIP权限";
        
        AjaxResult result = AjaxResult.error(code, msg, data);
        
        assertFalse(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals(msg, result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("error(code, msg, no, data) - 占位符替换")
    void testError_WithPlaceholder() {
        String code = "404";
        String msg = "用户N不存在";
        String no = "12345";
        String data = "请检查ID";
        
        AjaxResult result = AjaxResult.error(code, msg, no, data);
        
        assertFalse(result.isSuccess());
        assertEquals(code, result.getCode());
        assertEquals("用户12345不存在", result.getMsg());
        assertEquals(data, result.getData());
    }

    // ==================== isSuccess() 判断逻辑测试 ====================

    @Test
    @DisplayName("isSuccess() - code=200 为成功")
    void testIsSuccess_Code200() {
        AjaxResult result = new AjaxResult("200", "成功", null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() - code=0 为成功")
    void testIsSuccess_Code0() {
        AjaxResult result = new AjaxResult("0", "成功", null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() - code=success (忽略大小写) 为成功")
    void testIsSuccess_CodeSuccessIgnoreCase() {
        AjaxResult result1 = new AjaxResult("success", "成功", null);
        AjaxResult result2 = new AjaxResult("SUCCESS", "成功", null);
        AjaxResult result3 = new AjaxResult("Success", "成功", null);
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());
    }

    @Test
    @DisplayName("isSuccess() - 其他状态码为失败")
    void testIsSuccess_OtherCodes() {
        assertFalse(new AjaxResult("201", "已创建", null).isSuccess());
        assertFalse(new AjaxResult("400", "错误请求", null).isSuccess());
        assertFalse(new AjaxResult("404", "未找到", null).isSuccess());
        assertFalse(new AjaxResult("500", "服务器错误", null).isSuccess());
        assertFalse(new AjaxResult("", "空状态码", null).isSuccess());
        assertFalse(new AjaxResult(null, "null状态码", null).isSuccess());
    }

    // ==================== Getter/Setter 测试 ====================

    @Test
    @DisplayName("Getter/Setter 正常工作")
    void testGettersAndSetters() {
        AjaxResult result = new AjaxResult();
        result.setCode("201");
        result.setMsg("已创建");
        result.setData("新资源");
        
        assertEquals("201", result.getCode());
        assertEquals("已创建", result.getMsg());
        assertEquals("新资源", result.getData());
    }

    // ==================== 边界值测试 ====================

    @Test
    @DisplayName("复杂对象作为数据")
    void testComplexDataObject() {
        Map<String, Object> complexData = Map.of(
            "users", java.util.List.of("user1", "user2"),
            "total", 2,
            "page", 1
        );
        
        AjaxResult result = AjaxResult.success(complexData);
        
        assertTrue(result.isSuccess());
        assertEquals(complexData, result.getData());
    }

    @Test
    @DisplayName("空字符串消息")
    void testEmptyMessage() {
        AjaxResult result = AjaxResult.success("");
        
        assertTrue(result.isSuccess());
        assertEquals("", result.getMsg());
    }

    @Test
    @DisplayName("null 消息")
    void testNullMessage() {
        AjaxResult result = AjaxResult.success((String) null, null);
        
        assertTrue(result.isSuccess());
        assertNull(result.getMsg());
    }

    // ==================== HttpStatusCode 支持测试 ====================

    @Test
    @DisplayName("success(HttpStatusCode, data) - 使用 HTTP 状态码")
    void testSuccess_WithHttpStatusCode() {
        String data = "test data";
        AjaxResult result = AjaxResult.success(HttpStatusCode.OK, data);
        
        assertTrue(result.isSuccess());
        assertEquals("200", result.getCode());
        assertEquals("OK", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(HttpStatusCode, msg, data) - 使用 HTTP 状态码和自定义消息")
    void testSuccess_WithHttpStatusCodeAndMessage() {
        String data = "created resource";
        AjaxResult result = AjaxResult.success(HttpStatusCode.CREATED, "资源已创建", data);
        
        assertFalse(result.isSuccess()); // code=201，不是 200
        assertEquals("201", result.getCode());
        assertEquals("资源已创建", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("error(HttpStatusCode, detail) - 使用 HTTP 状态码")
    void testError_WithHttpStatusCode() {
        String detail = "用户不存在";
        AjaxResult result = AjaxResult.error(HttpStatusCode.NOT_FOUND, detail);
        
        assertFalse(result.isSuccess());
        assertEquals("404", result.getCode());
        assertEquals("Not Found", result.getMsg());
        assertEquals(detail, result.getData());
    }

    @Test
    @DisplayName("error(HttpStatusCode, msg, data) - 使用 HTTP 状态码和自定义消息")
    void testError_WithHttpStatusCodeAndMessage() {
        String data = "需要管理员权限";
        AjaxResult result = AjaxResult.error(HttpStatusCode.FORBIDDEN, "权限不足", data);
        
        assertFalse(result.isSuccess());
        assertEquals("403", result.getCode());
        assertEquals("权限不足", result.getMsg());
        assertEquals(data, result.getData());
    }

    // ==================== BusinessCode 支持测试 ====================

    @Test
    @DisplayName("success(BusinessCode, data) - 使用业务状态码")
    void testSuccess_WithBusinessCode() {
        String data = "user data";
        AjaxResult result = AjaxResult.success(AjaxStatusEnum.SUCCESS, data);
        
        // 注意：isSuccess() 只检查 code 是否为 "200"、"0" 或 "success"
        // COMMON_000 不匹配这些值，所以返回 false
        assertFalse(result.isSuccess());
        assertEquals("COMMON_000", result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("success(BusinessCode, msg, data) - 使用业务状态码和自定义消息")
    void testSuccess_WithBusinessCodeAndMessage() {
        String data = "order data";
        AjaxResult result = AjaxResult.success(AjaxStatusEnum.SUCCESS, "订单创建成功", data);
        
        // 注意：isSuccess() 只检查 code 是否为 "200"、"0" 或 "success"
        assertFalse(result.isSuccess());
        assertEquals("COMMON_000", result.getCode());
        assertEquals("订单创建成功", result.getMsg());
        assertEquals(data, result.getData());
    }

    @Test
    @DisplayName("error(BusinessCode, detail) - 使用业务状态码")
    void testError_WithBusinessCode() {
        String detail = "ID: 12345";
        AjaxResult result = AjaxResult.error(AjaxStatusEnum.NOT_FOUND, detail);
        
        assertFalse(result.isSuccess());
        assertEquals("COMMON_404", result.getCode());
        assertEquals("资源不存在", result.getMsg());
        assertEquals(detail, result.getData());
    }

    @Test
    @DisplayName("error(BusinessCode, args...) - 参数替换")
    void testError_WithBusinessCodeAndArgs() {
        // 使用显式 Object[] 避免与 error(BusinessCode, String, Object) 重载歧义
        AjaxResult result = AjaxResult.error(AjaxStatusEnum.PARAM_INVALID, new Object[]{"username"});
        
        assertFalse(result.isSuccess());
        assertEquals("COMMON_400", result.getCode());
        // PARAM_INVALID 默认消息为 "参数无效：{0}"，替换后应为 "参数无效：username"
        assertEquals("参数无效：username", result.getMsg());
    }

    @Test
    @DisplayName("error(BusinessCode, msg, data) - 使用业务状态码和自定义消息")
    void testError_WithBusinessCodeAndMessage() {
        String data = "required roles: ADMIN";
        AjaxResult result = AjaxResult.error(AjaxStatusEnum.FORBIDDEN, "访问被拒绝", data);
        
        assertFalse(result.isSuccess());
        assertEquals("COMMON_403", result.getCode());
        assertEquals("访问被拒绝", result.getMsg());
        assertEquals(data, result.getData());
    }
}
