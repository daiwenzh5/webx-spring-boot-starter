package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.config.WebxProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "webx", name = ["enabled"], havingValue = "true")
@EnableConfigurationProperties(WebxProperties::class)
class WebxAutoConfiguration
