package cc.hongz.ztools.response.ajax;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务状态码与国际化消息键（MessageSource key）映射注册表.
 * <p>
 * 解析顺序：
 * </p>
 * <ol>
 *   <li>{@link AjaxStatusInterface#getMessageKey()} 显式指定</li>
 *   <li>本注册表按 {@link AjaxStatusInterface#getCode()} 查询</li>
 *   <li>回退为业务状态码本身（兼容旧行为）</li>
 * </ol>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public final class AjaxStatusMessageKeys {

    private static final Map<String, String> CODE_TO_MESSAGE_KEY = new ConcurrentHashMap<>();

    static {
        registerAll(AjaxStatusEnum.class);
    }

    private AjaxStatusMessageKeys() {
    }

    /**
     * 注册单个业务状态码与消息键的映射.
     *
     * @param businessCode 业务状态码，如 USER_404
     * @param messageKey   MessageSource 键，如 user.not.found
     */
    public static void register(final String businessCode, final String messageKey) {
        if (businessCode == null || businessCode.isBlank() || messageKey == null || messageKey.isBlank()) {
            return;
        }
        CODE_TO_MESSAGE_KEY.put(businessCode, messageKey);
    }

    /**
     * 批量注册枚举中声明了 messageKey 的状态码.
     *
     * @param enumClass 实现 {@link AjaxStatusInterface} 的枚举类
     * @param <E>       枚举类型
     */
    public static <E extends Enum<E> & AjaxStatusInterface> void registerAll(final Class<E> enumClass) {
        if (enumClass == null) {
            return;
        }
        for (E status : enumClass.getEnumConstants()) {
            final String messageKey = status.getMessageKey();
            if (messageKey != null && !messageKey.isBlank()) {
                register(status.getCode(), messageKey);
            }
        }
    }

    /**
     * 解析 MessageSource 消息键.
     *
     * @param status 业务状态码
     * @return 消息键；若无映射则返回 {@link AjaxStatusInterface#getCode()}
     */
    public static String resolveKey(final AjaxStatusInterface status) {
        if (status == null) {
            return "";
        }
        final String explicitKey = status.getMessageKey();
        if (explicitKey != null && !explicitKey.isBlank()) {
            return explicitKey;
        }
        final String mappedKey = CODE_TO_MESSAGE_KEY.get(status.getCode());
        if (mappedKey != null && !mappedKey.isBlank()) {
            return mappedKey;
        }
        return status.getCode();
    }

    /**
     * 按业务状态码字符串查询消息键.
     *
     * @param businessCode 业务状态码
     * @return 消息键，未注册返回 null
     */
    public static String lookup(final String businessCode) {
        return CODE_TO_MESSAGE_KEY.get(businessCode);
    }

    /**
     * 清空动态注册（保留静态内置映射需重新加载类，仅用于测试）.
     */
    static void clearDynamicRegistrations() {
        CODE_TO_MESSAGE_KEY.clear();
        registerAll(AjaxStatusEnum.class);
    }
}
