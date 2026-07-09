package cc.hongz.ztools.response.ajax;

/**
 * AJAX 业务状态码工具类.
 * <p>
 * 提供业务状态码的构建、解析和前缀管理等实用方法。
 * 帮助枚举实现类以声明式方式定义状态码，避免硬编码完整 code 字符串。
 * </p>
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 在枚举构造器中构建 code
 * USER_NOT_FOUND(AjaxStatuses.buildCode("USER", "404"), "用户不存在：{0}", HttpStatusCode.NOT_FOUND)
 *
 * // 使用自定义分隔符
 * ORDER_CREATED(AjaxStatuses.buildCode("ORD", "2001", "-"), "订单创建成功", HttpStatusCode.CREATED)
 *
 * // 解析已有 code
 * String prefix = AjaxStatuses.extractPrefix("USER_404");   // "USER"
 * String number = AjaxStatuses.extractNumber("USER_404");   // "404"
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
public final class AjaxStatuses {

    /**
     * 默认分隔符：下划线.
     */
    public static final String DEFAULT_SEPARATOR = "_";

    private AjaxStatuses() {
        /* 工具类禁止实例化 */
    }

    /**
     * 使用默认分隔符构建业务状态码.
     * <p>
     * 格式：{prefix}_{number}，例如 USER_404
     * </p>
     *
     * @param prefix 前缀（业务域标识）
     * @param number 数字部分
     * @return 完整的业务状态码
     * @throws IllegalArgumentException 如果 prefix 或 number 为空
     */
    public static String buildCode(final String prefix, final String number) {
        return buildCode(prefix, number, DEFAULT_SEPARATOR);
    }

    /**
     * 使用自定义分隔符构建业务状态码.
     * <p>
     * 格式：{prefix}{separator}{number}，例如 ORD-2001
     * </p>
     *
     * @param prefix    前缀（业务域标识）
     * @param number    数字部分
     * @param separator 分隔符
     * @return 完整的业务状态码
     * @throws IllegalArgumentException 如果 prefix、number 或 separator 为空
     */
    public static String buildCode(final String prefix, final String number, final String separator) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Business code prefix cannot be null or empty");
        }
        if (number == null || number.isEmpty()) {
            throw new IllegalArgumentException("Business code number cannot be null or empty");
        }
        if (separator == null || separator.isEmpty()) {
            throw new IllegalArgumentException("Business code separator cannot be null or empty");
        }
        return prefix + separator + number;
    }

    /**
     * 从业务状态码中提取前缀.
     * <p>
     * 使用默认分隔符（下划线）进行分割。
     * 例如：USER_404 → USER
     * </p>
     *
     * @param code 业务状态码
     * @return 前缀部分，无法提取时返回 "COMMON"
     */
    public static String extractPrefix(final String code) {
        return extractPrefix(code, DEFAULT_SEPARATOR);
    }

    /**
     * 从业务状态码中提取前缀（自定义分隔符）.
     *
     * @param code      业务状态码
     * @param separator 分隔符
     * @return 前缀部分，无法提取时返回 "COMMON"
     */
    public static String extractPrefix(final String code, final String separator) {
        if (code == null || code.isEmpty()) {
            return "COMMON";
        }
        final int index = code.indexOf(separator);
        return index > 0 ? code.substring(0, index) : "COMMON";
    }

    /**
     * 从业务状态码中提取数字部分.
     * <p>
     * 使用默认分隔符（下划线）进行分割。
     * 例如：USER_404 → 404
     * </p>
     *
     * @param code 业务状态码
     * @return 数字部分，无法提取时返回原码
     */
    public static String extractNumber(final String code) {
        return extractNumber(code, DEFAULT_SEPARATOR);
    }

    /**
     * 从业务状态码中提取数字部分（自定义分隔符）.
     *
     * @param code      业务状态码
     * @param separator 分隔符
     * @return 数字部分，无法提取时返回原码
     */
    public static String extractNumber(final String code, final String separator) {
        if (code == null || code.isEmpty()) {
            return code;
        }
        final int index = code.indexOf(separator);
        return index > 0 && index < code.length() - separator.length()
                ? code.substring(index + separator.length())
                : code;
    }

    /**
     * 验证业务状态码格式是否合法.
     * <p>
     * 合法格式：{prefix}{separator}{number}，其中 prefix 和 number 非空
     * </p>
     *
     * @param code      业务状态码
     * @param separator 分隔符
     * @return 格式合法返回 true
     */
    public static boolean isValidFormat(final String code, final String separator) {
        if (code == null || code.isEmpty() || separator == null || separator.isEmpty()) {
            return false;
        }
        final int index = code.indexOf(separator);
        return index > 0 && index < code.length() - separator.length();
    }

    /**
     * 验证业务状态码格式是否合法（默认分隔符）.
     *
     * @param code 业务状态码
     * @return 格式合法返回 true
     */
    public static boolean isValidFormat(final String code) {
        return isValidFormat(code, DEFAULT_SEPARATOR);
    }
}
