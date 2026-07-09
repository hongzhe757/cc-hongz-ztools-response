package cc.hongz.ztools.response.exception;

import cc.hongz.ztools.response.ajax.AjaxStatusEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BusinessException 单元测试.
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
class BusinessExceptionTest {

    @Test
    void testConstructorWithStatusOnly() {
        BusinessException exception = new BusinessException(AjaxStatusEnum.NOT_FOUND);
        
        assertEquals(AjaxStatusEnum.NOT_FOUND, exception.getStatus());
        assertEquals("COMMON_404", exception.getCode());
        assertEquals("资源不存在", exception.getDefaultMessage());
        assertEquals("资源不存在", exception.getMessage());
        assertNotNull(exception.getArgs());
        assertEquals(0, exception.getArgs().length);
    }

    @Test
    void testConstructorWithSingleArg() {
        // PARAM_INVALID 有 {0} 占位符
        BusinessException exception = new BusinessException(AjaxStatusEnum.PARAM_INVALID, "username");
        
        assertEquals(AjaxStatusEnum.PARAM_INVALID, exception.getStatus());
        assertEquals("COMMON_400", exception.getCode());
        assertEquals("参数无效：{0}", exception.getDefaultMessage());
        assertEquals("参数无效：username", exception.getMessage());
        assertArrayEquals(new Object[]{"username"}, exception.getArgs());
    }

    @Test
    void testConstructorWithMultipleArgs() {
        BusinessException exception = new BusinessException(
                AjaxStatusEnum.PARAM_INVALID, "username", "password");
        
        assertEquals(AjaxStatusEnum.PARAM_INVALID, exception.getStatus());
        assertEquals("COMMON_400", exception.getCode());
        assertEquals("参数无效：{0}", exception.getDefaultMessage());
        // 只替换第一个占位符
        assertEquals("参数无效：username", exception.getMessage());
        assertArrayEquals(new Object[]{"username", "password"}, exception.getArgs());
    }

    @Test
    void testConstructorWithCause() {
        RuntimeException cause = new RuntimeException("Original error");
        BusinessException exception = new BusinessException(
                AjaxStatusEnum.INTERNAL_ERROR, cause);
        
        assertEquals(AjaxStatusEnum.INTERNAL_ERROR, exception.getStatus());
        assertEquals("系统内部错误", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCauseAndArgs() {
        RuntimeException cause = new RuntimeException("Original error");
        BusinessException exception = new BusinessException(
                AjaxStatusEnum.SERVICE_UNAVAILABLE, cause, "Database");
        
        assertEquals(AjaxStatusEnum.SERVICE_UNAVAILABLE, exception.getStatus());
        assertEquals("服务暂不可用，请稍后重试", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertArrayEquals(new Object[]{"Database"}, exception.getArgs());
    }

    @Test
    void testNullStatus() {
        BusinessException exception = new BusinessException(null);
        
        assertNull(exception.getStatus());
        assertEquals("Unknown business error", exception.getMessage());
    }

    @Test
    void testNullArgs() {
        BusinessException exception = new BusinessException(AjaxStatusEnum.SUCCESS, (Object[]) null);
        
        assertEquals(AjaxStatusEnum.SUCCESS, exception.getStatus());
        assertEquals("操作成功", exception.getMessage());
        assertNull(exception.getArgs());
    }

    @Test
    void testEmptyArgs() {
        BusinessException exception = new BusinessException(AjaxStatusEnum.SUCCESS);
        
        assertEquals(AjaxStatusEnum.SUCCESS, exception.getStatus());
        assertEquals("操作成功", exception.getMessage());
        assertNotNull(exception.getArgs());
        assertEquals(0, exception.getArgs().length);
    }
}
