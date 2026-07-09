package cc.hongz.ztools.response.custom;

import cc.hongz.ztools.response.ResponseConversionStrategy;
import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.standard.AliResponse;
import cc.hongz.ztools.response.standard.TencentResponse;

/**
 * 自定义响应转换器 - 提供 CustomResponse 与其他响应结构的互转功能.
 * <p>
 * 本类展示了如何为自定义响应结构实现转换逻辑，可以作为扩展其他自定义响应的参考模板。
 * </p>
 *
 * <h3>支持的转换方向：</h3>
 * <ul>
 *   <li>AjaxResult ↔ CustomResponse</li>
 *   <li>Result ↔ CustomResponse</li>
 *   <li>AliResponse ↔ CustomResponse</li>
 *   <li>TencentResponse ↔ CustomResponse</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. AjaxResult → CustomResponse
 * AjaxResult ajax = AjaxResult.success(user);
 * CustomResponse<User> custom = CustomResponseConverter.fromAjaxResult(ajax);
 *
 * // 2. CustomResponse → AjaxResult
 * CustomResponse<Order> customResp = CustomResponse.success(order);
 * AjaxResult ajaxResult = CustomResponseConverter.toAjaxResult(customResp);
 *
 * // 3. 注册到工厂（应用启动时）
 * CustomResponseConverter.registerAll();
 *
 * // 4. 通过工厂转换
 * CustomResponse<User> custom = ResponseConverterFactory.convert(ajax, CustomResponse.class);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see CustomResponse
 * @see cc.hongz.ztools.response.ResponseConverterFactory
 */
public final class CustomResponseConverter {

    private CustomResponseConverter() {
        // 工具类，禁止实例化
    }

    // ==================== 转换为 CustomResponse ====================

    /**
     * 将 AjaxResult 转换为 CustomResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return CustomResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> CustomResponse<T> fromAjaxResult(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return CustomResponse.error(500, "响应对象为空");
        }

        int code;
        try {
            code = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            code = ajaxResult.isSuccess() ? 200 : 500;
        }

        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(code);
        response.setMessage(ajaxResult.getMsg());
        response.setData((T) ajaxResult.getData());
        response.setTraceId(generateTraceId());

        return response;
    }

    /**
     * 将 Result 转换为 CustomResponse.
     *
     * @param result Result 对象
     * @param <T>    数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> fromResult(final Result<T> result) {
        if (result == null) {
            return CustomResponse.error(500, "响应对象为空");
        }

        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        response.setData(result.getData());
        response.setTraceId(generateTraceId());

        return response;
    }

    /**
     * 将 AliResponse 转换为 CustomResponse.
     *
     * @param aliResponse AliResponse 对象
     * @param <T>         数据类型
     * @return CustomResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> CustomResponse<T> fromAliResponse(final AliResponse<T> aliResponse) {
        if (aliResponse == null) {
            return CustomResponse.error(500, "响应对象为空");
        }

        int code;
        try {
            code = Integer.parseInt(aliResponse.getErrorCode());
        } catch (NumberFormatException e) {
            code = aliResponse.isSuccess() ? 200 : 500;
        }

        T data = null;
        if (aliResponse.getContent() != null) {
            data = aliResponse.getContent().getData();
        }

        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(code);
        response.setMessage(aliResponse.getErrorMsg());
        response.setData(data);
        response.setTraceId(generateTraceId());

        return response;
    }

    /**
     * 将 TencentResponse 转换为 CustomResponse.
     *
     * @param tencentResponse TencentResponse 对象
     * @param <T>             数据类型
     * @return CustomResponse
     */
    public static <T> CustomResponse<T> fromTencentResponse(final TencentResponse<T> tencentResponse) {
        if (tencentResponse == null) {
            return CustomResponse.error(500, "响应对象为空");
        }

        CustomResponse<T> response = new CustomResponse<>();
        response.setCode(tencentResponse.getRet());
        response.setMessage(tencentResponse.getMsg());
        response.setData(tencentResponse.getData());
        response.setTraceId(generateTraceId());

        return response;
    }

    // ==================== CustomResponse 转换为其他格式 ====================

    /**
     * 将 CustomResponse 转换为 AjaxResult.
     *
     * @param customResponse CustomResponse 对象
     * @param <T>            数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final CustomResponse<T> customResponse) {
        if (customResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }

        String codeStr = String.valueOf(customResponse.getCode());
        if (customResponse.isSuccess()) {
            return AjaxResult.success(codeStr, customResponse.getMessage(), customResponse.getData());
        } else {
            return AjaxResult.error(codeStr, customResponse.getMessage(), customResponse.getData());
        }
    }

    /**
     * 将 CustomResponse 转换为 Result.
     *
     * @param customResponse CustomResponse 对象
     * @param <T>            数据类型
     * @return Result
     */
    public static <T> Result<T> toResult(final CustomResponse<T> customResponse) {
        if (customResponse == null) {
            return Result.failure(500, "响应对象为空");
        }

        if (customResponse.isSuccess()) {
            return Result.success(
                    customResponse.getCode(),
                    customResponse.getMessage(),
                    customResponse.getData()
            );
        } else {
            return Result.failure(
                    customResponse.getCode(),
                    customResponse.getMessage()
            );
        }
    }

