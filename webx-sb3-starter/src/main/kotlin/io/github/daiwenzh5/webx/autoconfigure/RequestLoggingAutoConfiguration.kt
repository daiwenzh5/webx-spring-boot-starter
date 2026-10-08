package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.WebxProperties
import io.github.daiwenzh5.webx.filter.RequestLoggingFilter
import io.github.daiwenzh5.webx.trace.TraceIdGenerator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "webx.logging", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebxProperties::class)
class RequestLoggingAutoConfiguration {

    @Bean
    fun webxRequestLoggingFilter(properties: WebxProperties): FilterRegistrationBean<RequestLoggingFilter> {
        val reg = FilterRegistrationBean(RequestLoggingFilter(properties, ::resolve))
        reg.order = Ordered.HIGHEST_PRECEDENCE + 10
        reg.addUrlPatterns("/*")
        return reg
    }

    private fun resolve(req: jakarta.servlet.http.HttpServletRequest, header: String): String {
        val incoming = req.getHeader(header)
        return if (!incoming.isNullOrBlank()) incoming else TraceIdGenerator.next()
    }
}
