package cc.hongz.ztools.response.ajax;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AJAX 业务状态码注册表.
 * <p>
 * 集中管理所有 AJAX 业务状态码，支持动态注册和查询。
 * 应用启动时自动注册通用业务状态码。
 * </p>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public class AjaxStatusRegistry {
    
    private final Map<String, AjaxStatusInterface> codeMap = new ConcurrentHashMap<>();
    
    /**
     * 注册业务状态码.
     *
     * @param businessCode 业务状态码
     * @throws IllegalArgumentException 如果业务状态码为 null 或 code 为空
     */
    public void register(final AjaxStatusInterface businessCode) {
        if (businessCode == null) {
            throw new IllegalArgumentException("AjaxStatusInterface cannot be null");
        }
        if (businessCode.getCode() == null || businessCode.getCode().isEmpty()) {
            throw new IllegalArgumentException("AjaxStatusInterface code cannot be null or empty");
        }
        codeMap.put(businessCode.getCode(), businessCode);
        final String messageKey = businessCode.getMessageKey();
        if (messageKey != null && !messageKey.isBlank()) {
            AjaxStatusMessageKeys.register(businessCode.getCode(), messageKey);
        }
    }
    
    /**
     * 批量注册枚举类型的业务状态码.
     *
     * @param enumClass 业务状态码枚举类
     * @param <E>       枚举类型
     * @throws IllegalArgumentException 如果枚举类为 null
     */
    public <E extends Enum<E> & AjaxStatusInterface> void registerAll(final Class<E> enumClass) {
        if (enumClass == null) {
            throw new IllegalArgumentException("Enum class cannot be null");
        }
        for (final E code : enumClass.getEnumConstants()) {
            register(code);
        }
        AjaxStatusMessageKeys.registerAll(enumClass);
    }
    
    /**
     * 根据代码获取业务状态码.
     *
     * @param code 业务状态码字符串
     * @return 业务状态码对象，未找到返回 null
     */
    public AjaxStatusInterface getByCode(final String code) {
        return codeMap.get(code);
    }
    
    /**
     * 检查业务状态码是否已注册.
     *
     * @param code 业务状态码字符串
     * @return 已注册返回 true
     */
    public boolean contains(final String code) {
        return codeMap.containsKey(code);
    }
    
    /**
     * 获取所有已注册的业务状态码数量.
     *
     * @return 注册的业务状态码数量
     */
    public int size() {
        return codeMap.size();
    }
    
    /**
     * 清空所有注册的业务状态码.
     */
    public void clear() {
        codeMap.clear();
    }
    
    /**
     * 初始化时自动注册通用状态码.
     * <p>
     * 此方法应在应用启动时调用，确保通用业务状态码可用。
     * </p>
     */
    public void init() {
        registerAll(AjaxStatusEnum.class);
    }
    
    /**
     * 获取所有已注册的业务状态码（只读副本）.
     *
     * @return 业务状态码映射的副本
     */
    public Map<String, AjaxStatusInterface> getAll() {
        return new ConcurrentHashMap<>(codeMap);
    }

    /**
     * 根据前缀获取所有匹配的业务状态码.
     * <p>
     * 例如：getByPrefix("USER") 返回所有 USER_ 开头的状态码
     * </p>
     *
     * @param prefix 前缀
     * @return 匹配的业务状态码列表
     */
    public List<AjaxStatusInterface> getByPrefix(final String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return List.of();
        }
        final String prefixWithSeparator = prefix + AjaxStatuses.DEFAULT_SEPARATOR;
        return codeMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefixWithSeparator))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    /**
     * 检查指定前缀的状态码是否已注册.
     *
     * @param prefix 前缀
     * @return 存在该前缀的状态码返回 true
     */
    public boolean containsPrefix(final String prefix) {
        return !getByPrefix(prefix).isEmpty();
    }
}
