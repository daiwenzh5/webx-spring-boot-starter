package io.github.daiwenzh5.webx.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import java.io.IOException
import java.util.Locale

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
