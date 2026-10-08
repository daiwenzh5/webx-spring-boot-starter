package io.github.daiwenzh5.webx.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * 全局开关。xxx.enabled=true 后所有能力按各自子开关决定是否生效�? *
 * 子模块会各自 @ConfigurationProperties 绑定更细的配置：
 *   webx.response.* / webx.exception.* / webx.logging.* / webx.ratelimit.* / webx.method-override.*
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "webx")
data class WebxProperties(
    val enabled: Boolean = false,
    val response: ResponseProperties = ResponseProperties(),
    val exception: ExceptionProperties = ExceptionProperties(),
    val logging: LoggingProperties = LoggingProperties(),
    val ratelimit: RateLimitProperties = RateLimitProperties(),
    val methodOverride: MethodOverrideProperties = MethodOverrideProperties(),
)

data class ResponseProperties(
    /** 默认全局包装开关�?*/
    val enabled: Boolean = true,
    /** 路径白名单：匹配上的请求不包装。Ant 风格�?actuator/**）�?*/
    val skipPaths: List<String> = DEFAULT_RESPONSE_SKIP_PATHS,
    /** 路径白名单匹配器类型：ANT（默认）�?REGEX�?*/
    val skipMatcher: SkipMatcher = SkipMatcher.ANT,
) {
    companion object {
        val DEFAULT_RESPONSE_SKIP_PATHS = listOf(
            "/actuator/**",
            "/error",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/webjars/**",
        )
    }
}

enum class SkipMatcher { ANT, REGEX }

data class ExceptionProperties(
    val enabled: Boolean = true,
    /** 是否在响应中暴露 stack trace（仅开发环境建�?true）�?*/
    val exposeStackTrace: Boolean = false,
    /** 是否打印未捕获异常日志�?*/
    val logUnhandled: Boolean = true,
)

data class LoggingProperties(
    val enabled: Boolean = true,
    /** 慢请求阈值（毫秒）�?*/
    val slowThresholdMs: Long = 1500L,
    /** 请求头中透传 traceId 的字段名�?*/
    val traceIdHeader: String = "X-Trace-Id",
    /** 是否在响应头中回�?traceId�?*/
    val echoTraceId: Boolean = true,
)

data class RateLimitProperties(
    val enabled: Boolean = true,
    /** 全局默认 QPS�? 表示不限�?*/
    val defaultPermitsPerSecond: Double = 0.0,
    /** 限流命中后返回的错误码�?*/
    val errorCode: Int = 4029,
)

data class MethodOverrideProperties(
    val enabled: Boolean = true,
    /** 自定�?header 名称，默�?X-HTTP-Method-Override�?*/
    val header: String = "X-HTTP-Method-Override",
    /** 允许被覆盖的目标 method 集合�?*/
    val allowed: Set<String> = setOf("GET", "PUT", "DELETE", "PATCH"),
)
