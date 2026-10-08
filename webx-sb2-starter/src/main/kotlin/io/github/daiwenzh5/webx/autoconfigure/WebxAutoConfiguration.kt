package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.ResponseProperties
import io.github.daiwenzh5.webx.config.WebxProperties
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * 总入口（仅在 webx.enabled=true 且 web 应用时加载）。
 *
 * 各子能力 Auto-Configuration 通过 @Import 在此聚合，方便业务方使用
 * @SpringBootApplication 注解时只需要这一个 EnableWebx。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "webx", name = ["enabled"], havingValue = "true")
@EnableConfigurationProperties(WebxProperties::class)
@Import(
    ResponseWrapperAutoConfiguration::class,
    GlobalExceptionAutoConfiguration::class,
    RequestLoggingAutoConfiguration::class,
    RateLimitAutoConfiguration::class,
    MethodOverrideAutoConfiguration::class,
)
@AutoConfigureAfter(name = ["org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration"])
class WebxAutoConfiguration
