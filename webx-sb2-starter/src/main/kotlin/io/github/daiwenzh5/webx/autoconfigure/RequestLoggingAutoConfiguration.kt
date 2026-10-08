package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.WebxProperties
import io.github.daiwenzh5.webx.filter.RequestLoggingFilter
import io.github.daiwenzh5.webx.trace.TraceIdGenerator
import org.slf4j.MDC
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
        val registration = FilterRegistrationBean(RequestLoggingFilter(properties, ::generateOrEcho))
        registration.order = Ordered.HIGHEST_PRECEDENCE + 10
        registration.addUrlPatterns("/*")
        return registration
    }

    private fun generateOrEcho(request: javax.servlet.http.HttpServletRequest, headerName: String): String {
        val incoming = request.getHeader(headerName)
        return if (!incoming.isNullOrBlank()) incoming else TraceIdGenerator.next()
    }
}
