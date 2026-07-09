/**
 * 自定义响应结构包 - 提供扩展自定义返回格式的示例和工具.
 * <p>
 * 本包展示了如何定义和使用自定义的响应结构，包括：
 * </p>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.custom.CustomResponse} - 完整的自定义响应结构示例</li>
 *   <li>{@link cc.hongz.ztools.response.custom.CustomResponseConverter} - 自定义响应的转换器</li>
 * </ul>
 *
 * <h3>扩展步骤：</h3>
 * <ol>
 *   <li>定义你的自定义响应类（参考 {@link cc.hongz.ztools.response.custom.CustomResponse}）</li>
 *   <li>创建转换器类（参考 {@link cc.hongz.ztools.response.custom.CustomResponseConverter}）</li>
 *   <li>注册转换策略到 {@link cc.hongz.ztools.response.ResponseConverterFactory}</li>
 *   <li>使用 {@link cc.hongz.ztools.response.ResponseConverterFactory#convert(Object, Class)} 进行转换</li>
 * </ol>
 *
 * <h3>快速开始：</h3>
 * <pre>{@code
 * // 1. 使用预定义的 CustomResponse
 * CustomResponse<User> response = CustomResponse.success(user);
 *
 * // 2. 转换为其他格式
 * AjaxResult ajax = CustomResponseConverter.toAjaxResult(response);
 *
 * // 3. 注册到工厂（一次性）
 * CustomResponseConverter.registerAll();
 *
 * // 4. 通过工厂统一转换
 * CustomResponse<Order> custom = ResponseConverterFactory.convert(ajaxResult, CustomResponse.class);
 * }</pre>
 *
 * @author hongz
 * @since 1.0.0-SNAPSHOT
 * @see cc.hongz.ztools.response.custom.CustomResponse
 * @see cc.hongz.ztools.response.custom.CustomResponseConverter
 */
package cc.hongz.ztools.response.custom;
