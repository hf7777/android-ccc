
# Android Project Rules

## General Principles

- 所有代码必须简洁、可维护、生产可用
- 优先考虑稳定性和可读性，而不是炫技
- 避免过度设计
- 主要功能需要注释，但是不生成无意义注释
- 不输出教学解释，直接给可运行代码
- 修改代码时尽量最小改动，不破坏现有逻辑

---

## Tech Stack

- Language: Kotlin
- UI: View
- Architecture: MVVM
- Async: Coroutines + Flow
- Dependency Injection: Hilt
- Network: Retrofit + OkHttp
- Json: Kotlinx Serialization / Gson
- Local Storage: Room / DataStore

---

## Code Style

- Kotlin idiomatic 写法
- 使用 data class
- 使用 sealed class 管理状态
- 使用 extension function 提升复用
- 单个函数尽量不超过 50 行
- 单个文件尽量职责单一
- 命名清晰，不使用缩写变量名

---

## UI Rules

- UI 保持现代简洁风格
- 页面状态需支持：
  - Loading
  - Empty
  - Error
  - Success

- 所有尺寸使用 dp/sp
- 支持深色模式
- 注意键盘遮挡问题

---

## Architecture Rules

- UI 层不得直接请求网络
- ViewModel 负责状态管理
- Repository 负责数据来源整合
- 数据层与 UI 层解耦
- 避免 Activity 写业务逻辑

---

## Performance Rules

- 避免重复 recomposition
- 图片加载使用 Glide
- 避免主线程 IO 操作
- 大对象避免频繁创建

---

## Error Handling

- 所有网络请求必须 try-catch
- 用户可理解的错误提示
- 日志统一管理
- 空指针风险提前处理

---

## Output Rules

- 默认输出完整代码文件
- 如果涉及多个文件，说明文件结构
- 不生成测试代码
- 不生成单元测试
- 不生成 UI 测试
- 不写 mock 数据测试类

---

## When Refactoring

- 保持现有功能不变
- 优先优化结构与可读性
- 不随意改 public API

---

## Communication Rules

- 少解释，多输出结果
- 我要求优化时，直接给最佳实践方案
- 我要求修 bug 时，直接定位并修复
