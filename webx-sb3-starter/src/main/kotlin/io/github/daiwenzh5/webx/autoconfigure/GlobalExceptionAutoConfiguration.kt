package io.github.daiwenzh5.webx.autoconfigure

import io.github.daiwenzh5.webx.api.BusinessException
import io.github.daiwenzh5.webx.api.BuiltInErrorCodes
import io.github.daiwenzh5.webx.api.R
import io.github.daiwenzh5.webx.config.WebxProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@ConditionalOnProperty(prefix = "webx.exception", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(WebxProperties::class)
class GlobalExceptionAutoConfiguration {

    private val log = LoggerFactory.getLogger(GlobalExceptionAutoConfiguration::class.java)

    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(ex: BusinessException): ResponseEntity<R<Void>> =
        ResponseEntity.ok(R.fail(ex.errorCode.code, ex.message ?: ex.errorCode.message))

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegal(ex: IllegalArgumentException): ResponseEntity<R<Void>> =
        ResponseEntity.badRequest().body(R.fail(BuiltInErrorCodes.INVALID_PARAMETER, ex.message ?: "Invalid argument"))

    @ExceptionHandler(Exception::class)
    fun handleAny(ex: Exception, properties: WebxProperties): ResponseEntity<R<Map<String, Any?>>> {
        if (properties.exception.logUnhandled) log.error("unhandled exception", ex)
        val body = if (properties.exception.exposeStackTrace)
            mapOf("exception" to ex.javaClass.name, "message" to ex.message)
        else emptyMap()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(R.fail(BuiltInErrorCodes.INTERNAL_ERROR, "Internal server error", body))
    }
}
