# 路由框架使用文档

## 功能特性

- ✅ 页面跳转（普通跳转、ForResult 跳转）
- ✅ 参数传递（支持多种数据类型）
- ✅ 路由拦截器（登录验证、权限检查等）
- ✅ 自定义协议跳转（http/https/action 等）
- ✅ 转场动画
- ✅ Intent Flags 设置

## 基础使用

### 1. 注册路由

在 `RouterConfig.kt` 中注册路由：

```kotlin
Router.register(
    mapOf(
        Routes.LOGIN to LoginActivity::class.java,
        Routes.MAIN to MainActivity::class.java
    )
)
```

### 2. 简单跳转

```kotlin
// 方式1：使用扩展函数
navigation(Routes.MAIN)

// 方式2：使用 Router
Router.navigation(Routes.MAIN).navigation(this)

// 方式3：直接跳转
Router.navigation(this, Routes.MAIN)
```

### 3. 携带参数跳转

```kotlin
Router.navigation(Routes.HOME)
    .with("id", 123)
    .with("name", "张三")
    .with("isVip", true)
    .navigation(this)
```

### 4. 接收参数

```kotlin
// 在目标 Activity 中
val id = intent.getRouterInt("id")
val name = intent.getRouterString("name")
val isVip = intent.getRouterBoolean("isVip")
```

### 5. ForResult 跳转

```kotlin
// 发起跳转
navigationForResult(Routes.LOGIN, REQUEST_CODE_LOGIN)

// 或
Router.navigation(Routes.LOGIN)
    .withRequestCode(REQUEST_CODE_LOGIN)
    .navigation(this)

// 接收结果
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_CODE_LOGIN && resultCode == RESULT_OK) {
        // 处理登录成功
    }
}
```

### 6. 设置 Intent Flags

```kotlin
Router.navigation(Routes.MAIN)
    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    .navigation(this)
```

### 7. 设置转场动画

```kotlin
Router.navigation(Routes.HOME)
    .withTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    .navigation(this)
```

## 高级功能

### 1. 路由拦截器

创建拦截器：

```kotlin
class LoginInterceptor : RouterInterceptor {
    override fun intercept(context: Context, request: RouterRequest): Boolean {
        if (!isLoggedIn()) {
            // 跳转到登录页
            Router.navigation(Routes.LOGIN).navigation(context)
            return false // 拦截
        }
        return true // 放行
    }
}
```

注册拦截器：

```kotlin
Router.addInterceptor(LoginInterceptor())
```

### 2. 自定义协议跳转

#### HTTP/HTTPS 跳转

```kotlin
// 自动打开浏览器
Router.navigation("https://www.baidu.com").navigation(this)
```

#### Action 协议跳转

```kotlin
// action://main
Router.navigation("action://main").navigation(this)

// action://home?id=123&name=test
Router.navigation("action://home?id=123&name=test").navigation(this)
```

#### 自定义协议处理器

创建处理器：

```kotlin
class CustomSchemeHandler : SchemeHandler {
    override fun handle(context: Context, uri: Uri, extras: Bundle): Boolean {
        val host = uri.host ?: return false
        // 处理逻辑
        return true
    }
}
```

注册处理器：

```kotlin
Router.registerScheme("myapp", CustomSchemeHandler())
```

使用：

```kotlin
Router.navigation("myapp://detail?id=123").navigation(this)
```

## 完整示例

```kotlin
// 从登录页跳转到主页，携带用户信息
Router.navigation(Routes.MAIN)
    .with("userId", 10001)
    .with("userName", "张三")
    .with("token", "abc123xyz")
    .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    .withTransition(R.anim.fade_in, R.anim.fade_out)
    .navigation(this)

// 在主页接收参数
class MainActivity : BaseVbActivity<ActivityMainBinding>() {
    private val userId by lazy { intent.getRouterInt("userId") }
    private val userName by lazy { intent.getRouterString("userName") }
    private val token by lazy { intent.getRouterString("token") }
    
    override fun initView() {
        // 使用参数
        Timber.d("User: $userName ($userId), Token: $token")
    }
}
```

## 注意事项

1. 所有路由路径建议在 `Routes.kt` 中统一定义
2. 路由必须在使用前注册，建议在 Application 中初始化
3. 拦截器按添加顺序执行，任一拦截器返回 false 则终止跳转
4. 自定义协议优先级：http/https > 自定义协议 > 普通路由
5. 参数类型支持：String、Int、Long、Boolean、Float、Double、Bundle、ArrayList
