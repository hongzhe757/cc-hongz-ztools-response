/**
 * 响应式响应结构.
 * <p>
 * 提供基于 Project Reactor 的响应式响应格式定义，适用于 Spring WebFlux 等响应式框架：
 * </p>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.reactive.ReactiveAjaxResult} - 响应式 AJAX 结果（Mono）</li>
 *   <li>{@link cc.hongz.ztools.response.reactive.ReactiveCollectionResult} - 响应式集合结果（Flux）</li>
 *   <li>{@link cc.hongz.ztools.response.reactive.ReactivePageResult} - 响应式分页结果（Flux + Mono）</li>
 * </ul>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
package cc.hongz.ztools.response.reactive;
