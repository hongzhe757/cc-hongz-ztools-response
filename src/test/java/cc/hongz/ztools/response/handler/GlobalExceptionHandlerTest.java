package cc.hongz.ztools.response.handler;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.ajax.AjaxStatusEnum;
import cc.hongz.ztools.response.exception.BusinessException;
import cc.hongz.ztools.response.i18n.I18nResponseBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link GlobalExceptionHandler} 单元测试.
 */
@DisplayName("GlobalExceptionHandler 测试")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private I18nResponseBuilder responseBuilder;

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        responseBuilder = new I18nResponseBuilder(messageSource);
        handler = new GlobalExceptionHandler(responseBuilder);
    }

    @Test
    @DisplayName("BusinessException - 英文国际化消息")
    void testHandleBusinessExceptionEnglish() {
        LocaleContextHolder.setLocale(Locale.US);
        BusinessException ex = new BusinessException(AjaxStatusEnum.PARAM_INVALID, "username");

        ResponseEntity<AjaxResult> response = handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid parameter: username", response.getBody().getMsg());
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    @DisplayName("BusinessException - 返回对应 HTTP 状态与 AjaxResult")
    void testHandleBusinessException() {
        BusinessException ex = new BusinessException(AjaxStatusEnum.PARAM_INVALID, "email");

        ResponseEntity<AjaxResult> response = handler.handleBusinessException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("COMMON_400", response.getBody().getCode());
        assertTrue(response.getBody().getMsg().contains("email"));
    }

    @Test
    @DisplayName("MissingServletRequestParameterException - 400")
    void testHandleMissingParameter() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("userId", "String");

        ResponseEntity<AjaxResult> response = handler.handleMissingServletRequestParameterException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMMON_400", response.getBody().getCode());
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchException - 400")
    void testHandleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "id", null, new IllegalArgumentException("bad type"));

        ResponseEntity<AjaxResult> response = handler.handleMethodArgumentTypeMismatchException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMMON_400", response.getBody().getCode());
        assertTrue(response.getBody().getMsg().contains("id"));
        assertTrue(response.getBody().getMsg().contains("Integer"));
    }

    @Test
    @DisplayName("ConstraintViolationException - 400 并聚合消息")
    void testHandleConstraintViolation() {
        ConstraintViolation<?> violation = new SimpleConstraintViolation("must not be blank");
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));

        ResponseEntity<AjaxResult> response = handler.handleConstraintViolationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("COMMON_400", response.getBody().getCode());
        assertTrue(response.getBody().getMsg().contains("must not be blank"));
    }

    @Test
    @DisplayName("Exception - 500 内部错误")
    void testHandleGenericException() {
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        ResponseEntity<AjaxResult> response = handler.handleException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("COMMON_500", response.getBody().getCode());
        assertEquals(AjaxStatusEnum.INTERNAL_ERROR.getDefaultMessage(), response.getBody().getMsg());
        LocaleContextHolder.resetLocaleContext();
    }

    /**
     * 最小 ConstraintViolation 实现，用于单元测试.
     */
    private static final class SimpleConstraintViolation implements ConstraintViolation<Object> {

        private final String message;

        private SimpleConstraintViolation(final String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return message;
        }

        @Override
        public Object getRootBean() {
            return null;
        }

        @Override
        public Class<Object> getRootBeanClass() {
            return Object.class;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public jakarta.validation.metadata.ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(final Class<U> type) {
            return null;
        }
    }
}
