/**
 * 通用/动态响应结构包.
 * <p>
 * 本包提供非强类型的响应结构，适用于动态数据、遗留系统对接等场景。
 * </p>
 *
 * <h3>包含的类：</h3>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.generic.JsonResponse} - JSON 字符串响应包装器</li>
 *   <li>{@link cc.hongz.ztools.response.generic.MapResponse} - Map 形式响应包装器</li>
 * </ul>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li><strong>JsonResponse</strong>：已序列化的 JSON 字符串透传、动态生成的 JSON、第三方 API 对接</li>
 *   <li><strong>MapResponse</strong>：动态字段响应、配置信息、聚合数据、NoSQL 查询结果</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *   <li>这些类牺牲了类型安全性以换取灵活性</li>
 *   <li>建议在系统边界处转换为强类型响应（如 AjaxResult）</li>
 *   <li>避免在核心业务逻辑中过度使用</li>
 * </ul>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 */
package cc.hongz.ztools.response.generic;
