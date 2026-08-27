package cc.hongz.ztools.response;

/**
 * 响应转换策略接口 - 策略模式（Strategy Pattern）.
 * <p>
 * 定义响应结构转换的算法族，使它们可以互相替换。此接口将转换算法与使用它的客户端解耦，
 * 允许在运行时选择不同的转换策略。
 * </p>
 *
 * <h3>设计模式说明：</h3>
 * <ul>
 *   <li><strong>模式名称</strong>：策略模式（Strategy Pattern）</li>
 *   <li><strong>意图</strong>：定义一系列算法，把它们一个个封装起来，并且使它们可互换</li>
 *   <li><strong>适用场景</strong>：
 *     <ul>
 *       <li>需要在不同时间应用不同的转换规则</li>
 *       <li>有多种相关的类，只是行为不同</li>
 *       <li>需要使用算法的不同变体</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 定义自定义转换策略
 * public class CustomResultToAjaxStrategy implements ResponseConversionStrategy<Result<User>, AjaxResult> {
 *     @Override
 *     public AjaxResult convert(Result<User> source) {
 *         if (source.isSuccess()) {
 *             return AjaxResult.success("CUSTOM_" + source.getCode(), source.getMessage(), source.getData());
 *         } else {
 *             return AjaxResult.error("ERROR_" + source.getCode(), source.getMessage());
 *         }
 *     }
 * }
 *
 * // 2. 使用策略进行转换
 * Result<User> result = Result.success(user);
 * ResponseConversionStrategy<Result<User>, AjaxResult> strategy = new CustomResultToAjaxStrategy();
 * AjaxResult ajax = ResponseConverter.convert(result, strategy);
 * }</pre>
 *
 * @param <S> 源响应类型
 * @param <T> 目标响应类型
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see ResponseConverter#convert(Object, ResponseConversionStrategy)
 */
@FunctionalInterface
public interface ResponseConversionStrategy<S, T> {

    /**
     * 执行转换操作.
     *
     * @param source 源响应对象
     * @return 转换后的目标响应对象
     */
    T convert(S source);

    /**
     * 组合多个策略，形成转换链.
     * <p>
     * 默认方法，支持函数式编程风格的策略组合。
     * </p>
     *
     * @param after 后续策略
     * @param <V>   中间类型
     * @return 组合后的策略
     */
    default <V> ResponseConversionStrategy<S, V> andThen(
            final ResponseConversionStrategy<? super T,
            ? extends V> after
        ) {
        if (after == null) {
            throw new IllegalArgumentException("后续策略不能为空");
        }
        return (S s) -> {
            T intermediate = this.convert(s);
            return after.convert(intermediate);
        };
    }

    /**
     * 创建一个空操作策略（直接返回源对象）.
     * <p>
     * 用于测试或作为默认策略。
     * </p>
     *
     * @param <T> 响应类型
     * @return 空操作策略
     */
    static <T> ResponseConversionStrategy<T, T> identity() {
        return source -> source;
    }
}
