package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.WebxProperties
import io.github.daiwenzh5.webx.filter.HttpMethodOverrideFilter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "webx.method-override", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebxProperties::class)
class MethodOverrideAutoConfiguration {

    @Bean
    fun webxMethodOverrideFilter(properties: WebxProperties): FilterRegistrationBean<HttpMethodOverrideFilter> {
        val reg = FilterRegistrationBean(
            HttpMethodOverrideFilter(
                header = properties.methodOverride.header,
                allowed = properties.methodOverride.allowed,
            )
        )
        // 必须早于 Spring 的 HiddenHttpMethodFilter
        reg.order = Ordered.HIGHEST_PRECEDENCE
        reg.addUrlPatterns("/*")
        return reg
    }
}