    /**
     * 将 CustomResponse 转换为 AliResponse.
     *
     * @param customResponse CustomResponse 对象
     * @param <T>            数据类型
     * @return AliResponse
     */
    public static <T> AliResponse<T> toAliResponse(final CustomResponse<T> customResponse) {
        if (customResponse == null) {
            return AliResponse.error("500", "响应对象为空");
        }

        String errorCode = String.valueOf(customResponse.getCode());
        if (customResponse.isSuccess()) {
            AliResponse.ContentWrapper<T> content = new AliResponse.ContentWrapper<>();
            content.setData(customResponse.getData());
            AliResponse<T> aliResponse = new AliResponse<>();
            aliResponse.setSuccess(true);
            aliResponse.setContent(content);
            return aliResponse;
        } else {
            return AliResponse.error(errorCode, customResponse.getMessage());
        }
    }

    /**
     * 将 CustomResponse 转换为 TencentResponse.
     *
     * @param customResponse CustomResponse 对象
     * @param <T>            数据类型
     * @return TencentResponse
     */
    public static <T> TencentResponse<T> toTencentResponse(final CustomResponse<T> customResponse) {
        if (customResponse == null) {
            return TencentResponse.error(-1, "响应对象为空");
        }

        if (customResponse.isSuccess()) {
            return TencentResponse.success(customResponse.getData());
        } else {
            return TencentResponse.error(customResponse.getCode(), customResponse.getMessage());
        }
    }

    // ==================== 策略模式支持 ====================

    /**
     * 创建 AjaxResult → CustomResponse 的转换策略.
     *
     * @param <T> 数据类型
     * @return 转换策略
     */
    public static <T> ResponseConversionStrategy<AjaxResult, CustomResponse<T>> ajaxToCustomStrategy() {
        return CustomResponseConverter::fromAjaxResult;
    }

    /**
     * 创建 CustomResponse → AjaxResult 的转换策略.
     *
     * @param <T> 数据类型
     * @return 转换策略
     */
    public static <T> ResponseConversionStrategy<CustomResponse<T>, AjaxResult> customToAjaxStrategy() {
        return CustomResponseConverter::toAjaxResult;
    }

    /**
     * 创建 Result → CustomResponse 的转换策略.
     *
     * @param <T> 数据类型
     * @return 转换策略
     */
    public static <T> ResponseConversionStrategy<Result<T>, CustomResponse<T>> resultToCustomStrategy() {
        return CustomResponseConverter::fromResult;
    }

    /**
     * 创建 CustomResponse → Result 的转换策略.
     *
     * @param <T> 数据类型
     * @return 转换策略
     */
    public static <T> ResponseConversionStrategy<CustomResponse<T>, Result<T>> customToResultStrategy() {
        return CustomResponseConverter::toResult;
    }

    // ==================== 工厂注册 ====================

    /**
     * 注册所有 CustomResponse 相关的转换策略到工厂.
     * <p>
     * 建议在应用启动时调用此方法，以便通过 ResponseConverterFactory 进行统一转换。
     * </p>
     *
     * <h3>使用示例：</h3>
     * <pre>{@code
     * // Spring Boot 应用中
     * @Configuration
     * public class ResponseConfig {
     *     @PostConstruct
     *     public void init() {
     *         CustomResponseConverter.registerAll();
     *     }
     * }
     * }</pre>
     */
    public static void registerAll() {
        // AjaxResult ↔ CustomResponse
        cc.hongz.ztools.response.ResponseConverterFactory.register(
                AjaxResult.class,
                CustomResponse.class,
                CustomResponseConverter::fromAjaxResult
        );

        cc.hongz.ztools.response.ResponseConverterFactory.register(
                CustomResponse.class,
                AjaxResult.class,
                CustomResponseConverter::toAjaxResult
        );

        // Result ↔ CustomResponse
        cc.hongz.ztools.response.ResponseConverterFactory.register(
                Result.class,
                CustomResponse.class,
                CustomResponseConverter::fromResult
        );

        cc.hongz.ztools.response.ResponseConverterFactory.register(
                CustomResponse.class,
                Result.class,
                CustomResponseConverter::toResult
        );

        // AliResponse ↔ CustomResponse
        cc.hongz.ztools.response.ResponseConverterFactory.register(
                AliResponse.class,
                CustomResponse.class,
                CustomResponseConverter::fromAliResponse
        );

        cc.hongz.ztools.response.ResponseConverterFactory.register(
                CustomResponse.class,
                AliResponse.class,
                CustomResponseConverter::toAliResponse
        );

        // TencentResponse ↔ CustomResponse
        cc.hongz.ztools.response.ResponseConverterFactory.register(
                TencentResponse.class,
                CustomResponse.class,
                CustomResponseConverter::fromTencentResponse
        );

        cc.hongz.ztools.response.ResponseConverterFactory.register(
                CustomResponse.class,
                TencentResponse.class,
                CustomResponseConverter::toTencentResponse
        );
    }

    // ==================== 辅助方法 ====================

    /**
     * 生成链路追踪ID.
     * <p>
     * 实际项目中可以从 MDC、请求头或分布式追踪系统中获取
     * </p>
     *
     * @return traceId
     */
    private static String generateTraceId() {
        // TODO: 从 MDC 或请求上下文中获取真实的 traceId
        // return MDC.get("traceId");
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
