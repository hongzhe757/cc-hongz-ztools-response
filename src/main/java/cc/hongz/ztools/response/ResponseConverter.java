package cc.hongz.ztools.response;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.database.RedisResponse;
import cc.hongz.ztools.response.framework.MyBatisPlusPageResponse;
import cc.hongz.ztools.response.framework.PageHelperPageResponse;
import cc.hongz.ztools.response.generic.JsonResponse;
import cc.hongz.ztools.response.generic.MapResponse;
import cc.hongz.ztools.response.http.JdkHttpResponseWrapper;
import cc.hongz.ztools.response.common.DataListInfo;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.protocol.SseEvent;
import cc.hongz.ztools.response.protocol.WebSocketMessage;
import cc.hongz.ztools.response.reactive.ReactiveAjaxResult;
import cc.hongz.ztools.response.standard.AliResponse;
import cc.hongz.ztools.response.standard.BaiduResponse;
import cc.hongz.ztools.response.standard.GraphQLResponse;
import cc.hongz.ztools.response.standard.GrpcResponse;
import cc.hongz.ztools.response.standard.ProblemDetail;
import cc.hongz.ztools.response.standard.TencentResponse;

/**
 * 响应结构转换器 - 适配器模式（Adapter Pattern）实现.
 * <p>
 * 提供不同响应结构之间的相互转换功能，便于在异构系统间进行数据交换。
 * 本类采用了多种设计模式：
 * </p>
 *
 * <h3>涉及的设计模式：</h3>
 * <ul>
 *   <li><strong>适配器模式（Adapter Pattern）</strong>：将一种响应格式转换为另一种格式，使原本不兼容的接口能够协同工作</li>
 *   <li><strong>策略模式（Strategy Pattern）</strong>：通过 {@link ResponseConversionStrategy} 接口定义转换算法族，运行时可切换不同的转换策略</li>
 *   <li><strong>工厂方法模式（Factory Method）</strong>：通过静态工厂方法创建目标响应对象</li>
 *   <li><strong>门面模式（Facade Pattern）</strong>：提供统一的转换入口，简化复杂子系统的使用</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 基础转换 - 将 Result 转换为 AjaxResult
 * Result<User> result = Result.success(user);
 * AjaxResult ajax = ResponseConverter.toAjaxResult(result);
 *
 * // 2. 厂商风格互转
 * AliResponse<Order> aliResp = AliResponse.success(order);
 * TencentResponse<Order> txResp = ResponseConverter.aliToTencent(aliResp);
 *
 * // 3. 使用策略模式进行自定义转换
 * ResponseConversionStrategy<Result, AjaxResult> strategy = new CustomConversionStrategy();
 * AjaxResult converted = ResponseConverter.convert(result, strategy);
 *
 * // 4. 链式转换
 * AjaxResult finalResult = ResponseConverter.chain()
 *     .from(baiduResponse)
 *     .to(TencentResponse.class)
 *     .thenTo(AjaxResult.class)
 *     .execute();
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see ResponseConversionStrategy
 * @see ResponseConverterFactory
 */
@SuppressWarnings("checkstyle:MagicNumber")
public final class ResponseConverter {

    private ResponseConverter() {
        // 工具类，禁止实例化
    }

    // ==================== 转换为 AjaxResult（归一化标准）====================

