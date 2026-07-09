# hongz-ztools-response 规划文档索引

> 本文档汇总了 HTTP 状态码、业务状态码和国际化异常集成的所有规划文档。

---

## 📚 文档列表

### 1. 核心规划文档

#### 📘 [ROADMAP.md](./ROADMAP.md) - 完整演进路线图
**用途**: 详细的技术设计和实现方案  
**内容**:
- Phase 1: HTTP 状态码与业务状态码体系（含完整代码示例）
- Phase 2: 国际化异常集成（含 Spring 集成方案）
- Phase 3: Spring Boot Starter 与社区贡献
- 详细的任务清单和时间线
- 验收标准和里程碑

**适合人群**: 开发者、架构师、技术负责人  
**阅读时间**: 30-45 分钟

---

#### 📗 [ROADMAP_SUMMARY.md](./ROADMAP_SUMMARY.md) - 规划摘要
**用途**: 快速了解规划概览和核心价值  
**内容**:
- 三大核心规划的简要说明
- 关键交付物列表
- 示例代码片段
- 时间线可视化
- 验收标准摘要

**适合人群**: 项目经理、产品经理、新加入的团队成员  
**阅读时间**: 5-10 分钟

---

#### 📋 [TASK_CHECKLIST.md](./TASK_CHECKLIST.md) - 任务清单
**用途**: 跟踪实施进度和任务分配  
**内容**:
- 按阶段分解的详细任务列表
- 每个任务的预计工时和优先级
- 进度跟踪表格
- 里程碑定义
- 验收标准

**适合人群**: 项目经理、Scrum Master、开发人员  
**使用方式**: 每周更新进度，认领任务

---

### 2. 现有文档（已更新）

#### 📊 [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md) - 实现状态清单
**更新内容**: 新增"演进路线图"章节，链接到 ROADMAP.md  
**用途**: 查看当前已实现的响应结构和未来规划入口

---

### 3. 方案对比文档

#### 🔍 [COMPARISON.md](./COMPARISON.md) - 全面方案对比
**用途**: 对比分析 Java 生态中所有主流的响应结构和状态码管理方案  
**内容**:
- Spring Framework 原生方案（ResponseEntity、ProblemDetail、ErrorResponse）
- 开源响应包装库（R/Result、ApiResult、ApiResponse）
- 业务状态码管理（枚举、接口、注解驱动）
- 国际化异常处理（MessageSource、I18nException、第三方库）
- hongz-ztools-response 优势分析
- 综合对比矩阵和选型建议

**适合人群**: 架构师、技术负责人、需要做技术选型的开发者  
**阅读时间**: 20-30 分钟

---

#### 📖 [README.md](../README.md) - 模块主文档
**更新内容**: 在"未来规划"章节添加演进路线图引用  
**用途**: 模块整体介绍和快速入门

---

## 🎯 快速导航

### 我想...

#### 了解整体规划
👉 阅读 [ROADMAP_SUMMARY.md](./ROADMAP_SUMMARY.md)

#### 查看详细技术方案
👉 阅读 [ROADMAP.md](./ROADMAP.md)

#### 对比各种方案
👉 阅读 [COMPARISON.md](./COMPARISON.md)

#### 跟踪任务进度
👉 查看 [TASK_CHECKLIST.md](./TASK_CHECKLIST.md)

#### 了解当前实现状态
👉 查看 [IMPLEMENTATION_STATUS.md](./IMPLEMENTATION_STATUS.md)

#### 开始使用响应模块
👉 查看 [README.md](../README.md)

#### 学习自定义响应扩展
👉 查看 [CUSTOM_RESPONSE_GUIDE.md](./CUSTOM_RESPONSE_GUIDE.md)

#### 查看所有响应格式
👉 查看 [RESPONSE_FORMATS.md](./RESPONSE_FORMATS.md)

#### 快速查找用法
👉 查看 [QUICK_REFERENCE.md](./QUICK_REFERENCE.md)

