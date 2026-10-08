package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.WebxProperties
import io.github.daiwenzh5.webx.ratelimit.InMemoryRateLimiter
import io.github.daiwenzh5.webx.ratelimit.RateLimiter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "webx.ratelimit", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebxProperties::class)
class RateLimitAutoConfiguration {

    /**
     * 默认内存实现。业务项目可自定义 RateLimiter Bean 来覆盖（如 Redis）。
     */
    @Bean
    @ConditionalOnMissingBean
    fun webxRateLimiter(): RateLimiter = InMemoryRateLimiter()
}