    /**
     * 将 Result 转换为 AjaxResult.
     *
     * @param result Result 对象
     * @param <T>    数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final Result<T> result) {
        if (result == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (result.isSuccess()) {
            return AjaxResult.success(String.valueOf(result.getCode()), result.getMessage(), result.getData());
        } else {
            return AjaxResult.error(String.valueOf(result.getCode()), result.getMessage(), result.getData());
        }
    }

    /**
     * 将 AliResponse 转换为 AjaxResult.
     *
     * @param aliResponse AliResponse 对象
     * @param <T>         数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final AliResponse<T> aliResponse) {
        if (aliResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (aliResponse.isSuccess()) {
            Object data = aliResponse.getContent() != null ? aliResponse.getContent().getData() : null;
            return AjaxResult.success("200", "操作成功", data);
        } else {
            return AjaxResult.error(aliResponse.getErrorCode(), aliResponse.getErrorMsg());
        }
    }

    /**
     * 将 TencentResponse 转换为 AjaxResult.
     *
     * @param tencentResponse TencentResponse 对象
     * @param <T>             数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final TencentResponse<T> tencentResponse) {
        if (tencentResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (tencentResponse.isSuccess()) {
            return AjaxResult.success(String.valueOf(tencentResponse.getRet()), tencentResponse.getMsg(),
                tencentResponse.getData());
        } else {
            return AjaxResult.error(String.valueOf(tencentResponse.getRet()), tencentResponse.getMsg(),
                tencentResponse.getData());
        }
    }

    /**
     * 将 BaiduResponse 转换为 AjaxResult.
     *
     * @param baiduResponse BaiduResponse 对象
     * @param <T>           数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final BaiduResponse<T> baiduResponse) {
        if (baiduResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (baiduResponse.isSuccess()) {
            return AjaxResult.success(String.valueOf(baiduResponse.getErrno()), baiduResponse.getErrmsg(),
                baiduResponse.getData());
        } else {
            return AjaxResult.error(String.valueOf(baiduResponse.getErrno()), baiduResponse.getErrmsg(),
                baiduResponse.getData());
        }
    }

    // ==================== 转换为 Result ====================

    /**
     * 将 AjaxResult 转换为 Result.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return Result
     */
    @SuppressWarnings("unchecked")
    public static <T> Result<T> toResult(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return Result.failure(500, "响应对象为空");
        }
        int code;
        try {
            code = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            code = ajaxResult.isSuccess() ? 200 : 500;
        }
        if (ajaxResult.isSuccess()) {
            return Result.success(code, ajaxResult.getMsg(), (T) ajaxResult.getData());
        } else {
            return Result.failure(code, ajaxResult.getMsg());
        }
    }

    // ==================== 厂商风格互转 ====================

    /**
     * 将阿里风格转换为腾讯风格.
     *
     * @param aliResponse AliResponse 对象
     * @param <T>         数据类型
     * @return TencentResponse
     */
    public static <T> TencentResponse<T> aliToTencent(final AliResponse<T> aliResponse) {
        if (aliResponse == null) {
            return TencentResponse.error(-1, "响应对象为空");
        }
        if (aliResponse.isSuccess()) {
            T data = aliResponse.getContent() != null ? aliResponse.getContent().getData() : null;
            return TencentResponse.success(data);
        } else {
            int ret;
            try {
                ret = Integer.parseInt(aliResponse.getErrorCode());
            } catch (NumberFormatException e) {
                ret = -1;
            }
            return TencentResponse.error(ret, aliResponse.getErrorMsg());
        }
    }

    /**
     * 将腾讯风格转换为阿里风格.
     *
     * @param tencentResponse TencentResponse 对象
     * @param <T>             数据类型
     * @return AliResponse
     */
    public static <T> AliResponse<T> tencentToAli(final TencentResponse<T> tencentResponse) {
        if (tencentResponse == null) {
            return AliResponse.error("-1", "响应对象为空");
        }
        if (tencentResponse.isSuccess()) {
            return AliResponse.success(tencentResponse.getData());
        } else {
            return AliResponse.error(String.valueOf(tencentResponse.getRet()), tencentResponse.getMsg());
        }
    }

    /**
     * 将百度风格转换为腾讯风格.
     *
     * @param baiduResponse BaiduResponse 对象
     * @param <T>           数据类型
     * @return TencentResponse
     */
    public static <T> TencentResponse<T> baiduToTencent(final BaiduResponse<T> baiduResponse) {
        if (baiduResponse == null) {
            return TencentResponse.error(-1, "响应对象为空");
        }
        if (baiduResponse.isSuccess()) {
            return TencentResponse.success(baiduResponse.getData());
        } else {
            return TencentResponse.error(baiduResponse.getErrno(), baiduResponse.getErrmsg());
        }
    }

    // ==================== 转换为 DataListInfo ====================

    /**
     * 从 MyBatis-Plus 分页响应转换为 DataListInfo.
     *
     * @param pageResponse MyBatis-Plus 分页响应
     * @return DataListInfo
     */
    public static DataListInfo toDataListInfo(final MyBatisPlusPageResponse<?> pageResponse) {
        if (pageResponse == null) {
            return DataListInfo.empty();
        }
        return pageResponse.toDataListInfo();
    }

    /**
     * 从 PageHelper 分页响应转换为 DataListInfo.
     *
     * @param pageResponse PageHelper 分页响应
     * @return DataListInfo
     */
    public static DataListInfo toDataListInfo(final PageHelperPageResponse<?> pageResponse) {
        if (pageResponse == null) {
            return DataListInfo.empty();
        }
        return pageResponse.toDataListInfo();
    }

    // ==================== 通用转换方法 ====================

    /**
     * 判断响应是否成功（支持多种类型）.
     *
     * @param response 响应对象
     * @return 成功返回 true
     */
    public static boolean isSuccess(final Object response) {
        if (response instanceof AjaxResult) {
            return ((AjaxResult) response).isSuccess();
        } else if (response instanceof Result) {
            return ((Result<?>) response).isSuccess();
        } else if (response instanceof AliResponse) {
            return ((AliResponse<?>) response).isSuccess();
        } else if (response instanceof TencentResponse) {
            return ((TencentResponse<?>) response).isSuccess();
        } else if (response instanceof BaiduResponse) {
            return ((BaiduResponse<?>) response).isSuccess();
        }
        return false;
    }

    /**
     * 提取响应中的数据（支持多种类型）.
     *
     * @param response 响应对象
     * @param <T>      数据类型
     * @return 数据对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T extractData(final Object response) {
        if (response instanceof AjaxResult) {
            return (T) ((AjaxResult) response).getData();
        } else if (response instanceof Result) {
            return ((Result<T>) response).getData();
        } else if (response instanceof AliResponse) {
            AliResponse<T> ali = (AliResponse<T>) response;
            return ali.getContent() != null ? ali.getContent().getData() : null;
        } else if (response instanceof TencentResponse) {
            return ((TencentResponse<T>) response).getData();
        } else if (response instanceof BaiduResponse) {
            return ((BaiduResponse<T>) response).getData();
        }
        return null;
    }

    // ==================== 策略模式支持 ====================

    /**
     * 使用自定义策略进行转换.
     * <p>
     * 策略模式（Strategy Pattern）：允许在运行时选择不同的转换算法。
     * </p>
     *
     * @param source   源响应对象
     * @param strategy 转换策略
     * @param <S>      源类型
     * @param <T>      目标类型
     * @return 转换后的响应
     */
    public static <S, T> T convert(final S source, final ResponseConversionStrategy<S, T> strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("转换策略不能为空");
        }
        return strategy.convert(source);
    }

    /**
     * 创建链式转换器.
     * <p>
     * 建造者模式（Builder Pattern）：支持多步骤的复杂转换流程。
     * </p>
     *
     * @return 链式转换器构建器
     */
    public static ConversionChainBuilder chain() {
        return new ConversionChainBuilder();
    }

    // ==================== 更多转换方法 ====================

    /**
     * 将 ProblemDetail 转换为 AjaxResult.
     *
     * @param problemDetail ProblemDetail 对象
     * @return AjaxResult
     */
    public static AjaxResult toAjaxResult(final ProblemDetail problemDetail) {
        if (problemDetail == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        return AjaxResult.error(
                String.valueOf(problemDetail.getStatus()),
                problemDetail.getDetail() != null ? problemDetail.getDetail() : problemDetail.getTitle()
        );
    }

    /**
     * 将 GraphQLResponse 转换为 AjaxResult.
     *
     * @param graphqlResponse GraphQLResponse 对象
     * @param <T>             数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final GraphQLResponse<T> graphqlResponse) {
        if (graphqlResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (graphqlResponse.getData() != null && graphqlResponse.getErrors() == null) {
            return AjaxResult.success("200", "查询成功", graphqlResponse.getData());
        } else if (graphqlResponse.getErrors() != null && !graphqlResponse.getErrors().isEmpty()) {
            String errorMsg = graphqlResponse.getErrors().get(0).getMessage();
            return AjaxResult.error("400", errorMsg);
        }
        return AjaxResult.success(graphqlResponse.getData());
    }

    /**
     * 将 GrpcResponse 转换为 AjaxResult.
     *
     * @param grpcResponse GrpcResponse 对象
     * @param <T>          数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final GrpcResponse<T> grpcResponse) {
        if (grpcResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (grpcResponse.isSuccess()) {
            return AjaxResult.success(String.valueOf(grpcResponse.getCode()), "操作成功", grpcResponse.getData());
        } else {
            return AjaxResult.error(String.valueOf(grpcResponse.getCode()), grpcResponse.getMessage());
        }
    }

    /**
     * 将 HTTP 客户端包装器转换为 AjaxResult.
     *
     * @param wrapper JDK HTTP 响应包装器
     * @param <T>     数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final JdkHttpResponseWrapper<T> wrapper) {
        if (wrapper == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (wrapper.isSuccessful()) {
            return AjaxResult.success(String.valueOf(wrapper.getStatusCode()), "请求成功", wrapper.getBody());
        } else {
            return AjaxResult.error(String.valueOf(wrapper.getStatusCode()), "HTTP 错误");
        }
    }

    /**
     * 将 RedisResponse 转换为 AjaxResult.
     *
     * @param redisResponse RedisResponse 对象
     * @param <T>           数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final RedisResponse<T> redisResponse) {
        if (redisResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (redisResponse.isSuccess()) {
            return AjaxResult.success(redisResponse.getMessage(), redisResponse.getValue());
        } else {
            return AjaxResult.error(redisResponse.getMessage(), redisResponse.getMessage());
        }
    }

    /**
     * 将 WebSocketMessage 转换为 AjaxResult.
     *
     * @param wsMessage WebSocketMessage 对象
     * @param <T>       数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final WebSocketMessage<T> wsMessage) {
        if (wsMessage == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        return AjaxResult.success("200", wsMessage.getType(), wsMessage.getPayload());
    }

    /**
     * 将 SseEvent 转换为 AjaxResult.
     *
     * @param sseEvent SseEvent 对象
     * @param <T>      数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final SseEvent<T> sseEvent) {
        if (sseEvent == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        return AjaxResult.success("200", sseEvent.getEvent(), sseEvent.getData());
    }

    /**
     * 将 ReactiveAjaxResult 转换为普通 AjaxResult（阻塞获取）.
     * <p>
     * 注意：此方法会阻塞等待 Mono 完成，仅建议在非响应式环境中使用。
     * </p>
     *
     * @param reactiveResult ReactiveAjaxResult 对象
     * @param <T>            数据类型
     * @return AjaxResult
     */
    public static <T> AjaxResult toAjaxResult(final ReactiveAjaxResult<T> reactiveResult) {
        if (reactiveResult == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        // 阻塞获取结果（在非响应式环境中的兼容方案）
        return reactiveResult.toMono().block();
    }

    // ==================== 反向转换（从 AjaxResult 到其他格式）====================

    /**
     * 将 AjaxResult 转换为 AliResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return AliResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> AliResponse<T> toAliResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return AliResponse.error("-1", "响应对象为空");
        }
        if (ajaxResult.isSuccess()) {
            return AliResponse.success((T) ajaxResult.getData());
        } else {
            return AliResponse.error(ajaxResult.getCode(), ajaxResult.getMsg());
        }
    }

    /**
     * 将 AjaxResult 转换为 TencentResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return TencentResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> TencentResponse<T> toTencentResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return TencentResponse.error(-1, "响应对象为空");
        }
        int ret;
        try {
            ret = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            ret = ajaxResult.isSuccess() ? 0 : -1;
        }
        if (ajaxResult.isSuccess()) {
            return TencentResponse.success(ret, ajaxResult.getMsg(), (T) ajaxResult.getData());
        } else {
            return TencentResponse.error(ret, ajaxResult.getMsg());
        }
    }

    /**
     * 将 AjaxResult 转换为 BaiduResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return BaiduResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> BaiduResponse<T> toBaiduResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return BaiduResponse.error(-1, "响应对象为空");
        }
        int errno;
        try {
            errno = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            errno = ajaxResult.isSuccess() ? 0 : -1;
        }
        if (ajaxResult.isSuccess()) {
            return BaiduResponse.success(errno, ajaxResult.getMsg(), (T) ajaxResult.getData());
        } else {
            return BaiduResponse.error(errno, ajaxResult.getMsg());
        }
    }

    /**
     * 将 AjaxResult 转换为 ProblemDetail.
     *
     * @param ajaxResult AjaxResult 对象
     * @return ProblemDetail
     */
    public static ProblemDetail toProblemDetail(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return ProblemDetail.of(500, "Internal Server Error", "响应对象为空");
        }
        int status;
        try {
            status = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            status = ajaxResult.isSuccess() ? 200 : 500;
        }
        return ProblemDetail.of(status, ajaxResult.getMsg(), ajaxResult.getMsg());
    }

    /**
     * 将 AjaxResult 转换为 GraphQLResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return GraphQLResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> GraphQLResponse<T> toGraphQLResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return GraphQLResponse.error("响应对象为空");
        }
        if (ajaxResult.isSuccess()) {
            return GraphQLResponse.success((T) ajaxResult.getData());
        } else {
            return GraphQLResponse.error(ajaxResult.getMsg());
        }
    }

    /**
     * 将 AjaxResult 转换为 GrpcResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @param <T>        数据类型
     * @return GrpcResponse
     */
    @SuppressWarnings("unchecked")
    public static <T> GrpcResponse<T> toGrpcResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return GrpcResponse.error(GrpcResponse.STATUS_INTERNAL, "响应对象为空");
        }
        int code;
        try {
            code = Integer.parseInt(ajaxResult.getCode());
        } catch (NumberFormatException e) {
            code = ajaxResult.isSuccess() ? GrpcResponse.STATUS_OK : GrpcResponse.STATUS_INTERNAL;
        }
        if (ajaxResult.isSuccess()) {
            return GrpcResponse.success(code, (T) ajaxResult.getData());
        } else {
            return GrpcResponse.error(code, ajaxResult.getMsg());
        }
    }

    // ==================== 通用/动态响应转换 ====================

    /**
     * 将 JsonResponse 转换为 AjaxResult.
     *
     * @param jsonResponse JsonResponse 对象
     * @return AjaxResult
     */
    public static AjaxResult toAjaxResult(final JsonResponse jsonResponse) {
        if (jsonResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (jsonResponse.isSuccess()) {
            return AjaxResult.success(jsonResponse.getCode(), jsonResponse.getMsg(), jsonResponse.getJsonData());
        } else {
            return AjaxResult.error(jsonResponse.getCode(), jsonResponse.getMsg(), jsonResponse.getJsonData());
        }
    }

    /**
     * 将 MapResponse 转换为 AjaxResult.
     *
     * @param mapResponse MapResponse 对象
     * @return AjaxResult
     */
    public static AjaxResult toAjaxResult(final MapResponse mapResponse) {
        if (mapResponse == null) {
            return AjaxResult.error("500", "响应对象为空");
        }
        if (mapResponse.isSuccess()) {
            return AjaxResult.success(mapResponse.getCode(), mapResponse.getMsg(), mapResponse.getData());
        } else {
            return AjaxResult.error(mapResponse.getCode(), mapResponse.getMsg(), mapResponse.getData());
        }
    }

    /**
     * 将 AjaxResult 转换为 JsonResponse.
     * <p>
     * 注意：此方法会将 data 对象序列化为 JSON 字符串。
     * </p>
     *
     * @param ajaxResult AjaxResult 对象
     * @return JsonResponse
     */
    public static JsonResponse toJsonResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return JsonResponse.error("500", "响应对象为空");
        }
        String jsonData = null;
        if (ajaxResult.getData() != null) {
            // 这里需要使用 JSON 序列化库，如 FastJSON、Jackson 等
            // 为保持依赖简洁，此处仅做简单处理
            jsonData = ajaxResult.getData().toString();
        }
        if (ajaxResult.isSuccess()) {
            return JsonResponse.success(ajaxResult.getCode(), ajaxResult.getMsg(), jsonData);
        } else {
            return JsonResponse.error(ajaxResult.getCode(), ajaxResult.getMsg(), jsonData);
        }
    }

    /**
     * 将 AjaxResult 转换为 MapResponse.
     *
     * @param ajaxResult AjaxResult 对象
     * @return MapResponse
     */
    @SuppressWarnings("unchecked")
    public static MapResponse toMapResponse(final AjaxResult ajaxResult) {
        if (ajaxResult == null) {
            return MapResponse.error("500", "响应对象为空");
        }
        java.util.Map<String, Object> data = null;
        if (ajaxResult.getData() instanceof java.util.Map) {
            data = (java.util.Map<String, Object>) ajaxResult.getData();
        } else if (ajaxResult.getData() != null) {
            // 如果不是 Map，创建一个包含数据的 Map
            data = new java.util.HashMap<>();
            data.put("value", ajaxResult.getData());
        }
        if (ajaxResult.isSuccess()) {
            return MapResponse.success(ajaxResult.getCode(), ajaxResult.getMsg(), data);
        } else {
            return MapResponse.error(ajaxResult.getCode(), ajaxResult.getMsg(), data);
        }
    }
}