---

## 📅 时间线概览

```
2026 Q3 (7-9月)          2026 Q4 (10-12月)        2027 Q1 (1-3月)           2027 Q2 (4-6月)
├─ Phase 1               ├─ Phase 2               ├─ Phase 3                ├─ Phase 4
│  ├─ HTTP 状态码枚举     │  ├─ 消息资源文件        │  ├─ ResponseInterceptor │  ├─ Spring Boot Starter
│  ├─ 业务状态码接口     │  ├─ I18nResponseBuilder │  ├─ InterceptStrategy   │  ├─ 自动配置
│  ├─ CommonBusinessCode │  ├─ BusinessException   │  ├─ @SkipResponseIntercept│ ├─ 社区贡献
│  └─ AjaxResult 增强    │  └─ I18nExceptionHandler│  └─ 严格模式支持        │  └─ Maven Central 发布
```

---

## 🔑 核心价值

### 1. 标准化
- 对齐 Spring Framework 和 RFC 标准
- 统一的 HTTP 状态码和业务状态码体系
- 符合国际规范的错误响应格式

### 2. 灵活性
- 业务状态码与 HTTP 状态码解耦
- 支持自定义业务域和状态码规范
- 插件化设计，易于扩展

### 3. 国际化
- 原生支持多语言错误消息
- 与 Spring MessageSource 深度集成
- 运行时语言切换

### 4. 自动化
- 响应拦截器自动归一化
- 零侵入现有代码
- 严格模式强制规范

### 5. 生态整合
- 与 Spring Boot/Spring Cloud 无缝集成
- 提供开箱即用的 Starter
- 支持向社区贡献

---

## 💡 设计理念

### HTTP 状态码 vs 业务状态码

**为什么要区分？**

| 维度 | HTTP 状态码 | 业务状态码 |
|------|------------|-----------|
| **作用域** | 传输层协议 | 业务逻辑层 |
| **粒度** | 粗粒度（约 60 个） | 细粒度（无限扩展） |
| **语义** | 通用（如 404 Not Found） | 具体（如 USER_404 用户不存在） |
| **使用者** | 客户端、网关、代理 | 前端应用、业务系统 |
| **稳定性** | 高度稳定（RFC 标准） | 随业务演进 |

**映射关系**:
```
HTTP 404 ←→ USER_404 (用户不存在)
HTTP 404 ←→ ORDER_404 (订单不存在)
HTTP 403 ←→ USER_403 (用户被禁用)
HTTP 409 ←→ USER_409 (用户已存在)
```

### 国际化异常处理

**为什么需要国际化？**

1. **多语言用户**: 全球化应用需要支持不同语言的用户
2. **用户体验**: 母语错误消息更易理解
3. **合规要求**: 某些地区要求本地化界面
4. **专业形象**: 展示产品的国际化能力

**实现策略**:
```
异常抛出 → 异常处理器捕获 → 解析语言区域 → 查询消息源 → 构建响应
```

---

## 🤝 参与贡献

### 如何参与？

1. **认领任务**: 在 TASK_CHECKLIST.md 中选择感兴趣的任务
2. **提交 PR**: 按照代码规范提交 Pull Request
3. **代码评审**: 参与代码评审和改进
4. **文档完善**: 帮助完善文档和示例
5. **测试验证**: 编写单元测试和集成测试

### 贡献指南

- 遵循项目代码规范
- 编写完整的 JavaDoc 注释
- 提供单元测试覆盖
- 更新相关文档
- 在 CHANGELOG.md 中记录变更

---

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- GitHub Issues: 提交问题和建议
- 邮件: hongz@example.com
- 内部沟通: 企业微信/钉钉群组

---

## 📄 许可证

本项目采用 Apache License 2.0 开源许可证。

---

**最后更新**: 2026-06-22  
**版本**: 1.0.0-SNAPSHOT  
**维护者**: hongz-parent 团队
