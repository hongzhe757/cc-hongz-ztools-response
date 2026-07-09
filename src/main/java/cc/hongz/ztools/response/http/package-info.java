/**
 * HTTP 客户端响应包装类.
 * <p>
 * 提供对各种 HTTP 客户端响应的统一包装，便于转换和处理：
 * </p>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.http.JdkHttpResponseWrapper} - JDK HttpClient 响应包装</li>
 *   <li>{@link cc.hongz.ztools.response.http.OkHttpResponseWrapper} - OkHttp 响应包装</li>
 *   <li>{@link cc.hongz.ztools.response.http.ApacheHttpResponseWrapper} - Apache HttpClient 响应包装</li>
 *   <li>{@link cc.hongz.ztools.response.http.SpringResponseEntityWrapper} - Spring ResponseEntity 包装</li>
 *   <li>{@link cc.hongz.ztools.response.http.WebClientResponseWrapper} - Spring WebClient 响应包装</li>
 *   <li>{@link cc.hongz.ztools.response.http.FeignResponseWrapper} - Feign Client 响应包装</li>
 * </ul>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
package cc.hongz.ztools.response.http;
