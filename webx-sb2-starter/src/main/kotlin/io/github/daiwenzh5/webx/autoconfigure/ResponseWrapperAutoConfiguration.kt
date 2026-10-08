package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.annotation.RawResponse
import io.github.daiwenzh5.webx.api.R
import io.github.daiwenzh5.webx.config.ResponseProperties
import io.github.daiwenzh5.webx.config.SkipMatcher
import io.github.daiwenzh5.webx.config.WebxProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.util.AntPathMatcher
import org.springframework.util.PathMatcher
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import java.lang.reflect.Method

/**
 * 全局响应包装：
 *  - 在序列化前把 Controller 返回值包成 R<T>；
 *  - 类或方法标注 @RawResponse 时跳过；
 *  - 路径命中 skip-paths 时跳过（ANT 或 REGEX）；
 *  - 已经是 R 的返回值不再二次包装。
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "webx.response", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebxProperties::class)
class ResponseWrapperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    fun webxResponseBodyAdvice(properties: WebxProperties): ResponseBodyAdvice<Any> {
        val respProps = properties.response
        val pathMatcher: PathMatcher = AntPathMatcher()
        return object : ResponseBodyAdvice<Any> {
            override fun supports(
                returnType: MethodParameter,
                converterType: Class<out HttpMessageConverter<*>>,
            ): Boolean = true

            override fun beforeBodyWrite(
                body: Any?,
                returnType: MethodParameter,
                selectedContentType: MediaType,
                selectedConverterType: Class<out HttpMessageConverter<*>>,
                request: ServerHttpRequest,
                response: ServerHttpResponse,
            ): Any? {
                val path = request.uri.path
                val method: Method = returnType.method
                val beanType = returnType.declaringClass

                // 1. 已经是 R 不包装
                if (body is R<*>) return body
                // 2. void / null 不包装（由 ControllerAdvice 统一处理 null）
                if (body == null) return R.ok<Any>()

                // 3. @RawResponse 注解
                if (beanType.isAnnotationPresent(RawResponse::class.java)) return body
                if (method.isAnnotationPresent(RawResponse::class.java)) return body

                // 4. 路径白名单
                if (isSkipped(path, respProps, pathMatcher)) return body

                return R.ok(body)
            }
        }
    }

    private fun isSkipped(path: String, props: ResponseProperties, matcher: PathMatcher): Boolean {
        return when (props.skipMatcher) {
            SkipMatcher.ANT -> props.skipPaths.any { matcher.match(it, path) }
            SkipMatcher.REGEX -> props.skipPaths.any { Regex(it).matches(path) }
        }
    }
}
