package cc.hongz.ztools.response;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 响应转换链构建器 - 建造者模式（Builder Pattern）+ 责任链模式（Chain of Responsibility）.
 * <p>
 * 支持多步骤的复杂转换流程，允许将多个转换操作串联起来执行。
 * </p>
 *
 * <h3>设计模式说明：</h3>
 * <ul>
 *   <li><strong>建造者模式（Builder Pattern）</strong>：将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示</li>
 *   <li><strong>责任链模式（Chain of Responsibility）</strong>：使多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 1. 简单链式转换
 * AjaxResult result = ResponseConverter.chain()
 *     .from(baiduResponse)
 *     .to(TencentResponse.class)
 *     .thenTo(AjaxResult.class)
 *     .execute();
 *
 * // 2. 带自定义转换函数的链式转换
 * Result<User> finalResult = ResponseConverter.chain()
 *     .from(ajaxResult)
 *     .transform(ResponseConverter::toResult)
 *     .transform(r -> Result.success(r.getCode(), "Modified: " + r.getMessage(), r.getData()))
 *     .execute();
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class ConversionChainBuilder {

    private Object source;
    private final List<Function<Object, Object>> transformations = new ArrayList<>();

    /**
     * 设置源响应对象.
     *
     * @param newSource 源响应
     * @return 当前构建器
     */
    public ConversionChainBuilder from(final Object newSource) {
        this.source = newSource;
        return this;
    }

    /**
     * 添加一个类型转换步骤.
     * <p>
     * 根据目标类型自动选择合适的转换方法。
     * </p>
     *
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 当前构建器
     */
    @SuppressWarnings("unchecked")
    public <T> ConversionChainBuilder to(final Class<T> targetType) {
        transformations.add(current -> {
            if (targetType.isAssignableFrom(current.getClass())) {
                return current;
            }
            // 这里可以根据需要添加更多的自动转换逻辑
            throw new UnsupportedOperationException(
                    "不支持从 " + current.getClass().getSimpleName() + " 自动转换为 " + targetType.getSimpleName()
            );
        });
        return this;
    }

    /**
     * 添加一个转换步骤到指定类型.
     * <p>
     * 这是 {@link #to(Class)} 的别名，用于提高可读性。
     * </p>
     *
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 当前构建器
     */
    public <T> ConversionChainBuilder thenTo(final Class<T> targetType) {
        return to(targetType);
    }

    /**
     * 添加一个自定义转换函数.
     * <p>
     * 允许在转换链中插入任意的转换逻辑。
     * </p>
     *
     * @param transformer 转换函数
     * @param <T>         输入类型
     * @param <R>         输出类型
     * @return 当前构建器
     */
    @SuppressWarnings("unchecked")
    public <T, R> ConversionChainBuilder transform(final Function<T, R> transformer) {
        transformations.add(current -> (Object) transformer.apply((T) current));
        return this;
    }

    /**
     * 添加一个条件转换.
     * <p>
     * 只有当条件满足时才执行转换。
     * </p>
     *
     * @param condition   条件判断函数
     * @param transformer 转换函数
     * @param <T>         输入类型
     * @return 当前构建器
     */
    @SuppressWarnings("unchecked")
    public <T> ConversionChainBuilder transformIf(
            final java.util.function.Predicate<T> condition,
            final Function<T,
            T> transformer
        ) {
        transformations.add(current -> {
            T typed = (T) current;
            return condition.test(typed) ? transformer.apply(typed) : current;
        });
        return this;
    }

    /**
     * 执行转换链并返回最终结果.
     *
     * @param <T> 期望的返回类型
     * @return 转换后的结果
     */
    @SuppressWarnings("unchecked")
    public <T> T execute() {
        if (source == null) {
            throw new IllegalStateException("源对象未设置，请先调用 from() 方法");
        }

        Object current = source;
        for (Function<Object, Object> transformation : transformations) {
            current = transformation.apply(current);
        }

        return (T) current;
    }

    /**
     * 执行转换链并返回指定类型的结果.
     *
     * @param targetType 目标类型
     * @param <T>        目标类型泛型
     * @return 转换后的结果
     */
    public <T> T executeAs(final Class<T> targetType) {
        Object result = execute();
        if (!targetType.isInstance(result)) {
            throw new ClassCastException(
                    "转换结果类型为 " + result.getClass().getName() + "，无法转换为 " + targetType.getName()
            );
        }
        return targetType.cast(result);
    }

    /**
     * 获取当前转换链中的步骤数量.
     *
     * @return 步骤数量
     */
    public int getStepCount() {
        return transformations.size();
    }

    /**
     * 检查转换链是否为空.
     *
     * @return 如果没有任何转换步骤则返回 true
     */
    public boolean isEmpty() {
        return transformations.isEmpty();
    }
}
