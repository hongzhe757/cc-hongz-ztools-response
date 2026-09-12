package cc.hongz.ztools.response.handler;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.status.AjaxStatusEnum;
import cc.hongz.ztools.response.exception.BusinessException;
import cc.hongz.ztools.response.i18n.I18nResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器.
 * <p>
 * 统一处理 Controller 层抛出的异常，转换为标准的 AjaxResult 响应。
 * 支持国际化消息，自动根据当前语言环境返回对应语言的错误信息。
 * </p>
 * <h3>支持的异常类型</h3>
 * <ul>
 *   <li>{@link BusinessException} - 业务异常</li>
 *   <li>{@link MethodArgumentNotValidException} - 参数校验失败（@Valid）</li>
 *   <li>{@link ConstraintViolationException} - 参数约束违反（@Validated）</li>
 *   <li>{@link MissingServletRequestParameterException} - 缺少请求参数</li>
 *   <li>{@link MethodArgumentTypeMismatchException} - 参数类型不匹配</li>
 *   <li>{@link NoHandlerFoundException} - 404 未找到处理器</li>
 *   <li>{@link Exception} - 其他未知异常</li>
 * </ul>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 国际化响应构建器.
     */
    private final I18nResponseBuilder responseBuilder;

    /**
     * 创建全局异常处理器.
     *
     * @param responseBuilder 国际化响应构建器
     */
    public GlobalExceptionHandler(final I18nResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    /**
     * 处理业务异常.
     *
     * @param exception 业务异常
     * @return AjaxResult
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<AjaxResult> handleBusinessException(final BusinessException exception) {
        log.warn("Business exception: code={}, message={}", 
                exception.getCode(), exception.getMessage());
        
        AjaxResult result = responseBuilder.fromException(exception);
        HttpStatus httpStatus = mapToHttpStatus(exception.getStatus().getHttpStatus());
        
        return new ResponseEntity<>(result, httpStatus);
    }

    /**
     * 处理参数校验异常（@Valid）.
     *
     * @param exception 参数校验异常
     * @return AjaxResult
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AjaxResult> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception) {
        
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        log.warn("Validation failed: {}", errorMessage);
        
        AjaxResult result = responseBuilder.error(AjaxStatusEnum.PARAM_INVALID, errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数约束违反异常（@Validated）.
     *
     * @param exception 约束违反异常
     * @return AjaxResult
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AjaxResult> handleConstraintViolationException(
            final ConstraintViolationException exception) {
        
        String errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        
        log.warn("Constraint violation: {}", errorMessage);
        
        AjaxResult result = responseBuilder.error(AjaxStatusEnum.PARAM_INVALID, errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理缺少请求参数异常.
     *
     * @param exception 缺少参数异常
     * @return AjaxResult
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<AjaxResult> handleMissingServletRequestParameterException(
            final MissingServletRequestParameterException exception) {
        
        String paramName = exception.getParameterName();
        log.warn("Missing parameter: {}", paramName);
        
        AjaxResult result = responseBuilder.error(AjaxStatusEnum.PARAM_INVALID, paramName);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理参数类型不匹配异常.
     *
     * @param exception 类型不匹配异常
     * @return AjaxResult
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<AjaxResult> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException exception) {
        
        String paramName = exception.getName();
        String requiredType = exception.getRequiredType() != null 
                ? exception.getRequiredType().getSimpleName() 
                : "unknown";
        
        log.warn("Type mismatch for parameter '{}': expected {}", paramName, requiredType);
        
        String errorMessage = String.format("%s (expected %s)", paramName, requiredType);
        AjaxResult result = responseBuilder.error(AjaxStatusEnum.PARAM_INVALID, errorMessage);
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * 处理其他未知异常.
     *
     * @param exception 未知异常
     * @return AjaxResult
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<AjaxResult> handleException(final Exception exception) {
        log.error("Unexpected error occurred", exception);
        
        AjaxResult result = responseBuilder.error(AjaxStatusEnum.INTERNAL_ERROR);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 将 HttpStatusCode 映射为 Spring HttpStatus.
     *
     * @param httpStatusCode 自定义 HTTP 状态码
     * @return Spring HttpStatus
     */
    private HttpStatus mapToHttpStatus(final cc.hongz.ztools.status.HttpStatusCode httpStatusCode) {
        if (httpStatusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(httpStatusCode.getCode());
        } catch (IllegalArgumentException e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
