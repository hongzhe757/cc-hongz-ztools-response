package cc.hongz.ztools.response.http;

/**
 * HTTP 状态码枚举.
 * <p>
 * 完整覆盖 IETF RFC 标准定义的 HTTP 状态码，提供语义化访问。
 * 包括 RFC 7231、RFC 7235、RFC 7538 等标准定义的状态码。
 * </p>
 * 
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7231">RFC 7231</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7235">RFC 7235</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc7538">RFC 7538</a>
 */
public enum HttpStatusCode {
    
    // ==================== 1xx Informational ====================
    
    /**
     * 100 Continue - 请求已接收，继续处理.
     */
    CONTINUE(100, "Continue"),
    
    /**
     * 101 Switching Protocols - 协议切换.
     */
    SWITCHING_PROTOCOLS(101, "Switching Protocols"),
    
    /**
     * 102 Processing - 处理中 (WebDAV).
     */
    PROCESSING(102, "Processing"),
    
    /**
     * 103 Early Hints - 早期提示.
     */
    EARLY_HINTS(103, "Early Hints"),
    
    // ==================== 2xx Success ====================
    
    /**
     * 200 OK - 请求成功.
     */
    OK(200, "OK"),
    
    /**
     * 201 Created - 资源已创建.
     */
    CREATED(201, "Created"),
    
    /**
     * 202 Accepted - 请求已接受，但尚未处理完成.
     */
    ACCEPTED(202, "Accepted"),
    
    /**
     * 203 Non-Authoritative Information - 非权威信息.
     */
    NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    
    /**
     * 204 No Content - 无内容返回.
     */
    NO_CONTENT(204, "No Content"),
    
    /**
     * 205 Reset Content - 重置内容.
     */
    RESET_CONTENT(205, "Reset Content"),
    
    /**
     * 206 Partial Content - 部分内容.
     */
    PARTIAL_CONTENT(206, "Partial Content"),
    
    /**
     * 207 Multi-Status - 多状态 (WebDAV).
     */
    MULTI_STATUS(207, "Multi-Status"),
    
    /**
     * 208 Already Reported - 已报告 (WebDAV).
     */
    ALREADY_REPORTED(208, "Already Reported"),
    
    /**
     * 226 IM Used - 使用了实例操作.
     */
    IM_USED(226, "IM Used"),
    
    // ==================== 3xx Redirection ====================
    
    /**
     * 300 Multiple Choices - 多种选择.
     */
    MULTIPLE_CHOICES(300, "Multiple Choices"),
    
    /**
     * 301 Moved Permanently - 永久移动.
     */
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    
    /**
     * 302 Found - 临时移动（Found）.
     */
    FOUND(302, "Found"),
    
    /**
     * 303 See Other - 查看其他位置.
     */
    SEE_OTHER(303, "See Other"),
    
    /**
     * 304 Not Modified - 未修改.
     */
    NOT_MODIFIED(304, "Not Modified"),
    
    /**
     * 305 Use Proxy - 使用代理.
     */
    USE_PROXY(305, "Use Proxy"),
    
    /**
     * 307 Temporary Redirect - 临时重定向.
     */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    
    /**
     * 308 Permanent Redirect - 永久重定向.
     */
    PERMANENT_REDIRECT(308, "Permanent Redirect"),
    
    // ==================== 4xx Client Error ====================
    
    /**
     * 400 Bad Request - 错误请求.
     */
    BAD_REQUEST(400, "Bad Request"),
    
    /**
     * 401 Unauthorized - 未授权.
     */
    UNAUTHORIZED(401, "Unauthorized"),
    
    /**
     * 402 Payment Required - 需要付款.
     */
    PAYMENT_REQUIRED(402, "Payment Required"),
    
    /**
     * 403 Forbidden - 禁止访问.
     */
    FORBIDDEN(403, "Forbidden"),
    
    /**
     * 404 Not Found - 资源不存在.
     */
    NOT_FOUND(404, "Not Found"),
    
    /**
     * 405 Method Not Allowed - 方法不允许.
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    
    /**
     * 406 Not Acceptable - 不可接受.
     */
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    
    /**
     * 407 Proxy Authentication Required - 需要代理认证.
     */
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    
    /**
     * 408 Request Timeout - 请求超时.
     */
    REQUEST_TIMEOUT(408, "Request Timeout"),
    
    /**
     * 409 Conflict - 冲突.
     */
    CONFLICT(409, "Conflict"),
    
    /**
     * 410 Gone - 资源已删除.
     */
    GONE(410, "Gone"),
    
    /**
     * 411 Length Required - 需要内容长度.
     */
    LENGTH_REQUIRED(411, "Length Required"),
    
    /**
     * 412 Precondition Failed - 前置条件失败.
     */
    PRECONDITION_FAILED(412, "Precondition Failed"),
    
