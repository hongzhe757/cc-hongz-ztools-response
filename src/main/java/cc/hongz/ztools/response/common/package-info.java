/**
 * 通用响应数据结构包.
 * <p>
 * 本包提供可被各种响应结构复用的通用数据组件，如分页信息、错误详情等。
 * </p>
 *
 * <h3>包含的类：</h3>
 * <ul>
 *   <li>{@link cc.hongz.ztools.response.common.DataListInfo} - 分页数据列表信息（传统 Offset/Limit）</li>
 *   <li>{@link cc.hongz.ztools.response.common.PageData} - 通用分页数据包装器（支持 MyBatis-Plus/PageHelper 转换）</li>
 *   <li>{@link cc.hongz.ztools.response.common.CursorPageData} - 游标分页数据包装器（无限滚动、Feed 流）</li>
 *   <li>{@link cc.hongz.ztools.response.common.KeysetPageData} - 键集分页数据包装器（高性能深分页）</li>
 * </ul>
 *
 * <h3>设计说明：</h3>
 * <ul>
 *   <li>这些类是强类型的通用数据结构，与 {@code generic} 包的弱类型动态响应形成互补</li>
 *   <li>可与任何响应类型组合使用：{@code AjaxResult<PageData<User>>}、{@code Result<CursorPageData<Post>>}</li>
 *   <li>作为响应体系的组成部分，而非独立的模型层</li>
 * </ul>
 *
 * @author hongz
 */
package cc.hongz.ztools.response.common;
