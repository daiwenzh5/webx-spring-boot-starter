package io.github.daiwenzh5.webx.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.util.Locale

/**
 * X-HTTP-Method-Override 头部解析：
 *  - 仅在原 method 为 POST 时生效；
 *  - 仅当目标 method 在 allowed 集合中才允许覆盖；
 *  - 不改写 body；请求下游拿到的 request.getMethod() 已是覆盖后的 method。
 *
 * 设计参考 Spring 的 HiddenHttpMethodFilter，但走 header 而非 form 参数。
 */
class HttpMethodOverrideFilter(
    private val header: String,
    private val allowed: Set<String>,
) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val httpReq = req as HttpServletRequest
        val httpRes = res as HttpServletResponse
        if ("POST" != httpReq.method.uppercase(Locale.ROOT)) {
            chain.doFilter(req, res)
            return
        }
        val override = httpReq.getHeader(header)?.uppercase(Locale.ROOT)
        if (override != null && override in allowed) {
            chain.doFilter(MethodOverrideRequest(httpReq, override), httpRes)
        } else {
            chain.doFilter(req, res)
        }
    }

    private class MethodOverrideRequest(req: HttpServletRequest, private val method: String) :
        HttpServletRequestWrapper(req) {
        override fun getMethod(): String = method
    }
}
