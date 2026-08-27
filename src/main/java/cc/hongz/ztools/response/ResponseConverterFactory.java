package cc.hongz.ztools.response;

import java.util.HashMap;
import java.util.Map;

import cc.hongz.ztools.response.ajax.AjaxResult;
import cc.hongz.ztools.response.core.Result;
import cc.hongz.ztools.response.generic.JsonResponse;
import cc.hongz.ztools.response.generic.MapResponse;
import cc.hongz.ztools.response.standard.AliResponse;
import cc.hongz.ztools.response.standard.BaiduResponse;
import cc.hongz.ztools.response.standard.GraphQLResponse;
import cc.hongz.ztools.response.standard.GrpcResponse;
import cc.hongz.ztools.response.standard.ProblemDetail;
import cc.hongz.ztools.response.standard.TencentResponse;

/**
 * 响应转换器工厂 - 工厂方法模式（Factory Method Pattern）+ 注册表模式（Registry Pattern）.
 * <p>
 * 提供预定义的转换策略，并允许注册自定义的转换策略。通过工厂方法创建转换器实例，
 * 避免客户端直接依赖具体的转换实现。
 * </p>
 *
 * <h3>设计模式说明：</h3>
 * <ul>
 *   <li><strong>工厂方法模式（Factory Method）</strong>：定义一个用于创建对象的接口，让子类决定实例化哪一个类</li>
 *   <li><strong>注册表模式（Registry Pattern）</strong>：提供一个中心化的位置来存储和检索对象实例</li>
 *   <li><strong>单例模式（Singleton）</strong>：确保工厂类只有一个实例，全局可访问</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 获取预定义的转换策略
 * ResponseConversionStrategy<Result, AjaxResult> strategy = 
 *     ResponseConverterFactory.getStrategy(Result.class, AjaxResult.class);
 *
 * // 2. 注册自定义转换策略
 * ResponseConverterFactory.register(
 *     CustomRequest.class, 
 *     CustomResponse.class, 
 *     new CustomConversionStrategy()
 * );
 *
 * // 3. 使用工厂创建转换器
 * Object result = ResponseConverterFactory.convert(source, targetType);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public final class ResponseConverterFactory {

    private static final Map<String, ResponseConversionStrategy<?, ?>> STRATEGIES = new HashMap<>();

    static {
        // 注册预定义的转换策略
        registerDefaultStrategies();
    }

    private ResponseConverterFactory() {
        // 工具类，禁止实例化
    }

    /**
     * 生成策略键.
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 策略键
     */
    private static String generateKey(final Class<?> sourceType, final Class<?> targetType) {
        return sourceType.getName() + " -> " + targetType.getName();
    }

    /**
     * 注册转换策略.
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param strategy   转换策略
     * @param <S>        源类型泛型
     * @param <T>        目标类型泛型
     */
    public static <S, T> void register(
            final Class<S> sourceType,
            final Class<T> targetType,
            final ResponseConversionStrategy<S,
            T> strategy
        ) {
        if (sourceType == null || targetType == null || strategy == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        String key = generateKey(sourceType, targetType);
        STRATEGIES.put(key, strategy);
    }

    /**
     * 获取转换策略.
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param <S>        源类型泛型
     * @param <T>        目标类型泛型
     * @return 转换策略，如果不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <S, T> ResponseConversionStrategy<S, T> getStrategy(
            final Class<S> sourceType,
            final Class<T> targetType
        ) {
        String key = generateKey(sourceType, targetType);
        return (ResponseConversionStrategy<S, T>) STRATEGIES.get(key);
    }

    /**
     * 检查是否存在指定的转换策略.
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 如果存在返回 true
     */
    public static boolean hasStrategy(final Class<?> sourceType, final Class<?> targetType) {
        String key = generateKey(sourceType, targetType);
        return STRATEGIES.containsKey(key);
    }

    /**
     * 移除转换策略.
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     */
    public static void unregister(final Class<?> sourceType, final Class<?> targetType) {
        String key = generateKey(sourceType, targetType);
        STRATEGIES.remove(key);
    }

    /**
     * 清空所有注册的策略.
     * <p>
     * 主要用于测试场景。
     * </p>
     */
    public static void clear() {
        STRATEGIES.clear();
        registerDefaultStrategies();
    }

    /**
     * 获取已注册的策略数量.
     *
     * @return 策略数量
     */
    public static int getStrategyCount() {
        return STRATEGIES.size();
    }

    /**
     * 执行转换.
     *
     * @param source     源对象
     * @param targetType 目标类型
     * @param <S>        源类型
     * @param <T>        目标类型
     * @return 转换后的对象
     */
    @SuppressWarnings("unchecked")
    public static <S, T> T convert(final S source, final Class<T> targetType) {
        if (source == null) {
            throw new IllegalArgumentException("源对象不能为空");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("目标类型不能为空");
        }

        Class<?> sourceType = source.getClass();
        ResponseConversionStrategy<S, T> strategy = getStrategy((Class<S>) sourceType, targetType);

        if (strategy == null) {
            throw new UnsupportedOperationException(
                    "未找到从 " + sourceType.getSimpleName() + " 到 " + targetType.getSimpleName() + " 的转换策略"
            );
        }

        return strategy.convert(source);
    }

    /**
     * 注册默认的转换策略.
     */
    private static void registerDefaultStrategies() {
        // Result -> AjaxResult
        register(Result.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // AliResponse -> AjaxResult
        register(AliResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // TencentResponse -> AjaxResult
        register(TencentResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // BaiduResponse -> AjaxResult
        register(BaiduResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // ProblemDetail -> AjaxResult
        register(ProblemDetail.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // GraphQLResponse -> AjaxResult
        register(GraphQLResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // GrpcResponse -> AjaxResult
        register(GrpcResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // AjaxResult -> Result
        register(AjaxResult.class, Result.class,
                ResponseConverter::toResult);

        // AjaxResult -> AliResponse
        register(AjaxResult.class, AliResponse.class,
                ResponseConverter::toAliResponse);

        // AjaxResult -> TencentResponse
        register(AjaxResult.class, TencentResponse.class,
                ResponseConverter::toTencentResponse);

        // AjaxResult -> BaiduResponse
        register(AjaxResult.class, BaiduResponse.class,
                ResponseConverter::toBaiduResponse);

        // AjaxResult -> ProblemDetail
        register(AjaxResult.class, ProblemDetail.class,
                ResponseConverter::toProblemDetail);

        // AjaxResult -> GraphQLResponse
        register(AjaxResult.class, GraphQLResponse.class,
                ResponseConverter::toGraphQLResponse);

        // AjaxResult -> GrpcResponse
        register(AjaxResult.class, GrpcResponse.class,
                ResponseConverter::toGrpcResponse);

        // AliResponse -> TencentResponse
        register(AliResponse.class, TencentResponse.class,
                ResponseConverter::aliToTencent);

        // TencentResponse -> AliResponse
        register(TencentResponse.class, AliResponse.class,
                ResponseConverter::tencentToAli);

        // BaiduResponse -> TencentResponse
        register(BaiduResponse.class, TencentResponse.class,
                ResponseConverter::baiduToTencent);

        // JsonResponse -> AjaxResult
        register(JsonResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // MapResponse -> AjaxResult
        register(MapResponse.class, AjaxResult.class,
                ResponseConverter::toAjaxResult);

        // AjaxResult -> JsonResponse
        register(AjaxResult.class, JsonResponse.class,
                ResponseConverter::toJsonResponse);

        // AjaxResult -> MapResponse
        register(AjaxResult.class, MapResponse.class,
                ResponseConverter::toMapResponse);
    }
}
