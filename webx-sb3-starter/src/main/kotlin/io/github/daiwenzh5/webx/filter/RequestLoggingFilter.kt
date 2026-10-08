package io.github.daiwenzh5.webx.filter

import io.github.daiwenzh5.webx.config.WebxProperties
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.io.IOException

class RequestLoggingFilter(
    private val properties: WebxProperties,
    private val resolveTraceId: (HttpServletRequest, String) -> String,
) : Filter {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val httpReq = req as HttpServletRequest
        val httpRes = res as HttpServletResponse
        val traceId = resolveTraceId(httpReq, properties.logging.traceIdHeader)
        MDC.put("traceId", traceId)
        if (properties.logging.echoTraceId) httpRes.setHeader(properties.logging.traceIdHeader, traceId)

        val start = System.nanoTime()
        try {
            chain.doFilter(req, res)
        } finally {
            val costMs = (System.nanoTime() - start) / 1_000_000L
            if (costMs >= properties.logging.slowThresholdMs) {
                log.warn("slow request {} {} cost={}ms", httpReq.method, httpReq.requestURI, costMs)
            } else {
                log.info("{} {} cost={}ms", httpReq.method, httpReq.requestURI, costMs)
            }
            MDC.remove("traceId")
        }
    }
}