    /**
     * 413 Payload Too Large - 负载过大.
     */
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    
    /**
     * 414 URI Too Long - URI 过长.
     */
    URI_TOO_LONG(414, "URI Too Long"),
    
    /**
     * 415 Unsupported Media Type - 不支持的媒体类型.
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    
    /**
     * 416 Range Not Satisfiable - 范围无法满足.
     */
    RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
    
    /**
     * 417 Expectation Failed - 期望失败.
     */
    EXPECTATION_FAILED(417, "Expectation Failed"),
    
    /**
     * 418 I'm a teapot - 我是茶壶（愚人节玩笑）.
     */
    IM_A_TEAPOT(418, "I'm a teapot"),
    
    /**
     * 421 Misdirected Request - 错误的请求.
     */
    MISDIRECTED_REQUEST(421, "Misdirected Request"),
    
    /**
     * 422 Unprocessable Entity - 无法处理的实体 (WebDAV).
     */
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    
    /**
     * 423 Locked - 已锁定 (WebDAV).
     */
    LOCKED(423, "Locked"),
    
    /**
     * 424 Failed Dependency - 依赖失败 (WebDAV).
     */
    FAILED_DEPENDENCY(424, "Failed Dependency"),
    
    /**
     * 425 Too Early - 太早.
     */
    TOO_EARLY(425, "Too Early"),
    
    /**
     * 426 Upgrade Required - 需要升级.
     */
    UPGRADE_REQUIRED(426, "Upgrade Required"),
    
    /**
     * 428 Precondition Required - 需要前置条件.
     */
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    
    /**
     * 429 Too Many Requests - 请求过多.
     */
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    
    /**
     * 431 Request Header Fields Too Large - 请求头字段过大.
     */
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    
    /**
     * 451 Unavailable For Legal Reasons - 因法律原因不可用.
     */
    UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
    
    // ==================== 5xx Server Error ====================
    
    /**
     * 500 Internal Server Error - 服务器内部错误.
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    
    /**
     * 501 Not Implemented - 未实现.
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    
    /**
     * 502 Bad Gateway - 错误网关.
     */
    BAD_GATEWAY(502, "Bad Gateway"),
    
    /**
     * 503 Service Unavailable - 服务暂不可用.
     */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    
    /**
     * 504 Gateway Timeout - 网关超时.
     */
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    
    /**
     * 505 HTTP Version Not Supported - HTTP 版本不支持.
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    
    /**
     * 506 Variant Also Negotiates - 变体协商.
     */
    VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    
    /**
     * 507 Insufficient Storage - 存储空间不足 (WebDAV).
     */
    INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
    
    /**
     * 508 Loop Detected - 检测到循环 (WebDAV).
     */
    LOOP_DETECTED(508, "Loop Detected"),
    
    /**
     * 510 Not Extended - 未扩展.
     */
    NOT_EXTENDED(510, "Not Extended"),
    
    /**
     * 511 Network Authentication Required - 需要网络认证.
     */
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");
    
    private final int code;
    private final String reasonPhrase;
    
    HttpStatusCode(final int code, final String reasonPhrase) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }
    
    /**
     * 获取状态码数值.
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }
    
    /**
     * 获取原因短语.
     *
     * @return 原因短语
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }
    
    /**
     * 判断是否为信息性状态码 (1xx).
     *
     * @return 是信息性状态码返回 true
     */
    public boolean isInformational() {
        return code >= 100 && code < 200;
    }
    
    /**
     * 判断是否为成功状态码 (2xx).
     *
     * @return 是成功状态码返回 true
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }
    
    /**
     * 判断是否为重定向状态码 (3xx).
     *
     * @return 是重定向状态码返回 true
     */
    public boolean isRedirection() {
        return code >= 300 && code < 400;
    }
    
    /**
     * 判断是否为客户端错误状态码 (4xx).
     *
     * @return 是客户端错误状态码返回 true
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }
    
    /**
     * 判断是否为服务器错误状态码 (5xx).
     *
     * @return 是服务器错误状态码返回 true
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }
    
    /**
     * 判断是否为错误状态码 (4xx 或 5xx).
     *
     * @return 是错误状态码返回 true
     */
    public boolean isError() {
        return isClientError() || isServerError();
    }
    
    /**
     * 根据状态码值获取枚举.
     *
     * @param code 状态码
     * @return 对应的枚举值，未找到返回 null
     */
    public static HttpStatusCode valueOf(final int code) {
        for (final HttpStatusCode status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 检查给定的状态码是否有效.
     *
     * @param code 状态码
     * @return 有效返回 true
     */
    public static boolean isValid(final int code) {
        return valueOf(code) != null;
    }
    
    @Override
    public String toString() {
        return code + " " + reasonPhrase;
    }
}
