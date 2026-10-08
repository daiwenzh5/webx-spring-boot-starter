# webx-spring-boot-starter

[![CI](https://github.com/daiwenzh5/webx-spring-boot-starter/actions/workflows/ci.yml/badge.svg)](https://github.com/daiwenzh5/webx-spring-boot-starter/actions/workflows/ci.yml)
[![Publish](https://github.com/daiwenzh5/webx-spring-boot-starter/actions/workflows/publish.yml/badge.svg)](https://github.com/daiwenzh5/webx-spring-boot-starter/actions/workflows/publish.yml)

基于 Kotlin 的 Spring Boot Starter，提供一组**默认关闭、按需启用**的通用 Web 扩展 Auto-Configuration：

- **统一响应包装** `R<T>` —— 通过 `ResponseBodyAdvice` 自动包装 Controller 返回值。
- **全局异常处理** —— `BusinessException` + 内置错误码。
- **请求日志 / TraceId** —— MDC 注入、慢请求日志。
- **限流** —— `RateLimiter` 接口 + 内存默认实现（业务方可注入 Redis 版）。
- **HTTP Method Override** —— header `X-HTTP-Method-Override` 改写 POST 实际 method。

## 设计要点

- **JDK 8 + JDK 17/21 双支持**：仓库分 `webx-sb2-starter`（SB 2.7，javax.servlet）与 `webx-sb3-starter`（SB 3.x，jakarta.servlet）。
- **单一 Kotlin 实现**：`kotlin-stdlib` 作为传递依赖，业务项目无需显式声明。
- **core 不依赖 servlet**：所有 servlet API 引用都收敛在各自的 starter 模块。
- **默认全关闭**：必须 `webx.enabled=true` 才会激活，激活后各能力还可单独开关。

## 仓库布局

```
webx-spring-boot-starter/
├── settings.gradle.kts
├── build.gradle.kts
├── gradle/libs.versions.toml
├── webx-core/                   # 抽象与算法，无 servlet 依赖
├── webx-sb2-starter/            # SB 2.7 + javax.servlet
└── webx-sb3-starter/            # SB 3.x + jakarta.servlet
```

## 快速开始

### 从 GitHub Packages 引入

发布后的稳定版本（推荐生产使用）：

```kotlin
// settings.gradle.kts
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/daiwenzh5/webx-spring-boot-starter")
        credentials {
            // 业务项目用细粒度 PAT（read:packages）
            val githubUser: String? = providers.gradleProperty("gpr.user").orNull
                ?: System.getenv("GITHUB_ACTOR")
            val githubToken: String? = providers.gradleProperty("gpr.token").orNull
                ?: System.getenv("GITHUB_TOKEN")
            if (githubUser != null) setUsername(githubUser)
            if (githubToken != null) setPassword(githubToken)
        }
    }
}

// build.gradle.kts
dependencies {
    // Spring Boot 2.7（JDK 8）
    implementation("io.github.daiwenzh5.webx:webx-spring-boot-starter-2:0.1.0")

    // Spring Boot 3.x（JDK 17 / 21）
    implementation("io.github.daiwenzh5.webx:webx-spring-boot-starter:0.1.0")
}
```

### 本地开发引用（不依赖发布）

在仓库根目录直接：

```kotlin
// settings.gradle.kts 里 include(":example") 已经包含此模块
dependencies {
    implementation(project(":webx-sb3-starter"))   // 或 :webx-sb2-starter
}
```

### 发布新版本

```bash
git tag v0.1.0
git push origin v0.1.0
# .github/workflows/publish.yml 自动触发：构建 + 发布到 GitHub Packages + 创建 GitHub Release
```

手动触发（指定版本号）：在 GitHub Actions 页面 `Publish` workflow 选择 `Run workflow`，输入 `0.2.0-SNAPSHOT`。

### application.yml

```yaml
webx:
  enabled: true                 # 总开关；缺省 false
  response:
    enabled: true               # 各能力可单独关闭
    skip-paths:
      - /actuator/**
      - /error
      - /swagger-ui/**
      - /v3/api-docs/**
  exception:
    enabled: true
    expose-stack-trace: false   # 生产环境必须 false
  logging:
    enabled: true
    slow-threshold-ms: 1500
  ratelimit:
    enabled: true
  method-override:
    enabled: true
    header: X-HTTP-Method-Override
```

### 自定义限流实现（可选）

```kotlin
@Bean
fun redisRateLimiter(redisTemplate: StringRedisTemplate): RateLimiter =
    RedisRateLimiter(redisTemplate)  // 实现 RateLimiter 接口即可
```

业务方提供的 Bean 会通过 `@ConditionalOnMissingBean` 自动覆盖内存默认实现。

### 跳过响应包装

```kotlin
@RestController
@RawResponse                      // 整个 Controller 返回原始响应
class HealthController {
    @GetMapping("/health")
    fun health() = mapOf("status" to "UP")
}
```

### Method Override 示例

```bash
# 前端只能发 POST，但想调用 DELETE /api/users/42
curl -X POST \
     -H "X-HTTP-Method-Override: DELETE" \
     https://api.example.com/api/users/42
```

## 构建

仓库自带 **Gradle Wrapper**（锁版本 8.10.2），无需预装 Gradle。

```bash
# Linux / macOS / Git Bash
./gradlew :webx-sb2-starter:publishToMavenLocal
./gradlew :webx-sb3-starter:publishToMavenLocal

# Windows cmd / PowerShell
.\gradlew.bat :webx-sb2-starter:publishToMavenLocal
.\gradlew.bat :webx-sb3-starter:publishToMavenLocal
```

> 首次运行 Wrapper 会自动下载 Gradle 发行版（~150MB）到 `~/.gradle/wrapper/dists/`。
> 在 CI 上建议预热缓存：`./gradlew --version` 之后所有任务都走缓存路径。

## 路线图

- 幂等（`@Idempotent`，Redis 去重）
- 防重放签名
- 当前用户解析（`@CurrentUser`）
- 接口版本路由（`@Version`）
- 响应加解密

每个能力单独 PR，独立发版。
