package cc.hongz.ztools.response.interceptor;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.custom.CustomResponse;
import cc.hongz.ztools.response.standard.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 响应拦截辅助工具.
 * <p>
 * 提供响应类型识别、排除规则判断等静态方法，供 {@link ResponseInterceptor} 与单元测试复用。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public final class ResponseInterceptSupport {

    private static final List<String> STREAMING_CLASS_NAMES = List.of(
            "org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody",
            "org.springframework.web.servlet.mvc.method.annotation.SseEmitter",
            "org.springframework.core.io.Resource",
            "reactor.core.publisher.Flux",
            "reactor.core.publisher.Mono"
    );

    private ResponseInterceptSupport() {
    }

    /**
     * 判断是否为已标准化的响应类型（直接放行）.
     *
     * @param type 响应类型
     * @return 标准响应返回 true
     */
    public static boolean isStandardResponse(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return AjaxResult.class.isAssignableFrom(type)
                || Result.class.isAssignableFrom(type)
                || ProblemDetail.class.isAssignableFrom(type)
                || ResponseEntity.class.isAssignableFrom(type)
                || isErrorResponse(type);
    }

    /**
     * 判断是否为项目选定的目标响应格式.
     *
     * @param type   响应类型
     * @param style  响应风格
     * @return 匹配目标格式返回 true
     */
    public static boolean isTargetResponse(final Class<?> type, final InterceptorProperties.ResponseStyle style) {
        if (type == null || style == null) {
            return false;
        }
        return switch (style) {
            case AJAX_RESULT -> AjaxResult.class.isAssignableFrom(type);
            case CUSTOM -> CustomResponse.class.isAssignableFrom(type);
        };
    }

    /**
     * 判断是否为流式或二进制响应（不应包装）.
     *
     * @param type 响应类型
     * @return 流式响应返回 true
     */
    public static boolean isStreamingResponse(final Class<?> type) {
        if (type == null) {
            return false;
        }
        if (InputStream.class.isAssignableFrom(type) || byte[].class.isAssignableFrom(type)) {
            return true;
        }
        for (String className : STREAMING_CLASS_NAMES) {
            if (isAssignableFromOptional(type, className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为 Spring/Jakarta 框架内置类型.
     *
     * @param type 响应类型
     * @return 框架类型返回 true
     */
    public static boolean isFrameworkType(final Class<?> type) {
        if (type == null || type.getPackage() == null) {
            return false;
        }
        final String packageName = type.getPackage().getName();
        return packageName.startsWith("org.springframework")
                || packageName.startsWith("javax.")
                || packageName.startsWith("jakarta.");
    }

    /**
     * 判断类型是否在排除列表中.
     *
     * @param type       响应类型
     * @param properties 拦截器配置
     * @return 应排除返回 true
     */
    public static boolean isExcludedType(final Class<?> type, final InterceptorProperties properties) {
        if (type == null || properties == null) {
            return false;
        }
        final String typeName = type.getName();
        if (properties.getExcludeClasses() != null && properties.getExcludeClasses().contains(typeName)) {
            return true;
        }
        if (type.getPackage() == null || properties.getExcludePackages() == null) {
            return false;
        }
        final String packageName = type.getPackage().getName();
        for (String prefix : properties.getExcludePackages()) {
            if (prefix != null && (packageName.startsWith(prefix) || packageName.equals(prefix))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断 Controller 返回类型是否需要拦截.
     *
     * @param type       返回类型
     * @param properties 拦截器配置
     * @return 需要拦截返回 true
     */
    public static boolean shouldInterceptReturnType(final Class<?> type, final InterceptorProperties properties) {
        if (type == null || Void.TYPE.equals(type) || Void.class.equals(type)) {
            return false;
        }
        if (isStandardResponse(type) || isStreamingResponse(type) || isExcludedType(type, properties)) {
            return false;
        }
        if (InterceptorProperties.ResponseStyle.CUSTOM.equals(properties.getResponseStyle())
                && CustomResponse.class.isAssignableFrom(type)) {
            return false;
        }
        if (InterceptorProperties.ResponseStyle.AJAX_RESULT.equals(properties.getResponseStyle())
                && AjaxResult.class.isAssignableFrom(type)) {
            return false;
        }
        return true;
    }

    /**
     * 判断 Map 是否已包含 AJAX 风格字段.
     *
     * @param map Map 响应
     * @return 包含 code 与 msg 返回 true
     */
    public static boolean isAjaxLikeMap(final Map<?, ?> map) {
        return map != null && map.containsKey("code") && map.containsKey("msg");
    }

    /**
     * 判断集合类型（List/Set 等）.
     *
     * @param type 类型
     * @return 集合类型返回 true
     */
    public static boolean isCollectionType(final Class<?> type) {
        return type != null && Collection.class.isAssignableFrom(type) && !Map.class.isAssignableFrom(type);
    }

    private static boolean isErrorResponse(final Class<?> type) {
        return isAssignableFromOptional(type, "org.springframework.web.ErrorResponse");
    }

    private static boolean isAssignableFromOptional(final Class<?> type, final String className) {
        try {
            final Class<?> candidate = Class.forName(className);
            return candidate.isAssignableFrom(type);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
