package cc.hongz.ztools.response.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 跳过响应拦截注解.
 * <p>
 * 标注在 Controller 方法或类上，表示该响应不需要被 {@link ResponseInterceptor} 处理。
 * 适用于文件下载、流式响应、SSE 等场景。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SkipResponseIntercept {

    /**
     * 跳过原因说明（可选）.
     *
     * @return 说明文本
     */
    String value() default "";
}
