package io.github.daiwenzh5.webx.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

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
    // Wrap all controller return values into R<T> by default.
    val enabled: Boolean = true,
    // Path whitelist that bypasses wrapping. ANT style.
    val skipPaths: List<String> = DEFAULT_RESPONSE_SKIP_PATHS,
    // Matcher type for skipPaths: ANT or REGEX.
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
    // Include stack trace info in 500 responses (development only).
    val exposeStackTrace: Boolean = false,
    // Log unhandled exceptions.
    val logUnhandled: Boolean = true,
)

data class LoggingProperties(
    val enabled: Boolean = true,
    // Threshold (ms) above which a request is logged as "slow".
    val slowThresholdMs: Long = 1500L,
    // Header used to receive or send TraceId.
    val traceIdHeader: String = "X-Trace-Id",
    // Echo TraceId back to client in response header.
    val echoTraceId: Boolean = true,
)

data class RateLimitProperties(
    val enabled: Boolean = true,
    // Default QPS for rate-limited endpoints. 0 disables.
    val defaultPermitsPerSecond: Double = 0.0,
    // Error code returned when rate limit is hit.
    val errorCode: Int = 4029,
)

data class MethodOverrideProperties(
    val enabled: Boolean = true,
    // Header name used to override POST method.
    val header: String = "X-HTTP-Method-Override",
    // Allowed target HTTP methods.
    val allowed: Set<String> = setOf("GET", "PUT", "DELETE", "PATCH"),
)
