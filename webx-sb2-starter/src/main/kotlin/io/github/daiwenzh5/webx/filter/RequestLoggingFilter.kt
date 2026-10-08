package io.github.daiwenzh5.webx.filter

import io.github.daiwenzh5.webx.config.WebxProperties
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 请求日志与 TraceId 注入：
 *  - 从 header 透传 traceId，无则生成；
 *  - 写入 MDC；
 *  - 可选回写到响应头；
 *  - 慢请求记录 warn 日志。
 */
class RequestLoggingFilter(
    private val properties: WebxProperties,
    private val resolveTraceId: (HttpServletRequest, String) -> String,
) : Filter {

    private val log = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    @Throws(ServletException::class)
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
